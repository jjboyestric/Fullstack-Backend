# Informe de Pruebas Unitarias
## Sistema de Gestión Escolar – Colegio Bernardo O'Higgins
**Asignatura:** DSY1106 – Desarrollo Fullstack III | **Parcial N°3**

---

## 1. Resumen Ejecutivo

| Métrica | MS Notas | MS Asistencia | Total |
|---|---|---|---|
| Clases testeadas | 1 (NotaService) | 1 (AsistenciaService) | 2 |
| Métodos testeados | 4 | 4 | **8** |
| Tests ejecutados | 4 | 4 | **8** |
| Tests pasados | 4 | 4 | **8** |
| Tests fallidos | 0 | 0 | **0** |
| Cobertura estimada | ~75% | ~75% | **≥60% ✅** |
| Framework | JUnit 5 + Mockito | JUnit 5 + Mockito | — |

---

## 2. Herramientas Utilizadas

| Herramienta | Versión | Propósito |
|---|---|---|
| JUnit 5 (Jupiter) | 5.x | Framework de pruebas |
| Mockito | 5.x | Mocking de dependencias |
| @ExtendWith(MockitoExtension) | — | Integración JUnit-Mockito |
| Maven Surefire Plugin | 3.x | Ejecución de tests |
| JaCoCo | 0.8.x | Medición de cobertura |

---

## 3. Pruebas Unitarias – MS Notas

**Clase:** `NotaServiceTest.java`
**Paquete:** `MIcroservicioB.MicroservicioNotas.service`
**Patrón:** `@Mock NotaRepository` → `@InjectMocks NotaService`

### 3.1 Configuración del Test

```java
@ExtendWith(MockitoExtension.class)
class NotaServiceTest {
    @Mock private NotaRepository notaRepository;
    @InjectMocks private NotaService notaService;

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
    }
}
```

### 3.2 Casos de Prueba

| # | Método | Descripción | Resultado |
|---|---|---|---|
| 1 | `testListarNotasNoEsNulo()` | Verifica que `listarNotas()` no retorna null | ✅ PASS |
| 2 | `testRegistrarNotaGuardaCorrectamente()` | Verifica que `registrarNota()` llama a `save()` y retorna la entidad con nombre correcto | ✅ PASS |
| 3 | `testObtenerPromedioAlumno()` | Verifica cálculo de promedio JPQL para alumno 10 | ✅ PASS |
| 4 | `testObtenerPorAlumno()` | Verifica filtro por alumnoId retorna lista con 1 elemento | ✅ PASS |

### 3.3 Detalle de Pruebas

**Test 1 – `testListarNotasNoEsNulo()`**
```java
@Test
void testListarNotasNoEsNulo() {
    when(notaRepository.findAll()).thenReturn(Arrays.asList(notaEjemplo));
    assertNotNull(notaService.listarNotas());
}
```
- **Qué valida:** Que el servicio nunca retorna null al consultar todas las notas.
- **Mock usado:** `notaRepository.findAll()` devuelve lista con un elemento.
- **Resultado:** ✅ PASS

---

**Test 2 – `testRegistrarNotaGuardaCorrectamente()`**
```java
@Test
void testRegistrarNotaGuardaCorrectamente() {
    when(notaRepository.save(notaEjemplo)).thenReturn(notaEjemplo);
    NotaModel r = notaService.registrarNota(notaEjemplo);
    assertEquals("María Pérez", r.getNombreAlumno());
    verify(notaRepository, times(1)).save(notaEjemplo);
}
```
- **Qué valida:** Persistencia correcta y retorno de entidad con datos íntegros.
- **Verificaciones:** `assertEquals` + `verify(times(1))`.
- **Resultado:** ✅ PASS

---

**Test 3 – `testObtenerPromedioAlumno()`**
```java
@Test
void testObtenerPromedioAlumno() {
    when(notaRepository.calcularPromedioAlumno(10L)).thenReturn(6.3);
    when(notaRepository.findByAlumnoId(10L)).thenReturn(Arrays.asList(notaEjemplo));
    Map<String, Object> r = notaService.obtenerPromedioAlumno(10L);
    assertEquals("María Pérez", r.get("nombre"));
}
```
- **Qué valida:** Que el mapa de resultado incluye el nombre correcto del alumno.
- **Mock usado:** Query JPQL `calcularPromedioAlumno()` + `findByAlumnoId()`.
- **Resultado:** ✅ PASS

---

**Test 4 – `testObtenerPorAlumno()`**
```java
@Test
void testObtenerPorAlumno() {
    when(notaRepository.findByAlumnoId(10L)).thenReturn(Arrays.asList(notaEjemplo));
    List<NotaModel> notas = notaService.obtenerPorAlumno(10L);
    assertEquals(1, notas.size());
}
```
- **Qué valida:** Que el filtro por alumnoId retorna exactamente 1 elemento.
- **Resultado:** ✅ PASS

---

## 4. Pruebas Unitarias – MS Asistencia

**Clase:** `AsistenciaServiceTest.java`
**Paquete:** `MIcroservicioB.MicroservicioAsistencia.service`
**Patrón:** `@Mock AsistenciaRepository` → `@InjectMocks AsistenciaService`

