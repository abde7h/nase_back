package com.nase.controller;

import com.nase.model.Evento;
import com.nase.model.Persona;
import com.nase.repository.EventoRepository;
import com.nase.repository.PersonaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private static final Logger logger = LoggerFactory.getLogger(PersonaController.class);
    private final PersonaRepository personaRepository;
    private final EventoRepository eventoRepository;

    public PersonaController(PersonaRepository personaRepository, EventoRepository eventoRepository) {
        this.personaRepository = personaRepository;
        this.eventoRepository = eventoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Persona>> obtenerTodas() {
        return ResponseEntity.ok(personaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Persona> obtenerPorId(@PathVariable Long id) {
        return personaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Persona> crear(@RequestBody Persona persona) {
        logger.debug("Creando persona: {}", persona.getNombre());
        persona.setUltimoAcceso(LocalDateTime.now());
        return ResponseEntity.ok(personaRepository.save(persona));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Persona> actualizar(@PathVariable Long id, @RequestBody Persona persona) {
        if (!personaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        persona.setId(id);
        return ResponseEntity.ok(personaRepository.save(persona));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!personaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        personaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{personaId}/evento/{eventoId}")
    public ResponseEntity<?> registrarEnEvento(@PathVariable Long personaId, @PathVariable Long eventoId) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        
        persona.setEventoRegistrado(evento);
        persona = personaRepository.save(persona);
        
        return ResponseEntity.ok(persona);
    }
} 