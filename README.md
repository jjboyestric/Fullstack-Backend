# Colegio O'Higgins — Backend de Microservicios
**DSY1106 – Desarrollo Fullstack III | Evaluación Final Transversal**

Backend del Libro de Clases Digital del Colegio Bernardo O'Higgins. Compuesto por **cuatro servicios Java independientes**: un BFF Orquestador y tres microservicios de dominio (Usuarios, Notas y Asistencia).

> ⚠️ El microservicio `MicroservicioUsuarios` es nuevo en esta entrega y **no pudo compilarse/probarse con Maven en el entorno donde fue escrito** (sin acceso a internet para descargar dependencias). Antes de la presentación, ejecuta `./mvnw clean test` en cada servicio para confirmar que todo compila correctamente en tu máquina.

---

## Estructura del Repositorio

```
Fullstack-Backend-main/
├── BffOrquestador/          # BFF — agrega datos de los dos microservicios
│   ├── src/main/java/...
│   │   ├── controller/BffController.java
│   │   ├── service/OrquestadorService.java   ← Facade + Circuit Breaker
│   │   ├── dto/ResumenAlumnoDTO.java
│   │   └── config/WebClientConfig.java       ← Factory Method
│   ├── src/test/java/...
│   │   ├── BffOrquestadorApplicationTests.java
│   │   └── service/OrquestadorServiceTest.java  ← 3 tests (Circuit Breaker)
│   └── src/main/resources/application.properties
│
├── MicroservicioUsuarios/   # Login, Cursos, Profesores y Alumnos — Puerto 8083
│   ├── src/main/java/...
│   │   ├── controller/AuthController.java        ← POST /api/usuarios/login
│   │   ├── controller/CursoController.java       ← Cursos + resumen para Director
│   │   ├── controller/ProfesorController.java
│   │   ├── controller/AlumnoController.java
│   │   ├── service/UsuarioService.java
│   │   ├── model/{UsuarioModel,CursoModel,ProfesorModel,AlumnoModel}.java
│   │   └── dto/{LoginRequestDTO,LoginResponseDTO,CursoResumenDTO,ProfesorConCursosDTO}.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── data.sql                         ← 1 director, 3 profesores, 66 alumnos (4°A/B/C), 7 usuarios con login
│   └── src/test/java/.../service/
│       └── UsuarioServiceTest.java          ← 4 tests unitarios (login)
│
├── MicroservicioNotas/      # Gestión de evaluaciones — Puerto 8081
│   ├── src/main/java/...
│   │   ├── controller/NotaController.java
│   │   ├── service/NotaService.java
│   │   ├── model/NotaModel.java              ← @Entity JPA
│   │   └── repository/NotaRepository.java   ← JpaRepository + JPQL
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── data.sql                         ← Datos de prueba (10 notas)
│   └── src/test/java/.../service/
│       └── NotaServiceTest.java             ← 8 tests unitarios
│
├── MicroservicioAsistencia/ # Registro de asistencia — Puerto 8082
│   ├── src/main/java/...
│   │   ├── controller/AsistenciaController.java
│   │   ├── service/AsistenciaService.java
│   │   ├── model/AsistenciaModel.java        ← @Entity JPA + Enum
│   │   └── repository/AsistenciaRepository.java ← JpaRepository + JPQL
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── data.sql                         ← Datos de prueba (25 registros)
│   └── src/test/java/.../service/
│       └── AsistenciaServiceTest.java        ← 9 tests unitarios
│
└── docs/
    ├── Postman_Collection.json              ← Colección Postman lista para importar
    ├── Informe_Pruebas_Unitarias.md         ← Informe de cobertura y métricas
    ├── Descripcion_Persistencia.md          ← Detalle implementación JPA
    └── repositorios.txt                     ← Links a repositorios GitHub
```

---

## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje base |
| Spring Boot | 3.2.5 | Framework principal |
| Spring WebFlux | — | Programación reactiva (BFF) |
| Spring Data JPA | — | Persistencia (Notas y Asistencia) |
| H2 Database | — | Base de datos en memoria |
| Lombok | — | Reducción de boilerplate |
| SpringDoc OpenAPI | 2.3.0 | Swagger UI |
| JaCoCo | 0.8.11 | Cobertura de pruebas |
| JUnit 5 + Mockito | — | Pruebas unitarias |
| reactor-test / StepVerifier | — | Pruebas reactivas (BFF) |

---

## Requisitos Previos

- **Java 21**
- **Maven 3.8+** (o usar el wrapper `./mvnw` incluido en cada servicio)

---

