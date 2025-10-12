package com.gestionvehiculos.vehiculos.dto;

public class LoginResponse {

    private String token;
    private String login;
    private String apikey;
    private PersonaDTO persona;

    // Constructor vacío
    public LoginResponse() {
    }

    // Constructor con parámetros
    public LoginResponse(String token, String login, String apikey, PersonaDTO persona) {
        this.token = token;
        this.login = login;
        this.apikey = apikey;
        this.persona = persona;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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