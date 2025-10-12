package com.gestionvehiculos.vehiculos.model;

import com.gestionvehiculos.vehiculos.enums.EstadoConductor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "vehiculo_conductores")
public class VehiculoConductor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona conductor;

    @NotNull(message = "La fecha de asociación es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaAsociacion;

    @NotNull(message = "El estado del conductor es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoConductor estado;

    // Constructor vacío
    public VehiculoConductor() {
    }

    // Constructor con parámetros
    public VehiculoConductor(Vehiculo vehiculo, Persona conductor,
                             LocalDate fechaAsociacion, EstadoConductor estado) {
        this.vehiculo = vehiculo;
        this.conductor = conductor;
        this.fechaAsociacion = fechaAsociacion;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Persona getConductor() {
        return conductor;
    }

    public void setConductor(Persona conductor) {
        this.conductor = conductor;
    }

    public LocalDate getFechaAsociacion() {
        return fechaAsociacion;
    }

    public void setFechaAsociacion(LocalDate fechaAsociacion) {
        this.fechaAsociacion = fechaAsociacion;
    }

    public EstadoConductor getEstado() {
        return estado;
    }

    public void setEstado(EstadoConductor estado) {
        this.estado = estado;
    }
}