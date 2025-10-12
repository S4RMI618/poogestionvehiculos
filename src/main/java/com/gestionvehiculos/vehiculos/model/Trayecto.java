package com.gestionvehiculos.vehiculos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "trayectos")
public class Trayecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona conductor;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @NotBlank(message = "El código de ruta es obligatorio")
    @Column(nullable = false)
    private String codigoRuta;

    @NotBlank(message = "La ubicación es obligatoria")
    @Column(nullable = false, length = 500)
    private String ubicacion;

    @NotNull(message = "El orden de parada es obligatorio")
    @Column(nullable = false)
    private Integer ordenParada;

    @Column
    private Double latitud;

    @Column
    private Double longitud;

    @NotBlank(message = "El login del usuario es obligatorio")
    @Column(nullable = false)
    private String loginUsuarioRegistro;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    // Constructor vacío
    public Trayecto() {
        this.fechaRegistro = LocalDateTime.now();
    }

    // Constructor con parámetros
    public Trayecto(Persona conductor, Vehiculo vehiculo, String codigoRuta,
                    String ubicacion, Integer ordenParada, Double latitud,
                    Double longitud, String loginUsuarioRegistro) {
        this.conductor = conductor;
        this.vehiculo = vehiculo;
        this.codigoRuta = codigoRuta;
        this.ubicacion = ubicacion;
        this.ordenParada = ordenParada;
        this.latitud = latitud;
        this.longitud = longitud;
        this.loginUsuarioRegistro = loginUsuarioRegistro;
        this.fechaRegistro = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Persona getConductor() {
        return conductor;
    }

    public void setConductor(Persona conductor) {
        this.conductor = conductor;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Integer getOrdenParada() {
        return ordenParada;
    }

    public void setOrdenParada(Integer ordenParada) {
        this.ordenParada = ordenParada;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getLoginUsuarioRegistro() {
        return loginUsuarioRegistro;
    }

    public void setLoginUsuarioRegistro(String loginUsuarioRegistro) {
        this.loginUsuarioRegistro = loginUsuarioRegistro;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}