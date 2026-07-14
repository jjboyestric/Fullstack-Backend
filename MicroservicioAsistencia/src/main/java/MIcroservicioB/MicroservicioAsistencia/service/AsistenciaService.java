package MIcroservicioB.MicroservicioAsistencia.service;
import MIcroservicioB.MicroservicioAsistencia.model.AsistenciaModel;
import MIcroservicioB.MicroservicioAsistencia.repository.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
@Service
public class AsistenciaService {
    @Autowired
    private AsistenciaRepository asistenciaRepository;
    public List<AsistenciaModel> listar() {
        System.out.println("LOG: Consultando registros de asistencia.");
        return asistenciaRepository.findAll();
    }
    public List<AsistenciaModel> obtenerPorAlumno(Long alumnoId) {
        System.out.println("LOG: Asistencia del alumno ID: " + alumnoId);
        return asistenciaRepository.findByAlumnoId(alumnoId);
    }
    public AsistenciaModel registrar(AsistenciaModel a) {
        System.out.println("LOG: Registrando asistencia [" + a.getEstado() + "] para alumno: " + a.getNombreAlumno());
        return asistenciaRepository.save(a);
    }
    public AsistenciaModel actualizar(Long id, AsistenciaModel cambios) {
        return asistenciaRepository.findById(id).map(existente -> {
            existente.setFecha(cambios.getFecha());
            existente.setEstado(cambios.getEstado());
            existente.setAnotacion(cambios.getAnotacion());
            existente.setDocente(cambios.getDocente());
            System.out.println("LOG: Actualizando asistencia ID " + id + " -> " + cambios.getEstado());
            return asistenciaRepository.save(existente);
        }).orElse(null);
    }
    public void eliminar(Long id) { asistenciaRepository.deleteById(id); }
    public Map<String, Object> obtenerResumen(Long alumnoId) {
        Long presencias = asistenciaRepository.contarPresencias(alumnoId);
        Long ausencias  = asistenciaRepository.contarAusencias(alumnoId);
        Long total      = asistenciaRepository.contarTotal(alumnoId);
        double porcentaje = total > 0 ? Math.round((presencias * 100.0 / total) * 10.0) / 10.0 : 0.0;
        return Map.of("alumnoId", alumnoId, "asistidas", presencias,
            "faltadas", ausencias, "totalClases", total, "porcentaje", porcentaje);
    }
}
