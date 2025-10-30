package com.gestionvehiculos.vehiculos.dto;

import com.gestionvehiculos.vehiculos.enums.EstadoCargue;
import java.time.LocalDateTime;

public class TrayectoTmpDTO {

    private Long id;
    private String conductorId;
    private String vehiculoId;
    private String codigoRuta;
    private String ubicacion;
    private String ordenParada;
    private String latitud;
    private String longitud;
    private String loginUsuarioRegistro;
    private EstadoCargue estado;
    private String observacion;
    private Long idCargue;
    private LocalDateTime fechaCargue;

    // Constructor vacío
    public TrayectoTmpDTO() {
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConductorId() {
        return conductorId;
    }

    public void setConductorId(String conductorId) {
        this.conductorId = conductorId;
    }

    public String getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(String vehiculoId) {
        this.vehiculoId = vehiculoId;
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

    public String getOrdenParada() {
        return ordenParada;
    }

    public void setOrdenParada(String ordenParada) {
        this.ordenParada = ordenParada;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLoginUsuarioRegistro() {
        return loginUsuarioRegistro;
    }

    public void setLoginUsuarioRegistro(String loginUsuarioRegistro) {
        this.loginUsuarioRegistro = loginUsuarioRegistro;
    }

    public EstadoCargue getEstado() {
        return estado;
    }

    public void setEstado(EstadoCargue estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Long getIdCargue() {
        return idCargue;
    }

    public void setIdCargue(Long idCargue) {
        this.idCargue = idCargue;
    }

    public LocalDateTime getFechaCargue() {
        return fechaCargue;
    }

    public void setFechaCargue(LocalDateTime fechaCargue) {
        this.fechaCargue = fechaCargue;
    }
}