package com.nase.controller;

import com.nase.model.Persona;
import com.nase.repository.PersonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/verificacion")
public class VerificacionController {
    
    private static final Logger logger = LoggerFactory.getLogger(VerificacionController.class);
    private final PersonaRepository personaRepository;
    
    public VerificacionController(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }
    
    /**
     * Verifica si un usuario existe en el sistema por su número de teléfono
     * @param numeroTelefono Número de teléfono a verificar
     * @return Datos del usuario si existe
     */
    @GetMapping("/usuario/{numeroTelefono}")
    public ResponseEntity<?> verificarUsuarioPorNumero(@PathVariable String numeroTelefono) {
        logger.info("Verificando usuario con número: {}", numeroTelefono);
        
        Optional<Persona> personaOpt = personaRepository.findByNumeroTelefono(numeroTelefono);
        
        Map<String, Object> respuesta = new HashMap<>();
        if (personaOpt.isPresent()) {
            Persona persona = personaOpt.get();
            respuesta.put("existe", true);
            respuesta.put("id", persona.getId());
            respuesta.put("nombre", persona.getNombre() + " " + persona.getApellido());
            
            // Añadir información de localización
            Map<String, Double> localizacion = new HashMap<>();
            localizacion.put("latitud", persona.getLatitudUltima());
            localizacion.put("longitud", persona.getLongitudUltima());
            respuesta.put("localizacion", localizacion);
            
            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.put("existe", false);
            respuesta.put("mensaje", "Usuario no encontrado");
            return ResponseEntity.ok(respuesta);
        }
    }
    
    /**
     * Verifica si un usuario está presente en un evento específico
     */
    @GetMapping("/usuario/{numeroTelefono}/evento/{eventoId}")
    public ResponseEntity<?> verificarPresenciaEnEvento(
            @PathVariable String numeroTelefono,
            @PathVariable Long eventoId) {
        
        logger.info("Verificando presencia de usuario {} en evento {}", numeroTelefono, eventoId);
        
        Optional<Persona> personaOpt = personaRepository.findByNumeroTelefono(numeroTelefono);
        if (!personaOpt.isPresent()) {
            return ResponseEntity.ok(Map.of(
                "existe", false,
                "mensaje", "Usuario no encontrado"
            ));
        }
        
        Persona persona = personaOpt.get();
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("numeroTelefono", numeroTelefono);
        respuesta.put("nombre", persona.getNombre() + " " + persona.getApellido());
        
        if (persona.getEventoRegistrado() != null && 
                persona.getEventoRegistrado().getId().equals(eventoId)) {
            
            respuesta.put("evento", persona.getEventoRegistrado().getNombre());
            respuesta.put("presente", persona.getPresenteEvento());
            
        } else {
            respuesta.put("evento", "No registrado");
            respuesta.put("presente", false);
        }
        
        return ResponseEntity.ok(respuesta);
    }
    
    /**
     * Permite verificar un usuario mediante POST (alternativa al GET)
     */
    @PostMapping("/usuario/verificar")
    public ResponseEntity<?> verificarUsuarioPost(@RequestBody Map<String, String> request) {
        String numeroTelefono = request.get("numeroTelefono");
        if (numeroTelefono == null || numeroTelefono.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "El número de teléfono es obligatorio"
            ));
        }
        
        return verificarUsuarioPorNumero(numeroTelefono);
    }
} 