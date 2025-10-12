package com.gestionvehiculos.vehiculos.model;

import java.io.Serializable;
import java.util.Objects;

public class UsuarioId implements Serializable {

    private String login;
    private Long idPersona;

    // Constructor vacío
    public UsuarioId() {
    }

    // Constructor con parámetros
    public UsuarioId(String login, Long idPersona) {
        this.login = login;
        this.idPersona = idPersona;
    }

    // Getters y Setters
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioId usuarioId = (UsuarioId) o;
        return Objects.equals(login, usuarioId.login) &&
                Objects.equals(idPersona, usuarioId.idPersona);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, idPersona);
    }
}