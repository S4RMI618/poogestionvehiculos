package com.gestionvehiculos.vehiculos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TrayectoDTO {

    private Long id;

    @NotNull(message = "El ID del conductor es obligatorio")
    private Long conductorId;

    private String conductorNombre;

    @NotNull(message = "El ID del vehículo es obligatorio")
    private Long vehiculoId;

    private String vehiculoPlaca;

    @NotBlank(message = "El código de ruta es obligatorio")
    private String codigoRuta;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @NotNull(message = "El orden de parada es obligatorio")
    @Min(value = 0, message = "El orden de parada debe ser mayor o igual a 0")
    @Max(value = 6, message = "El orden de parada no puede ser mayor a 6")
    private Integer ordenParada;

    private Double latitud;

    private Double longitud;

    @NotBlank(message = "El login del usuario es obligatorio")
    private String loginUsuarioRegistro;

    private LocalDateTime fechaRegistro;

    // Constructor vacío
    public TrayectoDTO() {
    }

    // Constructor con parámetros
    public TrayectoDTO(Long id, Long conductorId, String conductorNombre,
                       Long vehiculoId, String vehiculoPlaca, String codigoRuta,
                       String ubicacion, Integer ordenParada, Double latitud,
                       Double longitud, String loginUsuarioRegistro,
                       LocalDateTime fechaRegistro) {
        this.id = id;
        this.conductorId = conductorId;
        this.conductorNombre = conductorNombre;
        this.vehiculoId = vehiculoId;
        this.vehiculoPlaca = vehiculoPlaca;
        this.codigoRuta = codigoRuta;
        this.ubicacion = ubicacion;
        this.ordenParada = ordenParada;
        this.latitud = latitud;
        this.longitud = longitud;
        this.loginUsuarioRegistro = loginUsuarioRegistro;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConductorId() {
        return conductorId;
    }

    public void setConductorId(Long conductorId) {
        this.conductorId = conductorId;
    }

    public String getConductorNombre() {
        return conductorNombre;
    }

    public void setConductorNombre(String conductorNombre) {
        this.conductorNombre = conductorNombre;
    }

    public Long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public String getVehiculoPlaca() {
        return vehiculoPlaca;
    }

    public void setVehiculoPlaca(String vehiculoPlaca) {
        this.vehiculoPlaca = vehiculoPlaca;
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