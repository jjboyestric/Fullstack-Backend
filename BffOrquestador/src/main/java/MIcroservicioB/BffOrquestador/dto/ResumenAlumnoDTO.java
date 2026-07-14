package MIcroservicioB.BffOrquestador.dto;

import lombok.Data;

/**
 * DTO que consolida datos de Notas y Asistencia para el frontend.
 * Patrón: DTO — evita exponer entidades internas al cliente.
 */
@Data
public class ResumenAlumnoDTO {
    private Long alumnoId;
    private String nombreAlumno;
    private String curso;
    private Double promedioGeneral;
    private Long totalClasesAsistidas;
    private Long totalClasesFaltadas;
    private Double porcentajeAsistencia;
}
