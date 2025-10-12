package com.gestionvehiculos.vehiculos.service;

import com.gestionvehiculos.vehiculos.dto.LoginRequest;
import com.gestionvehiculos.vehiculos.dto.LoginResponse;
import com.gestionvehiculos.vehiculos.dto.PersonaDTO;
import com.gestionvehiculos.vehiculos.model.Persona;
import com.gestionvehiculos.vehiculos.model.Usuario;
import com.gestionvehiculos.vehiculos.repository.PersonaRepository;
import com.gestionvehiculos.vehiculos.repository.UsuarioRepository;
import com.gestionvehiculos.vehiculos.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        // Buscar usuario
        Usuario usuario = usuarioRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        // Validar password
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Validar API Key
        if (!usuario.getApikey().equals(request.getApikey())) {
            throw new RuntimeException("API Key inválido");
        }

        // Obtener persona asociada
        Persona persona = personaRepository.findById(usuario.getIdPersona())
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        // Generar token JWT
        String token = jwtUtil.generateToken(usuario.getLogin(), usuario.getApikey());

        // Crear respuesta
        PersonaDTO personaDTO = new PersonaDTO(
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

        return new LoginResponse(token, usuario.getLogin(), usuario.getApikey(), personaDTO);
    }
}