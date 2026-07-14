package MIcroservicioB.MicroservicioNotas.repository;
import MIcroservicioB.MicroservicioNotas.model.NotaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
// Patrón: Repository Pattern
@Repository
public interface NotaRepository extends JpaRepository<NotaModel, Long> {
    List<NotaModel> findByAlumnoId(Long alumnoId);
    @Query("SELECT AVG(n.nota) FROM NotaModel n WHERE n.alumnoId = :alumnoId")
    Double calcularPromedioAlumno(@Param("alumnoId") Long alumnoId);
}
