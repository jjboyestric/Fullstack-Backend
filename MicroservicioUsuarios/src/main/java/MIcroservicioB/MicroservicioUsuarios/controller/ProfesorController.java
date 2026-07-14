package MIcroservicioB.MicroservicioUsuarios.controller;

import MIcroservicioB.MicroservicioUsuarios.dto.ProfesorConCursosDTO;
import MIcroservicioB.MicroservicioUsuarios.service.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profesores")
@CrossOrigin(origins = "*")
public class ProfesorController {

    @Autowired
    private ProfesorService profesorService;

    @GetMapping
    public List<ProfesorConCursosDTO> listar() {
        return profesorService.listarConCursos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfesorConCursosDTO> obtener(@PathVariable Long id) {
        ProfesorConCursosDTO dto = profesorService.obtenerConCursos(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }
}
