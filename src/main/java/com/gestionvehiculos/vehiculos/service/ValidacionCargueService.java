package com.gestionvehiculos.vehiculos.service;

import com.gestionvehiculos.vehiculos.enums.EstadoCargue;
import com.gestionvehiculos.vehiculos.enums.EstadoConductor;
import com.gestionvehiculos.vehiculos.enums.EstadoDocumento;
import com.gestionvehiculos.vehiculos.enums.TipoPersona;
import com.gestionvehiculos.vehiculos.model.*;
import com.gestionvehiculos.vehiculos.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ValidacionCargueService {

    private static final Logger logger = LoggerFactory.getLogger(ValidacionCargueService.class);

    @Autowired
    private TrayectoTmpRepository trayectoTmpRepository;

    @Autowired
    private TrayectoRepository trayectoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private VehiculoConductorRepository vehiculoConductorRepository;

    @Autowired
    private VehiculoDocumentoRepository vehiculoDocumentoRepository;

    /**
     * PASO 2: Validar registros cargados
     */
    public void validarCargue(Long idCargue) {
        logger.info("Iniciando validación de cargue: {}", idCargue);

        List<TrayectoTmp> registros = trayectoTmpRepository.findByIdCargueAndEstado(
                idCargue, EstadoCargue.CARGADO
        );

        int registrosValidados = 0;
        int registrosConError = 0;

        for (TrayectoTmp registro : registros) {
            try {
                validarRegistro(registro);

                if (registro.getObservacion().isEmpty()) {
                    registro.setEstado(EstadoCargue.VALIDADO);
                    registrosValidados++;
                } else {
                    registro.setEstado(EstadoCargue.ERROR);
                    registrosConError++;
                }

                trayectoTmpRepository.save(registro);

            } catch (Exception e) {
                registro.setEstado(EstadoCargue.ERROR);
                registro.agregarObservacion("Error inesperado: " + e.getMessage());
                trayectoTmpRepository.save(registro);
                registrosConError++;
            }
        }

        logger.info("Validación completada. Validados: {}, Errores: {}", registrosValidados, registrosConError);
    }

    /**
     * Validar un registro individual
     */
    private void validarRegistro(TrayectoTmp registro) {
        // 1. Validar campos obligatorios
        validarCamposObligatorios(registro);

        // 2. Validar formato de datos
        validarFormatoDatos(registro);

        // 3. Validar existencia de conductor
        if (!registro.getObservacion().contains("conductorId")) {
            validarConductor(registro);
        }

        // 4. Validar existencia de vehículo
        if (!registro.getObservacion().contains("vehiculoId")) {
            validarVehiculo(registro);
        }

        // 5. Validar relación conductor-vehículo
        if (!registro.getObservacion().contains("conductor") && !registro.getObservacion().contains("vehiculo")) {
            validarRelacionConductorVehiculo(registro);
        }
    }

    private void validarCamposObligatorios(TrayectoTmp registro) {
        if (registro.getConductorId() == null || registro.getConductorId().isEmpty()) {
            registro.agregarObservacion("El campo conductorId es obligatorio");
        }
        if (registro.getVehiculoId() == null || registro.getVehiculoId().isEmpty()) {
            registro.agregarObservacion("El campo vehiculoId es obligatorio");
        }
        if (registro.getCodigoRuta() == null || registro.getCodigoRuta().isEmpty()) {
            registro.agregarObservacion("El campo codigoRuta es obligatorio");
        }
        if (registro.getUbicacion() == null || registro.getUbicacion().isEmpty()) {
            registro.agregarObservacion("El campo ubicacion es obligatorio");
        }
        if (registro.getOrdenParada() == null || registro.getOrdenParada().isEmpty()) {
            registro.agregarObservacion("El campo ordenParada es obligatorio");
        }
    }

    private void validarFormatoDatos(TrayectoTmp registro) {
        // Validar que conductorId sea numérico
        try {
            Long.parseLong(registro.getConductorId());
        } catch (NumberFormatException e) {
            registro.agregarObservacion("conductorId debe ser numérico");
        }

        // Validar que vehiculoId sea numérico
        try {
            Long.parseLong(registro.getVehiculoId());
        } catch (NumberFormatException e) {
            registro.agregarObservacion("vehiculoId debe ser numérico");
        }

        // Validar que ordenParada sea numérico
        try {
            Integer orden = Integer.parseInt(registro.getOrdenParada());
            if (orden < 0 || orden > 6) {
                registro.agregarObservacion("ordenParada debe estar entre 0 y 6");
            }
        } catch (NumberFormatException e) {
            registro.agregarObservacion("ordenParada debe ser numérico");
        }

        // Validar latitud si existe
        if (registro.getLatitud() != null && !registro.getLatitud().isEmpty()) {
            try {
                Double lat = Double.parseDouble(registro.getLatitud());
                if (lat < -90 || lat > 90) {
                    registro.agregarObservacion("latitud debe estar entre -90 y 90");
                }
            } catch (NumberFormatException e) {
                registro.agregarObservacion("latitud debe ser numérica");
            }
        }

        // Validar longitud si existe
        if (registro.getLongitud() != null && !registro.getLongitud().isEmpty()) {
            try {
                Double lng = Double.parseDouble(registro.getLongitud());
                if (lng < -180 || lng > 180) {
                    registro.agregarObservacion("longitud debe estar entre -180 y 180");
                }
            } catch (NumberFormatException e) {
                registro.agregarObservacion("longitud debe ser numérica");
            }
        }
    }

    private void validarConductor(TrayectoTmp registro) {
        try {
            Long conductorId = Long.parseLong(registro.getConductorId());
            Persona conductor = personaRepository.findById(conductorId).orElse(null);

            if (conductor == null) {
                registro.agregarObservacion("Conductor no existe con ID: " + conductorId);
            } else if (conductor.getTipoPersona() != TipoPersona.C) {
                registro.agregarObservacion("La persona no es de tipo CONDUCTOR");
            }
        } catch (Exception e) {
            registro.agregarObservacion("Error al validar conductor: " + e.getMessage());
        }
    }

    private void validarVehiculo(TrayectoTmp registro) {
        try {
            Long vehiculoId = Long.parseLong(registro.getVehiculoId());
            Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId).orElse(null);

            if (vehiculo == null) {
                registro.agregarObservacion("Vehículo no existe con ID: " + vehiculoId);
            } else {
                // Validar documentos del vehículo
                List<VehiculoDocumento> documentos = vehiculoDocumentoRepository.findByVehiculoId(vehiculoId);

                if (documentos.isEmpty()) {
                    registro.agregarObservacion("El vehículo no tiene documentos asociados");
                } else {
                    boolean tieneDocumentosNoHabilitados = documentos.stream()
                            .anyMatch(doc -> doc.getEstado() != EstadoDocumento.HABILITADO);

                    if (tieneDocumentosNoHabilitados) {
                        registro.agregarObservacion("El vehículo tiene documentos no habilitados");
                    }
                }
            }
        } catch (Exception e) {
            registro.agregarObservacion("Error al validar vehículo: " + e.getMessage());
        }
    }

    private void validarRelacionConductorVehiculo(TrayectoTmp registro) {
        try {
            Long conductorId = Long.parseLong(registro.getConductorId());
            Long vehiculoId = Long.parseLong(registro.getVehiculoId());

            VehiculoConductor relacion = vehiculoConductorRepository
                    .findByVehiculoIdAndConductorId(vehiculoId, conductorId)
                    .orElse(null);

            if (relacion == null) {
                registro.agregarObservacion("El conductor no está asociado al vehículo");
            } else if (relacion.getEstado() != EstadoConductor.PO) {
                registro.agregarObservacion("El conductor no puede operar el vehículo. Estado: " + relacion.getEstado());
            }
        } catch (Exception e) {
            registro.agregarObservacion("Error al validar relación conductor-vehículo: " + e.getMessage());
        }
    }

    /**
     * PASO 3: Procesar registros validados (trasladar a tabla transaccional)
     */
    public void procesarCargue(Long idCargue) {
        logger.info("Iniciando procesamiento de cargue: {}", idCargue);

        List<TrayectoTmp> registrosValidados = trayectoTmpRepository.findByIdCargueAndEstado(
                idCargue, EstadoCargue.VALIDADO
        );

        int registrosProcesados = 0;

        for (TrayectoTmp registroTmp : registrosValidados) {
            try {
                // Crear trayecto en tabla transaccional
                Trayecto trayecto = new Trayecto();

                Persona conductor = personaRepository.findById(Long.parseLong(registroTmp.getConductorId())).get();
                Vehiculo vehiculo = vehiculoRepository.findById(Long.parseLong(registroTmp.getVehiculoId())).get();

                trayecto.setConductor(conductor);
                trayecto.setVehiculo(vehiculo);
                trayecto.setCodigoRuta(registroTmp.getCodigoRuta());
                trayecto.setUbicacion(registroTmp.getUbicacion());
                trayecto.setOrdenParada(Integer.parseInt(registroTmp.getOrdenParada()));

                // Latitud y longitud (pueden ser null)
                if (registroTmp.getLatitud() != null && !registroTmp.getLatitud().isEmpty()) {
                    trayecto.setLatitud(Double.parseDouble(registroTmp.getLatitud()));
                }
                if (registroTmp.getLongitud() != null && !registroTmp.getLongitud().isEmpty()) {
                    trayecto.setLongitud(Double.parseDouble(registroTmp.getLongitud()));
                }

                trayecto.setLoginUsuarioRegistro(registroTmp.getLoginUsuarioRegistro());
                trayecto.setFechaRegistro(LocalDateTime.now());

                trayectoRepository.save(trayecto);

                // Marcar como procesado
                registroTmp.setEstado(EstadoCargue.PROCESADO);
                trayectoTmpRepository.save(registroTmp);

                registrosProcesados++;

            } catch (Exception e) {
                logger.error("Error al procesar registro {}: {}", registroTmp.getId(), e.getMessage());
                registroTmp.setEstado(EstadoCargue.ERROR);
                registroTmp.agregarObservacion("Error al procesar: " + e.getMessage());
                trayectoTmpRepository.save(registroTmp);
            }
        }

        logger.info("Procesamiento completado. Registros procesados: {}", registrosProcesados);
    }
}