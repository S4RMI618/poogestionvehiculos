package com.gestionvehiculos.vehiculos.scheduled;

import com.gestionvehiculos.vehiculos.enums.EstadoConductor;
import com.gestionvehiculos.vehiculos.enums.EstadoDocumento;
import com.gestionvehiculos.vehiculos.model.Persona;
import com.gestionvehiculos.vehiculos.model.Trayecto;
import com.gestionvehiculos.vehiculos.model.VehiculoConductor;
import com.gestionvehiculos.vehiculos.model.VehiculoDocumento;
import com.gestionvehiculos.vehiculos.repository.VehiculoConductorRepository;
import com.gestionvehiculos.vehiculos.repository.VehiculoDocumentoRepository;
import com.gestionvehiculos.vehiculos.service.GoogleMapsService;
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
import java.util.Map;

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

    @Autowired
    private GoogleMapsService googleMapsService;

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

                        // TODO: Enviar correo electrónico al conductor (CEREZA opcional)
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
     * y las obtiene usando Google Maps API
     */
    @Scheduled(fixedRate = 90000) // 90 segundos = 90000 ms
    @Transactional
    public void verificarTrayectosSinCoordenadas() {
        logger.info("Iniciando verificación de trayectos sin coordenadas...");

        try {
            // Verificar configuración de Google Maps
            if (!googleMapsService.verificarConfiguracion()) {
                logger.warn("Google Maps API no está configurada. Se omite la tarea.");
                return;
            }

            List<Trayecto> trayectosSinCoordenadas = trayectoService.obtenerTrayectosSinCoordenadas();

            if (trayectosSinCoordenadas.isEmpty()) {
                logger.info("No se encontraron trayectos sin coordenadas");
                return;
            }

            logger.info("Se encontraron {} trayectos sin coordenadas", trayectosSinCoordenadas.size());
            int trayectosActualizados = 0;
            int trayectosConError = 0;

            for (Trayecto trayecto : trayectosSinCoordenadas) {
                try {
                    // Obtener coordenadas desde Google Maps
                    Map<String, Double> coordenadas = googleMapsService.obtenerCoordenadas(
                            trayecto.getUbicacion()
                    );

                    if (coordenadas != null) {
                        Double latitud = coordenadas.get("latitud");
                        Double longitud = coordenadas.get("longitud");

                        trayectoService.actualizarCoordenadas(trayecto.getId(), latitud, longitud);
                        trayectosActualizados++;

                        logger.info("✅ Trayecto {} actualizado - Ubicación: {} | Coordenadas: ({}, {})",
                                trayecto.getId(),
                                trayecto.getUbicacion(),
                                latitud,
                                longitud);
                    } else {
                        trayectosConError++;
                        logger.warn("❌ No se pudieron obtener coordenadas para: {}",
                                trayecto.getUbicacion());
                    }

                    // Pequeña pausa para no exceder límites de la API
                    Thread.sleep(200); // 200ms entre peticiones

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Tarea interrumpida");
                    break;
                } catch (Exception e) {
                    trayectosConError++;
                    logger.error("Error al actualizar coordenadas del trayecto {}: {}",
                            trayecto.getId(),
                            e.getMessage());
                }
            }

            logger.info("Verificación completada. ✅ {} actualizados | ❌ {} con errores",
                    trayectosActualizados,
                    trayectosConError);

        } catch (Exception e) {
            logger.error("Error general al verificar trayectos sin coordenadas: {}", e.getMessage());
        }
    }
}