package com.nase.controller;

import com.nase.service.VerificacionNumeroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/nokia/verificacion")
public class VerificacionNokiaController {
    
    private static final Logger logger = LoggerFactory.getLogger(VerificacionNokiaController.class);
    private final VerificacionNumeroService verificacionService;
    
    public VerificacionNokiaController(VerificacionNumeroService verificacionService) {
        this.verificacionService = verificacionService;
    }
    
    /**
     * Verifica que el número actual coincida con el registrado para una persona
     */
    @PostMapping("/persona/{personaId}")
    public ResponseEntity<?> verificarNumeroPersona(
            @PathVariable Long personaId,
            @RequestBody Map<String, String> request) {
        
        String numeroActual = request.get("numeroTelefono");
        
        if (numeroActual == null || numeroActual.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "El número de teléfono es obligatorio"
            ));
        }
        
        logger.info("Solicitada verificación para persona {} con número {}", personaId, numeroActual);
        Map<String, Object> resultado = verificacionService.verificarNumeroCoincide(numeroActual, personaId);
        
        return ResponseEntity.ok(resultado);
    }
    
    /**
     * Verifica un número directamente contra un hash
     */
    @PostMapping("/directo")
    public ResponseEntity<?> verificarNumeroDirecto(@RequestBody Map<String, String> request) {
        String numero = request.get("numeroTelefono");
        String hashNumero = request.get("hashNumero");
        
        if (numero == null || numero.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "El número de teléfono es obligatorio"
            ));
        }
        
        logger.info("Solicitada verificación directa para número {}", numero);
        Map<String, Object> resultado = verificacionService.verificarNumeroDirecto(numero, hashNumero);
        
        return ResponseEntity.ok(resultado);
    }
} 