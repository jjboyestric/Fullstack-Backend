package MIcroservicioB.MicroservicioUsuarios.service;

import MIcroservicioB.MicroservicioUsuarios.model.AlumnoModel;
import MIcroservicioB.MicroservicioUsuarios.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlumnoService {

    @Autowired
    private AlumnoRepository alumnoRepository;

    public List<AlumnoModel> listar() {
        return alumnoRepository.findAll();
    }

    public AlumnoModel obtenerPorId(Long id) {
        return alumnoRepository.findById(id).orElse(null);
    }

    public List<AlumnoModel> porCurso(String curso) {
        return alumnoRepository.findByCurso(curso);
    }

    public AlumnoModel porRut(String rut) {
        return alumnoRepository.findByRut(rut).orElse(null);
    }
}
