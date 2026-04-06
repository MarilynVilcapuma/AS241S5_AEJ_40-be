# AS241S5_AEJ_40-be — DetectorIA NoSQL

Microservicio REST reactivo desarrollado con **Spring WebFlux** y **MongoDB** que detecta si un texto fue escrito por un humano o generado por Inteligencia Artificial.

---

## API IA utilizada

### AI Detection API — RapidAPI (`ai-detection4`)

| Característica | Detalle |
|---|---|
| Proveedor | RapidAPI — `ai-detection4.p.rapidapi.com` |
| Endpoint | `POST /v1/ai-detection-rapid-api` |
| Autenticación | API Key via headers `X-RapidAPI-Key` y `X-RapidAPI-Host` |
| Entrada | JSON con campo `text` (texto a analizar) |
| Salida | `ai_score`, `summary` (human / ai / mixed), `total_num_words`, `lang` |
| Veredicto | `HUMAN` si humanScore ≥ 0.5 · `AI` si aiScore ≥ 0.5 · `MIXED` en otro caso |
| Timeout | 15 segundos (conexión, respuesta y lectura) |

**Ejemplo de respuesta de la API:**
```json
{
  "ai_score": 0.85,
  "summary": {
    "human": 0.15,
    "ai": 0.85,
    "mixed": 0.0
  },
  "total_num_words": 42,
  "lang": "es"
}
```

---

## Herramientas y versiones utilizadas

| Herramienta | Versión | Rol en el proyecto |
|---|---|---|
| Java | 17 | Lenguaje de programación |
| Spring Boot | 3.5.13 | Framework base |
| Spring WebFlux | 3.5.13 | Servidor HTTP reactivo (no bloqueante) |
| Spring Data MongoDB Reactive | 3.5.13 | Persistencia reactiva en MongoDB |
| Project Reactor | incluido en WebFlux | Programación reactiva (`Mono`, `Flux`) |
| Reactor Netty | incluido en WebFlux | Cliente HTTP reactivo (`WebClient`) |
| MongoDB Atlas | Cloud | Base de datos NoSQL en la nube |
| Springdoc OpenAPI | 2.8.16 | Documentación automática Swagger UI |
| Lombok | latest (Spring Boot managed) | Reducción de boilerplate (`@Data`, `@Builder`) |
| Maven | 3.x | Gestión de dependencias y build |

---

## Arquitectura del proyecto

```
src/main/java/com/example/detectoria_nosql/
│
├── config/
│   └── WebClientConfig.java          # Configura WebClient con timeouts y URL base
│
├── controller/
│   └── DetectorController.java       # Endpoint POST /api/detection
│
├── service/
│   └── RapidApiDetectorService.java  # Lógica: llama API, calcula veredicto, persiste
│
├── repository/
│   └── DetectionRepository.java      # Acceso reactivo a MongoDB
│
├── model/
│   └── DetectionDocument.java        # Documento guardado en colección "detections"
│
└── dto/
    ├── DetectRequest.java             # Entrada: { "text": "..." }
    ├── DetectResponse.java            # Respuesta de RapidAPI
    └── DetectionResultResponse.java  # Respuesta al cliente
```

---

## Flujo de una petición

```
Cliente
  │  POST /api/detection { "text": "..." }
  ▼
Controller → Service → WebClient → RapidAPI (externa)
                     ←────────────────────────────────
                     calcula veredicto (HUMAN / AI / MIXED)
                     guarda en MongoDB via Repository
  ▼
Cliente recibe: { "verdict": "AI", "humanScore": 0.15, "aiScore": 0.85, ... }
```

---

## Documentación de la API

Con la aplicación corriendo, accede a:

- **Swagger UI:** `http://localhost:8082/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8082/api-docs`
