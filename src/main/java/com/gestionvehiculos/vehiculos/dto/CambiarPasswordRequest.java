package com.gestionvehiculos.vehiculos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CambiarPasswordRequest {

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String nuevaPassword;

    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmarPassword;

    // Constructor vacío
    public CambiarPasswordRequest() {
    }

    // Constructor con parámetros
    public CambiarPasswordRequest(String nuevaPassword, String confirmarPassword) {
        this.nuevaPassword = nuevaPassword;
        this.confirmarPassword = confirmarPassword;
    }

    // Getters y Setters
    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }

    // Método de validación
    public boolean passwordsCoinciden() {
        return nuevaPassword != null && nuevaPassword.equals(confirmarPassword);
    }
}