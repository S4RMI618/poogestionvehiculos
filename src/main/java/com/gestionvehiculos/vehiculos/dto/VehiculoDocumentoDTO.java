package com.gestionvehiculos.vehiculos.dto;

import com.gestionvehiculos.vehiculos.enums.EstadoDocumento;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class VehiculoDocumentoDTO {

    private Long id;

    @NotNull(message = "El ID del documento es obligatorio")
    private Long documentoId;

    private String documentoNombre;

    @NotNull(message = "La fecha de expedición es obligatoria")
    private LocalDate fechaExpedicion;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;

    // QUITAR @NotNull de aquí - El estado no es obligatorio al crear
    private EstadoDocumento estado;

    // Constructor vacío
    public VehiculoDocumentoDTO() {
    }

    // Constructor con parámetros
    public VehiculoDocumentoDTO(Long id, Long documentoId, String documentoNombre,
                                LocalDate fechaExpedicion, LocalDate fechaVencimiento,
                                EstadoDocumento estado) {
        this.id = id;
        this.documentoId = documentoId;
        this.documentoNombre = documentoNombre;
        this.fechaExpedicion = fechaExpedicion;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(Long documentoId) {
        this.documentoId = documentoId;
    }

    public String getDocumentoNombre() {
        return documentoNombre;
    }

    public void setDocumentoNombre(String documentoNombre) {
        this.documentoNombre = documentoNombre;
    }

    public LocalDate getFechaExpedicion() {
        return fechaExpedicion;
    }

    public void setFechaExpedicion(LocalDate fechaExpedicion) {
        this.fechaExpedicion = fechaExpedicion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public EstadoDocumento getEstado() {
        return estado;
    }

    public void setEstado(EstadoDocumento estado) {
        this.estado = estado;
    }
}