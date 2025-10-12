package com.gestionvehiculos.vehiculos.controller;

import com.gestionvehiculos.vehiculos.dto.DocumentoDTO;
import com.gestionvehiculos.vehiculos.service.DocumentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@CrossOrigin(origins = "*")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    // Crear documento
    @PostMapping
    public ResponseEntity<?> crearDocumento(@Valid @RequestBody DocumentoDTO documentoDTO) {
        try {
            DocumentoDTO documentoCreado = documentoService.crearDocumento(documentoDTO);
            return new ResponseEntity<>(documentoCreado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Obtener todos los documentos
    @GetMapping
    public ResponseEntity<List<DocumentoDTO>> obtenerTodosLosDocumentos() {
        List<DocumentoDTO> documentos = documentoService.obtenerTodosLosDocumentos();
        return new ResponseEntity<>(documentos, HttpStatus.OK);
    }

    // Obtener documento por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDocumentoPorId(@PathVariable Long id) {
        try {
            DocumentoDTO documento = documentoService.obtenerDocumentoPorId(id);
            return new ResponseEntity<>(documento, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Obtener documento por código
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> obtenerDocumentoPorCodigo(@PathVariable String codigo) {
        try {
            DocumentoDTO documento = documentoService.obtenerDocumentoPorCodigo(codigo);
            return new ResponseEntity<>(documento, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Actualizar documento
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDocumento(@PathVariable Long id,
                                                 @Valid @RequestBody DocumentoDTO documentoDTO) {
        try {
            DocumentoDTO documentoActualizado = documentoService.actualizarDocumento(id, documentoDTO);
            return new ResponseEntity<>(documentoActualizado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Eliminar documento
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDocumento(@PathVariable Long id) {
        try {
            documentoService.eliminarDocumento(id);
            return new ResponseEntity<>("Documento eliminado exitosamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}