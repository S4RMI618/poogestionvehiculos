package com.gestionvehiculos.vehiculos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CrearVehiculoRequest {

    @NotNull(message = "Los datos del vehículo son obligatorios")
    @Valid
    private VehiculoDTO vehiculo;

    @NotEmpty(message = "Debe incluir al menos un documento")
    @Valid
    private List<VehiculoDocumentoDTO> documentos;

    // Constructor vacío
    public CrearVehiculoRequest() {
    }

    // Constructor con parámetros
    public CrearVehiculoRequest(VehiculoDTO vehiculo, List<VehiculoDocumentoDTO> documentos) {
        this.vehiculo = vehiculo;
        this.documentos = documentos;
    }

    // Getters y Setters
    public VehiculoDTO getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(VehiculoDTO vehiculo) {
        this.vehiculo = vehiculo;
    }

    public List<VehiculoDocumentoDTO> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<VehiculoDocumentoDTO> documentos) {
        this.documentos = documentos;
    }
}