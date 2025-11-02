package com.gestionvehiculos.vehiculos.controller;

import com.gestionvehiculos.vehiculos.dto.CrearVehiculoRequest;
import com.gestionvehiculos.vehiculos.dto.VehiculoDTO;
import com.gestionvehiculos.vehiculos.dto.VehiculoDocumentoDTO;
import com.gestionvehiculos.vehiculos.enums.EstadoDocumento;
import com.gestionvehiculos.vehiculos.enums.TipoVehiculo;
import com.gestionvehiculos.vehiculos.model.Persona;
import com.gestionvehiculos.vehiculos.service.VehiculoConductorService;
import com.gestionvehiculos.vehiculos.service.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private VehiculoConductorService vehiculoConductorService;
    /**
     * 1. Consultar todos los vehículos que tengan documentos vencidos
     * GET /api/vehiculos/documentos-vencidos
     */
    @GetMapping("/documentos-vencidos")
    public ResponseEntity<List<VehiculoDTO>> obtenerVehiculosConDocumentosVencidos() {
        List<VehiculoDTO> vehiculos = vehiculoService.buscarVehiculosConDocumentosVencidos();
        return new ResponseEntity<>(vehiculos, HttpStatus.OK);
    }

    /**
     * 2. Consultar todos los conductores que puedan operar
     * GET /api/vehiculos/conductores-operativos
     */
    @GetMapping("/conductores-operativos")
    public ResponseEntity<List<Persona>> obtenerConductoresQuePuedenOperar() {
        List<Persona> conductores = vehiculoConductorService.obtenerConductoresQuePuedenOperar();
        return new ResponseEntity<>(conductores, HttpStatus.OK);
    }

    /**
     * 3. Consultar un vehículo por placa donde se relacione la información
     *    de los conductores asociados, así como los documentos
     * GET /api/vehiculos/placa/{placa}/completo
     */
    @GetMapping("/placa/{placa}/completo")
    public ResponseEntity<?> obtenerVehiculoCompletoPorPlaca(@PathVariable String placa) {
        try {
            Map<String, Object> resultado = vehiculoService.buscarVehiculoCompletoPorPlaca(placa);
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 4. Consultar los vehículos que tienen documentos por vencer
     *    con un tiempo que se especifique como parámetro en la consulta
     * GET /api/vehiculos/documentos-por-vencer?dias=30
     */
    @GetMapping("/documentos-por-vencer")
    public ResponseEntity<?> obtenerVehiculosConDocumentosPorVencer(
            @RequestParam(defaultValue = "30") int dias) {
        try {
            if (dias < 0) {
                return new ResponseEntity<>("Los días deben ser un número positivo", HttpStatus.BAD_REQUEST);
            }
            List<VehiculoDTO> vehiculos = vehiculoService.buscarVehiculosConDocumentosPorVencer(dias);
            return new ResponseEntity<>(vehiculos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ==================== BÚSQUEDAS ESPECÍFICAS ====================

    // Buscar vehículos por tipo
    @GetMapping("/tipo/{tipoVehiculo}")
    public ResponseEntity<List<VehiculoDTO>> buscarPorTipoVehiculo(@PathVariable TipoVehiculo tipoVehiculo) {
        List<VehiculoDTO> vehiculos = vehiculoService.buscarPorTipoVehiculo(tipoVehiculo);
        return new ResponseEntity<>(vehiculos, HttpStatus.OK);
    }

    // Buscar vehículos por estado de documento
    @GetMapping("/estado-documento/{estado}")
    public ResponseEntity<List<VehiculoDTO>> buscarPorEstadoDocumento(@PathVariable EstadoDocumento estado) {
        List<VehiculoDTO> vehiculos = vehiculoService.buscarPorEstadoDocumento(estado);
        return new ResponseEntity<>(vehiculos, HttpStatus.OK);
    }

    // Buscar por placa simple
    @GetMapping("/placa/{placa}")
    public ResponseEntity<?> buscarPorPlaca(@PathVariable String placa) {
        try {
            VehiculoDTO vehiculo = vehiculoService.buscarPorPlaca(placa);
            return new ResponseEntity<>(vehiculo, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // ==================== CRUD BÁSICO ====================

    // Crear vehículo con documentos
    @PostMapping
    public ResponseEntity<?> crearVehiculo(@Valid @RequestBody CrearVehiculoRequest request) {
        try {
            VehiculoDTO vehiculo = vehiculoService.crearVehiculo(request);
            return new ResponseEntity<>(vehiculo, HttpStatus.CREATED);
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

    // Agregar documento a vehículo existente
    @PostMapping("/{vehiculoId}/documentos")
    public ResponseEntity<?> agregarDocumentoAVehiculo(@PathVariable Long vehiculoId,
                                                       @Valid @RequestBody VehiculoDocumentoDTO documentoDTO) {
        try {
            VehiculoDocumentoDTO documento = vehiculoService.agregarDocumentoAVehiculo(vehiculoId, documentoDTO);
            return new ResponseEntity<>(documento, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
}