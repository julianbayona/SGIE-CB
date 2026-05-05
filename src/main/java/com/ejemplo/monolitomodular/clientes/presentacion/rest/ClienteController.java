package com.ejemplo.monolitomodular.clientes.presentacion.rest;

import com.ejemplo.monolitomodular.auth.infraestructura.seguridad.UsuarioAutenticado;
import com.ejemplo.monolitomodular.clientes.aplicacion.dto.ClienteView;
import com.ejemplo.monolitomodular.clientes.aplicacion.dto.RegistrarClienteCommand;
import com.ejemplo.monolitomodular.clientes.aplicacion.puerto.entrada.ConsultarClienteUseCase;
import com.ejemplo.monolitomodular.clientes.aplicacion.puerto.entrada.RegistrarClienteUseCase;
import com.ejemplo.monolitomodular.clientes.presentacion.rest.dto.ClienteResponse;
import com.ejemplo.monolitomodular.clientes.presentacion.rest.dto.RegistrarClienteRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final RegistrarClienteUseCase registrarClienteUseCase;
    private final ConsultarClienteUseCase consultarClienteUseCase;

    public ClienteController(
            RegistrarClienteUseCase registrarClienteUseCase,
            ConsultarClienteUseCase consultarClienteUseCase
    ) {
        this.registrarClienteUseCase = registrarClienteUseCase;
        this.consultarClienteUseCase = consultarClienteUseCase;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @Valid @RequestBody RegistrarClienteRequest request
    ) {
        ClienteView cliente = registrarClienteUseCase.ejecutar(
                new RegistrarClienteCommand(
                        request.cedula(),
                        request.nombreCompleto(),
                        request.telefono(),
                        request.correo(),
                        request.tipoCliente(),
                        usuario.id()
                )
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cliente.id())
                .toUri();

        return ResponseEntity.created(location).body(toResponse(cliente));
    }

    @GetMapping("/{id}")
    public ClienteResponse obtenerPorId(@PathVariable UUID id) {
        return toResponse(consultarClienteUseCase.obtenerPorId(id));
    }

    @GetMapping
    public List<ClienteResponse> listar(@RequestParam(required = false) String q) {
        List<ClienteView> clientes = (q == null || q.isBlank())
                ? consultarClienteUseCase.listar()
                : consultarClienteUseCase.buscar(q);

        return clientes.stream()
                .map(this::toResponse)
                .toList();
    }

    private ClienteResponse toResponse(ClienteView cliente) {
        return new ClienteResponse(
                cliente.id(),
                cliente.cedula(),
                cliente.nombreCompleto(),
                cliente.telefono(),
                cliente.correo(),
                cliente.tipoCliente(),
                cliente.activo(),
                cliente.creadoPor()
        );
    }
}
