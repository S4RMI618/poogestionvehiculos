package com.gestionvehiculos.vehiculos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ActualizarLicenciaRequest {

    @NotBlank(message = "La licencia en Base64 es obligatoria")
    private String licenciaConduccionBase64;

    @NotNull(message = "La fecha de vigencia es obligatoria")
    private LocalDate fechaVigenciaLicencia;

    // Constructor vacío
    public ActualizarLicenciaRequest() {
    }

    // Constructor con parámetros
    public ActualizarLicenciaRequest(String licenciaConduccionBase64, LocalDate fechaVigenciaLicencia) {
        this.licenciaConduccionBase64 = licenciaConduccionBase64;
        this.fechaVigenciaLicencia = fechaVigenciaLicencia;
    }

    // Getters y Setters
    public String getLicenciaConduccionBase64() {
        return licenciaConduccionBase64;
    }

    public void setLicenciaConduccionBase64(String licenciaConduccionBase64) {
        this.licenciaConduccionBase64 = licenciaConduccionBase64;
    }

    public LocalDate getFechaVigenciaLicencia() {
        return fechaVigenciaLicencia;
    }

    public void setFechaVigenciaLicencia(LocalDate fechaVigenciaLicencia) {
        this.fechaVigenciaLicencia = fechaVigenciaLicencia;
    }
}