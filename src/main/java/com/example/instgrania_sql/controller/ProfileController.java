package com.example.instgrania_sql.controller;

import com.example.instgrania_sql.model.Profile;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Profiles", description = "Gestión de perfiles de Instagram")
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final InstagramService instagramService;

    @Operation(
            summary = "Registrar perfil",
            description = "Consulta la API de Instagram y guarda el perfil en la base de datos"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil registrado exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error al consultar la API de Instagram")
    })
    @PostMapping("/{username:.+}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Profile> create(
            @Parameter(description = "Nombre de usuario de Instagram", example = "cristiano")
            @PathVariable String username) {
        return instagramService.createProfile(username);
    }

    @Operation(
            summary = "Listar perfiles",
            description = "Retorna todos los perfiles. Filtra por estado con ?active=true|false"
    )
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public Flux<Profile> list(
            @Parameter(description = "true = activos, false = inactivos. Omitir para listar todos")
            @RequestParam(required = false) Boolean active) {
        return instagramService.listProfiles(active);
    }

    @Operation(
            summary = "Obtener perfil",
            description = "Retorna el perfil guardado en base de datos por nombre de usuario"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @GetMapping("/{username:.+}")
    public Mono<Profile> getByUsername(
            @Parameter(description = "Nombre de usuario de Instagram", example = "cristiano")
            @PathVariable String username) {
        return instagramService.getProfile(username);
    }

    @Operation(
            summary = "Actualizar perfil",
            description = "Re-consulta la API de Instagram y actualiza los datos en base de datos"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado en base de datos")
    })
    @PutMapping("/{username:.+}")
    public Mono<Profile> update(
            @Parameter(description = "Nombre de usuario de Instagram", example = "cristiano")
            @PathVariable String username) {
        return instagramService.updateProfile(username);
    }

    @Operation(
            summary = "Desactivar perfil",
            description = "Eliminado lógico: establece active = false"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil desactivado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @PatchMapping("/{id}/deactivate")
    public Mono<Profile> deactivate(
            @Parameter(description = "ID del perfil", example = "1")
            @PathVariable Long id) {
        return instagramService.deactivateProfile(id);
    }

    @Operation(
            summary = "Activar perfil",
            description = "Restaurado lógico: establece active = true"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil activado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @PatchMapping("/{id}/activate")
    public Mono<Profile> activate(
            @Parameter(description = "ID del perfil", example = "1")
            @PathVariable Long id) {
        return instagramService.activateProfile(id);
    }
}
