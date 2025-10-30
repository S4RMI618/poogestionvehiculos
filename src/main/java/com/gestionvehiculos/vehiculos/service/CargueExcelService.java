package com.gestionvehiculos.vehiculos.service;

import com.gestionvehiculos.vehiculos.dto.CargueResultadoDTO;
import com.gestionvehiculos.vehiculos.dto.TrayectoTmpDTO;
import com.gestionvehiculos.vehiculos.enums.EstadoCargue;
import com.gestionvehiculos.vehiculos.model.TrayectoTmp;
import com.gestionvehiculos.vehiculos.repository.TrayectoTmpRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CargueExcelService {

    private static final Logger logger = LoggerFactory.getLogger(CargueExcelService.class);

    @Autowired
    private TrayectoTmpRepository trayectoTmpRepository;

    @Autowired
    private ValidacionCargueService validacionCargueService;

    /**
     * Procesar archivo Excel completo: Carga -> Validación -> Procesamiento
     */
    public CargueResultadoDTO procesarArchivoCompleto(MultipartFile file, String loginUsuario) {
        try {
            // 1. Cargar datos desde Excel a TrayectoTmp
            Long idCargue = cargarDesdeExcel(file, loginUsuario);

            // 2. Validar registros
            validacionCargueService.validarCargue(idCargue);

            // 3. Procesar registros válidos
            validacionCargueService.procesarCargue(idCargue);

            // 4. Generar resultado
            return generarResultadoCargue(idCargue);

        } catch (Exception e) {
            logger.error("Error al procesar archivo Excel: {}", e.getMessage());
            throw new RuntimeException("Error al procesar el archivo: " + e.getMessage());
        }
    }

    /**
     * PASO 1: Cargar datos desde Excel a tabla temporal (sin validación)
     */
    public Long cargarDesdeExcel(MultipartFile file, String loginUsuario) throws IOException {
        // Validar archivo
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        if (!file.getOriginalFilename().endsWith(".xlsx") && !file.getOriginalFilename().endsWith(".xls")) {
            throw new RuntimeException("El archivo debe ser de tipo Excel (.xlsx o .xls)");
        }

        // Generar ID de cargue (formato: yyyyMMddHHmm)
        Long idCargue = generarIdCargue();
        logger.info("Iniciando carga de archivo Excel. ID Cargue: {}", idCargue);

        int registrosCargados = 0;
        int registrosConError = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Saltar la fila de encabezados (row 0)
            int primeraFila = sheet.getFirstRowNum() + 1;
            int ultimaFila = sheet.getLastRowNum();

            logger.info("Total de filas a procesar: {}", (ultimaFila - primeraFila + 1));

            for (int i = primeraFila; i <= ultimaFila; i++) {
                Row row = sheet.getRow(i);

                if (row == null || esFilaVacia(row)) {
                    continue;
                }

                try {
                    TrayectoTmp trayectoTmp = new TrayectoTmp();
                    trayectoTmp.setIdCargue(idCargue);
                    trayectoTmp.setLoginUsuarioRegistro(loginUsuario);
                    trayectoTmp.setEstado(EstadoCargue.CARGADO);

                    // Leer datos de las columnas (ajustar índices según tu plantilla)
                    trayectoTmp.setConductorId(obtenerValorCelda(row, 0));
                    trayectoTmp.setVehiculoId(obtenerValorCelda(row, 1));
                    trayectoTmp.setCodigoRuta(obtenerValorCelda(row, 2));
                    trayectoTmp.setUbicacion(obtenerValorCelda(row, 3));
                    trayectoTmp.setOrdenParada(obtenerValorCelda(row, 4));
                    trayectoTmp.setLatitud(obtenerValorCelda(row, 5));
                    trayectoTmp.setLongitud(obtenerValorCelda(row, 6));

                    trayectoTmpRepository.save(trayectoTmp);
                    registrosCargados++;

                } catch (Exception e) {
                    registrosConError++;
                    logger.warn("Error al cargar fila {}: {}", i, e.getMessage());
                }
            }
        }

        logger.info("Carga completada. Registros cargados: {}, Errores: {}", registrosCargados, registrosConError);
        return idCargue;
    }

    /**
     * Generar ID de cargue basado en fecha y hora (yyyyMMddHHmm)
     */
    private Long generarIdCargue() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String idString = ahora.format(formatter);
        return Long.parseLong(idString);
    }

    /**
     * Obtener valor de celda como String
     */
    private String obtenerValorCelda(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                // Convertir número a string sin notación científica
                double numValue = cell.getNumericCellValue();
                if (numValue == (long) numValue) {
                    return String.valueOf((long) numValue);
                }
                return String.valueOf(numValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    /**
     * Verificar si una fila está vacía
     */
    private boolean esFilaVacia(Row row) {
        if (row == null) {
            return true;
        }

        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = obtenerValorCelda(row, i);
                if (!value.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Generar resultado del cargue con estadísticas
     */
    public CargueResultadoDTO generarResultadoCargue(Long idCargue) {
        List<TrayectoTmp> registros = trayectoTmpRepository.findByIdCargue(idCargue);

        if (registros.isEmpty()) {
            throw new RuntimeException("No se encontraron registros para el cargue: " + idCargue);
        }

        // Contar por estado
        Map<EstadoCargue, Long> conteos = registros.stream()
                .collect(Collectors.groupingBy(TrayectoTmp::getEstado, Collectors.counting()));

        Integer totalRegistros = registros.size();
        Integer cargados = conteos.getOrDefault(EstadoCargue.CARGADO, 0L).intValue();
        Integer validados = conteos.getOrDefault(EstadoCargue.VALIDADO, 0L).intValue();
        Integer procesados = conteos.getOrDefault(EstadoCargue.PROCESADO, 0L).intValue();
        Integer errores = conteos.getOrDefault(EstadoCargue.ERROR, 0L).intValue();

        Map<String, Integer> resumen = new HashMap<>();
        resumen.put("CARGADO", cargados);
        resumen.put("VALIDADO", validados);
        resumen.put("PROCESADO", procesados);
        resumen.put("ERROR", errores);

        String mensaje = String.format(
                "Cargue completado. Total: %d | Procesados: %d | Errores: %d",
                totalRegistros, procesados, errores
        );

        LocalDateTime fechaCargue = registros.get(0).getFechaCargue();

        return new CargueResultadoDTO(
                idCargue,
                fechaCargue,
                totalRegistros,
                cargados,
                validados,
                procesados,
                errores,
                mensaje,
                resumen
        );
    }

    /**
     * Consultar registros de un cargue específico
     */
    public List<TrayectoTmpDTO> consultarCargue(Long idCargue) {
        List<TrayectoTmp> registros = trayectoTmpRepository.findByIdCargue(idCargue);

        return registros.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener todos los IDs de cargues realizados
     */
    public List<Long> obtenerTodosCargues() {
        return trayectoTmpRepository.findAllIdCargues();
    }

    /**
     * Convertir entidad a DTO
     */
    private TrayectoTmpDTO convertirADTO(TrayectoTmp entidad) {
        TrayectoTmpDTO dto = new TrayectoTmpDTO();
        dto.setId(entidad.getId());
        dto.setConductorId(entidad.getConductorId());
        dto.setVehiculoId(entidad.getVehiculoId());
        dto.setCodigoRuta(entidad.getCodigoRuta());
        dto.setUbicacion(entidad.getUbicacion());
        dto.setOrdenParada(entidad.getOrdenParada());
        dto.setLatitud(entidad.getLatitud());
        dto.setLongitud(entidad.getLongitud());
        dto.setLoginUsuarioRegistro(entidad.getLoginUsuarioRegistro());
        dto.setEstado(entidad.getEstado());
        dto.setObservacion(entidad.getObservacion());
        dto.setIdCargue(entidad.getIdCargue());
        dto.setFechaCargue(entidad.getFechaCargue());
        return dto;
    }
}