package com.nase.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class GeolocalizacionNokiaClient {

    private static final Logger logger = LoggerFactory.getLogger(GeolocalizacionNokiaClient.class);
    
    private final String apiKey = "fb420e5939mshd6b7c625a08cefdp12bf01jsn8726c4128732";
    private final String apiHost = "location-retrieval.nokia.rapidapi.com";
    private final String baseUrl = "https://location-retrieval.p-eu.rapidapi.com";
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Obtiene la ubicación geográfica de un dispositivo basado en su número de teléfono e IPs
     * @param numeroTelefono Número de teléfono
     * @param ipPublica Dirección IP pública
     * @param ipPrivada Dirección IP privada
     * @return Respuesta con datos de geolocalización
     */
    public JsonNode obtenerUbicacion(String numeroTelefono, String ipPublica, String ipPrivada) {
        try (AsyncHttpClient client = new DefaultAsyncHttpClient()) {
            // Construir el cuerpo de la solicitud
            ObjectNode requestBody = objectMapper.createObjectNode();
            ObjectNode device = objectMapper.createObjectNode();
            
            if (numeroTelefono != null && !numeroTelefono.isEmpty()) {
                device.set("phoneNumber", objectMapper.createObjectNode()
                        .put("number", numeroTelefono));
            }
            
            if (ipPublica != null && !ipPublica.isEmpty()) {
                ObjectNode ipv4Address = objectMapper.createObjectNode();
                ipv4Address.set("publicAddress", objectMapper.createObjectNode()
                        .put("address", ipPublica));
                
                if (ipPrivada != null && !ipPrivada.isEmpty()) {
                    ipv4Address.set("privateAddress", objectMapper.createObjectNode()
                            .put("address", ipPrivada));
                }
                
                device.set("ipv4Address", ipv4Address);
            }
            
            requestBody.set("device", device);
            requestBody.put("maxAge", 60); // Edad máxima en segundos
            
            logger.debug("Enviando solicitud de geolocalización para: {}", numeroTelefono);
            
            Response response = client.prepare("POST", baseUrl + "/retrieve")
                    .setHeader("x-rapidapi-key", apiKey)
                    .setHeader("x-rapidapi-host", apiHost)
                    .setHeader("Content-Type", "application/json")
                    .setBody(requestBody.toString())
                    .execute()
                    .toCompletableFuture()
                    .get();
            
            String responseBody = response.getResponseBody();
            logger.debug("Respuesta recibida: {}", responseBody);
            
            return objectMapper.readTree(responseBody);
            
        } catch (InterruptedException | ExecutionException | java.io.IOException e) {
            logger.error("Error al obtener geolocalización: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener geolocalización: " + e.getMessage(), e);
        }
    }
    
    /**
     * Calcula la distancia entre dos puntos usando la fórmula de Haversine
     * @param lat1 Latitud del punto 1
     * @param lon1 Longitud del punto 1
     * @param lat2 Latitud del punto 2
     * @param lon2 Longitud del punto 2
     * @return Distancia en metros
     */
    public double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        // Radio de la Tierra en metros
        final int R = 6371000;
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
} 