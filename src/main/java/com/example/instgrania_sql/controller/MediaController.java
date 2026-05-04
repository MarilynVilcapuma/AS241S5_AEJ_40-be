package com.example.instgrania_sql.controller;

import com.example.instgrania_sql.dto.MediaRequest;
import com.example.instgrania_sql.model.Post;
import com.example.instgrania_sql.service.InstagramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Media", description = "Gestión de imágenes y videos de Instagram")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MediaController {

    private final InstagramService instagramService;

    @Operation(
            summary = "Guardar media",
            description = "Descarga y guarda la imagen o video del post de Instagram vinculado al perfil"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Media guardado exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error al consultar la API de Instagram")
    })
    @PostMapping("/profiles/{username:.+}/media")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Post> create(
            @Parameter(description = "Nombre de usuario de Instagram", example = "cristiano")
            @PathVariable String username,
            @RequestBody MediaRequest request) {
        return instagramService.createMedia(username, request.getPostUrl());
    }

    @Operation(
            summary = "Listar todos los media",
            description = "Retorna todos los media registrados. Filtra por estado con ?active=true|false"
    )
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping("/media")
    public Flux<Post> listAll(
            @Parameter(description = "true = activos, false = inactivos. Omitir para listar todos")
            @RequestParam(required = false) Boolean active) {
        return instagramService.listAllMedia(active);
    }

    @Operation(
            summary = "Listar media por usuario",
            description = "Retorna todos los media de un usuario. Filtra por estado con ?active=true|false"
    )
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping("/profiles/{username:.+}/media")
    public Flux<Post> listByUser(
            @Parameter(description = "Nombre de usuario de Instagram", example = "cristiano")
            @PathVariable String username,
            @Parameter(description = "true = activos, false = inactivos. Omitir para listar todos")
            @RequestParam(required = false) Boolean active) {
        return instagramService.listMedia(username, active);
    }

    @Operation(
            summary = "Desactivar media",
            description = "Eliminado lógico: establece active = false en el media indicado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Media desactivado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Media no encontrado")
    })
    @PatchMapping("/media/{id}/deactivate")
    public Mono<Post> deactivate(
            @Parameter(description = "ID del media", example = "1")
            @PathVariable Long id) {
        return instagramService.deactivateMedia(id);
    }

    @Operation(
            summary = "Activar media",
            description = "Restaurado lógico: establece active = true en el media indicado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Media activado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Media no encontrado")
    })
    @PatchMapping("/media/{id}/activate")
    public Mono<Post> activate(
            @Parameter(description = "ID del media", example = "1")
            @PathVariable Long id) {
        return instagramService.activateMedia(id);
    }
}
