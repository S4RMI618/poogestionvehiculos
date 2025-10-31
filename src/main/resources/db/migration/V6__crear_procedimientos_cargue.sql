-- ==========================================================
-- PROCEDIMIENTOS ALMACENADOS PARA CARGUE MASIVO DE TRAYECTOS
-- ==========================================================

-- Eliminar procedimientos si existen
DROP PROCEDURE IF EXISTS sp_cargar_trayectos;
DROP PROCEDURE IF EXISTS sp_validar_trayectos;
DROP PROCEDURE IF EXISTS sp_procesar_trayectos;

-- ==============================================================
-- PROCEDIMIENTO 1: CARGUE
-- Los datos ya fueron insertados por Java en trayectos_tmp
-- Este SP solo marca el inicio del proceso y valida integridad
-- ==============================================================
DELIMITER //

CREATE PROCEDURE sp_cargar_trayectos(IN p_id_cargue BIGINT)
BEGIN
    DECLARE v_total_registros INT;

    -- Contar registros del cargue
    SELECT COUNT(*) INTO v_total_registros
    FROM trayectos_tmp
    WHERE id_cargue = p_id_cargue;

    -- Validar que existan registros
    IF v_total_registros = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se encontraron registros para el ID de cargue especificado';
    END IF;

    -- Log del proceso
    SELECT
        p_id_cargue as id_cargue,
        v_total_registros as registros_cargados,
        'Cargue iniciado correctamente' as mensaje,
        NOW() as fecha_proceso;

END //

DELIMITER ;

-- ===============================================================
-- PROCEDIMIENTO 2: VALIDACIÓN
-- Validar TODOS los registros y marcarlos como VALIDADO o ERROR
-- ===============================================================
DELIMITER //

