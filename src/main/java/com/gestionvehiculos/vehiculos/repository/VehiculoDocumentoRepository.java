package com.gestionvehiculos.vehiculos.repository;

import com.gestionvehiculos.vehiculos.model.VehiculoDocumento;
import com.gestionvehiculos.vehiculos.enums.EstadoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoDocumentoRepository extends JpaRepository<VehiculoDocumento, Long> {

    List<VehiculoDocumento> findByVehiculoId(Long vehiculoId);

    List<VehiculoDocumento> findByEstado(EstadoDocumento estado);

    @Query("SELECT vd FROM VehiculoDocumento vd WHERE vd.vehiculo.id = :vehiculoId AND vd.documento.id = :documentoId")
    Optional<VehiculoDocumento> findByVehiculoIdAndDocumentoId(
            @Param("vehiculoId") Long vehiculoId,
            @Param("documentoId") Long documentoId
    );

    @Query("SELECT DISTINCT vd.vehiculo FROM VehiculoDocumento vd WHERE vd.estado = :estado")
    List<com.gestionvehiculos.vehiculos.model.Vehiculo> findVehiculosByEstado(@Param("estado") EstadoDocumento estado);
}
