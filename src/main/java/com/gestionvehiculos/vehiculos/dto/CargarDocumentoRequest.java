package com.gestionvehiculos.vehiculos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CargarDocumentoRequest {

    @NotNull(message = "El ID del vehículo-documento es obligatorio")
    private Long vehiculoDocumentoId;

    @NotBlank(message = "El documento PDF en Base64 es obligatorio")
    private String documentoPdfBase64;

    // Constructor vacío
    public CargarDocumentoRequest() {
    }

    // Constructor con parámetros
    public CargarDocumentoRequest(Long vehiculoDocumentoId, String documentoPdfBase64) {
        this.vehiculoDocumentoId = vehiculoDocumentoId;
        this.documentoPdfBase64 = documentoPdfBase64;
    }

    // Getters y Setters
    public Long getVehiculoDocumentoId() {
        return vehiculoDocumentoId;
    }

    public void setVehiculoDocumentoId(Long vehiculoDocumentoId) {
        this.vehiculoDocumentoId = vehiculoDocumentoId;
    }

    public String getDocumentoPdfBase64() {
        return documentoPdfBase64;
    }

    public void setDocumentoPdfBase64(String documentoPdfBase64) {
        this.documentoPdfBase64 = documentoPdfBase64;
    }
}