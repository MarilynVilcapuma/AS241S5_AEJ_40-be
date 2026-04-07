# InstgranIA_SQL

InstgranIA_SQL es una API REST reactiva construida con Spring Boot que actúa como puente entre Instagram (a través de RapidAPI) y una base de datos PostgreSQL en la nube (Neon).

El objetivo del proyecto es permitir consultar, extraer y persistir datos públicos de Instagram — perfiles, publicaciones y enlaces de descarga de media — de forma estructurada y analítica, sin necesidad de autenticación directa con Instagram.

---

## ✅ APIs IA y Características

### API Utilizada — RapidAPI Instagram120

| Característica | Detalle |
|---|---|
| **Proveedor** | RapidAPI — `instagram120.p.rapidapi.com` |
| **Autenticación** | Header `x-rapidapi-key` + `x-rapidapi-host` |
| **Protocolo** | HTTPS con SSL |
| **Formato** | JSON |

### Endpoints consumidos

| Endpoint | Método | Descripción |
|---|---|---|
| `/api/instagram/profile` | POST | Obtiene datos del perfil público |
| `/api/instagram/links` | POST | Extrae links de descarga de posts, reels e imágenes |

### Características principales de la API

- **Sin autenticación de usuario** — no requiere login ni token de Instagram
- **Contenido público** — accede a perfiles y publicaciones públicas
- **Multi-formato** — devuelve links de descarga para imágenes (JPG), videos (MP4) y reels
- **Metadatos incluidos** — likes, comentarios, shortcode, thumbnail, timestamp de publicación
- **Respuesta JSON estructurada** — perfil con datos anidados (`edge_followed_by.count`), posts con array de `urls[]` y objeto `meta`

### Características del proyecto

- **Programación reactiva** — no bloqueante de extremo a extremo con `Mono` y `Flux`
- **Descarga inteligente** — si el post ya existe en BD, retorna desde BD sin consumir cuota de RapidAPI
- **Perfil automático** — si el perfil no existe al guardar un post, se crea automáticamente (integridad FK)
- **Limpieza de URLs** — elimina parámetros `?igsh=...` que la API no acepta
- **Trazabilidad** — cada ejecución del pipeline queda registrada con estado y contadores

---

## ✅ Herramientas y Versiones Utilizadas

### Lenguaje y Framework

| Herramienta | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.13 |
| Spring WebFlux | 6.2.x |
| Spring Data R2DBC | 3.5.x |
| Maven | 3.x |

### Base de Datos

| Herramienta | Versión | Uso |
|---|---|---|
| PostgreSQL (Neon Cloud) | 15+ | Base de datos relacional en la nube |
| R2DBC PostgreSQL Driver | 1.0.x | Driver reactivo para PostgreSQL |
| R2DBC Pool | 1.0.2 | Pool de conexiones reactivo |

### Librerías

| Librería | Versión | Uso |
|---|---|---|
| Reactor Netty | 1.2.16 | Cliente HTTP reactivo (WebClient) |
| Netty Handler | 4.1.132 | Manejo SSL para conexiones HTTPS |
| Lombok | — | Reducción de boilerplate (`@Data`, `@Builder`) |
| Jackson Databind | 2.x | Serialización/deserialización JSON |
| SpringDoc OpenAPI | 2.8.6 | Generación automática de Swagger UI |
| Reactor Test | — | Testing reactivo |

### Herramientas de Desarrollo

| Herramienta | Uso |
|---|---|
| VS Code | Editor principal |
| Swagger UI | Prueba de endpoints (`http://localhost:8084/swagger-ui.html`) |
| RapidAPI Console | Prueba directa de endpoints de la API externa |
| Neon Dashboard | Administración de la base de datos PostgreSQL en la nube |
| Git | Control de versiones |

### Configuración del Servidor

| Parámetro | Valor |
|---|---|
| Puerto | `8084` |
| Base URL | `http://localhost:8084` |
| Swagger UI | `http://localhost:8084/swagger-ui.html` |
| API Docs JSON | `http://localhost:8084/api-docs` |

---

## ✅ Estructura del Proyecto

```
InstgranIA_SQL/
│
├── src/main/java/com/example/instgrania_sql/
│   │
│   ├── InstgranIaSqlApplication.java         # Punto de entrada de la aplicación
│   │
│   ├── controller/
│   │   └── InstagramController.java          # Expone los endpoints REST de la API
│   │
│   ├── service/
│   │   └── InstagramService.java             # Lógica de negocio y orquestación
│   │
│   ├── client/
│   │   └── InstagramClient.java              # Comunicación HTTP con RapidAPI
│   │
│   ├── repository/
│   │   ├── PostRepository.java               # CRUD reactivo tabla posts
│   │   ├── ProfileRepository.java            # CRUD reactivo tabla profiles
│   │   └── PipelineExecutionRepository.java  # CRUD reactivo tabla pipeline_executions
│   │
│   ├── model/
│   │   ├── Post.java                         # Entidad mapeada a tabla posts
│   │   ├── Profile.java                      # Entidad mapeada a tabla profiles
│   │   └── PipelineExecution.java            # Entidad mapeada a tabla pipeline_executions
│   │
│   ├── dto/
│   │   ├── InstagramProfileApiResponse.java  # Wrapper del JSON de perfil { "result": {...} }
│   │   ├── InstagramProfileDto.java          # Datos del perfil deserializados de RapidAPI
│   │   ├── InstagramPostDto.java             # Datos del post/links deserializados de RapidAPI
│   │   ├── DownloadRequest.java              # Body del request POST /download
│   │   └── DownloadResponse.java            # Respuesta estructurada con links de descarga
│   │
│   └── config/
│       └── WebClientConfig.java              # Configura WebClient con SSL y headers RapidAPI
│
├── src/main/resources/
│   ├── application.yaml                      # Configuración de BD, puerto y RapidAPI
│   └── schema.sql                            # Script SQL — crea tablas e índices al arrancar
│
├── src/test/
│   └── InstgranIaSqlApplicationTests.java    # Tests de integración
│
├── pom.xml                                   # Dependencias y configuración de Maven
└── README.md                                 # Documentación del proyecto
```

### Responsabilidad de cada capa

| Capa | Archivo | Responsabilidad |
|---|---|---|
| **Controller** | `InstagramController` | Recibe HTTP, delega al service, retorna JSON |
| **Service** | `InstagramService` | Lógica de negocio, decisiones, transformaciones |
| **Client** | `InstagramClient` | Llamadas HTTP a RapidAPI, limpieza de URLs |
| **Repository** | `*Repository` | Operaciones CRUD contra PostgreSQL (sin SQL manual) |
| **Model** | `Post`, `Profile`, `PipelineExecution` | Representan las tablas de la BD |
| **DTO** | `*Dto`, `*Request`, `*Response` | Transportan datos entre capas y con la API externa |
| **Config** | `WebClientConfig` | Configura beans de infraestructura (WebClient + SSL) |

### Flujo entre capas

```
HTTP Request
     │
     ▼
 Controller  →  Service  →  Client (RapidAPI)
                        →  Repository (PostgreSQL)
                        →  DTOs (transformación)
     │
     ▼
HTTP Response
```
