package com.ejemplo.monolitomodular.cotizaciones.presentacion.rest;

import com.ejemplo.monolitomodular.auth.infraestructura.seguridad.UsuarioAutenticado;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.ActualizarItemCotizacionCommand;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.ActualizarItemsCotizacionCommand;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.CotizacionItemView;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.CotizacionView;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.DocumentoCotizacionView;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.GenerarCotizacionCommand;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.ActualizarItemCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.ConsultarCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.DescargarDocumentoCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.EnviarCotizacionEmailUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.EnviarCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.GenerarDocumentoCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.GenerarCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.ListarCotizacionesEventoUseCase;
import com.ejemplo.monolitomodular.cotizaciones.presentacion.rest.dto.ActualizarItemCotizacionRequest;
import com.ejemplo.monolitomodular.cotizaciones.presentacion.rest.dto.ActualizarItemsCotizacionRequest;
import com.ejemplo.monolitomodular.cotizaciones.presentacion.rest.dto.CotizacionItemResponse;
import com.ejemplo.monolitomodular.cotizaciones.presentacion.rest.dto.CotizacionResponse;
import com.ejemplo.monolitomodular.cotizaciones.presentacion.rest.dto.GenerarCotizacionRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CotizacionController {

    private final GenerarCotizacionUseCase generarCotizacionUseCase;
    private final ConsultarCotizacionUseCase consultarCotizacionUseCase;
    private final ActualizarItemCotizacionUseCase actualizarItemCotizacionUseCase;
    private final GenerarDocumentoCotizacionUseCase generarDocumentoCotizacionUseCase;
    private final EnviarCotizacionUseCase enviarCotizacionUseCase;
    private final ListarCotizacionesEventoUseCase listarCotizacionesEventoUseCase;
    private final DescargarDocumentoCotizacionUseCase descargarDocumentoCotizacionUseCase;
    private final EnviarCotizacionEmailUseCase enviarCotizacionEmailUseCase;

    public CotizacionController(
            GenerarCotizacionUseCase generarCotizacionUseCase,
            ConsultarCotizacionUseCase consultarCotizacionUseCase,
            ActualizarItemCotizacionUseCase actualizarItemCotizacionUseCase,
            GenerarDocumentoCotizacionUseCase generarDocumentoCotizacionUseCase,
            EnviarCotizacionUseCase enviarCotizacionUseCase,
            ListarCotizacionesEventoUseCase listarCotizacionesEventoUseCase,
            DescargarDocumentoCotizacionUseCase descargarDocumentoCotizacionUseCase,
            EnviarCotizacionEmailUseCase enviarCotizacionEmailUseCase
    ) {
        this.generarCotizacionUseCase = generarCotizacionUseCase;
        this.consultarCotizacionUseCase = consultarCotizacionUseCase;
        this.actualizarItemCotizacionUseCase = actualizarItemCotizacionUseCase;
        this.generarDocumentoCotizacionUseCase = generarDocumentoCotizacionUseCase;
        this.enviarCotizacionUseCase = enviarCotizacionUseCase;
        this.listarCotizacionesEventoUseCase = listarCotizacionesEventoUseCase;
        this.descargarDocumentoCotizacionUseCase = descargarDocumentoCotizacionUseCase;
        this.enviarCotizacionEmailUseCase = enviarCotizacionEmailUseCase;
    }

    @PostMapping("/reservas/{reservaRaizId}/cotizaciones")
    public CotizacionResponse generar(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable UUID reservaRaizId,
            @Valid @RequestBody GenerarCotizacionRequest request
    ) {
        return toResponse(generarCotizacionUseCase.ejecutar(toCommand(reservaRaizId, usuario, request)));
    }

    @GetMapping("/cotizaciones/{id}")
    public CotizacionResponse obtener(@PathVariable UUID id) {
        return toResponse(consultarCotizacionUseCase.obtenerPorId(id));
    }

    @GetMapping("/reservas/{reservaRaizId}/cotizacion-vigente")
    public CotizacionResponse obtenerVigente(@PathVariable UUID reservaRaizId) {
        return toResponse(consultarCotizacionUseCase.obtenerVigentePorReservaRaizId(reservaRaizId));
    }

    @GetMapping("/eventos/{eventoId}/cotizaciones")
    public List<CotizacionResponse> listarPorEvento(@PathVariable UUID eventoId) {
        return listarCotizacionesEventoUseCase.listarPorEvento(eventoId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/cotizaciones/{id}/documento")
    public ResponseEntity<byte[]> descargarDocumento(@PathVariable UUID id) {
        DocumentoCotizacionView documento = descargarDocumentoCotizacionUseCase.descargar(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, documento.contentType())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documento.nombreArchivo() + "\"")
                .body(documento.contenido());
    }

    @PatchMapping("/cotizaciones/{cotizacionId}/items/{itemId}")
    public CotizacionResponse actualizarItem(
            @PathVariable UUID cotizacionId,
            @PathVariable UUID itemId,
            @Valid @RequestBody ActualizarItemCotizacionRequest request
    ) {
        return toResponse(actualizarItemCotizacionUseCase.ejecutar(new ActualizarItemCotizacionCommand(
                cotizacionId,
                itemId,
                request.precioOverride()
        )));
    }

    @PutMapping("/cotizaciones/{cotizacionId}/items")
    public CotizacionResponse actualizarItems(
            @PathVariable UUID cotizacionId,
            @Valid @RequestBody ActualizarItemsCotizacionRequest request
    ) {
        return toResponse(actualizarItemCotizacionUseCase.ejecutar(new ActualizarItemsCotizacionCommand(
                cotizacionId,
                request.items().stream()
                        .map(item -> new ActualizarItemsCotizacionCommand.Item(item.itemId(), item.precioOverride()))
                        .toList()
        )));
    }

    @PatchMapping("/cotizaciones/{id}/generar")
    public CotizacionResponse generarDocumento(@PathVariable UUID id) {
        return toResponse(generarDocumentoCotizacionUseCase.generar(id));
    }

    @PatchMapping("/cotizaciones/{id}/enviar")
    public CotizacionResponse enviar(@PathVariable UUID id) {
        return toResponse(enviarCotizacionUseCase.enviar(id));
    }

    @PostMapping("/cotizaciones/{id}/enviar-email")
    public CotizacionResponse enviarEmail(@PathVariable UUID id) {
        return toResponse(enviarCotizacionEmailUseCase.enviarPorEmail(id));
    }

    @PatchMapping("/cotizaciones/{id}/aceptar")
    public CotizacionResponse aceptar(@PathVariable UUID id) {
        return toResponse(enviarCotizacionUseCase.aceptar(id));
    }

    @PatchMapping("/cotizaciones/{id}/rechazar")
    public CotizacionResponse rechazar(@PathVariable UUID id) {
        return toResponse(enviarCotizacionUseCase.rechazar(id));
    }

    private GenerarCotizacionCommand toCommand(UUID reservaRaizId, UsuarioAutenticado usuario, GenerarCotizacionRequest request) {
        return new GenerarCotizacionCommand(
                reservaRaizId,
                usuario.id(),
                request.descuento(),
                request.observaciones()
        );
    }

    private CotizacionResponse toResponse(CotizacionView view) {
        return new CotizacionResponse(
                view.id(),
                view.reservaId(),
                view.usuarioId(),
                view.estado(),
                view.vigente(),
                view.valorSubtotal(),
                view.descuento(),
                view.valorTotal(),
                view.observaciones(),
                view.items().stream().map(this::toResponse).toList()
        );
    }

    private CotizacionItemResponse toResponse(CotizacionItemView view) {
        return new CotizacionItemResponse(
                view.id(),
                view.tipoConcepto(),
                view.origenId(),
                view.descripcion(),
                view.precioBase(),
                view.precioOverride(),
                view.cantidad(),
                view.subtotal()
        );
    }
}
