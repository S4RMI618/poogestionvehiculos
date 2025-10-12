package com.gestionvehiculos.vehiculos.repository;

import com.gestionvehiculos.vehiculos.model.VehiculoConductor;
import com.gestionvehiculos.vehiculos.enums.EstadoConductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoConductorRepository extends JpaRepository<VehiculoConductor, Long> {

    List<VehiculoConductor> findByVehiculoId(Long vehiculoId);

    List<VehiculoConductor> findByConductorId(Long conductorId);

    List<VehiculoConductor> findByEstado(EstadoConductor estado);

    @Query("SELECT vc FROM VehiculoConductor vc WHERE vc.vehiculo.id = :vehiculoId AND vc.conductor.id = :conductorId")
    Optional<VehiculoConductor> findByVehiculoIdAndConductorId(
            @Param("vehiculoId") Long vehiculoId,
            @Param("conductorId") Long conductorId
    );

    @Query("SELECT vc.conductor FROM VehiculoConductor vc WHERE vc.estado = :estado")
    List<com.gestionvehiculos.vehiculos.model.Persona> findConductoresByEstado(@Param("estado") EstadoConductor estado);
}