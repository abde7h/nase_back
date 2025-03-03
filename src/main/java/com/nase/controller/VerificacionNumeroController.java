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
@RequestMapping("/api/numeros")
public class VerificacionNumeroController {

    private static final Logger logger = LoggerFactory.getLogger(VerificacionNumeroController.class);
    private final PersonaRepository personaRepository;

    public VerificacionNumeroController(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    /**
     * Verifica si un número de teléfono existe en la base de datos
     */
    @GetMapping("/verificar/{numero}")
    public ResponseEntity<Map<String, Object>> verificar(@PathVariable String numero) {
        logger.info("Verificando número: {}", numero);
        
        Optional<Persona> personaOpt = personaRepository.findByNumeroTelefono(numero);
        boolean existe = personaOpt.isPresent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("numero", numero);
        response.put("existe", existe);
        
        if (existe) {
            Persona persona = personaOpt.get();
            response.put("id", persona.getId());
            response.put("nombre", persona.getNombre());
            response.put("apellido", persona.getApellido());
            response.put("vip", persona.getVip());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint alternativo usando POST para verificar números
     */
    @PostMapping("/verificar")
    public ResponseEntity<?> verificarNumeroPost(@RequestBody Map<String, String> request) {
        String numero = request.get("numero");
        
        if (numero == null || numero.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "El número de teléfono es obligatorio"
            ));
        }
        
        return verificar(numero);
    }

    @PostMapping("/verificar/{numero}")
    public ResponseEntity<?> verificarNumeroPostPath(@PathVariable String numero) {
        return verificar(numero);
    }
} 