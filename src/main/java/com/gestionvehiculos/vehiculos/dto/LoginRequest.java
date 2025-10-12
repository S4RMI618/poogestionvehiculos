package com.gestionvehiculos.vehiculos.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "El login es obligatorio")
    private String login;

    @NotBlank(message = "El password es obligatorio")
    private String password;

    @NotBlank(message = "El API Key es obligatorio")
    private String apikey;

    // Constructor vacío
    public LoginRequest() {
    }

    // Constructor con parámetros
    public LoginRequest(String login, String password, String apikey) {
        this.login = login;
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