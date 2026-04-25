package com.ejemplo.monolitomodular.productos.presentacion.rest;

import com.ejemplo.monolitomodular.productos.aplicacion.dto.ProductoView;
import com.ejemplo.monolitomodular.productos.aplicacion.dto.RegistrarProductoCommand;
import com.ejemplo.monolitomodular.productos.aplicacion.puerto.entrada.ConsultarProductoUseCase;
import com.ejemplo.monolitomodular.productos.aplicacion.puerto.entrada.RegistrarProductoUseCase;
import com.ejemplo.monolitomodular.productos.presentacion.rest.dto.ProductoResponse;
import com.ejemplo.monolitomodular.productos.presentacion.rest.dto.RegistrarProductoRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/productos")
public class ProductoController {

    private final RegistrarProductoUseCase registrarProductoUseCase;
    private final ConsultarProductoUseCase consultarProductoUseCase;

    public ProductoController(
            RegistrarProductoUseCase registrarProductoUseCase,
            ConsultarProductoUseCase consultarProductoUseCase
    ) {
        this.registrarProductoUseCase = registrarProductoUseCase;
        this.consultarProductoUseCase = consultarProductoUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody RegistrarProductoRequest request) {
        ProductoView producto = registrarProductoUseCase.ejecutar(
                new RegistrarProductoCommand(request.nombre(), request.precio())
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(producto.id())
                .toUri();

        return ResponseEntity.created(location).body(toResponse(producto));
    }

    @GetMapping("/{id}")
    public ProductoResponse obtenerPorId(@PathVariable UUID id) {
        return toResponse(consultarProductoUseCase.obtenerPorId(id));
    }

    @GetMapping
    public List<ProductoResponse> listar() {
        return consultarProductoUseCase.listar().stream()
                .map(this::toResponse)
                .toList();
    }

    private ProductoResponse toResponse(ProductoView producto) {
        return new ProductoResponse(producto.id(), producto.nombre(), producto.precio());
    }
}
