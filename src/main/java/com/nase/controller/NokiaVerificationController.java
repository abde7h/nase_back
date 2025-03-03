package com.nase.controller;

import com.nase.service.VerificacionNokiaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/nokia-verification")
public class NokiaVerificationController {
    
    private final VerificacionNokiaService verificacionService;
    
    public NokiaVerificationController(VerificacionNokiaService verificacionService) {
        this.verificacionService = verificacionService;
    }
    
    // Métodos de ambos controladores aquí
    @GetMapping("/verificar/{personaId}")
    public ResponseEntity<?> verificarPersona(@PathVariable Long personaId) {
        Map<String, Object> resultado = verificacionService.verificarPersona(personaId);
        return ResponseEntity.ok(resultado);
    }
    
    @PostMapping("/verificacion/directo")
    public ResponseEntity<?> verificarNumeroDirecto(@RequestBody Map<String, String> request) {
        String numeroTelefono = request.get("numeroTelefono");
        String hashNumero = request.get("hashNumero");
        
        if (numeroTelefono == null || numeroTelefono.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "El número de teléfono es obligatorio"
            ));
        }
        
        Map<String, Object> resultado = verificacionService.verificarNumeroDirecto(numeroTelefono);
        return ResponseEntity.ok(resultado);
    }
} 