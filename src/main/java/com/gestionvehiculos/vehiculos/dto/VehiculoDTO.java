package com.gestionvehiculos.vehiculos.dto;

import com.gestionvehiculos.vehiculos.enums.TipoCombustible;
import com.gestionvehiculos.vehiculos.enums.TipoServicio;
import com.gestionvehiculos.vehiculos.enums.TipoVehiculo;
import jakarta.validation.constraints.*;
import java.util.List;

public class VehiculoDTO {

    private Long id;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    private TipoVehiculo tipoVehiculo;

    @NotBlank(message = "La placa es obligatoria")
    @Pattern(regexp = "^[A-Z]{3}[0-9]{3}$|^[A-Z]{3}[0-9]{2}[A-Z]$",
            message = "Formato de placa inválido")
    private String placa;

    @NotNull(message = "El tipo de servicio es obligatorio")
    private TipoServicio tipoServicio;

    @NotNull(message = "El tipo de combustible es obligatorio")
    private TipoCombustible tipoCombustible;

    @NotNull(message = "La capacidad de pasajeros es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacidadPasajeros;

    @NotBlank(message = "El color es obligatorio")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "El color debe ser un código hexadecimal válido")
    private String color;

    @NotNull(message = "El modelo es obligatorio")
    private Integer modelo;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "La línea es obligatoria")
    private String linea;

    private List<VehiculoDocumentoDTO> documentos;

    // Constructor vacío
    public VehiculoDTO() {
    }

    // Constructor con parámetros
    public VehiculoDTO(Long id, TipoVehiculo tipoVehiculo, String placa, TipoServicio tipoServicio,
                       TipoCombustible tipoCombustible, Integer capacidadPasajeros, String color,
                       Integer modelo, String marca, String linea) {
        this.id = id;
        this.tipoVehiculo = tipoVehiculo;
        this.placa = placa;
        this.tipoServicio = tipoServicio;
        this.tipoCombustible = tipoCombustible;
        this.capacidadPasajeros = capacidadPasajeros;
        this.color = color;
        this.modelo = modelo;
        this.marca = marca;
        this.linea = linea;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public TipoCombustible getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(TipoCombustible tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public Integer getCapacidadPasajeros() {
        return capacidadPasajeros;
    }

    public void setCapacidadPasajeros(Integer capacidadPasajeros) {
        this.capacidadPasajeros = capacidadPasajeros;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getModelo() {
        return modelo;
    }

    public void setModelo(Integer modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public List<VehiculoDocumentoDTO> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<VehiculoDocumentoDTO> documentos) {
        this.documentos = documentos;
    }
}