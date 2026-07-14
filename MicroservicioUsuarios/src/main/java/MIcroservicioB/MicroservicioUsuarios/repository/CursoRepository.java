package MIcroservicioB.MicroservicioUsuarios.repository;

import MIcroservicioB.MicroservicioUsuarios.model.CursoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<CursoModel, Long> {
    Optional<CursoModel> findByNombre(String nombre);
}
