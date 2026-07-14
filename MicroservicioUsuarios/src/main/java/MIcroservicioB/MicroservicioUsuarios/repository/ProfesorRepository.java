package MIcroservicioB.MicroservicioUsuarios.repository;

import MIcroservicioB.MicroservicioUsuarios.model.ProfesorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesorRepository extends JpaRepository<ProfesorModel, Long> {
}
