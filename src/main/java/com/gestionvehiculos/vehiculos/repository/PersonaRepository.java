package com.gestionvehiculos.vehiculos.repository;

import com.gestionvehiculos.vehiculos.model.Persona;
import com.gestionvehiculos.vehiculos.enums.TipoPersona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    Optional<Persona> findByIdentificacion(String identificacion);

    Optional<Persona> findByCorreoElectronico(String correoElectronico);

    List<Persona> findByTipoPersona(TipoPersona tipoPersona);

    boolean existsByIdentificacion(String identificacion);

    boolean existsByCorreoElectronico(String correoElectronico);

    @Query("SELECT p.tipoPersona, COUNT(p) FROM Persona p GROUP BY p.tipoPersona")
    List<Object[]> countByTipoPersona();
}