package com.gestionvehiculos.vehiculos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CrearRutaRequest {

    @NotEmpty(message = "Debe incluir al menos 2 trayectos (inicial y final)")
    @Size(min = 2, max = 7, message = "Una ruta debe tener mínimo 2 y máximo 7 trayectos")
    @Valid
    private List<TrayectoDTO> trayectos;

    public CrearRutaRequest() {
    }
    public CrearRutaRequest(List<TrayectoDTO> trayectos) {
        this.trayectos = trayectos;
    }
    // Getters y Setters
    public List<TrayectoDTO> getTrayectos() {
        return trayectos;
    }
    public void setTrayectos(List<TrayectoDTO> trayectos) {
        this.trayectos = trayectos;
    }
}