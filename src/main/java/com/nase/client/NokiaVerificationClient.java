package com.nase.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class NokiaVerificationClient {

    private static final Logger logger = LoggerFactory.getLogger(NokiaVerificationClient.class);
    
    @Value("${nokia.api.key}")
    private String apiKey;
    
    @Value("${nokia.api.url}")
    private String apiUrl;
    
    private final RestTemplate restTemplate;
    
    public NokiaVerificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Verifica la ubicación de un número de teléfono usando Nokia Network API
     */
    public Map<String, Object> verificarUbicacion(String numeroTelefono) {
        logger.info("Verificando ubicación del número {} con Nokia API", numeroTelefono);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("phoneNumber", numeroTelefono);
        requestBody.put("consentRequired", false);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl + "/geolocation/v1/verify", 
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            logger.debug("Respuesta de Nokia API: {}", response.getBody());
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error al verificar ubicación con Nokia API: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "No se pudo verificar la ubicación");
            errorResponse.put("detalles", e.getMessage());
            return errorResponse;
        }
    }
    
    /**
     * Verifica si un número está dentro del radio de coordenadas
     */
    public boolean verificarProximidad(String numeroTelefono, double latitud, double longitud, int radioMetros) {
        Map<String, Object> ubicacion = verificarUbicacion(numeroTelefono);
        
        if (ubicacion.containsKey("error")) {
            return false;
        }
        
        // Extraer latitud y longitud de la respuesta de Nokia
        Map<String, Object> locationInfo = (Map<String, Object>) ubicacion.get("location");
        double latitudUsuario = Double.parseDouble(locationInfo.get("latitude").toString());
        double longitudUsuario = Double.parseDouble(locationInfo.get("longitude").toString());
        
        // Calcular distancia
        double distancia = calcularDistancia(latitud, longitud, latitudUsuario, longitudUsuario);
        
        logger.info("Distancia calculada: {} metros, radio permitido: {} metros", distancia, radioMetros);
        return distancia <= radioMetros;
    }
    
    /**
     * Fórmula de Haversine para calcular distancia entre dos puntos geográficos
     */
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        int radioTierra = 6371000; // Radio de la Tierra en metros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                  Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                  Math.sin(dLon / 2) * Math.sin(dLon / 2);
                  
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return radioTierra * c;
    }
} 