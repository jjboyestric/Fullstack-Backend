package MIcroservicioB.BffOrquestador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BffOrquestadorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BffOrquestadorApplication.class, args);
		System.out.println("\n----------------------------------------------------------");
		System.out.println("BFF Orquestador - Colegio Bernardo O'Higgins");
		System.out.println("Swagger UI: http://localhost:8080/swagger-ui.html");
		System.out.println("----------------------------------------------------------\n");
	}
}
