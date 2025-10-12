package com.gestionvehiculos.vehiculos.controller;

import com.gestionvehiculos.vehiculos.dto.AsociarConductorRequest;
import com.gestionvehiculos.vehiculos.dto.VehiculoConductorDTO;
import com.gestionvehiculos.vehiculos.enums.EstadoConductor;
import com.gestionvehiculos.vehiculos.service.VehiculoConductorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehiculo-conductores")
@CrossOrigin(origins = "*")
public class VehiculoConductorController {

    @Autowired
    private VehiculoConductorService vehiculoConductorService;

    // Asociar conductor a vehículo
    @PostMapping
    public ResponseEntity<?> asociarConductor(@Valid @RequestBody AsociarConductorRequest request) {
        try {
            VehiculoConductorDTO resultado = vehiculoConductorService.asociarConductor(request);
            return new ResponseEntity<>(resultado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Obtener conductores de un vehículo
    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<List<VehiculoConductorDTO>> obtenerConductoresDeVehiculo(@PathVariable Long vehiculoId) {
        List<VehiculoConductorDTO> conductores = vehiculoConductorService.obtenerConductoresDeVehiculo(vehiculoId);
        return new ResponseEntity<>(conductores, HttpStatus.OK);
    }

    // Obtener vehículos de un conductor
    @GetMapping("/conductor/{conductorId}")
    public ResponseEntity<List<VehiculoConductorDTO>> obtenerVehiculosDeConductor(@PathVariable Long conductorId) {
        List<VehiculoConductorDTO> vehiculos = vehiculoConductorService.obtenerVehiculosDeConductor(conductorId);
        return new ResponseEntity<>(vehiculos, HttpStatus.OK);
    }

    // Cambiar estado de conductor
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstadoConductor(@PathVariable Long id,
                                                    @RequestParam EstadoConductor estado) {
        try {
            VehiculoConductorDTO resultado = vehiculoConductorService.cambiarEstadoConductor(id, estado);
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}