package MIcroservicioB.MicroservicioUsuarios.service;

import MIcroservicioB.MicroservicioUsuarios.dto.LoginRequestDTO;
import MIcroservicioB.MicroservicioUsuarios.dto.LoginResponseDTO;
import MIcroservicioB.MicroservicioUsuarios.model.UsuarioModel;
import MIcroservicioB.MicroservicioUsuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Autentica un usuario por email/password.
     * Devuelve Optional.empty() si las credenciales no son válidas.
     */
    public Optional<LoginResponseDTO> login(LoginRequestDTO request) {
        System.out.println("LOG: Intento de login para: " + request.getEmail());
        return usuarioRepository.findByEmail(request.getEmail())
                .filter(u -> u.getPassword().equals(request.getPassword()))
                .map(this::toDTO);
    }

    private LoginResponseDTO toDTO(UsuarioModel u) {
        return new LoginResponseDTO(u.getId(), u.getEmail(), u.getNombreCompleto(), u.getRol().name(), u.getRefId());
    }
}
