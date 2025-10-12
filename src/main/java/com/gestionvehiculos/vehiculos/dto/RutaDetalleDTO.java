package com.gestionvehiculos.vehiculos.dto;

import java.util.List;

public class RutaDetalleDTO {

    private String codigoRuta;
    private String conductorNombre;
    private String vehiculoPlaca;
    private List<TrayectoDTO> trayectos;

    // Constructor vacío
    public RutaDetalleDTO() {
    }

    // Constructor con parámetros
    public RutaDetalleDTO(String codigoRuta, String conductorNombre,
                          String vehiculoPlaca, List<TrayectoDTO> trayectos) {
        this.codigoRuta = codigoRuta;
        this.conductorNombre = conductorNombre;
        this.vehiculoPlaca = vehiculoPlaca;
        this.trayectos = trayectos;
    }

    // Getters y Setters
    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getConductorNombre() {
        return conductorNombre;
    }

    public void setConductorNombre(String conductorNombre) {
        this.conductorNombre = conductorNombre;
    }

    public String getVehiculoPlaca() {
        return vehiculoPlaca;
    }

    public void setVehiculoPlaca(String vehiculoPlaca) {
        this.vehiculoPlaca = vehiculoPlaca;
    }

    public List<TrayectoDTO> getTrayectos() {
        return trayectos;
    }

    public void setTrayectos(List<TrayectoDTO> trayectos) {
        this.trayectos = trayectos;
    }
}