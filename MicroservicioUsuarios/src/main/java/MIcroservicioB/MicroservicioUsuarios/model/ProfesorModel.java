package MIcroservicioB.MicroservicioUsuarios.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "profesores")
@Data
public class ProfesorModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;
    private String email;
    private String asignatura; // Cada profesor dicta una sola asignatura en los 3 cursos (4°A, 4°B, 4°C)
}
