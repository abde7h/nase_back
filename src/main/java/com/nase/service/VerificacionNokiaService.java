package com.nase.service;

import com.nase.client.NokiaVerificationClient;
import com.nase.model.Evento;
import com.nase.model.Persona;
import com.nase.repository.EventoRepository;
import com.nase.repository.PersonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Service
public class VerificacionNokiaService {

    private static final Logger logger = LoggerFactory.getLogger(VerificacionNokiaService.class);
    
    private final NokiaVerificationClient nokiaClient;
    private final PersonaRepository personaRepository;
    private final EventoRepository eventoRepository;
    
    public VerificacionNokiaService(
            NokiaVerificationClient nokiaClient,
            PersonaRepository personaRepository,
            EventoRepository eventoRepository) {
        this.nokiaClient = nokiaClient;
        this.personaRepository = personaRepository;
        this.eventoRepository = eventoRepository;
    }
    
    /**
     * Verifica la ubicación de una persona usando Nokia API
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> verificarPersona(Long personaId) {
        Optional<Persona> personaOpt = personaRepository.findById(personaId);
        
        if (!personaOpt.isPresent()) {
            return Map.of(
                "error", "Persona no encontrada",
                "personaId", personaId
            );
        }
        
        Persona persona = personaOpt.get();
        Map<String, Object> resultadoNokia = nokiaClient.verificarUbicacion(persona.getNumeroTelefono());
        
        // Actualizar ubicación en base de datos si la verificación fue exitosa
        if (!resultadoNokia.containsKey("error")) {
            Map<String, Object> locationInfo = (Map<String, Object>) resultadoNokia.get("location");
            double latitud = Double.parseDouble(locationInfo.get("latitude").toString());
            double longitud = Double.parseDouble(locationInfo.get("longitude").toString());
            
            persona.setLatitudUltima(latitud);
            persona.setLongitudUltima(longitud);
            persona.setUltimoAcceso(LocalDateTime.now());
            
            // Verificar si está en evento registrado
            if (persona.getEventoRegistrado() != null) {
                Evento evento = persona.getEventoRegistrado();
                boolean presente = nokiaClient.verificarProximidad(
                    persona.getNumeroTelefono(),
                    evento.getLatitud(),
                    evento.getLongitud(),
                    evento.getRadioMetros()
                );
                persona.setPresenteEvento(presente);
            }
            
            personaRepository.save(persona);
        }
        
        return resultadoNokia;
    }
    
    /**
     * Actualiza periódicamente la ubicación de todas las personas con eventos activos
     */
    @Scheduled(fixedRate = 900000) // 15 minutos
    public void actualizarUbicacionesAutomaticas() {
        logger.info("Iniciando actualización automática de ubicaciones");
        
        List<Persona> personasConEvento = personaRepository.findByEventoRegistradoIsNotNull();
        int actualizadas = 0;
        
        for (Persona persona : personasConEvento) {
            try {
                verificarPersona(persona.getId());
                actualizadas++;
            } catch (Exception e) {
                logger.error("Error al actualizar persona {}: {}", persona.getId(), e.getMessage());
            }
            
            // Pequeña pausa para no sobrecargar la API
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        logger.info("Actualización completada: {} personas actualizadas", actualizadas);
    }

    /**
     * Verifica un número directamente sin necesidad de tener la persona registrada
     */
    public Map<String, Object> verificarNumeroDirecto(String numeroTelefono) {
        logger.info("Verificando número directo: {}", numeroTelefono);
        
        // Primero verificamos si el número existe en nuestra base de datos
        Optional<Persona> personaOpt = personaRepository.findByNumeroTelefono(numeroTelefono);
        
        Map<String, Object> resultadoNokia = nokiaClient.verificarUbicacion(numeroTelefono);
        
        // Si la persona existe en nuestra base, añadimos información del evento
        if (personaOpt.isPresent() && !resultadoNokia.containsKey("error")) {
            Persona persona = personaOpt.get();
            
            if (persona.getEventoRegistrado() != null) {
                Evento evento = eventoRepository.findById(persona.getEventoRegistrado().getId()).orElse(null);
                
                if (evento != null) {
                    Map<String, Object> eventoInfo = new HashMap<>();
                    eventoInfo.put("id", evento.getId());
                    eventoInfo.put("nombre", evento.getNombre());
                    eventoInfo.put("ubicacion", evento.getUbicacion());
                    resultadoNokia.put("evento", eventoInfo);
                }
            }
        }
        
        return resultadoNokia;
    }
} 