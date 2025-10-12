package com.gestionvehiculos.vehiculos.controller;

import com.gestionvehiculos.vehiculos.dto.VehiculoDTO;
import com.gestionvehiculos.vehiculos.enums.EstadoConductor;
import com.gestionvehiculos.vehiculos.model.Persona;
import com.gestionvehiculos.vehiculos.service.PersonaService;
import com.gestionvehiculos.vehiculos.service.VehiculoConductorService;
import com.gestionvehiculos.vehiculos.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicController {

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private VehiculoConductorService vehiculoConductorService;

    @Autowired
    private PersonaService personaService;

    // Consultar todos los vehículos con documentos vencidos
    @GetMapping("/vehiculos/documentos-vencidos")
    public ResponseEntity<List<VehiculoDTO>> obtenerVehiculosConDocumentosVencidos() {
        List<VehiculoDTO> vehiculos = vehiculoService.buscarVehiculosConDocumentosVencidos();
        return new ResponseEntity<>(vehiculos, HttpStatus.OK);
    }

    // Consultar vehículos con documentos por vencer
    @GetMapping("/vehiculos/documentos-por-vencer")
    public ResponseEntity<List<VehiculoDTO>> obtenerVehiculosConDocumentosPorVencer(
            @RequestParam(defaultValue = "30") int dias) {
        List<VehiculoDTO> vehiculos = vehiculoService.buscarVehiculosConDocumentosPorVencer(dias);
        return new ResponseEntity<>(vehiculos, HttpStatus.OK);
    }

    // Consultar todos los conductores que pueden operar
    @GetMapping("/conductores/pueden-operar")
    public ResponseEntity<List<Persona>> obtenerConductoresQuePuedenOperar() {
        List<Persona> conductores = vehiculoConductorService.obtenerConductoresQuePuedenOperar();
        return new ResponseEntity<>(conductores, HttpStatus.OK);
    }

    // Consultar vehículo por placa con conductores y documentos
    @GetMapping("/vehiculos/placa/{placa}")
    public ResponseEntity<?> obtenerVehiculoCompletoPorPlaca(@PathVariable String placa) {
        try {
            Map<String, Object> resultado = vehiculoService.buscarVehiculoCompletoPorPlaca(placa);
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Consultar total de personas agrupadas por tipo
    @GetMapping("/personas/contar-por-tipo")
    public ResponseEntity<Map<String, Long>> contarPersonasPorTipo() {
        Map<String, Long> conteo = personaService.contarPersonasPorTipo();
        return new ResponseEntity<>(conteo, HttpStatus.OK);
    }
}