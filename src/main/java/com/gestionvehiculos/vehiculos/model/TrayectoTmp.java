package com.gestionvehiculos.vehiculos.model;

import com.gestionvehiculos.vehiculos.enums.EstadoCargue;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trayectos_tmp")
public class TrayectoTmp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campos replica de Trayecto (como String para compatibilidad con Excel)
    @Column(nullable = false)
    private String conductorId;

    @Column(nullable = false)
    private String vehiculoId;

    @Column(nullable = false)
    private String codigoRuta;

    @Column(nullable = false, length = 500)
    private String ubicacion;

    @Column(nullable = false)
    private String ordenParada;

    @Column
    private String latitud;

    @Column
    private String longitud;

    @Column(nullable = false)
    private String loginUsuarioRegistro;

    // Campos específicos de TrayectoTmp
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCargue estado;

    @Column(columnDefinition = "TEXT")
    private String observacion;

    @Column(nullable = false)
    private Long idCargue;

    @Column(nullable = false)
    private LocalDateTime fechaCargue;

    // Constructores
    public TrayectoTmp() {
        this.estado = EstadoCargue.CARGADO;
        this.fechaCargue = LocalDateTime.now();
        this.observacion = "";
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

    // Método helper para agregar observaciones
    public void agregarObservacion(String nuevaObservacion) {
        if (this.observacion == null || this.observacion.isEmpty()) {
            this.observacion = nuevaObservacion;
        } else {
            this.observacion += " | " + nuevaObservacion;
        }
    }
}