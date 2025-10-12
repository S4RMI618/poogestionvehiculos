package com.gestionvehiculos.vehiculos.controller;

import com.gestionvehiculos.vehiculos.dto.ActualizarLicenciaRequest;
import com.gestionvehiculos.vehiculos.dto.CrearPersonaRequest;
import com.gestionvehiculos.vehiculos.dto.PersonaDTO;
import com.gestionvehiculos.vehiculos.enums.TipoPersona;
import com.gestionvehiculos.vehiculos.service.PersonaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personas")
@CrossOrigin(origins = "*")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    // Crear persona (y usuario si es ADMINISTRATIVO)
    @PostMapping
    public ResponseEntity<?> crearPersona(@Valid @RequestBody CrearPersonaRequest request) {
        try {
            Map<String, Object> resultado = personaService.crearPersona(request.getPersona());
            return new ResponseEntity<>(resultado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Obtener todas las personas
    @GetMapping
    public ResponseEntity<List<PersonaDTO>> obtenerTodasLasPersonas() {
        List<PersonaDTO> personas = personaService.obtenerTodasLasPersonas();
        return new ResponseEntity<>(personas, HttpStatus.OK);
    }

    // Obtener persona por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPersonaPorId(@PathVariable Long id) {
        try {
            PersonaDTO persona = personaService.obtenerPersonaPorId(id);
            return new ResponseEntity<>(persona, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Obtener persona por identificación
    @GetMapping("/identificacion/{identificacion}")
    public ResponseEntity<?> obtenerPersonaPorIdentificacion(@PathVariable String identificacion) {
        try {
            PersonaDTO persona = personaService.obtenerPersonaPorIdentificacion(identificacion);
            return new ResponseEntity<>(persona, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Obtener personas por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> obtenerPersonasPorTipo(@PathVariable String tipo) {
        try {
            TipoPersona tipoPersona = TipoPersona.valueOf(tipo.toUpperCase());
            List<PersonaDTO> personas = personaService.obtenerPersonasPorTipo(tipoPersona);
            return new ResponseEntity<>(personas, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Tipo de persona inválido. Use: C (Conductor) o A (Administrativo)",
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Actualizar persona
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPersona(@PathVariable Long id,
                                               @Valid @RequestBody PersonaDTO personaDTO) {
        try {
            PersonaDTO personaActualizada = personaService.actualizarPersona(id, personaDTO);
            return new ResponseEntity<>(personaActualizada, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Actualizar licencia de conducción
    @PutMapping("/{id}/licencia")
    public ResponseEntity<?> actualizarLicenciaConduccion(@PathVariable Long id,
                                                          @Valid @RequestBody ActualizarLicenciaRequest request) {
        try {
            PersonaDTO personaActualizada = personaService.actualizarLicenciaConduccion(id, request);
            return new ResponseEntity<>(personaActualizada, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}