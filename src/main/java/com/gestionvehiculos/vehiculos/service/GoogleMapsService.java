package com.gestionvehiculos.vehiculos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleMapsService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleMapsService.class);

    @Value("${google.maps.api.key}")
    private String apiKey;

    @Value("${google.maps.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GoogleMapsService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Obtiene las coordenadas (latitud y longitud) de una dirección usando Google Maps Geocoding API
     *
     * @param direccion La dirección completa a geocodificar
     * @return Map con "latitud" y "longitud", o null si no se encontró
     */
    public Map<String, Double> obtenerCoordenadas(String direccion) {
        try {
            // Codificar la dirección para URL
            String direccionCodificada = URLEncoder.encode(direccion, StandardCharsets.UTF_8);

            // Construir URL manualmente para mejor control
            String url = apiUrl + "?address=" + direccionCodificada + "&key=" + apiKey;

            logger.info("Solicitando coordenadas para: {}", direccion);
            logger.debug("URL de Google Maps: {}", url.replace(apiKey, "***API_KEY***"));

            // Hacer petición GET
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            String response = responseEntity.getBody();

            logger.debug("Respuesta de Google Maps: {}", response);

            // Parsear respuesta JSON
            JsonNode root = objectMapper.readTree(response);
            String status = root.path("status").asText();

            logger.debug("Status de respuesta: {}", status);

            if ("OK".equals(status)) {
                JsonNode results = root.path("results");

                if (results.isArray() && results.size() > 0) {
                    JsonNode location = results.get(0)
                            .path("geometry")
                            .path("location");

                    double latitud = location.path("lat").asDouble();
                    double longitud = location.path("lng").asDouble();

                    // Obtener la dirección formateada de Google
                    String direccionFormateada = results.get(0).path("formatted_address").asText();

                    Map<String, Double> coordenadas = new HashMap<>();
                    coordenadas.put("latitud", latitud);
                    coordenadas.put("longitud", longitud);

                    logger.info("✅ Coordenadas obtenidas exitosamente");
                    logger.info("   Dirección formateada: {}", direccionFormateada);
                    logger.info("   Coordenadas: lat={}, lng={}", latitud, longitud);

                    return coordenadas;
                }

            } else if ("ZERO_RESULTS".equals(status)) {
                logger.warn("❌ No se encontraron resultados para: {}", direccion);
                logger.warn("   Sugerencia: Intenta con una dirección más específica o conocida");
                return null;

            } else if ("OVER_QUERY_LIMIT".equals(status)) {
                logger.error("❌ Límite de consultas excedido para Google Maps API");
                logger.error("   Verifica tu cuota en: https://console.cloud.google.com/google/maps-apis/");
                return null;

            } else if ("REQUEST_DENIED".equals(status)) {
                String errorMessage = root.path("error_message").asText();
                logger.error("❌ Solicitud denegada por Google Maps API");
                logger.error("   Error: {}", errorMessage);
                logger.error("   Verifica que tu API Key esté habilitada para Geocoding API");
                return null;

            } else if ("INVALID_REQUEST".equals(status)) {
                logger.error("❌ Solicitud inválida para: {}", direccion);
                return null;

            } else {
                logger.error("❌ Error desconocido. Status: {}", status);
                return null;
            }

        } catch (Exception e) {
            logger.error("❌ Excepción al obtener coordenadas: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }

        return null;
    }

    /**
     * Verifica si la API Key de Google Maps está configurada correctamente
     */
    public boolean verificarConfiguracion() {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("TU_API_KEY_AQUI")) {
            logger.error("Google Maps API Key no está configurada correctamente");
            return false;
        }
        logger.info("Google Maps API configurada correctamente");
        return true;
    }
}