package MIcroservicioB.MicroservicioAsistencia;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class MicroservicioAsistenciaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicroservicioAsistenciaApplication.class, args);
        System.out.println("\n--- Microservicio Asistencia - Puerto 8082 ---");
        System.out.println("Swagger: http://localhost:8082/swagger-ui.html");
        System.out.println("H2:      http://localhost:8082/h2-console\n");
    }
}
