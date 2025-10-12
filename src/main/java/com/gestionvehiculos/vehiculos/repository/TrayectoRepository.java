package com.gestionvehiculos.vehiculos.repository;

import com.gestionvehiculos.vehiculos.model.Trayecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrayectoRepository extends JpaRepository<Trayecto, Long> {

    List<Trayecto> findByCodigoRutaOrderByOrdenParadaAsc(String codigoRuta);

    @Query("SELECT DISTINCT t.codigoRuta FROM Trayecto t WHERE t.conductor.identificacion = :identificacion")
    List<String> findCodigosRutaByIdentificacionConductor(@Param("identificacion") String identificacion);

    @Query("SELECT DISTINCT t.codigoRuta, CONCAT(t.conductor.nombres, ' ', t.conductor.apellidos) " +
            "FROM Trayecto t WHERE t.vehiculo.placa = :placa")
    List<Object[]> findCodigosRutaYConductorByPlacaVehiculo(@Param("placa") String placa);

    @Query("SELECT t FROM Trayecto t WHERE t.latitud IS NULL OR t.longitud IS NULL")
    List<Trayecto> findTrayectosSinCoordenadas();

    @Query("SELECT t FROM Trayecto t " +
            "JOIN t.vehiculo v " +
            "JOIN v.documentos vd " +
            "JOIN VehiculoConductor vc ON vc.vehiculo.id = v.id AND vc.conductor.id = t.conductor.id " +
            "WHERE vd.estado = 'VENCIDO' OR vc.estado = 'RO' " +
            "ORDER BY t.codigoRuta, t.ordenParada")
    List<Trayecto> findTrayectosConProblemas();

    @Query("SELECT COUNT(t) FROM Trayecto t WHERE t.codigoRuta = :codigoRuta")
    Long countByCodigoRuta(@Param("codigoRuta") String codigoRuta);
}