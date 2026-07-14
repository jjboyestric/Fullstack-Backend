-- Datos de prueba para MicroservicioUsuarios
-- Se cargan automáticamente al iniciar la aplicación (H2 in-memory)

-- Cursos
INSERT INTO cursos (nombre) VALUES
('4°A'), ('4°B'), ('4°C');

-- Profesores
INSERT INTO profesores (nombre_completo, email, asignatura) VALUES
('Isidora Fernández Rojas', 'isidora.fernandez@profesor.cl', 'Inglés'),
('Pablo Contreras Muñoz', 'pablo.contreras@profesor.cl', 'Matemática'),
('Carla Torres Sepúlveda', 'carla.torres@profesor.cl', 'Historia');

-- Alumnos
INSERT INTO alumnos (nombre_completo, rut, curso) VALUES
('Valentina González Rodríguez', '20000000-K', '4°A'),
('Benjamín Muñoz López', '20000097-0', '4°A'),
('Martina Rojas Fuentes', '20000194-1', '4°A'),
('Mateo Díaz Hernández', '20000291-2', '4°A'),
('Isidora Pérez Torres', '20000388-3', '4°A'),
('Vicente Soto Araya', '20000485-4', '4°A'),
('Antonia Contreras Flores', '20000582-5', '4°A'),
('Joaquín Silva Espinoza', '20000679-6', '4°A'),
('Florencia Martínez Reyes', '20000776-7', '4°A'),
('Agustín Sepúlveda Gutiérrez', '20000873-8', '4°A'),
('Josefa Morales Vásquez', '20000970-9', '4°A'),
('Tomás Rodríguez Castillo', '20001067-K', '4°A'),
('Emilia López Vargas', '20001164-0', '4°A'),
('Diego Fuentes Ramírez', '20001261-1', '4°A'),
('Catalina Hernández Núñez', '20001358-2', '4°A'),
('Maximiliano Torres Carrasco', '20001455-3', '4°A'),
('Amanda Araya Tapia', '20001552-4', '4°A'),
('Cristóbal Flores Sánchez', '20001649-5', '4°A'),
('Renata Espinoza Pizarro', '20001746-6', '4°A'),
('Ignacio Reyes Vera', '20001843-7', '4°A'),
('Fernanda Gutiérrez Bravo', '20001940-8', '4°A'),
('Gaspar Vásquez Escobar', '20002037-9', '4°B'),
('Trinidad Castillo Riquelme', '20002134-K', '4°B'),
('Simón Vargas Cárdenas', '20002231-0', '4°B'),
('Rafaela Ramírez Cortés', '20002328-1', '4°B'),
('Bastián Núñez Miranda', '20002425-2', '4°B'),
('Constanza Carrasco Herrera', '20002522-3', '4°B'),
('Matías Tapia Toro', '20002619-4', '4°B'),
('Antonella Sánchez Salazar', '20002716-5', '4°B'),
('Felipe Pizarro González', '20002813-6', '4°B'),
('Javiera Vera Muñoz', '20002910-7', '4°B'),
('Nicolás Bravo Rojas', '20003007-8', '4°B'),
('Sofía Escobar Díaz', '20003104-9', '4°B'),
('Lucas Riquelme Pérez', '20003201-K', '4°B'),
('Camila Cárdenas Soto', '20003298-0', '4°B'),
('Santiago Cortés Contreras', '20003395-1', '4°B'),
('Paz Miranda Silva', '20003492-2', '4°B'),
('Emiliano Herrera Martínez', '20003589-3', '4°B'),
('Josefina Toro Sepúlveda', '20003686-4', '4°B'),
('Franco Salazar Morales', '20003783-5', '4°B'),
('Amanda González Rodríguez', '20003880-6', '4°B'),
('Dante Muñoz López', '20003977-7', '4°B'),
('Isabella Rojas Fuentes', '20004074-8', '4°B'),
('Julián Díaz Hernández', '20004171-9', '4°C'),
('Millaray Pérez Torres', '20004268-K', '4°C'),
('Alonso Soto Araya', '20004365-0', '4°C'),
('Magdalena Contreras Flores', '20004462-1', '4°C'),
('Ian Silva Espinoza', '20004559-2', '4°C'),
('Consuelo Martínez Reyes', '20004656-3', '4°C'),
('Thiago Sepúlveda Gutiérrez', '20004753-4', '4°C'),
('Valentina Morales Vásquez', '20004850-5', '4°C'),
('Benjamín Rodríguez Castillo', '20004947-6', '4°C'),
('Martina López Vargas', '20005044-7', '4°C'),
('Mateo Fuentes Ramírez', '20005141-8', '4°C'),
('Isidora Hernández Núñez', '20005238-9', '4°C'),
('Vicente Torres Carrasco', '20005335-K', '4°C'),
('Antonia Araya Tapia', '20005432-0', '4°C'),
('Joaquín Flores Sánchez', '20005529-1', '4°C'),
('Florencia Espinoza Pizarro', '20005626-2', '4°C'),
('Agustín Reyes Vera', '20005723-3', '4°C'),
('Josefa Gutiérrez Bravo', '20005820-4', '4°C'),
('Tomás Vásquez Escobar', '20005917-5', '4°C'),
('Emilia Castillo Riquelme', '20006014-6', '4°C'),
('Diego Vargas Cárdenas', '20006111-7', '4°C'),
('Catalina Ramírez Cortés', '20006208-8', '4°C'),
('Maximiliano Núñez Miranda', '20006305-9', '4°C');

-- Usuarios (login) — contraseña ficticia para todos: 12345
INSERT INTO usuarios (email, password, rol, nombre_completo, ref_id) VALUES
('ricardo.gomez@director.cl', '12345', 'DIRECTOR', 'Ricardo Gómez Alarcón', NULL),
('isidora.fernandez@profesor.cl', '12345', 'PROFESOR', 'Isidora Fernández Rojas', 1),
('pablo.contreras@profesor.cl', '12345', 'PROFESOR', 'Pablo Contreras Muñoz', 2),
('carla.torres@profesor.cl', '12345', 'PROFESOR', 'Carla Torres Sepúlveda', 3),
('vicente.soto@alumno.cl', '12345', 'ALUMNO', 'Vicente Soto Araya', 6),
('constanza.carrasco@alumno.cl', '12345', 'ALUMNO', 'Constanza Carrasco Herrera', 27),
('consuelo.martinez@alumno.cl', '12345', 'ALUMNO', 'Consuelo Martínez Reyes', 49);
