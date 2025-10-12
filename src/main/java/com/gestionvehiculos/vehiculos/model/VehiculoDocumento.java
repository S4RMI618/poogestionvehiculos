package com.gestionvehiculos.vehiculos.model;

import com.gestionvehiculos.vehiculos.enums.EstadoDocumento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "vehiculo_documentos")
public class VehiculoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "documento_id", nullable = false)
    private Documento documento;

    @NotNull(message = "La fecha de expedición es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaExpedicion;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @NotNull(message = "El estado del documento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDocumento estado;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String documentoPdfBase64;

    // Getter y Setter
    public String getDocumentoPdfBase64() {
        return documentoPdfBase64;
    }

    public void setDocumentoPdfBase64(String documentoPdfBase64) {
        this.documentoPdfBase64 = documentoPdfBase64;
    }

    // Constructor vacío
    public VehiculoDocumento() {
    }

    // Constructor con parámetros
    public VehiculoDocumento(Vehiculo vehiculo, Documento documento,
                             LocalDate fechaExpedicion, LocalDate fechaVencimiento,
                             EstadoDocumento estado) {
        this.vehiculo = vehiculo;
        this.documento = documento;
        this.fechaExpedicion = fechaExpedicion;
        this.fechaVencimiento = fechaVencimiento;
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

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public LocalDate getFechaExpedicion() {
        return fechaExpedicion;
    }

    public void setFechaExpedicion(LocalDate fechaExpedicion) {
        this.fechaExpedicion = fechaExpedicion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public EstadoDocumento getEstado() {
        return estado;
    }

    public void setEstado(EstadoDocumento estado) {
        this.estado = estado;
    }
}
