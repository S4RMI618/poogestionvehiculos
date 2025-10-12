package com.gestionvehiculos.vehiculos.dto;

import com.gestionvehiculos.vehiculos.enums.EstadoConductor;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class VehiculoConductorDTO {

    private Long id;

    @NotNull(message = "El ID del vehículo es obligatorio")
    private Long vehiculoId;

    @NotNull(message = "El ID del conductor es obligatorio")
    private Long conductorId;

    private String conductorNombre;

    private String vehiculoPlaca;

    @NotNull(message = "La fecha de asociación es obligatoria")
    private LocalDate fechaAsociacion;

    @NotNull(message = "El estado del conductor es obligatorio")
    private EstadoConductor estado;

    // Constructor vacío
    public VehiculoConductorDTO() {
    }

    // Constructor con parámetros
    public VehiculoConductorDTO(Long id, Long vehiculoId, Long conductorId,
                                String conductorNombre, String vehiculoPlaca,
                                LocalDate fechaAsociacion, EstadoConductor estado) {
        this.id = id;
        this.vehiculoId = vehiculoId;
        this.conductorId = conductorId;
        this.conductorNombre = conductorNombre;
        this.vehiculoPlaca = vehiculoPlaca;
        this.fechaAsociacion = fechaAsociacion;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public Long getConductorId() {
        return conductorId;
    }

    public void setConductorId(Long conductorId) {
        this.conductorId = conductorId;
    }

    public String getConductorNombre() {
        return conductorNombre;
    }

    public void setConductorNombre(String conductorNombre) {
        this.conductorNombre = conductorNombre;
    }

    public String getVehiculoPlaca() {
        return vehiculoPlaca;
    }

    public void setVehiculoPlaca(String vehiculoPlaca) {
        this.vehiculoPlaca = vehiculoPlaca;
    }

    public LocalDate getFechaAsociacion() {
        return fechaAsociacion;
    }

    public void setFechaAsociacion(LocalDate fechaAsociacion) {
        this.fechaAsociacion = fechaAsociacion;
    }

    public EstadoConductor getEstado() {
        return estado;
    }

    public void setEstado(EstadoConductor estado) {
        this.estado = estado;
    }
}