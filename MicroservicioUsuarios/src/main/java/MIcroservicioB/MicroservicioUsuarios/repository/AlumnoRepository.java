package MIcroservicioB.MicroservicioUsuarios.repository;

import MIcroservicioB.MicroservicioUsuarios.model.AlumnoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<AlumnoModel, Long> {
    List<AlumnoModel> findByCurso(String curso);
    Optional<AlumnoModel> findByRut(String rut);
}
