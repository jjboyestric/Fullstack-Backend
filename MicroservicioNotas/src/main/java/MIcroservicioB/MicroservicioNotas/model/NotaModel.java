package MIcroservicioB.MicroservicioNotas.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
@Entity
@Table(name = "notas")
@Data
public class NotaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long alumnoId;
    private String nombreAlumno;
    private String curso;
    private String asignatura;
    private String tipoEvaluacion;
    private Double nota;
    private LocalDate fechaEvaluacion;
    private String observaciones;
    @PrePersist
    protected void onCreate() {
        if (fechaEvaluacion == null) fechaEvaluacion = LocalDate.now();
    }
}
