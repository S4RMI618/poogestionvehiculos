package com.gestionvehiculos.vehiculos.util;

import java.security.SecureRandom;
import java.util.UUID;

public class GeneradorUtil {
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
    private static final SecureRandom random = new SecureRandom();

    /**
     * Forma de generar login:
     * Primera letra del nombre + Primera letra del apellido + Número de identificación
     */
    public static String generarLogin(String nombres, String apellidos, String identificacion) {
        String primeraLetraNombre = nombres.substring(0, 1).toLowerCase();
        String primeraLetraApellido = apellidos.substring(0, 1).toLowerCase();
        return primeraLetraNombre + primeraLetraApellido + identificacion;
    }
    /**
     * Password aleatorio de 12 caracteres
     */
    public static String generarPassword() {
        StringBuilder password = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            password.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return password.toString();
    }
    /**
     * API Key único basado en UUID
     */
    public static String generarApiKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}