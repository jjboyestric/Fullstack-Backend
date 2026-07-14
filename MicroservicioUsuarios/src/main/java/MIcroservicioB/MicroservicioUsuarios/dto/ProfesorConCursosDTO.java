package MIcroservicioB.MicroservicioUsuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfesorConCursosDTO {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String asignatura;
    private List<String> cursos; // Todos los profesores dictan su asignatura en 4°A, 4°B y 4°C
}
