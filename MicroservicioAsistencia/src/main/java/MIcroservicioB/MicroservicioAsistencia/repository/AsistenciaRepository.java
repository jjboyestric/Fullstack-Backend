package MIcroservicioB.MicroservicioAsistencia.repository;
import MIcroservicioB.MicroservicioAsistencia.model.AsistenciaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
// Patrón: Repository Pattern
@Repository
public interface AsistenciaRepository extends JpaRepository<AsistenciaModel, Long> {
    List<AsistenciaModel> findByAlumnoId(Long alumnoId);

    // "Asistió" = estuvo físicamente en clase (PRESENTE o llegó atrasado)
    @Query("SELECT COUNT(a) FROM AsistenciaModel a WHERE a.alumnoId = :id AND a.estado IN ('PRESENTE','TARDANZA')")
    Long contarPresencias(@Param("id") Long alumnoId);

    // "Faltó" = no estuvo en clase, con o sin justificativo
    @Query("SELECT COUNT(a) FROM AsistenciaModel a WHERE a.alumnoId = :id AND a.estado IN ('AUSENTE','JUSTIFICADO')")
    Long contarAusencias(@Param("id") Long alumnoId);

    @Query("SELECT COUNT(a) FROM AsistenciaModel a WHERE a.alumnoId = :id")
    Long contarTotal(@Param("id") Long alumnoId);
}
