package MIcroservicioB.MicroservicioUsuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursoResumenDTO {
    private Long id;
    private String nombre;
    private int totalAlumnos;
    private List<ProfesorConCursosDTO> profesores;
}