CREATE PROCEDURE sp_validar_trayectos(IN p_id_cargue BIGINT)
BEGIN
    DECLARE v_registros_procesados INT DEFAULT 0;
    DECLARE v_registros_validados INT DEFAULT 0;
    DECLARE v_registros_con_error INT DEFAULT 0;

    -- Iniciar transacción
    START TRANSACTION;

    -- ===========================================
    -- VALIDACIÓN 1: Campos obligatorios vacíos
    -- ===========================================
    UPDATE trayectos_tmp
    SET
        estado = 'ERROR',
        observacion = CONCAT(
            IFNULL(observacion, ''),
            IF(observacion IS NULL OR observacion = '', '', ' | '),
            'Campos obligatorios faltantes: ',
            IF(conductor_id IS NULL OR conductor_id = '', 'conductorId ', ''),
            IF(vehiculo_id IS NULL OR vehiculo_id = '', 'vehiculoId ', ''),
            IF(codigo_ruta IS NULL OR codigo_ruta = '', 'codigoRuta ', ''),
            IF(ubicacion IS NULL OR ubicacion = '', 'ubicacion ', ''),
            IF(orden_parada IS NULL OR orden_parada = '', 'ordenParada ', '')
        )
    WHERE id_cargue = p_id_cargue
    AND estado = 'CARGADO'
    AND (
        conductor_id IS NULL OR conductor_id = '' OR
        vehiculo_id IS NULL OR vehiculo_id = '' OR
        codigo_ruta IS NULL OR codigo_ruta = '' OR
        ubicacion IS NULL OR ubicacion = '' OR
        orden_parada IS NULL OR orden_parada = ''
    );

    -- ===========================================
    -- VALIDACIÓN 2: Formato numérico de IDs
    -- ===========================================
    UPDATE trayectos_tmp
    SET
        estado = 'ERROR',
        observacion = CONCAT(
            IFNULL(observacion, ''),
            IF(observacion IS NULL OR observacion = '', '', ' | '),
            'conductorId debe ser numérico'
        )
    WHERE id_cargue = p_id_cargue
    AND estado = 'CARGADO'
    AND conductor_id IS NOT NULL
    AND conductor_id != ''
    AND conductor_id NOT REGEXP '^[0-9]+$';

    UPDATE trayectos_tmp
    SET
        estado = 'ERROR',
        observacion = CONCAT(
            IFNULL(observacion, ''),
            IF(observacion IS NULL OR observacion = '', '', ' | '),
            'vehiculoId debe ser numérico'
        )
    WHERE id_cargue = p_id_cargue
    AND estado = 'CARGADO'
    AND vehiculo_id IS NOT NULL
    AND vehiculo_id != ''
    AND vehiculo_id NOT REGEXP '^[0-9]+$';

    UPDATE trayectos_tmp
    SET
        estado = 'ERROR',
        observacion = CONCAT(
            IFNULL(observacion, ''),
            IF(observacion IS NULL OR observacion = '', '', ' | '),
            'ordenParada debe ser numérico'
        )
    WHERE id_cargue = p_id_cargue
    AND estado = 'CARGADO'
    AND orden_parada IS NOT NULL
    AND orden_parada != ''
    AND orden_parada NOT REGEXP '^[0-9]+$';

    -- ===========================================
    -- VALIDACIÓN 3: Rango de ordenParada
    -- ===========================================
    UPDATE trayectos_tmp
    SET
        estado = 'ERROR',
        observacion = CONCAT(
            IFNULL(observacion, ''),
            IF(observacion IS NULL OR observacion = '', '', ' | '),
            'ordenParada debe estar entre 0 y 6'
        )
    WHERE id_cargue = p_id_cargue
    AND estado = 'CARGADO'
    AND orden_parada REGEXP '^[0-9]+$'
    AND (CAST(orden_parada AS UNSIGNED) < 0 OR CAST(orden_parada AS UNSIGNED) > 6);

    -- ===========================================
    -- VALIDACIÓN 4: Formato de coordenadas
    -- ===========================================
    UPDATE trayectos_tmp
    SET
        estado = 'ERROR',
        observacion = CONCAT(
            IFNULL(observacion, ''),
            IF(observacion IS NULL OR observacion = '', '', ' | '),
            'latitud debe ser numérica'
        )
    WHERE id_cargue = p_id_cargue
    AND estado = 'CARGADO'
    AND latitud IS NOT NULL
    AND latitud != ''
    AND latitud NOT REGEXP '^-?[0-9]+\.?[0-9]*$';

    UPDATE trayectos_tmp
    SET
        estado = 'ERROR',
        observacion = CONCAT(
            IFNULL(observacion, ''),
            IF(observacion IS NULL OR observacion = '', '', ' | '),
            'longitud debe ser numérica'
        )
    WHERE id_cargue = p_id_cargue
    AND estado = 'CARGADO'
    AND longitud IS NOT NULL
    AND longitud != ''
    AND longitud NOT REGEXP '^-?[0-9]+\.?[0-9]*$';

    -- Validar rangos de latitud
    UPDATE trayectos_tmp
    SET
        estado = 'ERROR',
        observacion = CONCAT(
            IFNULL(observacion, ''),
            IF(observacion IS NULL OR observacion = '', '', ' | '),
            'latitud debe estar entre -90 y 90'
        )
    WHERE id_cargue = p_id_cargue
    AND estado = 'CARGADO'
    AND latitud IS NOT NULL
    AND latitud != ''
    AND latitud REGEXP '^-?[0-9]+\.?[0-9]*$'
    AND (CAST(latitud AS DECIMAL(10,8)) < -90 OR CAST(latitud AS DECIMAL(10,8)) > 90);

    -- Validar rangos de longitud
    UPDATE trayectos_tmp
    SET
        estado = 'ERROR',
        observacion = CONCAT(
            IFNULL(observacion, ''),
            IF(observacion IS NULL OR observacion = '', '', ' | '),
            'longitud debe estar entre -180 y 180'
        )
    WHERE id_cargue = p_id_cargue
    AND estado = 'CARGADO'
    AND longitud IS NOT NULL
    AND longitud != ''
    AND longitud REGEXP '^-?[0-9]+\.?[0-9]*$'
    AND (CAST(longitud AS DECIMAL(11,8)) < -180 OR CAST(longitud AS DECIMAL(11,8)) > 180);

    -- ===========================================
    -- VALIDACIÓN 5: Existencia de conductor
    -- ===========================================
    UPDATE trayectos_tmp t
    LEFT JOIN personas p ON
        p.id = CAST(t.conductor_id AS UNSIGNED) AND
        p.tipo_persona = 'C'
    SET
        t.estado = 'ERROR',
        t.observacion = CONCAT(
            IFNULL(t.observacion, ''),
            IF(t.observacion IS NULL OR t.observacion = '', '', ' | '),
            'Conductor no existe con ID: ', t.conductor_id, ' o no es tipo CONDUCTOR'
        )
    WHERE t.id_cargue = p_id_cargue
    AND t.estado = 'CARGADO'
    AND t.conductor_id REGEXP '^[0-9]+$'
    AND p.id IS NULL;

    -- ===========================================
    -- VALIDACIÓN 6: Licencia vigente del conductor
    -- ===========================================
    UPDATE trayectos_tmp t
    JOIN personas p ON
        p.id = CAST(t.conductor_id AS UNSIGNED) AND
        p.tipo_persona = 'C'
    SET
        t.estado = 'ERROR',
        t.observacion = CONCAT(
            IFNULL(t.observacion, ''),
            IF(t.observacion IS NULL OR t.observacion = '', '', ' | '),
            'Licencia de conducción vencida para conductor: ', t.conductor_id
        )
    WHERE t.id_cargue = p_id_cargue
    AND t.estado = 'CARGADO'
    AND t.conductor_id REGEXP '^[0-9]+$'
    AND (p.fecha_vigencia_licencia IS NULL OR p.fecha_vigencia_licencia < CURDATE());

    -- ===========================================
    -- VALIDACIÓN 7: Existencia de vehículo
    -- ===========================================
    UPDATE trayectos_tmp t
    LEFT JOIN vehiculos v ON v.id = CAST(t.vehiculo_id AS UNSIGNED)
    SET
        t.estado = 'ERROR',
        t.observacion = CONCAT(
            IFNULL(t.observacion, ''),
            IF(t.observacion IS NULL OR t.observacion = '', '', ' | '),
            'Vehículo no existe con ID: ', t.vehiculo_id
        )
    WHERE t.id_cargue = p_id_cargue
    AND t.estado = 'CARGADO'
    AND t.vehiculo_id REGEXP '^[0-9]+$'
    AND v.id IS NULL;

    -- ===========================================
    -- VALIDACIÓN 8: Documentos del vehículo habilitados
    -- ===========================================
    UPDATE trayectos_tmp t
    JOIN vehiculos v ON v.id = CAST(t.vehiculo_id AS UNSIGNED)
    SET
        t.estado = 'ERROR',
        t.observacion = CONCAT(
            IFNULL(t.observacion, ''),
            IF(t.observacion IS NULL OR t.observacion = '', '', ' | '),
            'El vehículo tiene documentos no habilitados'
        )
    WHERE t.id_cargue = p_id_cargue
    AND t.estado = 'CARGADO'
    AND t.vehiculo_id REGEXP '^[0-9]+$'
    AND EXISTS (
        SELECT 1
        FROM vehiculo_documentos vd
        WHERE vd.vehiculo_id = v.id
        AND vd.estado != 'HABILITADO'
    );

    -- ===========================================
    -- VALIDACIÓN 9: Relación conductor-vehículo
    -- ===========================================
    UPDATE trayectos_tmp t
    LEFT JOIN vehiculo_conductores vc ON
        vc.conductor_id = CAST(t.conductor_id AS UNSIGNED) AND
        vc.vehiculo_id = CAST(t.vehiculo_id AS UNSIGNED) AND
        vc.estado = 'PO'
    SET
        t.estado = 'ERROR',
        t.observacion = CONCAT(
            IFNULL(t.observacion, ''),
            IF(t.observacion IS NULL OR t.observacion = '', '', ' | '),
            'El conductor ', t.conductor_id, ' no está autorizado para operar el vehículo ', t.vehiculo_id
        )
    WHERE t.id_cargue = p_id_cargue
    AND t.estado = 'CARGADO'
    AND t.conductor_id REGEXP '^[0-9]+$'
    AND t.vehiculo_id REGEXP '^[0-9]+$'
    AND vc.id IS NULL;

    -- ===========================================
    -- MARCAR COMO VALIDADO los que no tienen errores
    -- ===========================================
    UPDATE trayectos_tmp
    SET estado = 'VALIDADO'
    WHERE id_cargue = p_id_cargue
    AND estado = 'CARGADO';

    -- Commit de la transacción
    COMMIT;

    -- Contar resultados
    SELECT COUNT(*) INTO v_registros_validados
    FROM trayectos_tmp
    WHERE id_cargue = p_id_cargue
    AND estado = 'VALIDADO';

    SELECT COUNT(*) INTO v_registros_con_error
    FROM trayectos_tmp
    WHERE id_cargue = p_id_cargue
    AND estado = 'ERROR';

    SET v_registros_procesados = v_registros_validados + v_registros_con_error;

    -- Resultado
    SELECT
        p_id_cargue as id_cargue,
        v_registros_procesados AS total_procesado,
        v_registros_validados AS registros_validados,
        v_registros_con_error AS registros_con_error,
        'Validación completada' as mensaje,
        NOW() as fecha_proceso;

