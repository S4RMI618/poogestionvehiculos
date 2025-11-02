package com.gestionvehiculos.vehiculos.service;

import com.gestionvehiculos.vehiculos.dto.CrearVehiculoRequest;
import com.gestionvehiculos.vehiculos.dto.VehiculoConductorDTO;
import com.gestionvehiculos.vehiculos.dto.VehiculoDTO;
import com.gestionvehiculos.vehiculos.dto.VehiculoDocumentoDTO;
import com.gestionvehiculos.vehiculos.model.Documento;
import com.gestionvehiculos.vehiculos.model.Vehiculo;
import com.gestionvehiculos.vehiculos.model.VehiculoConductor;
import com.gestionvehiculos.vehiculos.model.VehiculoDocumento;
import com.gestionvehiculos.vehiculos.enums.EstadoDocumento;
import com.gestionvehiculos.vehiculos.enums.TipoVehiculo;
import com.gestionvehiculos.vehiculos.repository.DocumentoRepository;
import com.gestionvehiculos.vehiculos.repository.VehiculoConductorRepository;
import com.gestionvehiculos.vehiculos.repository.VehiculoDocumentoRepository;
import com.gestionvehiculos.vehiculos.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.time.LocalDate;

@Service
@Transactional
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private VehiculoDocumentoRepository vehiculoDocumentoRepository;

    @Autowired
    private VehiculoConductorRepository vehiculoConductorRepository;

    // Crear vehículo con documentos
    public VehiculoDTO crearVehiculo(CrearVehiculoRequest request) {
        // Validar que la placa no exista
        if (vehiculoRepository.existsByPlaca(request.getVehiculo().getPlaca())) {
            throw new RuntimeException("Ya existe un vehículo con la placa: " + request.getVehiculo().getPlaca());
        }

        // Validar formato de placa según tipo de vehículo
        validarFormatoPlaca(request.getVehiculo().getTipoVehiculo(), request.getVehiculo().getPlaca());

        // Crear vehículo
        Vehiculo vehiculo = convertirDTOaEntidad(request.getVehiculo());
        Vehiculo vehiculoGuardado = vehiculoRepository.save(vehiculo);

        // Crear documentos asociados con estado "EN_VERIFICACION"
        for (VehiculoDocumentoDTO docDTO : request.getDocumentos()) {
            Documento documento = documentoRepository.findById(docDTO.getDocumentoId())
                    .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + docDTO.getDocumentoId()));

            VehiculoDocumento vehiculoDocumento = new VehiculoDocumento(
                    vehiculoGuardado,
                    documento,
                    docDTO.getFechaExpedicion(),
                    docDTO.getFechaVencimiento(),
                    EstadoDocumento.EN_VERIFICACION
            );

            vehiculoDocumentoRepository.save(vehiculoDocumento);
        }

        return obtenerVehiculoPorId(vehiculoGuardado.getId());
    }

    // Obtener todos los vehículos
    public List<VehiculoDTO> obtenerTodosLosVehiculos() {
        return vehiculoRepository.findAll().stream()
                .map(this::convertirEntidadaDTOConDocumentos)
                .collect(Collectors.toList());
    }

    // Obtener vehículo por ID
    public VehiculoDTO obtenerVehiculoPorId(Long id) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + id));
        return convertirEntidadaDTOConDocumentos(vehiculo);
    }

    // Buscar vehículo por placa
    public VehiculoDTO buscarPorPlaca(String placa) {
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con placa: " + placa));
        return convertirEntidadaDTOConDocumentos(vehiculo);
    }

    // Buscar vehículos por tipo
    public List<VehiculoDTO> buscarPorTipoVehiculo(TipoVehiculo tipoVehiculo) {
        return vehiculoRepository.findByTipoVehiculo(tipoVehiculo).stream()
                .map(this::convertirEntidadaDTOConDocumentos)
                .collect(Collectors.toList());
    }

    // Buscar vehículos que tienen un documento específico
    public List<VehiculoDTO> buscarPorDocumento(Long documentoId) {
        return vehiculoRepository.findByDocumentoId(documentoId).stream()
                .map(this::convertirEntidadaDTOConDocumentos)
                .collect(Collectors.toList());
    }

    // Buscar vehículos por estado de documento
    public List<VehiculoDTO> buscarPorEstadoDocumento(EstadoDocumento estado) {
        return vehiculoDocumentoRepository.findVehiculosByEstado(estado).stream()
                .map(this::convertirEntidadaDTOConDocumentos)
                .collect(Collectors.toList());
    }

    // Actualizar vehículo
    public VehiculoDTO actualizarVehiculo(Long id, VehiculoDTO vehiculoDTO) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + id));

        // Validar que la placa no exista en otro vehículo
        if (!vehiculo.getPlaca().equals(vehiculoDTO.getPlaca()) &&
                vehiculoRepository.existsByPlaca(vehiculoDTO.getPlaca())) {
            throw new RuntimeException("Ya existe un vehículo con la placa: " + vehiculoDTO.getPlaca());
        }

        // Validar formato de placa según tipo de vehículo
        validarFormatoPlaca(vehiculoDTO.getTipoVehiculo(), vehiculoDTO.getPlaca());

        vehiculo.setTipoVehiculo(vehiculoDTO.getTipoVehiculo());
        vehiculo.setPlaca(vehiculoDTO.getPlaca().toUpperCase());
        vehiculo.setTipoServicio(vehiculoDTO.getTipoServicio());
        vehiculo.setTipoCombustible(vehiculoDTO.getTipoCombustible());
        vehiculo.setCapacidadPasajeros(vehiculoDTO.getCapacidadPasajeros());
        vehiculo.setColor(vehiculoDTO.getColor());
        vehiculo.setModelo(vehiculoDTO.getModelo());
        vehiculo.setMarca(vehiculoDTO.getMarca());
        vehiculo.setLinea(vehiculoDTO.getLinea());

        Vehiculo vehiculoActualizado = vehiculoRepository.save(vehiculo);
        return convertirEntidadaDTOConDocumentos(vehiculoActualizado);
    }

    // Eliminar vehículo
    public void eliminarVehiculo(Long id) {
        if (!vehiculoRepository.existsById(id)) {
            throw new RuntimeException("Vehículo no encontrado con ID: " + id);
        }
        vehiculoRepository.deleteById(id);
    }

    // Agregar documento a vehículo
    public VehiculoDocumentoDTO agregarDocumentoAVehiculo(Long vehiculoId, VehiculoDocumentoDTO documentoDTO) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + vehiculoId));

        Documento documento = documentoRepository.findById(documentoDTO.getDocumentoId())
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + documentoDTO.getDocumentoId()));

        // Verificar si ya existe la relación
        if (vehiculoDocumentoRepository.findByVehiculoIdAndDocumentoId(vehiculoId, documentoDTO.getDocumentoId()).isPresent()) {
            throw new RuntimeException("El vehículo ya tiene asociado este documento");
        }

        VehiculoDocumento vehiculoDocumento = new VehiculoDocumento(
                vehiculo,
                documento,
                documentoDTO.getFechaExpedicion(),
                documentoDTO.getFechaVencimiento(),
                documentoDTO.getEstado() != null ? documentoDTO.getEstado() : EstadoDocumento.EN_VERIFICACION
        );

        VehiculoDocumento guardado = vehiculoDocumentoRepository.save(vehiculoDocumento);
        return convertirVehiculoDocumentoaDTO(guardado);
    }
    // Buscar vehículos con documentos vencidos
    public List<VehiculoDTO> buscarVehiculosConDocumentosVencidos() {
        return vehiculoRepository.findVehiculosConDocumentosVencidos().stream()
                .map(this::convertirEntidadaDTOConDocumentos)
                .collect(Collectors.toList());
    }

    // Buscar vehículos con documentos por vencer
    public List<VehiculoDTO> buscarVehiculosConDocumentosPorVencer(int diasAnticipacion) {
        LocalDate fechaLimite = LocalDate.now().plusDays(diasAnticipacion);
        return vehiculoRepository.findVehiculosConDocumentosPorVencer(fechaLimite).stream()
                .map(this::convertirEntidadaDTOConDocumentos)
                .collect(Collectors.toList());
    }

    // Buscar vehículo por placa con conductores y documentos
    public Map<String, Object> buscarVehiculoCompletoPorPlaca(String placa) {
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con placa: " + placa));

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("vehiculo", convertirEntidadaDTOConDocumentos(vehiculo));

        // Obtener conductores asociados
        List<VehiculoConductorDTO> conductores = vehiculoConductorRepository
                .findByVehiculoId(vehiculo.getId()).stream()
                .map(this::convertirVehiculoConductoraDTO)
                .collect(Collectors.toList());
        resultado.put("conductores", conductores);

        return resultado;
    }

    /**
     * Agregar documentos a un vehículo existente
     */
    public Vehiculo agregarDocumentos(Long vehiculoId, List<VehiculoDocumentoDTO> documentosDTO) {
        // Verificar que el vehículo existe
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + vehiculoId));

        // Validar que se envíen documentos
        if (documentosDTO == null || documentosDTO.isEmpty()) {
            throw new RuntimeException("Debe enviar al menos un documento");
        }

        // Agregar cada documento
        for (VehiculoDocumentoDTO docDTO : documentosDTO) {
            // Validar que el documento existe
            Documento documento = documentoRepository.findById(docDTO.getDocumentoId())
                    .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + docDTO.getDocumentoId()));

            // Verificar si el documento ya está asociado al vehículo
            boolean yaExiste = vehiculoDocumentoRepository
                    .findByVehiculoIdAndDocumentoId(vehiculoId, docDTO.getDocumentoId())
                    .isPresent();

            if (yaExiste) {
                throw new RuntimeException("El documento " + documento.getNombre() + " ya está asociado a este vehículo");
            }

            // Crear la asociación
            VehiculoDocumento vehiculoDocumento = new VehiculoDocumento();
            vehiculoDocumento.setVehiculo(vehiculo);
            vehiculoDocumento.setDocumento(documento);
            vehiculoDocumento.setFechaExpedicion(docDTO.getFechaExpedicion());
            vehiculoDocumento.setFechaVencimiento(docDTO.getFechaVencimiento());
            vehiculoDocumento.setEstado(EstadoDocumento.HABILITADO); // Por defecto habilitado

            vehiculoDocumentoRepository.save(vehiculoDocumento);
        }

        // Retornar el vehículo actualizado
        return vehiculoRepository.findById(vehiculoId).orElseThrow();
    }

    // Método auxiliar para convertir VehiculoConductor a DTO
    private VehiculoConductorDTO convertirVehiculoConductoraDTO(VehiculoConductor vc) {
        String conductorNombre = vc.getConductor().getNombres() + " " + vc.getConductor().getApellidos();
        String vehiculoPlaca = vc.getVehiculo().getPlaca();

        return new VehiculoConductorDTO(
                vc.getId(),
                vc.getVehiculo().getId(),
                vc.getConductor().getId(),
                conductorNombre,
                vehiculoPlaca,
                vc.getFechaAsociacion(),
                vc.getEstado()
        );
    }

    // Validar formato de placa
    private void validarFormatoPlaca(TipoVehiculo tipoVehiculo, String placa) {
        String placaUpper = placa.toUpperCase();

        if (tipoVehiculo == TipoVehiculo.AUTOMOVIL) {
            // 3 letras + 3 números
            if (!placaUpper.matches("^[A-Z]{3}[0-9]{3}$")) {
                throw new RuntimeException("Formato de placa inválido para automóvil. Debe ser 3 letras seguidas de 3 números");
            }
        } else if (tipoVehiculo == TipoVehiculo.MOTOCICLETA) {
            // 3 letras + 2 números + 1 letra
            if (!placaUpper.matches("^[A-Z]{3}[0-9]{2}[A-Z]$")) {
                throw new RuntimeException("Formato de placa inválido para motocicleta. Debe ser 3 letras, 2 números y 1 letra");
            }
        }
    }

    // Métodos de conversión
    private VehiculoDTO convertirEntidadaDTOConDocumentos(Vehiculo vehiculo) {
        VehiculoDTO dto = new VehiculoDTO(
                vehiculo.getId(),
                vehiculo.getTipoVehiculo(),
                vehiculo.getPlaca(),
                vehiculo.getTipoServicio(),
                vehiculo.getTipoCombustible(),
                vehiculo.getCapacidadPasajeros(),
                vehiculo.getColor(),
                vehiculo.getModelo(),
                vehiculo.getMarca(),
                vehiculo.getLinea()
        );

        List<VehiculoDocumentoDTO> documentos = vehiculoDocumentoRepository
                .findByVehiculoId(vehiculo.getId()).stream()
                .map(this::convertirVehiculoDocumentoaDTO)
                .collect(Collectors.toList());

        dto.setDocumentos(documentos);
        return dto;
    }

    private Vehiculo convertirDTOaEntidad(VehiculoDTO dto) {
        return new Vehiculo(
                dto.getTipoVehiculo(),
                dto.getPlaca().toUpperCase(),
                dto.getTipoServicio(),
                dto.getTipoCombustible(),
                dto.getCapacidadPasajeros(),
                dto.getColor(),
                dto.getModelo(),
                dto.getMarca(),
                dto.getLinea()
        );
    }

    private VehiculoDocumentoDTO convertirVehiculoDocumentoaDTO(VehiculoDocumento vd) {
        return new VehiculoDocumentoDTO(
                vd.getId(),
                vd.getDocumento().getId(),
                vd.getDocumento().getNombre(),
                vd.getFechaExpedicion(),
                vd.getFechaVencimiento(),
                vd.getEstado()
        );
    }


}