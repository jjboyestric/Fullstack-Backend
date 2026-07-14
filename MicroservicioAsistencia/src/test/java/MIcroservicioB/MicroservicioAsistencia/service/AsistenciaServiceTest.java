package MIcroservicioB.MicroservicioAsistencia.service;

import MIcroservicioB.MicroservicioAsistencia.model.AsistenciaModel;
import MIcroservicioB.MicroservicioAsistencia.repository.AsistenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para AsistenciaService.
 * Patrón: AAA (Arrange–Act–Assert)
 * Herramientas: JUnit 5 + Mockito (@Mock / @InjectMocks)
 */
@ExtendWith(MockitoExtension.class)
class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @InjectMocks
    private AsistenciaService asistenciaService;

    private AsistenciaModel registroPresente;
    private AsistenciaModel registroAusente;

    @BeforeEach
    void setUp() {
        registroPresente = new AsistenciaModel();
        registroPresente.setId(1L);
        registroPresente.setAlumnoId(10L);
        registroPresente.setNombreAlumno("Juan González");
        registroPresente.setCurso("3°A");
        registroPresente.setFecha(LocalDate.now());
        registroPresente.setEstado(AsistenciaModel.EstadoAsistencia.PRESENTE);
        registroPresente.setDocente("Prof. Ramírez");

        registroAusente = new AsistenciaModel();
        registroAusente.setId(2L);
        registroAusente.setAlumnoId(10L);
        registroAusente.setNombreAlumno("Juan González");
        registroAusente.setCurso("3°A");
        registroAusente.setFecha(LocalDate.now().minusDays(1));
        registroAusente.setEstado(AsistenciaModel.EstadoAsistencia.AUSENTE);
        registroAusente.setDocente("Prof. Ramírez");
    }

    // ── Test 1 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("listar() debe retornar lista no nula")
    void testListarNoEsNulo() {
        when(asistenciaRepository.findAll()).thenReturn(Arrays.asList(registroPresente));
        List<AsistenciaModel> resultado = asistenciaService.listar();
        assertNotNull(resultado);
        System.out.println("TEST PASS: listar() retorna lista no nula. Size=" + resultado.size());
    }

    // ── Test 2 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("listar() debe retornar todos los registros correctamente")
    void testListarRetordaTodosLosRegistros() {
        when(asistenciaRepository.findAll()).thenReturn(Arrays.asList(registroPresente, registroAusente));
        List<AsistenciaModel> resultado = asistenciaService.listar();
        assertEquals(2, resultado.size());
        verify(asistenciaRepository, times(1)).findAll();
        System.out.println("TEST PASS: listar() retornó " + resultado.size() + " registros.");
    }

    // ── Test 3 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("registrar() debe guardar y retornar el registro con estado PRESENTE")
    void testRegistrarGuarda() {
        when(asistenciaRepository.save(registroPresente)).thenReturn(registroPresente);
        AsistenciaModel resultado = asistenciaService.registrar(registroPresente);
        assertEquals(AsistenciaModel.EstadoAsistencia.PRESENTE, resultado.getEstado());
        assertEquals("Juan González", resultado.getNombreAlumno());
        verify(asistenciaRepository, times(1)).save(registroPresente);
        System.out.println("TEST PASS: registrar() guardó estado PRESENTE para " + resultado.getNombreAlumno());
    }

    // ── Test 4 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("registrar() debe guardar correctamente estado AUSENTE")
    void testRegistrarEstadoAusente() {
        when(asistenciaRepository.save(registroAusente)).thenReturn(registroAusente);
        AsistenciaModel resultado = asistenciaService.registrar(registroAusente);
        assertEquals(AsistenciaModel.EstadoAsistencia.AUSENTE, resultado.getEstado());
        System.out.println("TEST PASS: registrar() guardó estado AUSENTE.");
    }

    // ── Test 5 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("obtenerResumen() debe calcular porcentaje 18/20 = 90%")
    void testObtenerResumen() {
        when(asistenciaRepository.contarPresencias(10L)).thenReturn(18L);
        when(asistenciaRepository.contarAusencias(10L)).thenReturn(2L);
        when(asistenciaRepository.contarTotal(10L)).thenReturn(20L);
        Map<String, Object> resultado = asistenciaService.obtenerResumen(10L);
        assertEquals(18L, resultado.get("asistidas"));
        assertEquals(2L,  resultado.get("faltadas"));
        assertEquals(90.0, resultado.get("porcentaje"));
        System.out.println("TEST PASS: resumen 18/20 = " + resultado.get("porcentaje") + "%");
    }

    // ── Test 6 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("obtenerResumen() con total=0 debe retornar porcentaje=0.0 (sin división por cero)")
    void testResumenSinClases() {
        when(asistenciaRepository.contarPresencias(99L)).thenReturn(0L);
        when(asistenciaRepository.contarAusencias(99L)).thenReturn(0L);
        when(asistenciaRepository.contarTotal(99L)).thenReturn(0L);
        Map<String, Object> resultado = asistenciaService.obtenerResumen(99L);
        assertEquals(0.0, resultado.get("porcentaje"));
        System.out.println("TEST PASS: resumen sin clases → porcentaje=0.0 (sin división por cero).");
    }

    // ── Test 7 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("obtenerPorAlumno() debe filtrar correctamente por alumnoId")
    void testObtenerPorAlumno() {
        when(asistenciaRepository.findByAlumnoId(10L)).thenReturn(Arrays.asList(registroPresente, registroAusente));
        List<AsistenciaModel> resultado = asistenciaService.obtenerPorAlumno(10L);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(r -> r.getAlumnoId().equals(10L)));
        System.out.println("TEST PASS: obtenerPorAlumno(10) retornó " + resultado.size() + " registros.");
    }

    // ── Test 8 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("eliminar() debe llamar a deleteById exactamente una vez")
    void testEliminar() {
        doNothing().when(asistenciaRepository).deleteById(1L);
        asistenciaService.eliminar(1L);
        verify(asistenciaRepository, times(1)).deleteById(1L);
        System.out.println("TEST PASS: eliminar(1) llamó deleteById exactamente 1 vez.");
    }

    // ── Test 9 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("obtenerPorAlumno() con alumno sin registros debe retornar lista vacía")
    void testObtenerPorAlumnoSinRegistros() {
        when(asistenciaRepository.findByAlumnoId(99L)).thenReturn(Collections.emptyList());
        List<AsistenciaModel> resultado = asistenciaService.obtenerPorAlumno(99L);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        System.out.println("TEST PASS: alumno sin registros → lista vacía.");
    }
}
