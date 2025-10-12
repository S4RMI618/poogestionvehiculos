package com.gestionvehiculos.vehiculos.controller;

import com.gestionvehiculos.vehiculos.dto.CambiarPasswordRequest;
import com.gestionvehiculos.vehiculos.dto.UsuarioDTO;
import com.gestionvehiculos.vehiculos.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener usuario por login
    @GetMapping("/{login}")
    public ResponseEntity<?> obtenerUsuarioPorLogin(@PathVariable String login) {
        try {
            UsuarioDTO usuario = usuarioService.obtenerUsuarioPorLogin(login);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Cambiar password
    @PutMapping("/{login}/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@PathVariable String login,
                                             @Valid @RequestBody CambiarPasswordRequest request) {
        try {
            UsuarioDTO usuario = usuarioService.cambiarPassword(login, request);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Regenerar API Key
    @GetMapping("/{login}/regenerar-apikey")
    public ResponseEntity<?> regenerarApiKey(@PathVariable String login) {
        try {
            UsuarioDTO usuario = usuarioService.regenerarApiKey(login);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}