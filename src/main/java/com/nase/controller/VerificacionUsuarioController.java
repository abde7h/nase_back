package com.nase.controller;

import com.nase.service.VerificacionUsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/verificacion/usuario")
public class VerificacionUsuarioController {
    
    private static final Logger logger = LoggerFactory.getLogger(VerificacionUsuarioController.class);
    private final VerificacionUsuarioService verificacionService;
    
    public VerificacionUsuarioController(VerificacionUsuarioService verificacionService) {
        this.verificacionService = verificacionService;
    }
    
    /**
     * Verifica si un número está registrado en el sistema
     */
    @GetMapping("/{numeroTelefono}")
    public ResponseEntity<?> verificarNumero(@PathVariable String numeroTelefono) {
        Map<String, Object> resultado = verificacionService.verificarUsuario(numeroTelefono);
        return ResponseEntity.ok(resultado);
    }
    
    /**
     * Verifica si un número está presente en un evento específico
     */
    @GetMapping("/{numeroTelefono}/evento/{eventoId}")
    public ResponseEntity<?> verificarPresenciaEvento(
            @PathVariable String numeroTelefono,
            @PathVariable Long eventoId) {
        
        Map<String, Object> resultado = verificacionService.verificarUsuarioEnEvento(numeroTelefono, eventoId);
        return ResponseEntity.ok(resultado);
    }
    
    /**
     * Verifica un número a través de POST (para datos sensibles o formularios)
     */
    @PostMapping("/verificar")
    public ResponseEntity<?> verificarNumeroPost(@RequestBody Map<String, String> request) {
        String numeroTelefono = request.get("numeroTelefono");
        
        if (numeroTelefono == null || numeroTelefono.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "El número de teléfono es obligatorio"
            ));
        }
        
        Map<String, Object> resultado = verificacionService.verificarUsuario(numeroTelefono);
        return ResponseEntity.ok(resultado);
    }
} 