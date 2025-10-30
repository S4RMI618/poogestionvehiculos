package com.gestionvehiculos.vehiculos.controller;

import com.gestionvehiculos.vehiculos.dto.CargueResultadoDTO;
import com.gestionvehiculos.vehiculos.dto.TrayectoTmpDTO;
import com.gestionvehiculos.vehiculos.service.CargueExcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cargue")
@CrossOrigin(origins = "*")
public class CargueController {

    private static final Logger logger = LoggerFactory.getLogger(CargueController.class);

    @Autowired
    private CargueExcelService cargueExcelService;

    /**
     * Endpoint para cargar archivo Excel completo
     * POST /api/cargue/archivo
     */
    @PostMapping("/archivo")
    public ResponseEntity<?> cargarArchivoExcel(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            String loginUsuario = authentication.getName();
            logger.info("Usuario {} iniciando carga de archivo: {}", loginUsuario, file.getOriginalFilename());

            // Procesar archivo completo (Carga -> Validación -> Procesamiento)
            CargueResultadoDTO resultado = cargueExcelService.procesarArchivoCompleto(file, loginUsuario);

            logger.info("Carga completada exitosamente. ID Cargue: {}", resultado.getIdCargue());

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            logger.error("Error al procesar archivo Excel: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("mensaje", "No se pudo procesar el archivo Excel");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Endpoint para consultar un cargue específico
     * GET /api/cargue/{idCargue}
     */
    @GetMapping("/{idCargue}")
    public ResponseEntity<?> consultarCargue(@PathVariable Long idCargue) {
        try {
            List<TrayectoTmpDTO> registros = cargueExcelService.consultarCargue(idCargue);

            if (registros.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontraron registros para el cargue: " + idCargue);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            return ResponseEntity.ok(registros);

        } catch (Exception e) {
            logger.error("Error al consultar cargue: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint para consultar resumen de un cargue
     * GET /api/cargue/{idCargue}/resumen
     */
    @GetMapping("/{idCargue}/resumen")
    public ResponseEntity<?> consultarResumenCargue(@PathVariable Long idCargue) {
        try {
            CargueResultadoDTO resultado = cargueExcelService.generarResultadoCargue(idCargue);
            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            logger.error("Error al consultar resumen de cargue: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Endpoint para listar todos los cargues realizados
     * GET /api/cargue/listar
     */
    @GetMapping("/listar")
    public ResponseEntity<?> listarCargues() {
        try {
            List<Long> idsCargues = cargueExcelService.obtenerTodosCargues();

            Map<String, Object> response = new HashMap<>();
            response.put("totalCargues", idsCargues.size());
            response.put("cargues", idsCargues);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al listar cargues: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint de prueba para verificar el servicio
     * GET /api/cargue/test
     */
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Servicio de cargue funcionando correctamente");
        response.put("version", "1.0");
        return ResponseEntity.ok(response);
    }
}
