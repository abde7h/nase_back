package com.nase.repository;

import com.nase.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    Optional<Persona> findByNumeroTelefono(String numeroTelefono);
    Persona findByIpPublica(String ipPublica);
    List<Persona> findByEventoRegistradoIsNotNull();
} 