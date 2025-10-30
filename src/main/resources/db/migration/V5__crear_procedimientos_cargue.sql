-- Eliminar procedimientos si existen (para recrearlos)
DROP PROCEDURE IF EXISTS sp_cargar_trayectos;
DROP PROCEDURE IF EXISTS sp_validar_trayectos;
DROP PROCEDURE IF EXISTS sp_procesar_trayectos;

-- ============================================================================
-- PROCEDIMIENTO 1: CARGUE
-- Obtener datos del Excel y trasladarlos a trayectos_tmp
-- ============================================================================
DELIMITER //

CREATE PROCEDURE sp_cargar_trayectos(IN p_id_cargue BIGINT)
BEGIN
    -- Este procedimiento se usa cuando se hace cargue directo desde la aplicación
    -- En nuestro caso, el cargue lo hace el servicio Java leyendo el Excel

    -- Log de inicio
    SELECT CONCAT('Iniciando cargue para ID: ', p_id_cargue) AS mensaje;

    -- Aquí se podrían agregar validaciones adicionales o transformaciones
    -- Por ahora, el cargue lo maneja completamente el servicio Java

    SELECT
        COUNT(*) as total_registros_cargados
    FROM trayectos_tmp
    WHERE id_cargue = p_id_cargue
    AND estado = 'CARGADO';

END //

DELIMITER ;

-- ============================================================================
-- PROCEDIMIENTO 2:
-- Validar registros y marcar como VALIDADO o ERROR
-- ============================================================================
DELIMITER //

CREATE PROCEDURE sp_validar_trayectos(IN p_id_cargue BIGINT)
BEGIN
    DECLARE v_registros_validados INT DEFAULT 0;
    DECLARE v_registros_con_error INT DEFAULT 0;

    -- Log de inicio
    SELECT CONCAT('Iniciando validación para cargue ID: ', p_id_cargue) AS mensaje;

    -- Validar campos obligatorios
    UPDATE trayectos_tmp
    SET
        estado = 'ERROR',
        observacion = CONCAT(IFNULL(observacion, ''),
                            IF(observacion IS NULL OR observacion = '', '', ' | '),
                            'Campos obligatorios faltantes')
    WHERE id_cargue = p_id_cargue
    AND estado = 'CARGADO'
    AND (
        conductor_id IS NULL OR conductor_id = '' OR
        vehiculo_id IS NULL OR vehiculo_id = '' OR
        codigo_ruta IS NULL OR codigo_ruta = '' OR
        ubicacion IS NULL OR ubicacion = '' OR
        orden_parada IS NULL OR orden_parada = ''
    );

    -- Validar que conductor existe y es tipo C
    UPDATE trayectos_tmp t
    LEFT JOIN personas p ON p.id = CAST(t.conductor_id AS UNSIGNED)
    SET
        t.estado = 'ERROR',
        t.observacion = CONCAT(IFNULL(t.observacion, ''),
                              IF(t.observacion IS NULL OR t.observacion = '', '', ' | '),
                              'Conductor no existe o no es tipo CONDUCTOR')
    WHERE t.id_cargue = p_id_cargue
    AND t.estado = 'CARGADO'
    AND (p.id IS NULL OR p.tipo_persona != 'C');

    -- Validar que vehículo existe
    UPDATE trayectos_tmp t
    LEFT JOIN vehiculos v ON v.id = CAST(t.vehiculo_id AS UNSIGNED)
    SET
        t.estado = 'ERROR',
        t.observacion = CONCAT(IFNULL(t.observacion, ''),
                              IF(t.observacion IS NULL OR t.observacion = '', '', ' | '),
                              'Vehículo no existe')
    WHERE t.id_cargue = p_id_cargue
    AND t.estado = 'CARGADO'
    AND v.id IS NULL;

    -- Validar relación conductor-vehículo
    UPDATE trayectos_tmp t
    LEFT JOIN vehiculo_conductores vc ON
        vc.conductor_id = CAST(t.conductor_id AS UNSIGNED) AND
        vc.vehiculo_id = CAST(t.vehiculo_id AS UNSIGNED) AND
        vc.estado = 'PO'
    SET
        t.estado = 'ERROR',
        t.observacion = CONCAT(IFNULL(t.observacion, ''),
                              IF(t.observacion IS NULL OR t.observacion = '', '', ' | '),
                              'Conductor no autorizado para operar el vehículo')
    WHERE t.id_cargue = p_id_cargue
    AND t.estado = 'CARGADO'
    AND vc.id IS NULL;

    -- Validar que vehículo tenga documentos habilitados
    UPDATE trayectos_tmp t
    SET
        t.estado = 'ERROR',
        t.observacion = CONCAT(IFNULL(t.observacion, ''),
                              IF(t.observacion IS NULL OR t.observacion = '', '', ' | '),
                              'Vehículo tiene documentos no habilitados')
    WHERE t.id_cargue = p_id_cargue
    AND t.estado = 'CARGADO'
    AND EXISTS (
        SELECT 1
        FROM vehiculo_documentos vd
        WHERE vd.vehiculo_id = CAST(t.vehiculo_id AS UNSIGNED)
        AND vd.estado != 'HABILITADO'
    );

    -- Marcar como VALIDADO los que no tienen errores
    UPDATE trayectos_tmp
    SET estado = 'VALIDADO'
    WHERE id_cargue = p_id_cargue
    AND estado = 'CARGADO';

    -- Contar resultados
    SELECT COUNT(*) INTO v_registros_validados
    FROM trayectos_tmp
    WHERE id_cargue = p_id_cargue
    AND estado = 'VALIDADO';

    SELECT COUNT(*) INTO v_registros_con_error
    FROM trayectos_tmp
    WHERE id_cargue = p_id_cargue
    AND estado = 'ERROR';

    -- Resultado
    SELECT
        v_registros_validados AS registros_validados,
        v_registros_con_error AS registros_con_error,
        (v_registros_validados + v_registros_con_error) AS total_procesado;

