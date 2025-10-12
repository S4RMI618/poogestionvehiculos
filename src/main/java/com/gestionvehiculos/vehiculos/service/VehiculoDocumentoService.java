package com.gestionvehiculos.vehiculos.service;

import com.gestionvehiculos.vehiculos.dto.VehiculoDocumentoDTO;
import com.gestionvehiculos.vehiculos.model.VehiculoDocumento;
import com.gestionvehiculos.vehiculos.enums.EstadoDocumento;
import com.gestionvehiculos.vehiculos.repository.VehiculoDocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import com.gestionvehiculos.vehiculos.dto.CargarDocumentoRequest;
import java.util.ArrayList;

@Service
@Transactional
public class VehiculoDocumentoService {

    @Autowired
    private VehiculoDocumentoRepository vehiculoDocumentoRepository;

    // Obtener documentos de un vehículo
    public List<VehiculoDocumentoDTO> obtenerDocumentosDeVehiculo(Long vehiculoId) {
        return vehiculoDocumentoRepository.findByVehiculoId(vehiculoId).stream()
                .map(this::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }

    // Actualizar estado de documento
    public VehiculoDocumentoDTO actualizarEstadoDocumento(Long id, EstadoDocumento nuevoEstado) {
        VehiculoDocumento vehiculoDocumento = vehiculoDocumentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relación vehículo-documento no encontrada con ID: " + id));

        vehiculoDocumento.setEstado(nuevoEstado);
        VehiculoDocumento actualizado = vehiculoDocumentoRepository.save(vehiculoDocumento);
        return convertirEntidadaDTO(actualizado);
    }

    // Actualizar fechas de documento
    public VehiculoDocumentoDTO actualizarFechasDocumento(Long id, VehiculoDocumentoDTO documentoDTO) {
        VehiculoDocumento vehiculoDocumento = vehiculoDocumentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relación vehículo-documento no encontrada con ID: " + id));

        vehiculoDocumento.setFechaExpedicion(documentoDTO.getFechaExpedicion());
        vehiculoDocumento.setFechaVencimiento(documentoDTO.getFechaVencimiento());
        vehiculoDocumento.setEstado(documentoDTO.getEstado());

        VehiculoDocumento actualizado = vehiculoDocumentoRepository.save(vehiculoDocumento);
        return convertirEntidadaDTO(actualizado);
    }

    // Eliminar documento de vehículo
    public void eliminarDocumentoDeVehiculo(Long id) {
        if (!vehiculoDocumentoRepository.existsById(id)) {
            throw new RuntimeException("Relación vehículo-documento no encontrada con ID: " + id);
        }
        vehiculoDocumentoRepository.deleteById(id);
    }

    // Método de conversión
    private VehiculoDocumentoDTO convertirEntidadaDTO(VehiculoDocumento vd) {
        return new VehiculoDocumentoDTO(
                vd.getId(),
                vd.getDocumento().getId(),
                vd.getDocumento().getNombre(),
                vd.getFechaExpedicion(),
                vd.getFechaVencimiento(),
                vd.getEstado()
        );
    }

    // Cargar documento PDF en Base64
    public VehiculoDocumentoDTO cargarDocumentoPdf(CargarDocumentoRequest request) {
        VehiculoDocumento vehiculoDocumento = vehiculoDocumentoRepository
                .findById(request.getVehiculoDocumentoId())
                .orElseThrow(() -> new RuntimeException("Documento del vehículo no encontrado con ID: "
                        + request.getVehiculoDocumentoId()));

        vehiculoDocumento.setDocumentoPdfBase64(request.getDocumentoPdfBase64());
        VehiculoDocumento actualizado = vehiculoDocumentoRepository.save(vehiculoDocumento);

        return convertirEntidadaDTO(actualizado);
    }

    // Cargar múltiples documentos PDF
    public List<VehiculoDocumentoDTO> cargarMultiplesDocumentos(List<CargarDocumentoRequest> requests) {
        List<VehiculoDocumentoDTO> resultados = new ArrayList<>();

        for (CargarDocumentoRequest request : requests) {
            VehiculoDocumentoDTO resultado = cargarDocumentoPdf(request);
            resultados.add(resultado);
        }

        return resultados;
    }
}