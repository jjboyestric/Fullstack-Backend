package MIcroservicioB.MicroservicioNotas;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class MicroservicioNotasApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicroservicioNotasApplication.class, args);
        System.out.println("\n--- Microservicio Notas - Puerto 8081 ---");
        System.out.println("Swagger: http://localhost:8081/swagger-ui.html");
        System.out.println("H2:      http://localhost:8081/h2-console\n");
    }
}
