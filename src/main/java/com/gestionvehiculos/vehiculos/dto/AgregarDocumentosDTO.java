package com.gestionvehiculos.vehiculos.dto;

import java.util.List;

public class AgregarDocumentosDTO {

    private List<VehiculoDocumentoDTO> documentos;

    // Constructor vacío
    public AgregarDocumentosDTO() {
    }

    // Constructor con parámetros
    public AgregarDocumentosDTO(List<VehiculoDocumentoDTO> documentos) {
        this.documentos = documentos;
    }

    // Getters y Setters
    public List<VehiculoDocumentoDTO> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<VehiculoDocumentoDTO> documentos) {
        this.documentos = documentos;
    }
}