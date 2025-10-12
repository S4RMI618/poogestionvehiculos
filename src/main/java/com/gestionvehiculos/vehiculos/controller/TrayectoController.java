package com.gestionvehiculos.vehiculos.controller;

import com.gestionvehiculos.vehiculos.dto.CrearRutaRequest;
import com.gestionvehiculos.vehiculos.dto.RutaDetalleDTO;
import com.gestionvehiculos.vehiculos.dto.TrayectoDTO;
import com.gestionvehiculos.vehiculos.services.TrayectoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trayectos")
@CrossOrigin(origins = "*")
public class TrayectoController {

    @Autowired
    private TrayectoService trayectoService;

    // Crear un trayecto individual
    @PostMapping
    public ResponseEntity<?> crearTrayecto(@Valid @RequestBody TrayectoDTO trayectoDTO) {
        try {
            TrayectoDTO trayectoCreado = trayectoService.crearTrayecto(trayectoDTO);
            return new ResponseEntity<>(trayectoCreado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Crear una ruta completa (múltiples trayectos)
    @PostMapping("/ruta")
    public ResponseEntity<?> crearRuta(@Valid @RequestBody CrearRutaRequest request) {
        try {
            List<TrayectoDTO> trayectosCreados = trayectoService.crearRuta(request.getTrayectos());
            return new ResponseEntity<>(trayectosCreados, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Consultar ruta por código de ruta (PROTEGIDO)
    @GetMapping("/ruta/{codigoRuta}")
    public ResponseEntity<?> consultarRutaPorCodigo(@PathVariable String codigoRuta) {
        try {
            RutaDetalleDTO ruta = trayectoService.consultarRutaPorCodigo(codigoRuta);
            return new ResponseEntity<>(ruta, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Consultar códigos de ruta por identificación de conductor (PROTEGIDO)
    @GetMapping("/conductor/{identificacion}")
    public ResponseEntity<?> consultarRutasPorConductor(@PathVariable String identificacion) {
        try {
            List<String> codigosRuta = trayectoService.consultarRutasPorConductor(identificacion);
            return new ResponseEntity<>(codigosRuta, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Consultar códigos de ruta y conductor por placa de vehículo (PROTEGIDO)
    @GetMapping("/vehiculo/{placa}")
    public ResponseEntity<?> consultarRutasPorPlacaVehiculo(@PathVariable String placa) {
        try {
            Map<String, List<String>> rutasPorConductor = trayectoService.consultarRutasPorPlacaVehiculo(placa);
            return new ResponseEntity<>(rutasPorConductor, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Consultar rutas con problemas (PROTEGIDO)
    @GetMapping("/con-problemas")
    public ResponseEntity<List<RutaDetalleDTO>> consultarRutasConProblemas() {
        List<RutaDetalleDTO> rutasConProblemas = trayectoService.consultarRutasConProblemas();
        return new ResponseEntity<>(rutasConProblemas, HttpStatus.OK);
    }
}