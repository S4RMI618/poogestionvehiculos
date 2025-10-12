package com.gestionvehiculos.vehiculos.dto;

import com.gestionvehiculos.vehiculos.enums.TipoAplicacion;
import com.gestionvehiculos.vehiculos.enums.TipoObligatoriedad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DocumentoDTO {

    private Long id;

    @NotBlank(message = "El código del documento es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre del documento es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo de aplicación es obligatorio")
    private TipoAplicacion tipoAplicacion;

    @NotNull(message = "El tipo de obligatoriedad es obligatorio")
    private TipoObligatoriedad tipoObligatoriedad;

    private String descripcion;

    // Constructor vacío
    public DocumentoDTO() {
    }

    // Constructor con parámetros
    public DocumentoDTO(Long id, String codigo, String nombre, TipoAplicacion tipoAplicacion,
                        TipoObligatoriedad tipoObligatoriedad, String descripcion) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipoAplicacion = tipoAplicacion;
        this.tipoObligatoriedad = tipoObligatoriedad;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoAplicacion getTipoAplicacion() {
        return tipoAplicacion;
    }

    public void setTipoAplicacion(TipoAplicacion tipoAplicacion) {
        this.tipoAplicacion = tipoAplicacion;
    }

    public TipoObligatoriedad getTipoObligatoriedad() {
        return tipoObligatoriedad;
    }

    public void setTipoObligatoriedad(TipoObligatoriedad tipoObligatoriedad) {
        this.tipoObligatoriedad = tipoObligatoriedad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
