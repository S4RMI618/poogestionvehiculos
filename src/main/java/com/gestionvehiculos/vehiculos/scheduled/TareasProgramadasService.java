package com.gestionvehiculos.vehiculos.scheduled;

import com.gestionvehiculos.vehiculos.enums.EstadoConductor;
import com.gestionvehiculos.vehiculos.enums.EstadoDocumento;
import com.gestionvehiculos.vehiculos.model.Persona;
import com.gestionvehiculos.vehiculos.model.Trayecto;
import com.gestionvehiculos.vehiculos.model.VehiculoConductor;
import com.gestionvehiculos.vehiculos.model.VehiculoDocumento;
import com.gestionvehiculos.vehiculos.repository.VehiculoConductorRepository;
import com.gestionvehiculos.vehiculos.repository.VehiculoDocumentoRepository;
import com.gestionvehiculos.vehiculos.service.PersonaService;
import com.gestionvehiculos.vehiculos.services.TrayectoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class TareasProgramadasService {

    private static final Logger logger = LoggerFactory.getLogger(TareasProgramadasService.class);

    @Autowired
    private PersonaService personaService;

    @Autowired
    private VehiculoConductorRepository vehiculoConductorRepository;

    @Autowired
    private VehiculoDocumentoRepository vehiculoDocumentoRepository;

    @Autowired
    private TrayectoService trayectoService;

    /**
     * Tarea programada que verifica cada 2 minutos las licencias vencidas
     * y cambia el estado del conductor a RO (Restringido para Operar)
     */
    @Scheduled(fixedRate = 120000) // 2 minutos = 120000 ms
    @Transactional
    public void verificarLicenciasVencidas() {
        logger.info("Iniciando verificación de licencias vencidas...");

        try {
            List<Persona> conductoresConLicenciaVencida = personaService.obtenerConductoresConLicenciaVencida();

            if (conductoresConLicenciaVencida.isEmpty()) {
                logger.info("No se encontraron conductores con licencia vencida");
                return;
            }

            int conductoresActualizados = 0;

            for (Persona conductor : conductoresConLicenciaVencida) {
                // Obtener todas las relaciones del conductor con vehículos
                List<VehiculoConductor> relaciones = vehiculoConductorRepository
                        .findByConductorId(conductor.getId());

                for (VehiculoConductor relacion : relaciones) {
                    if (relacion.getEstado() != EstadoConductor.RO) {
                        relacion.setEstado(EstadoConductor.RO);
                        vehiculoConductorRepository.save(relacion);
                        conductoresActualizados++;

                        logger.info("Conductor {} {} restringido para operar vehículo {}",
                                conductor.getNombres(),
                                conductor.getApellidos(),
                                relacion.getVehiculo().getPlaca());

                        // TODO: Enviar correo electrónico al conductor
                    }
                }
            }

            logger.info("Verificación completada. {} conductores restringidos", conductoresActualizados);

        } catch (Exception e) {
            logger.error("Error al verificar licencias vencidas: {}", e.getMessage());
        }
    }

    /**
     * Tarea programada que verifica cada 2 minutos los documentos vencidos
     * y cambia su estado a VENCIDO
     */
    @Scheduled(fixedRate = 120000) // 2 minutos = 120000 ms
    @Transactional
    public void verificarDocumentosVencidos() {
        logger.info("Iniciando verificación de documentos vencidos...");

        try {
            List<VehiculoDocumento> todosLosDocumentos = vehiculoDocumentoRepository.findAll();
            LocalDate hoy = LocalDate.now();
            int documentosActualizados = 0;

            for (VehiculoDocumento documento : todosLosDocumentos) {
                if (documento.getFechaVencimiento().isBefore(hoy) &&
                        documento.getEstado() != EstadoDocumento.VENCIDO) {

                    documento.setEstado(EstadoDocumento.VENCIDO);
                    vehiculoDocumentoRepository.save(documento);
                    documentosActualizados++;

                    logger.info("Documento {} del vehículo {} marcado como VENCIDO",
                            documento.getDocumento().getNombre(),
                            documento.getVehiculo().getPlaca());
                }
            }

            logger.info("Verificación completada. {} documentos marcados como vencidos",
                    documentosActualizados);

        } catch (Exception e) {
            logger.error("Error al verificar documentos vencidos: {}", e.getMessage());
        }
    }

    /**
     * Tarea programada que verifica cada 90 segundos los trayectos sin coordenadas
     * y las obtiene usando Google Maps API que no pudimos accedder

    @Scheduled(fixedRate = 90000) // 90 segundos = 90000 ms
    @Transactional
    public void verificarTrayectosSinCoordenadas() {
        logger.info("Iniciando verificación de trayectos sin coordenadas...");

        try {
            List<Trayecto> trayectosSinCoordenadas = trayectoService.obtenerTrayectosSinCoordenadas();

            if (trayectosSinCoordenadas.isEmpty()) {
                logger.info("No se encontraron trayectos sin coordenadas");
                return;
            }

            int trayectosActualizados = 0;

            for (Trayecto trayecto : trayectosSinCoordenadas) {
                try {
                    // TODO: Implementar integración con Google Maps API
                    // Por ahora, asignar coordenadas de ejemplo (Ibagué, Tolima)
                    Double latitud = 4.4389 + (Math.random() * 0.1); // Ejemplo
                    Double longitud = -75.2322 + (Math.random() * 0.1); // Ejemplo

                    trayectoService.actualizarCoordenadas(trayecto.getId(), latitud, longitud);
                    trayectosActualizados++;

                    logger.info("Coordenadas actualizadas para trayecto {} en ubicación: {}",
                            trayecto.getId(),
                            trayecto.getUbicacion());

                } catch (Exception e) {
                    logger.error("Error al actualizar coordenadas del trayecto {}: {}",
                            trayecto.getId(),
                            e.getMessage());
                }
            }

            logger.info("Verificación completada. {} trayectos actualizados con coordenadas",
                    trayectosActualizados);

        } catch (Exception e) {
            logger.error("Error al verificar trayectos sin coordenadas: {}", e.getMessage());
        }
    }
     */
}