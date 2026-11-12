# Consodig Consultores - Project Management Service

Microservicio backend para la gestión de proyectos y procesos de evaluación de **Consodig Consultores**. Permite registrar proyectos, adjuntar documentos, configurar formatos de evaluación y coordinar el trabajo entre administradores y evaluadores.

## Funcionalidades

### Proyectos

- Crear, consultar, listar, actualizar y eliminar proyectos.
- Asociar proyectos con investigadores y líneas de investigación.
- Registrar título, resumen, palabras clave, objetivos, justificación y nivel de estudio.
- Consultar proyectos por investigador y sus archivos asociados.

### Formatos y criterios

- Crear, consultar, actualizar y eliminar formatos.
- Activar o desactivar formatos.
- Agregar y eliminar ítems de un formato.
- Configurar el peso de cada ítem y calcular el peso total.
- Crear, consultar, actualizar y eliminar criterios.
- Asociar ítems con criterios de evaluación.

### Evaluaciones

- Asignar un formato y un evaluador a un proyecto.
- Definir tiempo límite y calificación mínima requerida.
- Aceptar o rechazar evaluaciones asignadas.
- Calificar ítems con observaciones.
- Validar que la calificación no supere el peso del ítem.
- Finalizar evaluaciones cuando todos los ítems han sido calificados.
- Calcular la calificación total y determinar aprobación.
- Editar ítems evaluados y recalcular la calificación.
- Validar evaluaciones completadas.
- Invalidar evaluaciones con motivo y crear una reevaluación.
- Consultar evaluaciones por estado, proyecto o identificador.

### Archivos y notificaciones

- Cargar archivos relacionados con proyectos mediante Cloudinary.
- Consultar archivos asociados a proyectos.
- Notificar asignaciones y cambios de estado de evaluaciones.
- Consultar notificaciones y marcarlas como leídas.

## Flujo de evaluación

1. Un administrador selecciona un proyecto, un formato y un evaluador.
2. El servicio crea la evaluación con tiempo límite y calificación mínima.
3. El evaluador acepta o rechaza la asignación.
4. Si acepta, califica los ítems y registra observaciones.
5. El servicio calcula la calificación total y marca la evaluación como aprobada o no aprobada.
6. El administrador puede validar la evaluación completada.
7. Si se invalida, se conserva el motivo y se crea una nueva evaluación para repetir el proceso.
8. Los eventos relevantes generan notificaciones para los usuarios involucrados.

## Estados de evaluación

| Estado | Descripción |
|---|---|
| `ASIGNADA` | Evaluación creada y pendiente de respuesta |
| `ACEPTADA` | El evaluador aceptó y puede calificar |
| `RECHAZADA` | El evaluador rechazó o excedió el tiempo de aceptación |
| `COMPLETADA` | Todos los ítems fueron calificados y la evaluación finalizó |

Una evaluación completada puede validarse o invalidarse. La invalidación conserva el historial y permite generar una reevaluación.

## Stack tecnológico

- Java 21
- Spring Boot 3.5.7
- Spring Web
- Spring Data JPA / Hibernate
- MySQL
- Cloudinary
- Thymeleaf
- Lombok
- Maven
- Docker

## Requisitos y configuración

- JDK 21
- MySQL
- Git
- Cuenta de Cloudinary
- Acceso a los identificadores de usuarios del servicio de autenticación

Clona el repositorio:

```bash
git clone https://github.com/BSebastianGonzalez/cc_project_management.git
cd cc_project_management
```

Crea la base de datos:

```sql
CREATE DATABASE consodig;
```

Configura las variables de entorno:

```properties
DB_URL=jdbc:mysql://localhost:3306/consodig
DB_USER=tu_usuario_mysql
DB_PASSWORD=tu_contraseña_mysql
CLOUDINARY_NAME=tu_cloud_name
CLOUDINARY_KEY=tu_api_key
CLOUDINARY_SECRET=tu_api_secret
PORT=8081
```

Inicia el servicio:

```powershell
.\mvnw.cmd spring-boot:run
```

El puerto predeterminado es `8081`, salvo que se defina `PORT`.

