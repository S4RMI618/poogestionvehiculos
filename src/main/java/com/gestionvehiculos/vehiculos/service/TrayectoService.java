package com.gestionvehiculos.vehiculos.services;

import com.gestionvehiculos.vehiculos.dto.RutaDetalleDTO;
import com.gestionvehiculos.vehiculos.dto.TrayectoDTO;
import com.gestionvehiculos.vehiculos.enums.EstadoConductor;
import com.gestionvehiculos.vehiculos.enums.EstadoDocumento;
import com.gestionvehiculos.vehiculos.enums.TipoPersona;
import com.gestionvehiculos.vehiculos.model.*;
import com.gestionvehiculos.vehiculos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrayectoService {

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

    // Crear un trayecto individual
    public TrayectoDTO crearTrayecto(TrayectoDTO trayectoDTO) {
        // Validar conductor
        Persona conductor = personaRepository.findById(trayectoDTO.getConductorId())
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado con ID: " + trayectoDTO.getConductorId()));

        if (conductor.getTipoPersona() != TipoPersona.C) {
            throw new RuntimeException("La persona debe ser de tipo CONDUCTOR");
        }

        // Validar vehículo
        Vehiculo vehiculo = vehiculoRepository.findById(trayectoDTO.getVehiculoId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + trayectoDTO.getVehiculoId()));

        // Validar que el conductor pueda operar el vehículo
        VehiculoConductor vehiculoConductor = vehiculoConductorRepository
                .findByVehiculoIdAndConductorId(trayectoDTO.getVehiculoId(), trayectoDTO.getConductorId())
                .orElseThrow(() -> new RuntimeException("El conductor no está asociado a este vehículo"));

        if (vehiculoConductor.getEstado() != EstadoConductor.PO) {
            throw new RuntimeException("El conductor no puede operar este vehículo. Estado actual: " +
                    vehiculoConductor.getEstado());
        }

        // Validar que todos los documentos del vehículo estén habilitados
        List<VehiculoDocumento> documentos = vehiculoDocumentoRepository.findByVehiculoId(trayectoDTO.getVehiculoId());

        boolean tieneDocumentosNoHabilitados = documentos.stream()
                .anyMatch(doc -> doc.getEstado() != EstadoDocumento.HABILITADO);

        if (tieneDocumentosNoHabilitados) {
            throw new RuntimeException("El vehículo tiene documentos que no están habilitados");
        }

        if (documentos.isEmpty()) {
            throw new RuntimeException("El vehículo no tiene documentos asociados");
        }

        // Crear el trayecto
        Trayecto trayecto = new Trayecto();
        trayecto.setConductor(conductor);
        trayecto.setVehiculo(vehiculo);
        trayecto.setCodigoRuta(trayectoDTO.getCodigoRuta());
        trayecto.setUbicacion(trayectoDTO.getUbicacion());
        trayecto.setOrdenParada(trayectoDTO.getOrdenParada());
        trayecto.setLatitud(trayectoDTO.getLatitud());
        trayecto.setLongitud(trayectoDTO.getLongitud());
        trayecto.setLoginUsuarioRegistro(trayectoDTO.getLoginUsuarioRegistro());

        Trayecto trayectoGuardado = trayectoRepository.save(trayecto);
        return convertirEntidadaDTO(trayectoGuardado);
    }

    // Crear una ruta completa (múltiples trayectos)
    public List<TrayectoDTO> crearRuta(List<TrayectoDTO> trayectosDTO) {
        // Validar que tenga mínimo 2 trayectos (inicial y final)
        if (trayectosDTO.size() < 2) {
            throw new RuntimeException("Una ruta debe tener al menos 2 trayectos (inicial y final)");
        }
        // Validar que tenga máximo 7 trayectos (inicial + 5 intermedios + final)
        if (trayectosDTO.size() > 7) {
            throw new RuntimeException("Una ruta puede tener máximo 7 trayectos");
        }
        // Validar que todos tengan el mismo código de ruta
        String codigoRuta = trayectosDTO.get(0).getCodigoRuta();
        boolean todosMismoCodigoRuta = trayectosDTO.stream()
                .allMatch(t -> t.getCodigoRuta().equals(codigoRuta));

        if (!todosMismoCodigoRuta) {
            throw new RuntimeException("Todos los trayectos deben tener el mismo código de ruta");
        }
        // Validar que el orden de paradas sea correcto (0, 1, 2, ..., n)
        List<Integer> ordenes = trayectosDTO.stream()
                .map(TrayectoDTO::getOrdenParada)
                .sorted()
                .toList();
        for (int i = 0; i < ordenes.size(); i++) {
            if (ordenes.get(i) != i) {
                throw new RuntimeException("El orden de paradas debe ser secuencial comenzando desde 0");
            }
        }

        // Crear todos los trayectos
        List<TrayectoDTO> trayectosCreados = new ArrayList<>();
        for (TrayectoDTO trayectoDTO : trayectosDTO) {
            TrayectoDTO trayectoCreado = crearTrayecto(trayectoDTO);
            trayectosCreados.add(trayectoCreado);
        }

        return trayectosCreados;
    }

    // Consultar ruta por código
    public RutaDetalleDTO consultarRutaPorCodigo(String codigoRuta) {
        List<Trayecto> trayectos = trayectoRepository.findByCodigoRutaOrderByOrdenParadaAsc(codigoRuta);

        if (trayectos.isEmpty()) {
            throw new RuntimeException("No se encontró ninguna ruta con el código: " + codigoRuta);
        }

        Trayecto primerTrayecto = trayectos.get(0);
        String conductorNombre = primerTrayecto.getConductor().getNombres() + " " +
                primerTrayecto.getConductor().getApellidos();
        String vehiculoPlaca = primerTrayecto.getVehiculo().getPlaca();

        List<TrayectoDTO> trayectosDTO = trayectos.stream()
                .map(this::convertirEntidadaDTO)
                .collect(Collectors.toList());

        return new RutaDetalleDTO(codigoRuta, conductorNombre, vehiculoPlaca, trayectosDTO);
    }

    // Consultar códigos de ruta por identificación de conductor
    public List<String> consultarRutasPorConductor(String identificacion) {
        Persona conductor = personaRepository.findByIdentificacion(identificacion)
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado con identificación: " + identificacion));

        if (conductor.getTipoPersona() != TipoPersona.C) {
            throw new RuntimeException("La persona debe ser de tipo CONDUCTOR");
        }

        List<String> codigosRuta = trayectoRepository.findCodigosRutaByIdentificacionConductor(identificacion);

        if (codigosRuta.isEmpty()) {
            throw new RuntimeException("El conductor no tiene rutas registradas");
        }

        return codigosRuta;
    }

    // Consultar códigos de ruta y conductor por placa de vehículo
    public Map<String, List<String>> consultarRutasPorPlacaVehiculo(String placa) {
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con placa: " + placa));

        List<Object[]> resultados = trayectoRepository.findCodigosRutaYConductorByPlacaVehiculo(placa.toUpperCase());

        if (resultados.isEmpty()) {
            throw new RuntimeException("El vehículo no tiene rutas registradas");
        }

        Map<String, List<String>> rutasPorConductor = new LinkedHashMap<>();

        for (Object[] resultado : resultados) {
            String codigoRuta = (String) resultado[0];
            String conductorNombre = (String) resultado[1];

            rutasPorConductor.computeIfAbsent(conductorNombre, k -> new ArrayList<>()).add(codigoRuta);
        }

        return rutasPorConductor;
    }

    // Consultar rutas con problemas (vehículos no habilitados o conductores restringidos)
    public List<RutaDetalleDTO> consultarRutasConProblemas() {
        List<Trayecto> trayectosConProblemas = trayectoRepository.findTrayectosConProblemas();

        // Agrupar por código de ruta
        Map<String, List<Trayecto>> trayectosPorRuta = trayectosConProblemas.stream()
                .collect(Collectors.groupingBy(Trayecto::getCodigoRuta));

        List<RutaDetalleDTO> rutasConProblemas = new ArrayList<>();

        for (Map.Entry<String, List<Trayecto>> entry : trayectosPorRuta.entrySet()) {
            String codigoRuta = entry.getKey();
            List<Trayecto> trayectos = entry.getValue();

            // Ordenar por orden de parada
            trayectos.sort(Comparator.comparing(Trayecto::getOrdenParada));

            Trayecto primerTrayecto = trayectos.get(0);
            String conductorNombre = primerTrayecto.getConductor().getNombres() + " " +
                    primerTrayecto.getConductor().getApellidos();
            String vehiculoPlaca = primerTrayecto.getVehiculo().getPlaca();

            List<TrayectoDTO> trayectosDTO = trayectos.stream()
                    .map(this::convertirEntidadaDTO)
                    .collect(Collectors.toList());

            rutasConProblemas.add(new RutaDetalleDTO(codigoRuta, conductorNombre, vehiculoPlaca, trayectosDTO));
        }

        return rutasConProblemas;
    }

    // Obtener trayectos sin coordenadas (para la tarea programada)
    public List<Trayecto> obtenerTrayectosSinCoordenadas() {
        return trayectoRepository.findTrayectosSinCoordenadas();
    }

    // Actualizar coordenadas de un trayecto
    public void actualizarCoordenadas(Long trayectoId, Double latitud, Double longitud) {
        Trayecto trayecto = trayectoRepository.findById(trayectoId)
                .orElseThrow(() -> new RuntimeException("Trayecto no encontrado con ID: " + trayectoId));

        trayecto.setLatitud(latitud);
        trayecto.setLongitud(longitud);
        trayectoRepository.save(trayecto);
    }

    // Método de conversión
    private TrayectoDTO convertirEntidadaDTO(Trayecto trayecto) {
        String conductorNombre = trayecto.getConductor().getNombres() + " " +
                trayecto.getConductor().getApellidos();
        String vehiculoPlaca = trayecto.getVehiculo().getPlaca();

        return new TrayectoDTO(
                trayecto.getId(),
                trayecto.getConductor().getId(),
                conductorNombre,
                trayecto.getVehiculo().getId(),
                vehiculoPlaca,
                trayecto.getCodigoRuta(),
                trayecto.getUbicacion(),
                trayecto.getOrdenParada(),
                trayecto.getLatitud(),
                trayecto.getLongitud(),
                trayecto.getLoginUsuarioRegistro(),
                trayecto.getFechaRegistro()
        );
    }
}