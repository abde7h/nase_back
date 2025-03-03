package com.nase.controller;

import com.nase.model.Persona;
import com.nase.service.AsistenciaEventoService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/asistencia")
public class AsistenciaController {

    private static final Logger logger = LoggerFactory.getLogger(AsistenciaController.class);
    private final AsistenciaEventoService asistenciaService;
    
    public AsistenciaController(AsistenciaEventoService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }
    
    /**
     * Verifica la asistencia de una persona a un evento basado en su ubicación
     */
    @GetMapping("/verificar/{personaId}")
    public ResponseEntity<?> verificarAsistencia(
            @PathVariable Long personaId,
            HttpServletRequest request) {
        
        // Obtener IPs
        String ipPublica = request.getRemoteAddr();
        String ipPrivada = request.getHeader("X-Forwarded-For");
        
        logger.debug("Verificando asistencia para persona {}. IP pública: {}, IP privada: {}", 
                personaId, ipPublica, ipPrivada);
        
        Persona persona = asistenciaService.verificarAsistenciaEvento(personaId, ipPublica, ipPrivada);
        
        Map<String, Object> response = new HashMap<>();
        response.put("personaId", persona.getId());
        response.put("nombre", persona.getNombre());
        response.put("presente", persona.getPresenteEvento());
        
        if (persona.getEventoRegistrado() != null) {
            response.put("evento", persona.getEventoRegistrado().getNombre());
        }
        
        if (persona.getLatitudUltima() != null && persona.getLongitudUltima() != null) {
            response.put("latitud", persona.getLatitudUltima());
            response.put("longitud", persona.getLongitudUltima());
        }
        
        return ResponseEntity.ok(response);
    }
} 