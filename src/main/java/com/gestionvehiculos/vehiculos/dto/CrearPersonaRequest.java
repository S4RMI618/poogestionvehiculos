package com.gestionvehiculos.vehiculos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class CrearPersonaRequest {

    @NotNull(message = "Los datos de la persona son obligatorios")
    @Valid
    private PersonaDTO persona;

    // Constructor vacío
    public CrearPersonaRequest() {
    }

    // Constructor con parámetros
    public CrearPersonaRequest(PersonaDTO persona) {
        this.persona = persona;
    }

    // Getters y Setters
    public PersonaDTO getPersona() {
        return persona;
    }

    public void setPersona(PersonaDTO persona) {
        this.persona = persona;
    }
}