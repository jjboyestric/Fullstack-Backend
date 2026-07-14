package MIcroservicioB.MicroservicioUsuarios.service;

import MIcroservicioB.MicroservicioUsuarios.dto.LoginRequestDTO;
import MIcroservicioB.MicroservicioUsuarios.dto.LoginResponseDTO;
import MIcroservicioB.MicroservicioUsuarios.model.UsuarioModel;
import MIcroservicioB.MicroservicioUsuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para UsuarioService.
 * Patrón: AAA (Arrange–Act–Assert)
 * Herramientas: JUnit 5 + Mockito (@Mock / @InjectMocks)
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioModel usuarioProfesor;

    @BeforeEach
    void setUp() {
        usuarioProfesor = new UsuarioModel();
        usuarioProfesor.setId(1L);
        usuarioProfesor.setEmail("isidora.fernandez@profesor.cl");
        usuarioProfesor.setPassword("12345");
        usuarioProfesor.setRol(UsuarioModel.RolUsuario.PROFESOR);
        usuarioProfesor.setNombreCompleto("Isidora Fernández Rojas");
        usuarioProfesor.setRefId(1L);
    }

    // ── Test 1 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("login() con credenciales correctas debe retornar el DTO del usuario")
    void testLoginCredencialesCorrectas() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("isidora.fernandez@profesor.cl");
        request.setPassword("12345");

        when(usuarioRepository.findByEmail("isidora.fernandez@profesor.cl")).thenReturn(Optional.of(usuarioProfesor));

        Optional<LoginResponseDTO> resultado = usuarioService.login(request);

        assertTrue(resultado.isPresent());
        assertEquals("PROFESOR", resultado.get().getRol());
        assertEquals(1L, resultado.get().getRefId());
        System.out.println("TEST PASS: login correcto para " + resultado.get().getEmail());
    }

    // ── Test 2 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("login() con contraseña incorrecta debe retornar Optional vacío")
    void testLoginPasswordIncorrecta() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("isidora.fernandez@profesor.cl");
        request.setPassword("clave-mala");

        when(usuarioRepository.findByEmail("isidora.fernandez@profesor.cl")).thenReturn(Optional.of(usuarioProfesor));

        Optional<LoginResponseDTO> resultado = usuarioService.login(request);

        assertTrue(resultado.isEmpty());
        System.out.println("TEST PASS: login con password incorrecta fue rechazado.");
    }

    // ── Test 3 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("login() con email inexistente debe retornar Optional vacío")
    void testLoginEmailInexistente() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("no-existe@alumno.cl");
        request.setPassword("12345");

        when(usuarioRepository.findByEmail("no-existe@alumno.cl")).thenReturn(Optional.empty());

        Optional<LoginResponseDTO> resultado = usuarioService.login(request);

        assertTrue(resultado.isEmpty());
        verify(usuarioRepository, times(1)).findByEmail("no-existe@alumno.cl");
        System.out.println("TEST PASS: login con email inexistente fue rechazado.");
    }

    // ── Test 4 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("login() de un DIRECTOR debe retornar refId nulo")
    void testLoginDirectorRefIdNulo() {
        UsuarioModel director = new UsuarioModel();
        director.setId(99L);
        director.setEmail("ricardo.gomez@director.cl");
        director.setPassword("12345");
        director.setRol(UsuarioModel.RolUsuario.DIRECTOR);
        director.setNombreCompleto("Ricardo Gómez Alarcón");
        director.setRefId(null);

        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("ricardo.gomez@director.cl");
        request.setPassword("12345");

        when(usuarioRepository.findByEmail("ricardo.gomez@director.cl")).thenReturn(Optional.of(director));

        Optional<LoginResponseDTO> resultado = usuarioService.login(request);

        assertTrue(resultado.isPresent());
        assertEquals("DIRECTOR", resultado.get().getRol());
        assertNull(resultado.get().getRefId());
        System.out.println("TEST PASS: login de director con refId nulo, rol=" + resultado.get().getRol());
    }
}
