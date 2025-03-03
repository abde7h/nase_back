package com.nase.repository;

import com.nase.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    Persona findByNumeroTelefono(String numeroTelefono);
    Persona findByIpPublica(String ipPublica);
} 