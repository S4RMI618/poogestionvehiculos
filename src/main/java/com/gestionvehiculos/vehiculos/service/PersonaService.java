package com.gestionvehiculos.vehiculos.service;

import com.gestionvehiculos.vehiculos.dto.ActualizarLicenciaRequest;
import com.gestionvehiculos.vehiculos.dto.PersonaDTO;
import com.gestionvehiculos.vehiculos.dto.UsuarioDTO;
import com.gestionvehiculos.vehiculos.enums.TipoPersona;
import com.gestionvehiculos.vehiculos.model.Persona;
import com.gestionvehiculos.vehiculos.model.Usuario;
import com.gestionvehiculos.vehiculos.repository.PersonaRepository;
import com.gestionvehiculos.vehiculos.repository.UsuarioRepository;
import com.gestionvehiculos.vehiculos.util.GeneradorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Crear persona (y usuario si es ADMINISTRATIVO)
    public Map<String, Object> crearPersona(PersonaDTO personaDTO) {
        // Validar que no exista la identificación
        if (personaRepository.existsByIdentificacion(personaDTO.getIdentificacion())) {
            throw new RuntimeException("Ya existe una persona con la identificación: " + personaDTO.getIdentificacion());
        }

        // Validar que no exista el correo
        if (personaRepository.existsByCorreoElectronico(personaDTO.getCorreoElectronico())) {
            throw new RuntimeException("Ya existe una persona con el correo electrónico: " + personaDTO.getCorreoElectronico());
        }

        // Crear persona
        Persona persona = convertirDTOaEntidad(personaDTO);
        Persona personaGuardada = personaRepository.save(persona);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("persona", convertirEntidadaDTO(personaGuardada));

        // Si es ADMINISTRATIVO, crear usuario
        if (personaGuardada.getTipoPersona() == TipoPersona.A) {
            Map<String, Object> datosUsuario = crearUsuarioParaAdministrativo(personaGuardada);

            Usuario usuario = (Usuario) datosUsuario.get("usuario");
            String passwordPlano = (String) datosUsuario.get("passwordPlano");

            Usuario usuarioGuardado = usuarioRepository.save(usuario);

            UsuarioDTO usuarioDTO = convertirUsuarioEntidadaDTO(usuarioGuardado);
            respuesta.put("usuario", usuarioDTO);
            respuesta.put("passwordGenerado", passwordPlano); // Password en texto plano
        }

        return respuesta;
    }

    // Crear usuario para persona administrativa
    private Map<String, Object> crearUsuarioParaAdministrativo(Persona persona) {
        String login = GeneradorUtil.generarLogin(
                persona.getNombres(),
                persona.getApellidos(),
                persona.getIdentificacion()
        );

        // Verificar que el login no exista
        if (usuarioRepository.existsByLogin(login)) {
            throw new RuntimeException("El login generado ya existe: " + login);
        }

        // Generar password plano y encriptado
        String passwordPlano = GeneradorUtil.generarPassword();
        String passwordEncriptado = passwordEncoder.encode(passwordPlano);
        String apikey = GeneradorUtil.generarApiKey();

        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setIdPersona(persona.getId());
        usuario.setPassword(passwordEncriptado); // Guardar encriptado en BD
        usuario.setApikey(apikey);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("usuario", usuario);
        resultado.put("passwordPlano", passwordPlano); // Retornar plano para mostrar al usuario

        return resultado;
    }

    // Obtener todas las personas
    public List<PersonaDTO> obtenerTodasLasPersonas() {
        return personaRepository.findAll().stream()
                .map(this::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }

    // Obtener persona por ID
    public PersonaDTO obtenerPersonaPorId(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + id));
        return convertirEntidadaDTO(persona);
    }

    // Obtener persona por identificación
    public PersonaDTO obtenerPersonaPorIdentificacion(String identificacion) {
        Persona persona = personaRepository.findByIdentificacion(identificacion)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con identificación: " + identificacion));
        return convertirEntidadaDTO(persona);
    }

    // Obtener personas por tipo
    public List<PersonaDTO> obtenerPersonasPorTipo(TipoPersona tipoPersona) {
        return personaRepository.findByTipoPersona(tipoPersona).stream()
                .map(this::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }

    // Actualizar persona
    public PersonaDTO actualizarPersona(Long id, PersonaDTO personaDTO) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + id));

        // Validar que no exista otra persona con la misma identificación
        if (!persona.getIdentificacion().equals(personaDTO.getIdentificacion()) &&
                personaRepository.existsByIdentificacion(personaDTO.getIdentificacion())) {
            throw new RuntimeException("Ya existe una persona con la identificación: " + personaDTO.getIdentificacion());
        }

        // Validar que no exista otra persona con el mismo correo
        if (!persona.getCorreoElectronico().equals(personaDTO.getCorreoElectronico()) &&
                personaRepository.existsByCorreoElectronico(personaDTO.getCorreoElectronico())) {
            throw new RuntimeException("Ya existe una persona con el correo electrónico: " + personaDTO.getCorreoElectronico());
        }

        persona.setIdentificacion(personaDTO.getIdentificacion());
        persona.setTipoIdentificacion(personaDTO.getTipoIdentificacion());
        persona.setNombres(personaDTO.getNombres());
        persona.setApellidos(personaDTO.getApellidos());
        persona.setCorreoElectronico(personaDTO.getCorreoElectronico());
        persona.setTipoPersona(personaDTO.getTipoPersona());

        Persona personaActualizada = personaRepository.save(persona);
        return convertirEntidadaDTO(personaActualizada);
    }

    // Contar personas por tipo
    public Map<String, Long> contarPersonasPorTipo() {
        List<Object[]> resultados = personaRepository.countByTipoPersona();
        Map<String, Long> conteo = new HashMap<>();

        for (Object[] resultado : resultados) {
            TipoPersona tipo = (TipoPersona) resultado[0];
            Long cantidad = (Long) resultado[1];
            conteo.put(tipo.name(), cantidad);
        }

        return conteo;
    }

    // Actualizar licencia de conducción
    public PersonaDTO actualizarLicenciaConduccion(Long personaId, ActualizarLicenciaRequest request) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + personaId));

        if (persona.getTipoPersona() != TipoPersona.C) {
            throw new RuntimeException("Solo las personas de tipo CONDUCTOR pueden tener licencia de conducción");
        }

        persona.setLicenciaConduccionBase64(request.getLicenciaConduccionBase64());
        persona.setFechaVigenciaLicencia(request.getFechaVigenciaLicencia());

        Persona personaActualizada = personaRepository.save(persona);
        return convertirEntidadaDTO(personaActualizada);
    }

    // Obtener conductores con licencia vencida (para tarea programada)
    public List<Persona> obtenerConductoresConLicenciaVencida() {
        List<Persona> conductores = personaRepository.findByTipoPersona(TipoPersona.C);
        LocalDate hoy = LocalDate.now();

        return conductores.stream()
                .filter(c -> c.getFechaVigenciaLicencia() != null && c.getFechaVigenciaLicencia().isBefore(hoy))
                .collect(Collectors.toList());
    }

    // Métodos de conversión
    private PersonaDTO convertirEntidadaDTO(Persona persona) {
        return new PersonaDTO(
                persona.getId(),
                persona.getIdentificacion(),
                persona.getTipoIdentificacion(),
                persona.getNombres(),
                persona.getApellidos(),
                persona.getCorreoElectronico(),
                persona.getTipoPersona(),
                persona.getLicenciaConduccionBase64(),
                persona.getFechaVigenciaLicencia()
        );
    }

    private Persona convertirDTOaEntidad(PersonaDTO dto) {
        return new Persona(
                dto.getIdentificacion(),
                dto.getTipoIdentificacion(),
                dto.getNombres(),
                dto.getApellidos(),
                dto.getCorreoElectronico(),
                dto.getTipoPersona()
        );
    }

    private UsuarioDTO convertirUsuarioEntidadaDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO(
                usuario.getLogin(),
                usuario.getIdPersona(),
                null, // No devolver password
                usuario.getApikey()
        );

        if (usuario.getPersona() != null) {
            dto.setPersona(convertirEntidadaDTO(usuario.getPersona()));
        }

        return dto;
    }
}