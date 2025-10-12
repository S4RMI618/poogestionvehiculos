package com.gestionvehiculos.vehiculos.dto;

import com.gestionvehiculos.vehiculos.enums.TipoIdentificacion;
import com.gestionvehiculos.vehiculos.enums.TipoPersona;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class PersonaDTO {

    private Long id;

    @NotBlank(message = "La identificación es obligatoria")
    private String identificacion;

    @NotNull(message = "El tipo de identificación es obligatorio")
    private TipoIdentificacion tipoIdentificacion;

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe ser válido")
    private String correoElectronico;

    @NotNull(message = "El tipo de persona es obligatorio")
    private TipoPersona tipoPersona;

    private String licenciaConduccionBase64;
    private LocalDate fechaVigenciaLicencia;

    public PersonaDTO() {
    }

    // Constructor con parámetros
    public PersonaDTO(Long id, String identificacion, TipoIdentificacion tipoIdentificacion,
                      String nombres, String apellidos, String correoElectronico,
                      TipoPersona tipoPersona, String licenciaConduccionBase64,
                      LocalDate fechaVigenciaLicencia) {
        this.id = id;
        this.identificacion = identificacion;
        this.tipoIdentificacion = tipoIdentificacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correoElectronico = correoElectronico;
        this.tipoPersona = tipoPersona;
        this.licenciaConduccionBase64 = licenciaConduccionBase64;
        this.fechaVigenciaLicencia = fechaVigenciaLicencia;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public TipoIdentificacion getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(TipoIdentificacion tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public TipoPersona getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(TipoPersona tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

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