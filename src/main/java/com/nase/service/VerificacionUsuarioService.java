package com.nase.service;

import com.nase.model.Evento;
import com.nase.model.Persona;
import com.nase.repository.EventoRepository;
import com.nase.repository.PersonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class VerificacionUsuarioService {
    
    private static final Logger logger = LoggerFactory.getLogger(VerificacionUsuarioService.class);
    
    private final PersonaRepository personaRepository;
    private final EventoRepository eventoRepository;
    
    public VerificacionUsuarioService(PersonaRepository personaRepository, EventoRepository eventoRepository) {
        this.personaRepository = personaRepository;
        this.eventoRepository = eventoRepository;
    }
    
    /**
     * Verifica si un número de teléfono está registrado y devuelve los datos asociados
     */
    public Map<String, Object> verificarUsuario(String numeroTelefono) {
        logger.debug("Verificando existencia del número: {}", numeroTelefono);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("numero", numeroTelefono);
        
        Optional<Persona> personaOpt = personaRepository.findByNumeroTelefono(numeroTelefono);
        boolean existe = personaOpt.isPresent();
        
        resultado.put("registrado", existe);
        
        if (existe) {
            Persona persona = personaOpt.get();
            
            Map<String, Object> datosPersona = new HashMap<>();
            datosPersona.put("id", persona.getId());
            datosPersona.put("nombre", persona.getNombre());
            datosPersona.put("apellido", persona.getApellido());
            datosPersona.put("vip", persona.getVip());
            
            resultado.put("persona", datosPersona);
            
            if (persona.getEventoRegistrado() != null) {
                Map<String, Object> datosEvento = new HashMap<>();
                datosEvento.put("id", persona.getEventoRegistrado().getId());
                datosEvento.put("nombre", persona.getEventoRegistrado().getNombre());
                
                resultado.put("evento", datosEvento);
                resultado.put("presenteEvento", persona.getPresenteEvento());
            }
        }
        
        return resultado;
    }
    
    /**
     * Verifica si un número está presente en un evento específico
     */
    public Map<String, Object> verificarUsuarioEnEvento(String numeroTelefono, Long eventoId) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("numero", numeroTelefono);
        resultado.put("eventoId", eventoId);
        
        Optional<Persona> personaOpt = personaRepository.findByNumeroTelefono(numeroTelefono);
        
        boolean registrado = personaOpt.isPresent();
        boolean enEvento = false;
        
        if (registrado) {
            Persona persona = personaOpt.get();
            if (persona.getEventoRegistrado() != null && 
                persona.getEventoRegistrado().getId().equals(eventoId)) {
                enEvento = true;
                resultado.put("presente", persona.getPresenteEvento());
            }
        }
        
        resultado.put("registrado", registrado);
        resultado.put("enEvento", enEvento);
        
        return resultado;
    }
} 