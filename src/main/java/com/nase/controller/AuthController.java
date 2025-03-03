package com.nase.controller;

import com.nase.model.Persona;
import com.nase.repository.PersonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final PersonaRepository personaRepository;

    public AuthController(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String numeroTelefono = request.get("numeroTelefono");
        
        logger.debug("Intento de login con número: {}", numeroTelefono);
        
        Optional<Persona> personaOpt = personaRepository.findByNumeroTelefono(numeroTelefono);
        
        if (!personaOpt.isPresent()) {
            return ResponseEntity.status(401).body(Map.of(
                "error", "Usuario no encontrado",
                "authenticated", false
            ));
        }
        
        Persona persona = personaOpt.get();
        
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("userId", persona.getId());
        response.put("nombre", persona.getNombre());
        response.put("apellido", persona.getApellido());
        response.put("vip", persona.getVip());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{numeroTelefono}")
    public ResponseEntity<?> verificarNumero(@PathVariable String numeroTelefono) {
        logger.debug("Verificando existencia del número: {}", numeroTelefono);
        
        Optional<Persona> personaOpt = personaRepository.findByNumeroTelefono(numeroTelefono);
        boolean existe = personaOpt.isPresent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("numero", numeroTelefono);
        response.put("existe", existe);
        
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