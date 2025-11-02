package com.gestionvehiculos.vehiculos.repository;

import com.gestionvehiculos.vehiculos.model.Usuario;
import com.gestionvehiculos.vehiculos.model.UsuarioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UsuarioId> {

    Optional<Usuario> findByLogin(String login);

    Optional<Usuario> findByApikey(String apikey);

    Optional<Usuario> findByIdPersona(Long idPersona);

    boolean existsByLogin(String login);

    boolean existsByApikey(String apikey);

    @Modifying
    @Query("UPDATE Usuario u SET u.password = :password WHERE u.login = :login")
    int actualizarPassword(@Param("login") String login, @Param("password") String password);
}