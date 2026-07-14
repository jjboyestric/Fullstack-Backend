package MIcroservicioB.MicroservicioAsistencia.controller;
import MIcroservicioB.MicroservicioAsistencia.model.AsistenciaModel;
import MIcroservicioB.MicroservicioAsistencia.service.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/asistencia")
@CrossOrigin(origins = "*")
public class AsistenciaController {
    @Autowired
    private AsistenciaService asistenciaService;
    @GetMapping
    public List<AsistenciaModel> listar() { return asistenciaService.listar(); }
    @GetMapping("/alumno/{id}")
    public List<AsistenciaModel> porAlumno(@PathVariable Long id) { return asistenciaService.obtenerPorAlumno(id); }
    @GetMapping("/alumno/{id}/resumen")
    public Map<String, Object> resumen(@PathVariable Long id) { return asistenciaService.obtenerResumen(id); }
    @PostMapping
    public ResponseEntity<AsistenciaModel> registrar(@RequestBody AsistenciaModel a) {
        return ResponseEntity.status(HttpStatus.CREATED).body(asistenciaService.registrar(a));
    }
    @PutMapping("/{id}")
    public ResponseEntity<AsistenciaModel> actualizar(@PathVariable Long id, @RequestBody AsistenciaModel a) {
        AsistenciaModel actualizada = asistenciaService.actualizar(id, a);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asistenciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
