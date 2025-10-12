package com.gestionvehiculos.vehiculos.service;

import com.gestionvehiculos.vehiculos.dto.CambiarPasswordRequest;
import com.gestionvehiculos.vehiculos.dto.UsuarioDTO;
import com.gestionvehiculos.vehiculos.model.Usuario;
import com.gestionvehiculos.vehiculos.repository.UsuarioRepository;
import com.gestionvehiculos.vehiculos.util.GeneradorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Obtener usuario por login
    public UsuarioDTO obtenerUsuarioPorLogin(String login) {
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con login: " + login));
        return convertirEntidadaDTO(usuario);
    }

    // Cambiar password de un usuario
    public UsuarioDTO cambiarPassword(String login, CambiarPasswordRequest request) {
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con login: " + login));

        String nuevaPasswordEncriptada = passwordEncoder.encode(request.getNuevaPassword());
        usuario.setPassword(nuevaPasswordEncriptada);

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirEntidadaDTO(usuarioActualizado);
    }

    // Regenerar API Key
    public UsuarioDTO regenerarApiKey(String login) {
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con login: " + login));

        String nuevoApiKey = GeneradorUtil.generarApiKey();
        usuario.setApikey(nuevoApiKey);

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirEntidadaDTO(usuarioActualizado);
    }

    // Validar usuario y API Key
    public boolean validarUsuarioYApiKey(String login, String apikey) {
        Usuario usuario = usuarioRepository.findByLogin(login).orElse(null);
        if (usuario == null) {
            return false;
        }
        return usuario.getApikey().equals(apikey);
    }

    // Método de conversión
    private UsuarioDTO convertirEntidadaDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO(
                usuario.getLogin(),
                usuario.getIdPersona(),
                null, // No devolver password
                usuario.getApikey()
        );
        return dto;
    }
}