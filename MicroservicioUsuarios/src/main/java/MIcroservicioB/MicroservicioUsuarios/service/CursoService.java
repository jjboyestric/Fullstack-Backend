package MIcroservicioB.MicroservicioUsuarios.service;

import MIcroservicioB.MicroservicioUsuarios.dto.CursoResumenDTO;
import MIcroservicioB.MicroservicioUsuarios.dto.ProfesorConCursosDTO;
import MIcroservicioB.MicroservicioUsuarios.model.AlumnoModel;
import MIcroservicioB.MicroservicioUsuarios.model.CursoModel;
import MIcroservicioB.MicroservicioUsuarios.repository.AlumnoRepository;
import MIcroservicioB.MicroservicioUsuarios.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private ProfesorService profesorService;

    public List<CursoModel> listarCursos() {
        return cursoRepository.findAll();
    }

    public List<AlumnoModel> alumnosDeCurso(String nombreCurso) {
        System.out.println("LOG: Consultando roster del curso " + nombreCurso);
        return alumnoRepository.findByCurso(nombreCurso);
    }

    /**
     * Vista del Director: cada curso con su cantidad de alumnos y los profesores
     * que dictan clases en él (todos los profesores dictan en los 3 cursos).
     */
    public List<CursoResumenDTO> resumenCursos() {
        List<ProfesorConCursosDTO> todosLosProfesores = profesorService.listarConCursos();
        return cursoRepository.findAll().stream()
                .map(curso -> {
                    int total = alumnoRepository.findByCurso(curso.getNombre()).size();
                    return new CursoResumenDTO(curso.getId(), curso.getNombre(), total, todosLosProfesores);
                })
                .collect(Collectors.toList());
    }
}
