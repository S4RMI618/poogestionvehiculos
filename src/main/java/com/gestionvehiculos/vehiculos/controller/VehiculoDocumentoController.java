package com.gestionvehiculos.vehiculos.controller;

import com.gestionvehiculos.vehiculos.dto.CargarDocumentoRequest;
import com.gestionvehiculos.vehiculos.dto.VehiculoDocumentoDTO;
import com.gestionvehiculos.vehiculos.enums.EstadoDocumento;
import com.gestionvehiculos.vehiculos.service.VehiculoDocumentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehiculo-documentos")
@CrossOrigin(origins = "*")
public class VehiculoDocumentoController {

    @Autowired
    private VehiculoDocumentoService vehiculoDocumentoService;

    // Obtener documentos de un vehículo
    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<List<VehiculoDocumentoDTO>> obtenerDocumentosDeVehiculo(@PathVariable Long vehiculoId) {
        List<VehiculoDocumentoDTO> documentos = vehiculoDocumentoService.obtenerDocumentosDeVehiculo(vehiculoId);
        return new ResponseEntity<>(documentos, HttpStatus.OK);
    }

    // Actualizar estado de documento
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstadoDocumento(@PathVariable Long id,
                                                       @RequestParam EstadoDocumento estado) {
        try {
            VehiculoDocumentoDTO documentoActualizado = vehiculoDocumentoService.actualizarEstadoDocumento(id, estado);
            return new ResponseEntity<>(documentoActualizado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Actualizar fechas y estado de documento
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarFechasDocumento(@PathVariable Long id,
                                                       @Valid @RequestBody VehiculoDocumentoDTO documentoDTO) {
        try {
            VehiculoDocumentoDTO documentoActualizado = vehiculoDocumentoService.actualizarFechasDocumento(id, documentoDTO);
            return new ResponseEntity<>(documentoActualizado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Eliminar documento de vehículo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDocumentoDeVehiculo(@PathVariable Long id) {
        try {
            vehiculoDocumentoService.eliminarDocumentoDeVehiculo(id);
            return new ResponseEntity<>("Documento eliminado del vehículo exitosamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // NUEVO: Cargar documento PDF en Base64
    @PostMapping("/cargar-documento")
    public ResponseEntity<?> cargarDocumentoPdf(@Valid @RequestBody CargarDocumentoRequest request) {
        try {
            VehiculoDocumentoDTO resultado = vehiculoDocumentoService.cargarDocumentoPdf(request);
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // NUEVO: Cargar múltiples documentos PDF
    @PostMapping("/cargar-multiples-documentos")
    public ResponseEntity<?> cargarMultiplesDocumentos(@Valid @RequestBody List<CargarDocumentoRequest> requests) {
        try {
            List<VehiculoDocumentoDTO> resultados = vehiculoDocumentoService.cargarMultiplesDocumentos(requests);
            return new ResponseEntity<>(resultados, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}