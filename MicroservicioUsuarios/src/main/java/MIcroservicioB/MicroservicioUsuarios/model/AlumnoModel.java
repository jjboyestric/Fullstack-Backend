package MIcroservicioB.MicroservicioUsuarios.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "alumnos")
@Data
public class AlumnoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;
    private String rut;
    private String curso; // Ej: "4°A" — coincide con el campo "curso" usado en Notas y Asistencia
}
