package com.gestionvehiculos.vehiculos.controller;

import com.gestionvehiculos.vehiculos.dto.CrearVehiculoRequest;
import com.gestionvehiculos.vehiculos.dto.VehiculoDTO;
import com.gestionvehiculos.vehiculos.dto.VehiculoDocumentoDTO;
import com.gestionvehiculos.vehiculos.enums.EstadoDocumento;
import com.gestionvehiculos.vehiculos.enums.TipoVehiculo;
import com.gestionvehiculos.vehiculos.service.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    // Crear vehículo con documentos
    @PostMapping
    public ResponseEntity<?> crearVehiculo(@Valid @RequestBody CrearVehiculoRequest request) {
        try {
            VehiculoDTO vehiculoCreado = vehiculoService.crearVehiculo(request);
            return new ResponseEntity<>(vehiculoCreado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Obtener todos los vehículos
    @GetMapping
    public ResponseEntity<List<VehiculoDTO>> obtenerTodosLosVehiculos() {
        List<VehiculoDTO> vehiculos = vehiculoService.obtenerTodosLosVehiculos();
        return new ResponseEntity<>(vehiculos, HttpStatus.OK);
    }

    // Obtener vehículo por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerVehiculoPorId(@PathVariable Long id) {
        try {
            VehiculoDTO vehiculo = vehiculoService.obtenerVehiculoPorId(id);
            return new ResponseEntity<>(vehiculo, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Buscar vehículo por placa
    @GetMapping("/placa/{placa}")
    public ResponseEntity<?> buscarPorPlaca(@PathVariable String placa) {
        try {
            VehiculoDTO vehiculo = vehiculoService.buscarPorPlaca(placa);
            return new ResponseEntity<>(vehiculo, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Buscar vehículos por tipo
    @GetMapping("/tipo/{tipoVehiculo}")
    public ResponseEntity<?> buscarPorTipoVehiculo(@PathVariable String tipoVehiculo) {
        try {
            TipoVehiculo tipo = TipoVehiculo.valueOf(tipoVehiculo.toUpperCase());
            List<VehiculoDTO> vehiculos = vehiculoService.buscarPorTipoVehiculo(tipo);
            return new ResponseEntity<>(vehiculos, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Tipo de vehículo inválido. Use: AUTOMOVIL o MOTOCICLETA",
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Buscar vehículos que tienen un documento específico
    @GetMapping("/documento/{documentoId}")
    public ResponseEntity<?> buscarPorDocumento(@PathVariable Long documentoId) {
        try {
            List<VehiculoDTO> vehiculos = vehiculoService.buscarPorDocumento(documentoId);
            return new ResponseEntity<>(vehiculos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Buscar vehículos por estado de documento
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> buscarPorEstadoDocumento(@PathVariable String estado) {
        try {
            EstadoDocumento estadoDoc = EstadoDocumento.valueOf(estado.toUpperCase());
            List<VehiculoDTO> vehiculos = vehiculoService.buscarPorEstadoDocumento(estadoDoc);
            return new ResponseEntity<>(vehiculos, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Estado inválido. Use: HABILITADO, VENCIDO o EN_VERIFICACION",
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Actualizar vehículo
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarVehiculo(@PathVariable Long id,
                                                @Valid @RequestBody VehiculoDTO vehiculoDTO) {
        try {
            VehiculoDTO vehiculoActualizado = vehiculoService.actualizarVehiculo(id, vehiculoDTO);
            return new ResponseEntity<>(vehiculoActualizado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Eliminar vehículo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarVehiculo(@PathVariable Long id) {
        try {
            vehiculoService.eliminarVehiculo(id);
            return new ResponseEntity<>("Vehículo eliminado exitosamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Agregar documento a vehículo
    @PostMapping("/{vehiculoId}/documentos")
    public ResponseEntity<?> agregarDocumentoAVehiculo(@PathVariable Long vehiculoId,
                                                       @Valid @RequestBody VehiculoDocumentoDTO documentoDTO) {
        try {
            VehiculoDocumentoDTO documentoAgregado = vehiculoService.agregarDocumentoAVehiculo(vehiculoId, documentoDTO);
            return new ResponseEntity<>(documentoAgregado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}