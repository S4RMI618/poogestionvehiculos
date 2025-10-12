package com.gestionvehiculos.vehiculos.dto;

import com.gestionvehiculos.vehiculos.enums.EstadoConductor;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AsociarConductorRequest {

    @NotNull(message = "El ID del vehículo es obligatorio")
    private Long vehiculoId;

    @NotNull(message = "El ID del conductor es obligatorio")
    private Long conductorId;

    private LocalDate fechaAsociacion;

    private EstadoConductor estado;

    // Constructor vacío
    public AsociarConductorRequest() {
    }

    // Constructor con parámetros
    public AsociarConductorRequest(Long vehiculoId, Long conductorId,
                                   LocalDate fechaAsociacion, EstadoConductor estado) {
        this.vehiculoId = vehiculoId;
        this.conductorId = conductorId;
        this.fechaAsociacion = fechaAsociacion;
        this.estado = estado;
    }

    // Getters y Setters
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