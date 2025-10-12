package com.gestionvehiculos.vehiculos.model;

import com.gestionvehiculos.vehiculos.enums.TipoIdentificacion;
import com.gestionvehiculos.vehiculos.enums.TipoPersona;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.persistence.Lob;

import java.time.LocalDate;

@Entity
@Table(name = "personas")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La identificación es obligatoria")
    @Column(unique = true, nullable = false)
    private String identificacion;

    @NotNull(message = "El tipo de identificación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoIdentificacion tipoIdentificacion;

    @NotBlank(message = "Los nombres son obligatorios")
    @Column(nullable = false)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Column(nullable = false)
    private String apellidos;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe ser válido")
    @Column(unique = true, nullable = false)
    private String correoElectronico;

    @NotNull(message = "El tipo de persona es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPersona tipoPersona;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String licenciaConduccionBase64;

    @Column(name = "fecha_vigencia_licencia")
    private LocalDate fechaVigenciaLicencia;

    // Constructor vacío
    public Persona() {
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



    // Constructor con parámetros
    public Persona(String identificacion, TipoIdentificacion tipoIdentificacion,
                   String nombres, String apellidos, String correoElectronico,
                   TipoPersona tipoPersona) {
        this.identificacion = identificacion;
        this.tipoIdentificacion = tipoIdentificacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correoElectronico = correoElectronico;
        this.tipoPersona = tipoPersona;
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
}