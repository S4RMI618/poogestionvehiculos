package com.gestionvehiculos.vehiculos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "usuarios")
@IdClass(UsuarioId.class)
public class Usuario implements Serializable {

    @Id
    @NotBlank(message = "El login es obligatorio")
    @Column(nullable = false)
    private String login;

    @Id
    @NotNull(message = "El ID de persona es obligatorio")
    @Column(name = "id_persona", nullable = false)
    private Long idPersona;

    @ManyToOne
    @JoinColumn(name = "id_persona", insertable = false, updatable = false)
    private Persona persona;

    @NotBlank(message = "El password es obligatorio")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El API Key es obligatorio")
    @Column(unique = true, nullable = false)
    private String apikey;

    // Constructor vacío
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(String login, Long idPersona, String password, String apikey) {
        this.login = login;
        this.idPersona = idPersona;
        this.password = password;
        this.apikey = apikey;
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

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }
}