## Ejecución

Iniciar en este orden (los microservicios deben estar corriendo antes que el BFF):

```bash
# 1. MS Usuarios (puerto 8083) — login, cursos, profesores, alumnos
cd MicroservicioUsuarios
./mvnw spring-boot:run

# 2. MS Notas (puerto 8081)
cd MicroservicioNotas
./mvnw spring-boot:run

# 3. MS Asistencia (puerto 8082)
cd MicroservicioAsistencia
./mvnw spring-boot:run

# 4. BFF Orquestador (puerto 8080) — iniciar último
cd BffOrquestador
./mvnw spring-boot:run
```

Cada servicio imprime en consola la URL del Swagger al iniciarse.

---

## Datos de Prueba

Los microservicios cargan datos de prueba automáticamente al iniciar (via `data.sql`):

- **MS Usuarios:** 1 director, 3 profesores (Inglés, Matemática, Historia), 66 alumnos repartidos en 4°A (21), 4°B (22) y 4°C (23), y 7 cuentas de acceso (login)
- **MS Notas:** 396 notas (66 alumnos × 3 asignaturas × 2 evaluaciones)
- **MS Asistencia:** 660 registros de asistencia (66 alumnos × 10 registros)

### Cuentas de acceso (contraseña `12345` para todas)

| Rol | Email | Nombre |
|---|---|---|
| Director | `ricardo.gomez@director.cl` | Ricardo Gómez Alarcón |
| Profesor (Inglés) | `isidora.fernandez@profesor.cl` | Isidora Fernández Rojas |
| Profesor (Matemática) | `pablo.contreras@profesor.cl` | Pablo Contreras Muñoz |
| Profesor (Historia) | `carla.torres@profesor.cl` | Carla Torres Sepúlveda |
| Alumno 4°A | `vicente.soto@alumno.cl` | Vicente Soto Araya |
| Alumno 4°B | `constanza.carrasco@alumno.cl` | Constanza Carrasco Herrera |
| Alumno 4°C | `consuelo.martinez@alumno.cl` | Consuelo Martínez Reyes |

Para probar el BFF, buscar el resumen del **alumno ID 6** (`Vicente Soto Araya`, cuenta demo de 4°A).

---

## Pruebas Unitarias

```bash
# Ejecutar todos los tests con reporte de cobertura JaCoCo
cd MicroservicioUsuarios    && ./mvnw test
cd MicroservicioNotas       && ./mvnw test
cd MicroservicioAsistencia  && ./mvnw test
cd BffOrquestador           && ./mvnw test

# El reporte HTML de cobertura se genera en:
# target/site/jacoco/index.html
```

| Servicio | Tests | Cobertura estimada |
|---|---|---|
| MS Usuarios | 4 tests | ~75% |
| MS Notas | 8 tests | ~80% |
| MS Asistencia | 9 tests | ~80% |
| BFF Orquestador | 3 tests | ~70% |

---

## Swagger UI

| Servicio | URL |
|---|---|
| BFF Orquestador | http://localhost:8080/swagger-ui.html |
| MS Usuarios | http://localhost:8083/swagger-ui.html |
| MS Notas | http://localhost:8081/swagger-ui.html |
| MS Asistencia | http://localhost:8082/swagger-ui.html |

---

## Consola H2

| Servicio | URL | JDBC URL |
|---|---|---|
| MS Usuarios | http://localhost:8083/h2-console | `jdbc:h2:mem:usuariosdb` |
| MS Notas | http://localhost:8081/h2-console | `jdbc:h2:mem:notasdb` |
| MS Asistencia | http://localhost:8082/h2-console | `jdbc:h2:mem:asistenciadb` |

Credenciales: Usuario `sa` | Contraseña: (vacía)

---

## Patrones de Diseño Aplicados

| Patrón | Ubicación | Descripción |
|---|---|---|
| **Facade** | `OrquestadorService` | Oculta la complejidad de llamar a 2 microservicios |
| **Circuit Breaker** | `OrquestadorService.onErrorReturn()` | Evita fallos en cascada |
| **Factory Method** | `WebClientConfig` | Centraliza creación de WebClient |
| **Repository** | `NotaRepository`, `AsistenciaRepository` | Abstrae el acceso a datos JPA |
| **Singleton (IoC)** | `@Service`, `@Repository`, `@RestController` | Spring gestiona beans como singletons |
| **DTO** | `ResumenAlumnoDTO` | Desacopla el modelo interno del contrato API |

---

## Repositorio Frontend

- https://github.com/jjboyestric/Fullstack-Frontend