END //

DELIMITER ;

-- ============================================================================
-- PROCEDIMIENTO 3
-- Trasladar registros validados a tabla transaccional trayectos
-- ============================================================================
DELIMITER //

CREATE PROCEDURE sp_procesar_trayectos(IN p_id_cargue BIGINT)
BEGIN
    DECLARE v_registros_procesados INT DEFAULT 0;
    DECLARE v_error_count INT DEFAULT 0;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        -- Si hay error, hacer rollback
        ROLLBACK;
        SELECT 'Error al procesar trayectos' AS mensaje;
    END;

    -- Iniciar transacción
    START TRANSACTION;

    -- Log de inicio
    SELECT CONCAT('Iniciando procesamiento para cargue ID: ', p_id_cargue) AS mensaje;

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
            WHEN t.latitud IS NOT NULL AND t.latitud != ''
            THEN CAST(t.latitud AS DECIMAL(10, 8))
            ELSE NULL
        END,
        CASE
            WHEN t.longitud IS NOT NULL AND t.longitud != ''
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
        v_registros_procesados AS registros_procesados,
        'Procesamiento completado exitosamente' AS mensaje;

END //

DELIMITER ;

-- ============================================================================
-- recordar las consultas de cargue
-- ============================================================================

-- Ver resumen de un cargue específico
-- SELECT
--     id_cargue,
--     estado,
--     COUNT(*) as cantidad
-- FROM trayectos_tmp
-- WHERE id_cargue = 202410290845
-- GROUP BY id_cargue, estado;

-- Ver registros con error de un cargue
-- SELECT
--     id,
--     conductor_id,
--     vehiculo_id,
--     codigo_ruta,
--     ubicacion,
--     estado,
--     observacion
-- FROM trayectos_tmp
-- WHERE id_cargue = 202410290845
-- AND estado = 'ERROR';

-- Ver todos los cargues realizados
-- SELECT DISTINCT
--     id_cargue,
--     fecha_cargue,
--     COUNT(*) as total_registros
-- FROM trayectos_tmp
-- GROUP BY id_cargue, fecha_cargue
-- ORDER BY id_cargue DESC;