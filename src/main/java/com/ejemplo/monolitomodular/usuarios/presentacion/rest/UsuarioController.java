package com.ejemplo.monolitomodular.usuarios.presentacion.rest;

import com.ejemplo.monolitomodular.auth.infraestructura.seguridad.UsuarioAutenticado;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.aplicacion.dto.CrearUsuarioCommand;
import com.ejemplo.monolitomodular.usuarios.aplicacion.dto.UsuarioView;
import com.ejemplo.monolitomodular.usuarios.aplicacion.puerto.entrada.GestionarUsuarioUseCase;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.presentacion.rest.dto.CrearUsuarioRequest;
import com.ejemplo.monolitomodular.usuarios.presentacion.rest.dto.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final GestionarUsuarioUseCase gestionarUsuarioUseCase;

    public UsuarioController(GestionarUsuarioUseCase gestionarUsuarioUseCase) {
        this.gestionarUsuarioUseCase = gestionarUsuarioUseCase;
    }

    @GetMapping
    public List<UsuarioResponse> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        exigirAdministrador(usuario);
        return gestionarUsuarioUseCase.listar().stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @Valid @RequestBody CrearUsuarioRequest request
    ) {
        exigirAdministrador(usuario);
        UsuarioView creado = gestionarUsuarioUseCase.crear(new CrearUsuarioCommand(
                request.nombre(),
                request.contrasena(),
                request.rol()
        ));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(creado));
    }

    @PatchMapping("/{id}/activar")
    public UsuarioResponse activar(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable UUID id
    ) {
        exigirAdministrador(usuario);
        return toResponse(gestionarUsuarioUseCase.activar(id));
    }

    @PatchMapping("/{id}/desactivar")
    public UsuarioResponse desactivar(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable UUID id
    ) {
        exigirAdministrador(usuario);
        return toResponse(gestionarUsuarioUseCase.desactivar(id));
    }

    private void exigirAdministrador(UsuarioAutenticado usuario) {
        if (usuario == null || usuario.rol() != RolUsuario.ADMINISTRADOR) {
            throw new DomainException("Solo un Administrador puede gestionar usuarios");
        }
    }

    private UsuarioResponse toResponse(UsuarioView view) {
        return new UsuarioResponse(view.id(), view.nombre(), view.rol(), view.activo());
    }
}
