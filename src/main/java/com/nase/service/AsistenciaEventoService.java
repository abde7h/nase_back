package com.nase.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nase.client.GeolocalizacionNokiaClient;
import com.nase.model.Evento;
import com.nase.model.Persona;
import com.nase.repository.PersonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AsistenciaEventoService {
    
    private static final Logger logger = LoggerFactory.getLogger(AsistenciaEventoService.class);
    
    private final PersonaRepository personaRepository;
    private final GeolocalizacionNokiaClient geoClient;
    
    @Autowired
    public AsistenciaEventoService(PersonaRepository personaRepository, GeolocalizacionNokiaClient geoClient) {
        this.personaRepository = personaRepository;
        this.geoClient = geoClient;
    }
    
    /**
     * Actualiza la ubicación de una persona y verifica si está presente en el evento
     * @param personaId ID de la persona
     * @param ipPublica IP pública de la petición
     * @param ipPrivada IP privada (opcional)
     * @return Resultado de la verificación
     */
    public Persona verificarAsistenciaEvento(Long personaId, String ipPublica, String ipPrivada) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        
        // Actualizar información de acceso
        persona.setIpPublica(ipPublica);
        persona.setIpPrivada(ipPrivada);
        persona.setUltimoAcceso(LocalDateTime.now());
        
        // Si no está registrado en ningún evento, no continuamos
        if (persona.getEventoRegistrado() == null) {
            logger.info("La persona {} no está registrada en ningún evento", persona.getNombre());
            persona.setPresenteEvento(false);
            return personaRepository.save(persona);
        }
        
        try {
            JsonNode ubicacion = geoClient.obtenerUbicacion(
                    persona.getNumeroTelefono(), 
                    ipPublica, 
                    ipPrivada
            );
            
            // Extraer coordenadas de la respuesta
            if (ubicacion.has("location") && ubicacion.get("location").has("coordinate")) {
                JsonNode coordinate = ubicacion.get("location").get("coordinate");
                
                if (coordinate.has("latitude") && coordinate.has("longitude")) {
                    double latitud = coordinate.get("latitude").asDouble();
                    double longitud = coordinate.get("longitude").asDouble();
                    
                    // Actualizar ubicación de la persona
                    persona.setLatitudUltima(latitud);
                    persona.setLongitudUltima(longitud);
                    
                    // Obtener el evento registrado
                    Evento evento = persona.getEventoRegistrado();
                    
                    // Calcular distancia entre la persona y el evento
                    double distancia = geoClient.calcularDistancia(
                            latitud, longitud,
                            evento.getLatitud(), evento.getLongitud()
                    );
                    
                    // Verificar si está dentro del radio del evento
                    boolean presente = distancia <= evento.getRadioMetros();
                    persona.setPresenteEvento(presente);
                    
                    logger.info("Persona {} {} presente en el evento {}. Distancia: {} metros", 
                            persona.getNombre(), 
                            presente ? "está" : "no está",
                            evento.getNombre(),
                            Math.round(distancia));
                }
            }
        } catch (Exception e) {
            logger.error("Error al verificar asistencia: {}", e.getMessage(), e);
        }
        
        return personaRepository.save(persona);
    }
} 