END //

DELIMITER ;

-- ============================================================================
-- PROCEDIMIENTO 3: PROCESADO
-- Trasladar registros VALIDADOS a tabla transaccional trayectos
-- ============================================================================
DELIMITER //

CREATE PROCEDURE sp_procesar_trayectos(IN p_id_cargue BIGINT)
BEGIN
    DECLARE v_registros_procesados INT DEFAULT 0;
    DECLARE v_registros_disponibles INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'Error al procesar trayectos' AS mensaje, 0 AS registros_procesados;
    END;

    -- Verificar que haya registros validados
    SELECT COUNT(*) INTO v_registros_disponibles
    FROM trayectos_tmp
    WHERE id_cargue = p_id_cargue
    AND estado = 'VALIDADO';

    IF v_registros_disponibles = 0 THEN
        SELECT
            p_id_cargue as id_cargue,
            0 AS registros_procesados,
            'No hay registros validados para procesar' AS mensaje,
            NOW() as fecha_proceso;
    ELSE
        -- Iniciar transacción
        START TRANSACTION;

        -- Insertar registros validados en la tabla trayectos
        INSERT INTO trayectos (
            conductor_id,
            vehiculo_id,
            codigo_ruta,
            ubicacion,
            orden_parada,
            latitud,
            longitud,
            login_usuario_registro,
            fecha_registro
        )
        SELECT
            CAST(t.conductor_id AS UNSIGNED),
            CAST(t.vehiculo_id AS UNSIGNED),
            t.codigo_ruta,
            t.ubicacion,
            CAST(t.orden_parada AS UNSIGNED),
            CASE
                WHEN t.latitud IS NOT NULL AND t.latitud != '' AND t.latitud REGEXP '^-?[0-9]+\.?[0-9]*$'
                THEN CAST(t.latitud AS DECIMAL(10, 8))
                ELSE NULL
            END,
            CASE
                WHEN t.longitud IS NOT NULL AND t.longitud != '' AND t.longitud REGEXP '^-?[0-9]+\.?[0-9]*$'
                THEN CAST(t.longitud AS DECIMAL(11, 8))
                ELSE NULL
            END,
            t.login_usuario_registro,
            NOW()
        FROM trayectos_tmp t
        WHERE t.id_cargue = p_id_cargue
        AND t.estado = 'VALIDADO';

        -- Obtener número de registros insertados
        SET v_registros_procesados = ROW_COUNT();

        -- Actualizar estado a PROCESADO en trayectos_tmp
        UPDATE trayectos_tmp
        SET estado = 'PROCESADO'
        WHERE id_cargue = p_id_cargue
        AND estado = 'VALIDADO';

        -- Commit de la transacción
        COMMIT;

        -- Resultado
        SELECT
            p_id_cargue as id_cargue,
            v_registros_procesados AS registros_procesados,
            'Procesamiento completado exitosamente' AS mensaje,
            NOW() as fecha_proceso;
    END IF;

END //

DELIMITER ;

-- ============================================================================