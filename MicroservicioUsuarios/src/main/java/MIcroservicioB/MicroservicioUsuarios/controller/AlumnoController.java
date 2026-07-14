package MIcroservicioB.MicroservicioUsuarios.controller;

import MIcroservicioB.MicroservicioUsuarios.model.AlumnoModel;
import MIcroservicioB.MicroservicioUsuarios.service.AlumnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
@CrossOrigin(origins = "*")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoService;

    @GetMapping
    public List<AlumnoModel> listar() {
        return alumnoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlumnoModel> obtener(@PathVariable Long id) {
        AlumnoModel alumno = alumnoService.obtenerPorId(id);
        return alumno != null ? ResponseEntity.ok(alumno) : ResponseEntity.notFound().build();
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<AlumnoModel> porRut(@PathVariable String rut) {
        AlumnoModel alumno = alumnoService.porRut(rut);
        return alumno != null ? ResponseEntity.ok(alumno) : ResponseEntity.notFound().build();
    }
}
