package com.gestionvehiculos.vehiculos.service;

import com.gestionvehiculos.vehiculos.dto.DocumentoDTO;
import com.gestionvehiculos.vehiculos.model.Documento;
import com.gestionvehiculos.vehiculos.repository.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    // Crear documento
    public DocumentoDTO crearDocumento(DocumentoDTO documentoDTO) {
        if (documentoRepository.existsByCodigo(documentoDTO.getCodigo())) {
            throw new RuntimeException("Ya existe un documento con el código: " + documentoDTO.getCodigo());
        }

        Documento documento = convertirDTOaEntidad(documentoDTO);
        Documento documentoGuardado = documentoRepository.save(documento);
        return convertirEntidadaDTO(documentoGuardado);
    }

    // Obtener todos los documentos
    public List<DocumentoDTO> obtenerTodosLosDocumentos() {
        return documentoRepository.findAll().stream()
                .map(this::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }

    // Obtener documento por ID
    public DocumentoDTO obtenerDocumentoPorId(Long id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + id));
        return convertirEntidadaDTO(documento);
    }

    // Obtener documento por código
    public DocumentoDTO obtenerDocumentoPorCodigo(String codigo) {
        Documento documento = documentoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con código: " + codigo));
        return convertirEntidadaDTO(documento);
    }

    // Actualizar documento
    public DocumentoDTO actualizarDocumento(Long id, DocumentoDTO documentoDTO) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + id));

        // Verificar si el código ya existe en otro documento
        if (!documento.getCodigo().equals(documentoDTO.getCodigo()) &&
                documentoRepository.existsByCodigo(documentoDTO.getCodigo())) {
            throw new RuntimeException("Ya existe un documento con el código: " + documentoDTO.getCodigo());
        }

        documento.setCodigo(documentoDTO.getCodigo());
        documento.setNombre(documentoDTO.getNombre());
        documento.setTipoAplicacion(documentoDTO.getTipoAplicacion());
        documento.setTipoObligatoriedad(documentoDTO.getTipoObligatoriedad());
        documento.setDescripcion(documentoDTO.getDescripcion());

        Documento documentoActualizado = documentoRepository.save(documento);
        return convertirEntidadaDTO(documentoActualizado);
    }

    // Eliminar documento
    public void eliminarDocumento(Long id) {
        if (!documentoRepository.existsById(id)) {
            throw new RuntimeException("Documento no encontrado con ID: " + id);
        }
        documentoRepository.deleteById(id);
    }

    // Métodos de conversión
    private DocumentoDTO convertirEntidadaDTO(Documento documento) {
        return new DocumentoDTO(
                documento.getId(),
                documento.getCodigo(),
                documento.getNombre(),
                documento.getTipoAplicacion(),
                documento.getTipoObligatoriedad(),
                documento.getDescripcion()
        );
    }

    private Documento convertirDTOaEntidad(DocumentoDTO dto) {
        return new Documento(
                dto.getCodigo(),
                dto.getNombre(),
                dto.getTipoAplicacion(),
                dto.getTipoObligatoriedad(),
                dto.getDescripcion()
        );
    }
}