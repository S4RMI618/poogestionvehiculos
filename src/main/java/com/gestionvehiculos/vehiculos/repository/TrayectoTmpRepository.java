package com.gestionvehiculos.vehiculos.repository;

import com.gestionvehiculos.vehiculos.model.TrayectoTmp;
import com.gestionvehiculos.vehiculos.enums.EstadoCargue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrayectoTmpRepository extends JpaRepository<TrayectoTmp, Long> {

    List<TrayectoTmp> findByIdCargue(Long idCargue);

    List<TrayectoTmp> findByIdCargueAndEstado(Long idCargue, EstadoCargue estado);

    @Query("SELECT DISTINCT t.idCargue FROM TrayectoTmp t ORDER BY t.idCargue DESC")
    List<Long> findAllIdCargues();

    @Query("SELECT COUNT(t) FROM TrayectoTmp t WHERE t.idCargue = :idCargue AND t.estado = :estado")
    Long countByIdCargueAndEstado(@Param("idCargue") Long idCargue, @Param("estado") EstadoCargue estado);

    @Query(value = "CALL sp_cargar_trayectos(:idCargue)", nativeQuery = true)
    void ejecutarProcedimientoCargue(@Param("idCargue") Long idCargue);

    @Query(value = "CALL sp_validar_trayectos(:idCargue)", nativeQuery = true)
    void ejecutarProcedimientoValidacion(@Param("idCargue") Long idCargue);

    @Query(value = "CALL sp_procesar_trayectos(:idCargue)", nativeQuery = true)
    void ejecutarProcedimientoProcesado(@Param("idCargue") Long idCargue);
}