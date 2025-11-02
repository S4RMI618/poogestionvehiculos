package com.gestionvehiculos.vehiculos.repository;

import com.gestionvehiculos.vehiculos.model.Vehiculo;
import com.gestionvehiculos.vehiculos.enums.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByPlaca(String placa);

    List<Vehiculo> findByTipoVehiculo(TipoVehiculo tipoVehiculo);

    boolean existsByPlaca(String placa);

    @Query("SELECT DISTINCT v FROM Vehiculo v JOIN v.documentos vd WHERE vd.documento.id = :documentoId")
    List<Vehiculo> findByDocumentoId(@Param("documentoId") Long documentoId);

    @Query("SELECT DISTINCT v FROM Vehiculo v JOIN v.documentos vd WHERE vd.fechaVencimiento < CURRENT_DATE")
    List<Vehiculo> findVehiculosConDocumentosVencidos();

    @Query("SELECT DISTINCT v FROM Vehiculo v JOIN v.documentos vd WHERE vd.fechaVencimiento BETWEEN CURRENT_DATE AND :fechaLimite")
    List<Vehiculo> findVehiculosConDocumentosPorVencer(@Param("fechaLimite") LocalDate fechaLimite);

}