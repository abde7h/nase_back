package com.nase.controller;

import com.nase.service.VerificacionNokiaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/nokia")
public class NokiaVerificacionController {

    private final VerificacionNokiaService verificacionService;
    
    public NokiaVerificacionController(VerificacionNokiaService verificacionService) {
        this.verificacionService = verificacionService;
    }
    
    @GetMapping("/verificar/{personaId}")
    public ResponseEntity<?> verificarPersona(@PathVariable Long personaId) {
        Map<String, Object> resultado = verificacionService.verificarPersona(personaId);
        return ResponseEntity.ok(resultado);
    }
    
    @PostMapping("/verificacion/directo")
    public ResponseEntity<?> verificarNumeroDirecto(@RequestBody Map<String, String> request) {
        String numeroTelefono = request.get("numeroTelefono");
        
        if (numeroTelefono == null || numeroTelefono.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "El número de teléfono es obligatorio"
            ));
        }
        
        // Aquí irían validaciones adicionales, como formato del número
        
        Map<String, Object> resultado = verificacionService.verificarNumeroDirecto(numeroTelefono);
        return ResponseEntity.ok(resultado);
    }
} 