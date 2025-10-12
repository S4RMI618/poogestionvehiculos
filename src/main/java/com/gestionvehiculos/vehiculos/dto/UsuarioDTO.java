package com.gestionvehiculos.vehiculos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UsuarioDTO {

    @NotBlank(message = "El login es obligatorio")
    private String login;

    @NotNull(message = "El ID de persona es obligatorio")
    private Long idPersona;

    private String password;

    private String apikey;

    private PersonaDTO persona;

    // Constructor vacío
    public UsuarioDTO() {
    }

    // Constructor con parámetros
    public UsuarioDTO(String login, Long idPersona, String password, String apikey) {
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

    public PersonaDTO getPersona() {
        return persona;
    }

    public void setPersona(PersonaDTO persona) {
        this.persona = persona;
    }
}