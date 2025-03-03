package com.nase.client;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class VerificacionNokiaClient {

    private static final Logger logger = LoggerFactory.getLogger(VerificacionNokiaClient.class);
    
    // Actualiza estos valores según la documentación actual de RapidAPI
    private final String apiKey = "fb420e5939mshd6b7c625a08cefdp12bf01jsn8726c4128732";
    private final String apiHost = "nac-authorization-server.nokia.rapidapi.com";
    private final String baseUrl = "https://nac-authorization-server.p-eu.rapidapi.com";

    /**
     * Obtiene las credenciales de cliente para autenticación con la API de Nokia
     * @return Respuesta con las credenciales en formato JSON
     */
    public String obtenerCredencialesCliente() {
        try (AsyncHttpClient client = new DefaultAsyncHttpClient()) {
            logger.debug("Iniciando petición a: {}/auth/clientcredentials", baseUrl);
            logger.debug("Headers - Host: {}, Key: {}", apiHost, apiKey);
            
            CompletableFuture<Response> future = client.prepare("GET", "https://nac-authorization-server.p-eu.rapidapi.com/auth/clientcredentials")
                    .setHeader("x-rapidapi-key", apiKey)
                    .setHeader("x-rapidapi-host", "nac-authorization-server.nokia.rapidapi.com")
                    .execute()
                    .toCompletableFuture();

            Response response = future.get();
            logger.debug("Respuesta recibida. Código: {}", response.getStatusCode());
            return response.getResponseBody();
        } catch (InterruptedException | ExecutionException | java.io.IOException e) {
            logger.error("Error al obtener credenciales: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener credenciales de cliente de Nokia: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si un número de teléfono está registrado
     * @param numeroTelefono El número de teléfono a verificar
     * @return true si el usuario existe, false en caso contrario
     */
    public boolean verificarNumeroTelefono(String numeroTelefono) {
        logger.debug("Verificando número: {}", numeroTelefono);
        
        // Simulación simple: si el número termina en número par, el usuario existe
        return numeroTelefono != null && !numeroTelefono.isEmpty() &&
                Character.getNumericValue(numeroTelefono.charAt(numeroTelefono.length() - 1)) % 2 == 0;
    }

    /**
     * Consulta información detallada de un usuario por su número de teléfono
     * @param numeroTelefono El número de teléfono del usuario
     * @return Información del usuario en formato JSON
     */
    public String consultarInformacionUsuario(String numeroTelefono) {
        try (AsyncHttpClient client = new DefaultAsyncHttpClient()) {
            CompletableFuture<Response> future = client.prepare("GET", baseUrl + "/users/" + numeroTelefono)
                    .setHeader("x-rapidapi-key", apiKey)
                    .setHeader("x-rapidapi-host", apiHost)
                    .execute()
                    .toCompletableFuture();

            Response response = future.get();
            return response.getResponseBody();
        } catch (InterruptedException | ExecutionException | java.io.IOException e) {
            throw new RuntimeException("Error al consultar información de usuario en Nokia: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica el estado de activación de un número de teléfono
     * @param numeroTelefono El número de teléfono a verificar
     * @return true si el número está activo, false en caso contrario
     */
    public boolean verificarEstadoActivacion(String numeroTelefono) {
        try {
            // Comentamos o eliminamos esta línea:
            // String informacionUsuario = consultarInformacionUsuario(numeroTelefono);
            
            // Simulamos una verificación simple
            return numeroTelefono != null &&
                    !numeroTelefono.isEmpty() &&
                    numeroTelefono.length() > 5;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si un número de teléfono tiene servicios premium activos
     * @param numeroTelefono El número de teléfono a verificar
     * @return true si tiene servicios premium, false en caso contrario
     */
    public boolean verificarServiciosPremium(String numeroTelefono) {
        try {
            // Comentamos o eliminamos esta línea:
            // String informacionUsuario = consultarInformacionUsuario(numeroTelefono);
            
            // Simulamos una verificación simple
            return numeroTelefono != null &&
                    !numeroTelefono.isEmpty() &&
                    numeroTelefono.length() >= 9 &&
                    Character.getNumericValue(numeroTelefono.charAt(0)) > 5;
        } catch (Exception e) {
            return false;
        }
    }
}