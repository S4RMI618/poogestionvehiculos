package com.gestionvehiculos.vehiculos.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class CargueResultadoDTO {

    private Long idCargue;
    private LocalDateTime fechaCargue;
    private Integer totalRegistros;
    private Integer registrosCargados;
    private Integer registrosValidados;
    private Integer registrosProcesados;
    private Integer registrosConError;
    private String mensaje;
    private Map<String, Integer> resumenEstados;

    // Constructor vacío
    public CargueResultadoDTO() {
    }

    // Constructor completo
    public CargueResultadoDTO(Long idCargue, LocalDateTime fechaCargue, Integer totalRegistros,
                              Integer registrosCargados, Integer registrosValidados,
                              Integer registrosProcesados, Integer registrosConError,
                              String mensaje, Map<String, Integer> resumenEstados) {
        this.idCargue = idCargue;
        this.fechaCargue = fechaCargue;
        this.totalRegistros = totalRegistros;
        this.registrosCargados = registrosCargados;
        this.registrosValidados = registrosValidados;
        this.registrosProcesados = registrosProcesados;
        this.registrosConError = registrosConError;
        this.mensaje = mensaje;
        this.resumenEstados = resumenEstados;
    }

    // Getters y Setters
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

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public Integer getRegistrosCargados() {
        return registrosCargados;
    }

    public void setRegistrosCargados(Integer registrosCargados) {
        this.registrosCargados = registrosCargados;
    }

    public Integer getRegistrosValidados() {
        return registrosValidados;
    }

    public void setRegistrosValidados(Integer registrosValidados) {
        this.registrosValidados = registrosValidados;
    }

    public Integer getRegistrosProcesados() {
        return registrosProcesados;
    }

    public void setRegistrosProcesados(Integer registrosProcesados) {
        this.registrosProcesados = registrosProcesados;
    }

    public Integer getRegistrosConError() {
        return registrosConError;
    }

    public void setRegistrosConError(Integer registrosConError) {
        this.registrosConError = registrosConError;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Map<String, Integer> getResumenEstados() {
        return resumenEstados;
    }

    public void setResumenEstados(Map<String, Integer> resumenEstados) {
        this.resumenEstados = resumenEstados;
    }
}