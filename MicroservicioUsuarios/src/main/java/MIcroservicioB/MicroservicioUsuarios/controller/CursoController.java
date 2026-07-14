package MIcroservicioB.MicroservicioUsuarios.controller;

import MIcroservicioB.MicroservicioUsuarios.dto.CursoResumenDTO;
import MIcroservicioB.MicroservicioUsuarios.model.AlumnoModel;
import MIcroservicioB.MicroservicioUsuarios.model.CursoModel;
import MIcroservicioB.MicroservicioUsuarios.service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    public List<CursoModel> listar() {
        return cursoService.listarCursos();
    }

    // Vista del Director: cursos + profesores asignados a cada uno + total de alumnos
    @GetMapping("/resumen")
    public List<CursoResumenDTO> resumen() {
        return cursoService.resumenCursos();
    }

    @GetMapping("/{nombre}/alumnos")
    public List<AlumnoModel> alumnosDeCurso(@PathVariable String nombre) {
        return cursoService.alumnosDeCurso(nombre);
    }
}
