package MIcroservicioB.MicroservicioUsuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO — desacopla el modelo interno de Usuario del contrato expuesto al frontend (no incluye password)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private Long id;
    private String email;
    private String nombreCompleto;
    private String rol;      // DIRECTOR | PROFESOR | ALUMNO
    private Long refId;      // id de ProfesorModel o AlumnoModel (null si es DIRECTOR)
}
