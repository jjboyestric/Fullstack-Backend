package MIcroservicioB.MicroservicioNotas.controller;
import MIcroservicioB.MicroservicioNotas.model.NotaModel;
import MIcroservicioB.MicroservicioNotas.service.NotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/notas")
@CrossOrigin(origins = "*")
public class NotaController {
    @Autowired
    private NotaService notaService;
    @GetMapping
    public List<NotaModel> listar() { return notaService.listarNotas(); }
    @GetMapping("/alumno/{id}")
    public List<NotaModel> porAlumno(@PathVariable Long id) { return notaService.obtenerPorAlumno(id); }
    @GetMapping("/alumno/{id}/promedio")
    public Map<String, Object> promedio(@PathVariable Long id) { return notaService.obtenerPromedioAlumno(id); }
    @PostMapping
    public ResponseEntity<NotaModel> registrar(@RequestBody NotaModel nota) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notaService.registrarNota(nota));
    }
    @PutMapping("/{id}")
    public ResponseEntity<NotaModel> actualizar(@PathVariable Long id, @RequestBody NotaModel nota) {
        NotaModel actualizada = notaService.actualizarNota(id, nota);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notaService.eliminarNota(id);
        return ResponseEntity.noContent().build();
    }
}
