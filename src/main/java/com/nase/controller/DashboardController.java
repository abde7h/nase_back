package com.nase.controller;

import com.nase.model.Evento;
import com.nase.model.Persona;
import com.nase.repository.EventoRepository;
import com.nase.repository.PersonaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final EventoRepository eventoRepository;
    private final PersonaRepository personaRepository;

    public DashboardController(EventoRepository eventoRepository, PersonaRepository personaRepository) {
        this.eventoRepository = eventoRepository;
        this.personaRepository = personaRepository;
    }

    @GetMapping("/evento/{eventoId}/stats")
    public ResponseEntity<?> estadisticasEvento(@PathVariable Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        
        // Buscar todas las personas registradas en este evento
        List<Persona> registrados = personaRepository.findAll().stream()
                .filter(p -> p.getEventoRegistrado() != null && 
                       p.getEventoRegistrado().getId().equals(eventoId))
                .collect(Collectors.toList());
        
        int totalRegistrados = registrados.size();
        long presentes = registrados.stream()
                .filter(Persona::getPresenteEvento)
                .count();
        
        // Calcular porcentaje de asistencia
        double porcentajeAsistencia = totalRegistrados > 0 
                ? (double) presentes / totalRegistrados * 100 
                : 0;
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("eventoId", evento.getId());
        stats.put("nombreEvento", evento.getNombre());
        stats.put("totalRegistrados", totalRegistrados);
        stats.put("presentes", presentes);
        stats.put("porcentajeAsistencia", Math.round(porcentajeAsistencia * 100.0) / 100.0); // Redondear a 2 decimales
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/evento/{eventoId}/asistentes")
    public ResponseEntity<?> listaAsistentes(@PathVariable Long eventoId) {
        eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        
        // Buscar todas las personas presentes en este evento
        List<Map<String, Object>> asistentes = personaRepository.findAll().stream()
                .filter(p -> p.getEventoRegistrado() != null && 
                       p.getEventoRegistrado().getId().equals(eventoId) &&
                       p.getPresenteEvento())
                .map(p -> {
                    Map<String, Object> asistente = new HashMap<>();
                    asistente.put("id", p.getId());
                    asistente.put("nombre", p.getNombre());
                    asistente.put("telefono", p.getNumeroTelefono());
                    asistente.put("ultimoAcceso", p.getUltimoAcceso());
                    return asistente;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(asistentes);
    }
} 