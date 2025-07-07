# EduTech Microservices Platform

**EduTech** es una plataforma educativa modular basada en microservicios desarrollada con Java 17, Spring Boot y Maven. Este proyecto está orientado a la gestión integral de usuarios, cursos, pagos, evaluaciones y soporte técnico.

## 🚀 Tecnologías utilizadas

- Java 17
- Spring Boot 3.5.x
- Maven
- Base de datos en memoria H2 por microservicio
- OpenAPI / Swagger para documentación de la API
- JUnit + Mockito para pruebas unitarias

## 🧩 Estructura de microservicios

Cada microservicio funciona de forma independiente, escuchando en su propio puerto:

| Servicio     | Descripción                        | Puerto | Swagger UI                                 |
|--------------|------------------------------------|--------|---------------------------------------------|
| Users        | Gestión de usuarios y roles        | `8080` | [Ver Swagger](http://localhost:8080/swagger-ui.html) |
| Courses      | Cursos, niveles, categorías        | `8081` | [Ver Swagger](http://localhost:8081/swagger-ui.html) |
| Payments     | Registro y control de pagos        | `8082` | [Ver Swagger](http://localhost:8082/swagger-ui.html) |
| Evaluations  | Evaluaciones por asignatura        | `8083` | [Ver Swagger](http://localhost:8083/swagger-ui.html) |
| Support      | Gestión de tickets de soporte      | `8084` | [Ver Swagger](http://localhost:8084/swagger-ui.html) |

## 📦 Requisitos previos

- Java 17+
- Maven 3.8+
- IDE compatible con Spring Boot (IntelliJ IDEA, STS, VSCode)

## ▶️ Instrucciones de ejecución

1. Clona este repositorio:

   ```bash
   git clone https://github.com/OmarNietoc/Edutech
   cd edutech-microservices
   ```

2. Abre el proyecto en tu IDE favorito.

3. Ejecuta cada microservicio desde su clase `Application.java` o con el siguiente comando:

   ```bash
   mvn spring-boot:run
   ```

4. Accede a la documentación de cada microservicio en Swagger UI (ver tabla arriba).

## 🧪 Testing

- Se incluyen pruebas unitarias con JUnit y Mockito.
- Se usa JaCoCo para análisis de cobertura.
- Ejecuta las pruebas con:

   ```bash
   mvn test
   ```

## 📬 Postman

Importar en postman el archivo:
```bash
    edtutech\edutech.postman_collection.json
```
## 📥 Ejemplos de Peticiones (JSON)

```json
{
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "password": "12345678",
  "status": true,
  "role": 1
}
```

## 🧠 Arquitectura del sistema

La arquitectura sigue principios de Domain-Driven Design (DDD). Cada microservicio representa un dominio de negocio separado:

- **Users**: Gestión de usuarios y roles
- **Courses**: Gestión de cursos, niveles, categorías y matrículas
- **Payments**: Pagos y estados
- **Evaluations**: Evaluaciones asociadas a asignaturas
- **Support**: Tickets y estados de soporte

Esto permite escalar, probar y desplegar servicios de forma independiente.

## 📄 Licencias

Proyecto desarrollado con fines académicos. Todos los derechos reservados © 2025.
