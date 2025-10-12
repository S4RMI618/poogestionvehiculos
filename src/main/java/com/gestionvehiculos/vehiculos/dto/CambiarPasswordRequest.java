package com.gestionvehiculos.vehiculos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CambiarPasswordRequest {

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String nuevaPassword;

    // Constructor vacío
    public CambiarPasswordRequest() {
    }

    // Constructor con parámetros
    public CambiarPasswordRequest(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }

    // Getters y Setters
    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }
}