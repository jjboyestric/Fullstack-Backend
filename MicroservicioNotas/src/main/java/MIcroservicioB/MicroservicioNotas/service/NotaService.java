package MIcroservicioB.MicroservicioNotas.service;
import MIcroservicioB.MicroservicioNotas.model.NotaModel;
import MIcroservicioB.MicroservicioNotas.repository.NotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
@Service
public class NotaService {
    @Autowired
    private NotaRepository notaRepository;
    public List<NotaModel> listarNotas() {
        System.out.println("LOG: Consultando todas las notas.");
        return notaRepository.findAll();
    }
    public List<NotaModel> obtenerPorAlumno(Long alumnoId) {
        System.out.println("LOG: Notas del alumno ID: " + alumnoId);
        return notaRepository.findByAlumnoId(alumnoId);
    }
    public NotaModel registrarNota(NotaModel nota) {
        System.out.println("LOG: Registrando nota para: " + nota.getNombreAlumno());
        return notaRepository.save(nota);
    }
    public NotaModel actualizarNota(Long id, NotaModel cambios) {
        return notaRepository.findById(id).map(existente -> {
            existente.setAsignatura(cambios.getAsignatura());
            existente.setTipoEvaluacion(cambios.getTipoEvaluacion());
            existente.setNota(cambios.getNota());
            existente.setFechaEvaluacion(cambios.getFechaEvaluacion());
            existente.setObservaciones(cambios.getObservaciones());
            System.out.println("LOG: Actualizando nota ID " + id + " -> " + cambios.getNota());
            return notaRepository.save(existente);
        }).orElse(null);
    }
    public void eliminarNota(Long id) { notaRepository.deleteById(id); }
    public Map<String, Object> obtenerPromedioAlumno(Long alumnoId) {
        Double promedio = notaRepository.calcularPromedioAlumno(alumnoId);
        List<NotaModel> notas = notaRepository.findByAlumnoId(alumnoId);
        String nombre = notas.isEmpty() ? "Alumno " + alumnoId : notas.get(0).getNombreAlumno();
        String curso  = notas.isEmpty() ? "N/A" : notas.get(0).getCurso();
        return Map.of("alumnoId", alumnoId, "nombre", nombre, "curso", curso,
            "promedioGeneral", promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0,
            "totalEvaluaciones", notas.size());
    }
}
