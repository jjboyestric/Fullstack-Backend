package MIcroservicioB.MicroservicioUsuarios.service;

import MIcroservicioB.MicroservicioUsuarios.dto.ProfesorConCursosDTO;
import MIcroservicioB.MicroservicioUsuarios.model.CursoModel;
import MIcroservicioB.MicroservicioUsuarios.model.ProfesorModel;
import MIcroservicioB.MicroservicioUsuarios.repository.CursoRepository;
import MIcroservicioB.MicroservicioUsuarios.repository.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfesorService {

    @Autowired
    private ProfesorRepository profesorRepository;

    @Autowired
    private CursoRepository cursoRepository;

    public List<ProfesorModel> listar() {
        return profesorRepository.findAll();
    }

    public ProfesorModel obtenerPorId(Long id) {
        return profesorRepository.findById(id).orElse(null);
    }

    /**
     * Cada profesor dicta su asignatura en TODOS los cursos existentes (4°A, 4°B, 4°C),
     * por lo tanto la lista de cursos es la misma para todos: los nombres de todos los CursoModel.
     */
    public List<ProfesorConCursosDTO> listarConCursos() {
        List<String> nombresCursos = cursoRepository.findAll().stream()
                .map(CursoModel::getNombre)
                .collect(Collectors.toList());

        return profesorRepository.findAll().stream()
                .map(p -> new ProfesorConCursosDTO(p.getId(), p.getNombreCompleto(), p.getEmail(), p.getAsignatura(), nombresCursos))
                .collect(Collectors.toList());
    }

    public ProfesorConCursosDTO obtenerConCursos(Long id) {
        return listarConCursos().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
