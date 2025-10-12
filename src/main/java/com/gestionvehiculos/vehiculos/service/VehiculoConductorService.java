package com.gestionvehiculos.vehiculos.service;

import com.gestionvehiculos.vehiculos.dto.AsociarConductorRequest;
import com.gestionvehiculos.vehiculos.dto.VehiculoConductorDTO;
import com.gestionvehiculos.vehiculos.enums.EstadoConductor;
import com.gestionvehiculos.vehiculos.enums.TipoPersona;
import com.gestionvehiculos.vehiculos.model.Persona;
import com.gestionvehiculos.vehiculos.model.Vehiculo;
import com.gestionvehiculos.vehiculos.model.VehiculoConductor;
import com.gestionvehiculos.vehiculos.repository.PersonaRepository;
import com.gestionvehiculos.vehiculos.repository.VehiculoConductorRepository;
import com.gestionvehiculos.vehiculos.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehiculoConductorService {

    @Autowired
    private VehiculoConductorRepository vehiculoConductorRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    // Asociar conductor a vehículo
    public VehiculoConductorDTO asociarConductor(AsociarConductorRequest request) {
        Vehiculo vehiculo = vehiculoRepository.findById(request.getVehiculoId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + request.getVehiculoId()));

        Persona conductor = personaRepository.findById(request.getConductorId())
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado con ID: " + request.getConductorId()));

        // Validar que la persona sea de tipo CONDUCTOR
        if (conductor.getTipoPersona() != TipoPersona.C) {
            throw new RuntimeException("La persona debe ser de tipo CONDUCTOR para ser asociada a un vehículo");
        }

        // Verificar si ya existe la asociación
        if (vehiculoConductorRepository.findByVehiculoIdAndConductorId(
                request.getVehiculoId(), request.getConductorId()).isPresent()) {
            throw new RuntimeException("El conductor ya está asociado a este vehículo");
        }

        VehiculoConductor vehiculoConductor = new VehiculoConductor();
        vehiculoConductor.setVehiculo(vehiculo);
        vehiculoConductor.setConductor(conductor);
        vehiculoConductor.setFechaAsociacion(request.getFechaAsociacion() != null ?
                request.getFechaAsociacion() : LocalDate.now());
        vehiculoConductor.setEstado(request.getEstado() != null ?
                request.getEstado() : EstadoConductor.EA);

        VehiculoConductor guardado = vehiculoConductorRepository.save(vehiculoConductor);
        return convertirEntidadaDTO(guardado);
    }

    // Obtener conductores de un vehículo
    public List<VehiculoConductorDTO> obtenerConductoresDeVehiculo(Long vehiculoId) {
        return vehiculoConductorRepository.findByVehiculoId(vehiculoId).stream()
                .map(this::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }

    // Obtener vehículos de un conductor
    public List<VehiculoConductorDTO> obtenerVehiculosDeConductor(Long conductorId) {
        return vehiculoConductorRepository.findByConductorId(conductorId).stream()
                .map(this::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }

    // Cambiar estado de conductor en vehículo
    public VehiculoConductorDTO cambiarEstadoConductor(Long id, EstadoConductor nuevoEstado) {
        VehiculoConductor vehiculoConductor = vehiculoConductorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asociación vehículo-conductor no encontrada con ID: " + id));

        vehiculoConductor.setEstado(nuevoEstado);
        VehiculoConductor actualizado = vehiculoConductorRepository.save(vehiculoConductor);
        return convertirEntidadaDTO(actualizado);
    }

    // Obtener todos los conductores que pueden operar
    public List<Persona> obtenerConductoresQuePuedenOperar() {
        return vehiculoConductorRepository.findConductoresByEstado(EstadoConductor.PO);
    }

    // Método de conversión
    private VehiculoConductorDTO convertirEntidadaDTO(VehiculoConductor vc) {
        String conductorNombre = vc.getConductor().getNombres() + " " + vc.getConductor().getApellidos();
        String vehiculoPlaca = vc.getVehiculo().getPlaca();

        return new VehiculoConductorDTO(
                vc.getId(),
                vc.getVehiculo().getId(),
                vc.getConductor().getId(),
                conductorNombre,
                vehiculoPlaca,
                vc.getFechaAsociacion(),
                vc.getEstado()
        );
    }
}