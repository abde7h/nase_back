package com.nase.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Component
public class VerificacionNokiaClient {
    
    private static final Logger logger = LoggerFactory.getLogger(VerificacionNokiaClient.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${rapidapi.key:fb420e5939mshd6b7c625a08cefdp12bf01jsn8726c4128732}")
    private String rapidApiKey;
    
    @Value("${rapidapi.host:number-verification.nokia.rapidapi.com}")
    private String rapidApiHost;
    
    @Value("${rapidapi.url:https://number-verification.p-eu.rapidapi.com/verify}")
    private String rapidApiUrl;
    
    public VerificacionNokiaClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Genera un hash SHA-256 del número de teléfono para la verificación
     */
    public String generarHashNumero(String numeroTelefono) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(numeroTelefono.getBytes());
            
            // Convertir bytes a string hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error al generar hash para el número: {}", e.getMessage());
            throw new RuntimeException("Error al generar hash para verificación", e);
        }
    }
    
    /**
     * Verifica si el número de teléfono coincide con el hash almacenado
     */
    public boolean verificarNumero(String numeroTelefono, String hashAlmacenado) {
        try {
            // Asegurarse de que el número tenga formato internacional (con +)
            if (!numeroTelefono.startsWith("+")) {
                numeroTelefono = "+34" + numeroTelefono; // Prefijo de España como ejemplo
            }
            
            // Preparar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-rapidapi-key", rapidApiKey);
            headers.set("x-rapidapi-host", rapidApiHost);
            
            // Preparar el cuerpo de la solicitud
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("phoneNumber", numeroTelefono);
            requestBody.put("hashedPhoneNumber", hashAlmacenado);
            
            // Crear la entidad HTTP con headers y cuerpo
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
            
            // Ejecutar la solicitud
            ResponseEntity<String> response = restTemplate.exchange(
                    rapidApiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            // Procesar la respuesta
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                logger.info("Respuesta de verificación: {}", jsonResponse);
                
                // La API retorna un campo 'match' con true/false
                if (jsonResponse.has("match")) {
                    return jsonResponse.get("match").asBoolean();
                }
            }
            
            logger.warn("Verificación fallida. Código: {}", response.getStatusCodeValue());
            return false;
            
        } catch (Exception e) {
            logger.error("Error en verificación de número: {}", e.getMessage());
            return false;
        }
    }
}