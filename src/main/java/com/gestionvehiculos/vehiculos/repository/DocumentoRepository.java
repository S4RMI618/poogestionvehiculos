package com.gestionvehiculos.vehiculos.repository;


import com.gestionvehiculos.vehiculos.model.Documento;
import com.gestionvehiculos.vehiculos.enums.TipoAplicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    Optional<Documento> findByCodigo(String codigo);

    List<Documento> findByTipoAplicacion(TipoAplicacion tipoAplicacion);

    boolean existsByCodigo(String codigo);
}
