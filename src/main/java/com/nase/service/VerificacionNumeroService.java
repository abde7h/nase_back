package com.nase.service;

import com.nase.client.VerificacionNokiaClient;
import com.nase.model.Persona;
import com.nase.repository.PersonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VerificacionNumeroService {
    
    private static final Logger logger = LoggerFactory.getLogger(VerificacionNumeroService.class);
    private final PersonaRepository personaRepository;
    private final VerificacionNokiaClient verificacionClient;
    
    public VerificacionNumeroService(PersonaRepository personaRepository, VerificacionNokiaClient verificacionClient) {
        this.personaRepository = personaRepository;
        this.verificacionClient = verificacionClient;
    }
    
    /**
     * Verifica que el número proporcionado coincida con el número registrado en la base de datos
     * @param numeroTelefono Número de teléfono a verificar (actual)
     * @param personaId ID de la persona en la base de datos
     * @return Resultado de la verificación
     */
    public Map<String, Object> verificarNumeroCoincide(String numeroTelefono, Long personaId) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("numeroActual", numeroTelefono);
        resultado.put("personaId", personaId);
        
        try {
            // Buscar la persona en la base de datos
            Persona persona = personaRepository.findById(personaId)
                    .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
            
            String numeroRegistrado = persona.getNumeroTelefono();
            resultado.put("numeroRegistrado", numeroRegistrado);
            
            // Generar hash del número registrado (simulando que ya lo teníamos almacenado)
            String hashNumeroRegistrado = verificacionClient.generarHashNumero(numeroRegistrado);
            
            // Realizar la verificación a través de la API de Nokia
            boolean coincide = verificacionClient.verificarNumero(numeroTelefono, hashNumeroRegistrado);
            
            resultado.put("coincide", coincide);
            resultado.put("mensaje", coincide ? 
                    "El número actual coincide con el número registrado" : 
                    "El número actual no coincide con el número registrado");
            
            logger.info("Verificación de número para persona {}: {}", personaId, coincide ? "EXITOSA" : "FALLIDA");
            
        } catch (Exception e) {
            logger.error("Error al verificar número: {}", e.getMessage());
            resultado.put("coincide", false);
            resultado.put("error", e.getMessage());
            resultado.put("mensaje", "Error al realizar la verificación");
        }
        
        return resultado;
    }
    
    /**
     * Verifica un número de teléfono por su valor sin asociarlo a una persona
     * @param numeroTelefono Número a verificar
     * @param hashNumero Hash contra el que verificar
     * @return Resultado de la verificación
     */
    public Map<String, Object> verificarNumeroDirecto(String numeroTelefono, String hashNumero) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("numero", numeroTelefono);
        
        try {
            // Si no se proporciona un hash, generamos uno para simular la verificación
            if (hashNumero == null || hashNumero.isEmpty()) {
                hashNumero = verificacionClient.generarHashNumero(numeroTelefono);
            }
            
            boolean coincide = verificacionClient.verificarNumero(numeroTelefono, hashNumero);
            
            resultado.put("verificado", coincide);
            resultado.put("mensaje", coincide ? 
                    "Verificación exitosa" : 
                    "Verificación fallida");
            
        } catch (Exception e) {
            logger.error("Error en verificación directa: {}", e.getMessage());
            resultado.put("verificado", false);
            resultado.put("error", e.getMessage());
        }
        
        return resultado;
    }
} 