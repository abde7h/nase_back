package com.nase.controller;

import com.nase.model.Persona;
import com.nase.repository.PersonaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/simple")
public class SimpleController {

    private final PersonaRepository personaRepository;

    public SimpleController(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @GetMapping("/check/{numero}")
    public ResponseEntity<?> verificar(@PathVariable String numero) {
        Optional<Persona> personaOpt = personaRepository.findByNumeroTelefono(numero);
        return ResponseEntity.ok(Map.of(
            "numero", numero,
            "existe", personaOpt.isPresent()
        ));
    }
} 