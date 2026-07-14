package MIcroservicioB.MicroservicioUsuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MicroservicioUsuariosApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicroservicioUsuariosApplication.class, args);
        System.out.println("\n--- Microservicio Usuarios - Puerto 8083 ---");
        System.out.println("Swagger: http://localhost:8083/swagger-ui.html");
        System.out.println("H2:      http://localhost:8083/h2-console\n");
    }
}
