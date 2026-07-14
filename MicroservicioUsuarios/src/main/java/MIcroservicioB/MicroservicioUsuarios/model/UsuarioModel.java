package MIcroservicioB.MicroservicioUsuarios.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class UsuarioModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password; // NOTA: contraseña en texto plano solo para fines académicos/demostrativos.

    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    private String nombreCompleto;

    /**
     * Referencia al id de ProfesorModel o AlumnoModel según el rol.
     * Es null cuando el rol es DIRECTOR (el director no tiene una entidad asociada).
     */
    private Long refId;

    public enum RolUsuario { DIRECTOR, PROFESOR, ALUMNO }
}