## API REST

Todos los endpoints utilizan el prefijo `/api`.

### Proyectos

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/proyectos` | Crear proyecto |
| `GET` | `/api/proyectos` | Listar proyectos |
| `GET` | `/api/proyectos/{id}` | Consultar proyecto |
| `PUT` | `/api/proyectos/{id}` | Actualizar proyecto |
| `DELETE` | `/api/proyectos/{id}` | Eliminar proyecto |
| `GET` | `/api/proyectos/{id}/archivos` | Consultar archivos |
| `GET` | `/api/proyectos/investigador/{investigadorId}` | Listar por investigador |

### Evaluaciones

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/evaluaciones/asignar` | Asignar evaluación |
| `PUT` | `/api/evaluaciones/{id}/aceptar` | Aceptar evaluación |
| `PUT` | `/api/evaluaciones/{id}/rechazar` | Rechazar evaluación |
| `POST` | `/api/evaluaciones/{id}/items` | Calificar un ítem |
| `PUT` | `/api/evaluaciones/{id}/finalizar` | Finalizar evaluación |
| `GET` | `/api/evaluaciones` | Listar evaluaciones |
| `GET` | `/api/evaluaciones/{id}` | Consultar evaluación |
| `GET` | `/api/evaluaciones/estado/{estado}` | Filtrar por estado |
| `GET` | `/api/evaluaciones/proyecto/{proyectoId}` | Listar por proyecto |
| `PUT` | `/api/evaluaciones/{evaluacionId}/editar` | Editar ítems evaluados |
| `POST` | `/api/evaluaciones/{id}/validar` | Validar evaluación |
| `POST` | `/api/evaluaciones/{id}/invalidar` | Invalidar y generar reevaluación |

### Formatos, criterios, archivos y notificaciones

| Recurso | Ruta base | Operaciones principales |
|---|---|---|
| Formatos | `/api/formatos` | CRUD, ítems y activación |
| Criterios | `/api/criterios` | CRUD y asociación con ítems |
| Archivos | `/api/archivos` | Carga de evidencias de proyectos |
| Notificaciones | `/api/notificaciones` | Consulta y marcado como leído |

## Arquitectura

```text
src/main/java/com/cc/be/
├── controller/   # Endpoints REST
├── dto/          # Objetos de entrada y respuesta
├── model/        # Entidades JPA y estados
├── repository/   # Repositorios Spring Data
└── service/      # Casos de uso y reglas de negocio
```

Los dominios principales son `proyecto`, `evaluacion`, `formato`, `criterio`, `archivo` y `notificacion`.

## Reglas de negocio relevantes

- Un proyecto no puede tener más de una evaluación activa.
- Un proyecto completado solo puede volver a evaluarse mediante invalidación y reevaluación.
- La aceptación está limitada por el tiempo configurado.
- Solo una evaluación aceptada puede recibir calificaciones.
- Una calificación no puede superar el peso máximo del ítem.
- No se puede finalizar una evaluación con ítems pendientes.
- La aprobación compara la calificación total con la calificación mínima requerida.
- La invalidación conserva el motivo y la referencia de la evaluación original.

## Pruebas, compilación y Docker

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

El artefacto se genera en `target/cc_pm-0.0.1-SNAPSHOT.jar`.

```bash
./mvnw clean package -DskipTests
docker build -t consodig-project-management .
docker run --rm -p 8081:8081 --env-file .env consodig-project-management
```

## Integración

Este servicio utiliza identificadores de cuentas y perfiles administrados por:

<https://github.com/BSebastianGonzalez/cc_authentication>

La separación permite mantener las responsabilidades de identidad y de gestión de proyectos en microservicios independientes.

## Consideraciones

- Los archivos se almacenan en Cloudinary y la base de datos conserva sus referencias.
- El servicio mantiene referencias por identificador hacia usuarios del microservicio de autenticación.
- Para producción se recomiendan migraciones versionadas en lugar de `ddl-auto=update`.
- Las credenciales de base de datos y Cloudinary deben mantenerse como secretos de entorno.
