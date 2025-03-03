package com.nase.controller;

import com.nase.model.Persona;
import com.nase.repository.PersonaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final PersonaRepository personaRepository;

    public AuthController(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginPorNumero(@RequestBody Map<String, String> credentials) {
        String numeroTelefono = credentials.get("numeroTelefono");
        
        if (numeroTelefono == null || numeroTelefono.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "El número de teléfono es obligatorio"
            ));
        }
        
        logger.debug("Intento de login con número: {}", numeroTelefono);
        
        Persona persona = personaRepository.findByNumeroTelefono(numeroTelefono);
        
        if (persona == null) {
            return ResponseEntity.status(401).body(Map.of(
                "autenticado", false,
                "mensaje", "Número de teléfono no registrado"
            ));
        }
        
        // Actualizar último acceso
        persona.setUltimoAcceso(LocalDateTime.now());
        personaRepository.save(persona);
        
        Map<String, Object> response = new HashMap<>();
        response.put("autenticado", true);
        response.put("personaId", persona.getId());
        response.put("nombre", persona.getNombre());
        
        if (persona.getEventoRegistrado() != null) {
            Map<String, Object> eventoInfo = new HashMap<>();
            eventoInfo.put("id", persona.getEventoRegistrado().getId());
            eventoInfo.put("nombre", persona.getEventoRegistrado().getNombre());
            eventoInfo.put("ubicacion", persona.getEventoRegistrado().getUbicacion());
            response.put("evento", eventoInfo);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Verifica si un número de teléfono está registrado en el sistema
     */
    @GetMapping("/verificar/{numeroTelefono}")
    public ResponseEntity<?> verificarNumero(@PathVariable String numeroTelefono) {
        logger.debug("Verificando existencia del número: {}", numeroTelefono);
        
        Persona persona = personaRepository.findByNumeroTelefono(numeroTelefono);
        boolean existe = persona != null;
        
        Map<String, Object> response = new HashMap<>();
        response.put("numero", numeroTelefono);
        response.put("registrado", existe);
        
        if (existe) {
            Map<String, Object> datosPersona = new HashMap<>();
            datosPersona.put("id", persona.getId());
            datosPersona.put("nombre", persona.getNombre());
            
            if (persona.getEventoRegistrado() != null) {
                datosPersona.put("eventoId", persona.getEventoRegistrado().getId());
                datosPersona.put("eventoNombre", persona.getEventoRegistrado().getNombre());
            }
            
            response.put("persona", datosPersona);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Verifica si un número de teléfono está registrado (método POST)
     */
    @PostMapping("/verificar")
    public ResponseEntity<?> verificarNumeroPost(@RequestBody Map<String, String> request) {
        String numeroTelefono = request.get("numeroTelefono");
        
        if (numeroTelefono == null || numeroTelefono.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "El número de teléfono es obligatorio"
            ));
        }
        
        return verificarNumero(numeroTelefono);
    }
} 