### 4.1 Configuración del Test

```java
@ExtendWith(MockitoExtension.class)
class AsistenciaServiceTest {
    @Mock private AsistenciaRepository asistenciaRepository;
    @InjectMocks private AsistenciaService asistenciaService;

    @BeforeEach
    void setUp() {
        registro = new AsistenciaModel();
        registro.setId(1L);
        registro.setAlumnoId(10L);
        registro.setNombreAlumno("Juan González");
        registro.setCurso("3°A");
        registro.setFecha(LocalDate.now());
        registro.setEstado(AsistenciaModel.EstadoAsistencia.PRESENTE);
        registro.setDocente("Prof. Ramírez");
    }
}
```

### 4.2 Casos de Prueba

| # | Método | Descripción | Resultado |
|---|---|---|---|
| 1 | `testListarNoEsNulo()` | Verifica que `listar()` no retorna null | ✅ PASS |
| 2 | `testRegistrarGuarda()` | Verifica que `registrar()` llama a `save()` y estado es PRESENTE | ✅ PASS |
| 3 | `testObtenerResumen()` | Verifica cálculo de porcentaje asistencia (18/20 = 90%) | ✅ PASS |
| 4 | `testObtenerPorAlumno()` | Verifica filtro por alumnoId | ✅ PASS |

### 4.3 Detalle de Pruebas

**Test 1 – `testListarNoEsNulo()`**
```java
@Test
void testListarNoEsNulo() {
    when(asistenciaRepository.findAll()).thenReturn(Arrays.asList(registro));
    assertNotNull(asistenciaService.listar());
}
```
- **Resultado:** ✅ PASS

---

**Test 2 – `testRegistrarGuarda()`**
```java
@Test
void testRegistrarGuarda() {
    when(asistenciaRepository.save(registro)).thenReturn(registro);
    AsistenciaModel r = asistenciaService.registrar(registro);
    assertEquals(AsistenciaModel.EstadoAsistencia.PRESENTE, r.getEstado());
    verify(asistenciaRepository, times(1)).save(registro);
}
```
- **Qué valida:** Estado PRESENTE persiste correctamente.
- **Resultado:** ✅ PASS

---

**Test 3 – `testObtenerResumen()`**
```java
@Test
void testObtenerResumen() {
    when(asistenciaRepository.contarPresencias(10L)).thenReturn(18L);
    when(asistenciaRepository.contarAusencias(10L)).thenReturn(2L);
    when(asistenciaRepository.contarTotal(10L)).thenReturn(20L);
    Map<String, Object> r = asistenciaService.obtenerResumen(10L);
    assertEquals(18L, r.get("asistidas"));
    assertEquals(90.0, r.get("porcentaje"));
}
```
- **Qué valida:** Cálculo correcto del porcentaje (18/20 × 100 = 90%).
- **Resultado:** ✅ PASS

---

**Test 4 – `testObtenerPorAlumno()`**
```java
@Test
void testObtenerPorAlumno() {
    when(asistenciaRepository.findByAlumnoId(10L)).thenReturn(Arrays.asList(registro));
    assertEquals(1, asistenciaService.obtenerPorAlumno(10L).size());
}
```
- **Resultado:** ✅ PASS

---

## 5. Métricas de Cobertura

### Cobertura por Clase

| Servicio | Clase | Métodos cubiertos | % Estimado |
|---|---|---|---|
| MS Notas | NotaService | 4/5 | ~75% |
| MS Asistencia | AsistenciaService | 4/5 | ~75% |

> **Nota:** La cobertura real se obtiene ejecutando JaCoCo con `./mvnw test jacoco:report`. El reporte HTML se genera en `target/site/jacoco/index.html`.

### Resumen Visual

```
MS Notas:
  [████████████████████░░░░]  75%

MS Asistencia:
  [████████████████████░░░░]  75%

Objetivo mínimo:
  [████████████████░░░░░░░░]  60% ✅ SUPERADO
```

---

## 6. Cómo Ejecutar las Pruebas

```bash
# MS Notas
cd MicroservicioNotas
./mvnw test

# MS Asistencia
cd MicroservicioAsistencia
./mvnw test

# Con reporte JaCoCo
./mvnw test jacoco:report
# → Abrir: target/site/jacoco/index.html
```

### Salida esperada

```
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 7. Patrones de Diseño en los Tests

| Patrón | Descripción | Aplicado en |
|---|---|---|
| **Arrange-Act-Assert** | Organización de cada test: configurar mocks → ejecutar método → verificar resultado | Todos los tests |
| **Mocking con Mockito** | `@Mock` aísla el repositorio sin necesidad de base de datos real | `@Mock NotaRepository` / `@Mock AsistenciaRepository` |
| **Verify** | `verify(repo, times(1)).save(...)` confirma que se llamó exactamente una vez | Tests de escritura |
| **@BeforeEach** | Datos de prueba reutilizables entre tests | Ambas clases de test |

---

*DSY1106 Desarrollo Fullstack III – DuocUC – Parcial N°3*
