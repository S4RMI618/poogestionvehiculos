package com.gestionvehiculos.vehiculos.service;

import com.gestionvehiculos.vehiculos.model.*;
import com.gestionvehiculos.vehiculos.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// IMPORTACIONES PARA LOS STORED PROCEDURES
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

@Service
@Transactional
public class ValidacionCargueService {

    private static final Logger logger = LoggerFactory.getLogger(ValidacionCargueService.class);

    @Autowired
    private TrayectoTmpRepository trayectoTmpRepository;

    @Autowired
    private EntityManager entityManager;

    /**
     * PASO 2: Validar registros cargados usando SP
     */
    public void validarCargue(Long idCargue) {
        logger.info("Ejecutando procedimiento de validación para cargue: {}", idCargue);

        try {
            // Ejecutar procedimiento almacenado de validación
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("sp_validar_trayectos")
                    .registerStoredProcedureParameter("p_id_cargue", Long.class, ParameterMode.IN)
                    .setParameter("p_id_cargue", idCargue);

            query.execute();

            logger.info("Validación completada exitosamente");

        } catch (Exception e) {
            logger.error("Error al ejecutar validación: {}", e.getMessage());
            throw new RuntimeException("Error en validación: " + e.getMessage());
        }
    }

    /**
     * PASO 3: Procesar registros validados usando SP
     */
    public void procesarCargue(Long idCargue) {
        logger.info("Ejecutando procedimiento de procesamiento para cargue: {}", idCargue);

        try {
            // Ejecutar procedimiento almacenado de procesamiento
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("sp_procesar_trayectos")
                    .registerStoredProcedureParameter("p_id_cargue", Long.class, ParameterMode.IN)
                    .setParameter("p_id_cargue", idCargue);

            query.execute();

            logger.info("Procesamiento completado exitosamente");

        } catch (Exception e) {
            logger.error("Error al ejecutar procesamiento: {}", e.getMessage());
            throw new RuntimeException("Error en procesamiento: " + e.getMessage());
        }
    }
}