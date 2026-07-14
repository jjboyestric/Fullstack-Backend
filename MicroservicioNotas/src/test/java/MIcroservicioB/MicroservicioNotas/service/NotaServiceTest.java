package MIcroservicioB.MicroservicioNotas.service;

import MIcroservicioB.MicroservicioNotas.model.NotaModel;
import MIcroservicioB.MicroservicioNotas.repository.NotaRepository;
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
 * Pruebas unitarias para NotaService.
 * Patrón: AAA (Arrange–Act–Assert)
 * Herramientas: JUnit 5 + Mockito (@Mock / @InjectMocks)
 */
@ExtendWith(MockitoExtension.class)
class NotaServiceTest {

    @Mock
    private NotaRepository notaRepository;

    @InjectMocks
    private NotaService notaService;

    private NotaModel notaEjemplo;
    private NotaModel notaEjemplo2;

    @BeforeEach
    void setUp() {
        notaEjemplo = new NotaModel();
        notaEjemplo.setId(1L);
        notaEjemplo.setAlumnoId(10L);
        notaEjemplo.setNombreAlumno("María Pérez");
        notaEjemplo.setCurso("3°A");
        notaEjemplo.setAsignatura("Matemáticas");
        notaEjemplo.setTipoEvaluacion("PRUEBA");
        notaEjemplo.setNota(6.5);
        notaEjemplo.setFechaEvaluacion(LocalDate.now());

        notaEjemplo2 = new NotaModel();
        notaEjemplo2.setId(2L);
        notaEjemplo2.setAlumnoId(10L);
        notaEjemplo2.setNombreAlumno("María Pérez");
        notaEjemplo2.setCurso("3°A");
        notaEjemplo2.setAsignatura("Lenguaje");
        notaEjemplo2.setTipoEvaluacion("EXAMEN");
        notaEjemplo2.setNota(5.5);
        notaEjemplo2.setFechaEvaluacion(LocalDate.now());
    }

    // ── Test 1 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("listarNotas() debe retornar lista no nula")
    void testListarNotasNoEsNulo() {
        when(notaRepository.findAll()).thenReturn(Arrays.asList(notaEjemplo));
        List<NotaModel> resultado = notaService.listarNotas();
        assertNotNull(resultado);
        System.out.println("TEST PASS: listarNotas retorna lista no nula. Size=" + resultado.size());
    }

    // ── Test 2 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("listarNotas() debe retornar todas las notas correctamente")
    void testListarNotasRetornaTodasLasNotas() {
        when(notaRepository.findAll()).thenReturn(Arrays.asList(notaEjemplo, notaEjemplo2));
        List<NotaModel> resultado = notaService.listarNotas();
        assertEquals(2, resultado.size());
        verify(notaRepository, times(1)).findAll();
        System.out.println("TEST PASS: listarNotas retorna " + resultado.size() + " notas.");
    }

    // ── Test 3 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("registrarNota() debe guardar correctamente y retornar la entidad")
    void testRegistrarNotaGuardaCorrectamente() {
        when(notaRepository.save(notaEjemplo)).thenReturn(notaEjemplo);
        NotaModel resultado = notaService.registrarNota(notaEjemplo);
        assertEquals("María Pérez", resultado.getNombreAlumno());
        assertEquals(6.5, resultado.getNota());
        verify(notaRepository, times(1)).save(notaEjemplo);
        System.out.println("TEST PASS: registrarNota guardó y retornó: " + resultado.getNombreAlumno());
    }

    // ── Test 4 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("obtenerPromedioAlumno() debe calcular promedio y retornar datos del alumno")
    void testObtenerPromedioAlumno() {
        when(notaRepository.calcularPromedioAlumno(10L)).thenReturn(6.0);
        when(notaRepository.findByAlumnoId(10L)).thenReturn(Arrays.asList(notaEjemplo, notaEjemplo2));
        Map<String, Object> resultado = notaService.obtenerPromedioAlumno(10L);
        assertEquals("María Pérez", resultado.get("nombre"));
        assertEquals("3°A", resultado.get("curso"));
        assertEquals(2, resultado.get("totalEvaluaciones"));
        System.out.println("TEST PASS: promedio alumno ID=10 → " + resultado.get("promedioGeneral"));
    }

    // ── Test 5 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("obtenerPorAlumno() debe retornar solo las notas del alumno indicado")
    void testObtenerPorAlumno() {
        when(notaRepository.findByAlumnoId(10L)).thenReturn(Arrays.asList(notaEjemplo, notaEjemplo2));
        List<NotaModel> resultado = notaService.obtenerPorAlumno(10L);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(n -> n.getAlumnoId().equals(10L)));
        System.out.println("TEST PASS: obtenerPorAlumno(10) retornó " + resultado.size() + " notas.");
    }

    // ── Test 6 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("obtenerPorAlumno() con alumno sin notas debe retornar lista vacía")
    void testObtenerPorAlumnoSinNotas() {
        when(notaRepository.findByAlumnoId(99L)).thenReturn(Collections.emptyList());
        List<NotaModel> resultado = notaService.obtenerPorAlumno(99L);
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
        System.out.println("TEST PASS: alumno sin notas retorna lista vacía.");
    }

    // ── Test 7 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("eliminarNota() debe llamar a deleteById exactamente una vez")
    void testEliminarNota() {
        doNothing().when(notaRepository).deleteById(1L);
        notaService.eliminarNota(1L);
        verify(notaRepository, times(1)).deleteById(1L);
        System.out.println("TEST PASS: eliminarNota(1) llamó deleteById exactamente 1 vez.");
    }

    // ── Test 8 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("obtenerPromedioAlumno() con promedio null debe retornar 0.0")
    void testPromedioNullRetornaCero() {
        when(notaRepository.calcularPromedioAlumno(99L)).thenReturn(null);
        when(notaRepository.findByAlumnoId(99L)).thenReturn(Collections.emptyList());
        Map<String, Object> resultado = notaService.obtenerPromedioAlumno(99L);
        assertEquals(0.0, resultado.get("promedioGeneral"));
        System.out.println("TEST PASS: promedio null → 0.0");
    }
}
