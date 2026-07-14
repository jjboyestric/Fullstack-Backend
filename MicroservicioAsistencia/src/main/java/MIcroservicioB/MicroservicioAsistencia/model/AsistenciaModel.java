package MIcroservicioB.MicroservicioAsistencia.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
@Entity
@Table(name = "asistencia")
@Data
public class AsistenciaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long alumnoId;
    private String nombreAlumno;
    private String curso;
    private LocalDate fecha;
    @Enumerated(EnumType.STRING)
    private EstadoAsistencia estado;
    private String anotacion;
    private String docente;
    @PrePersist
    protected void onCreate() {
        if (fecha == null) fecha = LocalDate.now();
    }
    public enum EstadoAsistencia { PRESENTE, AUSENTE, TARDANZA, JUSTIFICADO }
}
