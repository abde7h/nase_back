package com.nase.service;

import com.nase.model.Persona;
import com.nase.repository.PersonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VerificacionUsuarioService {
    
    private static final Logger logger = LoggerFactory.getLogger(VerificacionUsuarioService.class);
    
    private final PersonaRepository personaRepository;
    
    public VerificacionUsuarioService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }
    
    /**
     * Verifica si un número de teléfono está registrado y devuelve los datos asociados
     */
    public Map<String, Object> verificarNumero(String numeroTelefono) {
        logger.debug("Verificando existencia del número: {}", numeroTelefono);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("numero", numeroTelefono);
        
        Persona persona = personaRepository.findByNumeroTelefono(numeroTelefono);
        boolean existe = persona != null;
        
        resultado.put("registrado", existe);
        
        if (existe) {
            Map<String, Object> datosPersona = new HashMap<>();
            datosPersona.put("id", persona.getId());
            datosPersona.put("nombre", persona.getNombre());
            
            if (persona.getEventoRegistrado() != null) {
                Map<String, Object> datosEvento = new HashMap<>();
                datosEvento.put("id", persona.getEventoRegistrado().getId());
                datosEvento.put("nombre", persona.getEventoRegistrado().getNombre());
                datosEvento.put("ubicacion", persona.getEventoRegistrado().getUbicacion());
                
                datosPersona.put("evento", datosEvento);
            }
            
            resultado.put("persona", datosPersona);
        }
        
        return resultado;
    }
    
    /**
     * Verifica si un número está presente en un evento específico
     */
    public Map<String, Object> verificarPresenciaEvento(String numeroTelefono, Long eventoId) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("numero", numeroTelefono);
        resultado.put("eventoId", eventoId);
        
        Persona persona = personaRepository.findByNumeroTelefono(numeroTelefono);
        
        boolean registrado = persona != null;
        boolean enEvento = registrado && 
                persona.getEventoRegistrado() != null && 
                persona.getEventoRegistrado().getId().equals(eventoId);
        boolean presente = enEvento && persona.getPresenteEvento() != null && persona.getPresenteEvento();
        
        resultado.put("registrado", registrado);
        resultado.put("asignadoEvento", enEvento);
        resultado.put("presente", presente);
        
        if (registrado) {
            resultado.put("personaId", persona.getId());
            resultado.put("nombre", persona.getNombre());
        }
        
        return resultado;
    }
} 