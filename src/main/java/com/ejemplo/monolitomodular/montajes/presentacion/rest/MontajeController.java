package com.ejemplo.monolitomodular.montajes.presentacion.rest;

import com.ejemplo.monolitomodular.auth.infraestructura.seguridad.UsuarioAutenticado;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.AdicionalEventoCommand;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.AdicionalEventoView;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.ConfigurarMontajeCommand;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.InfraestructuraReservaCommand;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.InfraestructuraReservaView;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.MontajeMesaReservaCommand;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.MontajeMesaReservaView;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.MontajeView;
import com.ejemplo.monolitomodular.montajes.aplicacion.puerto.entrada.ConfigurarMontajeUseCase;
import com.ejemplo.monolitomodular.montajes.aplicacion.puerto.entrada.ConsultarMontajeUseCase;
import com.ejemplo.monolitomodular.montajes.presentacion.rest.dto.AdicionalEventoRequest;
import com.ejemplo.monolitomodular.montajes.presentacion.rest.dto.AdicionalEventoResponse;
import com.ejemplo.monolitomodular.montajes.presentacion.rest.dto.ConfigurarMontajeRequest;
import com.ejemplo.monolitomodular.montajes.presentacion.rest.dto.InfraestructuraReservaRequest;
import com.ejemplo.monolitomodular.montajes.presentacion.rest.dto.InfraestructuraReservaResponse;
import com.ejemplo.monolitomodular.montajes.presentacion.rest.dto.MontajeMesaReservaRequest;
import com.ejemplo.monolitomodular.montajes.presentacion.rest.dto.MontajeMesaReservaResponse;
import com.ejemplo.monolitomodular.montajes.presentacion.rest.dto.MontajeResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/reservas/{reservaRaizId}/montaje")
public class MontajeController {

    private final ConfigurarMontajeUseCase configurarMontajeUseCase;
    private final ConsultarMontajeUseCase consultarMontajeUseCase;

    public MontajeController(
            ConfigurarMontajeUseCase configurarMontajeUseCase,
            ConsultarMontajeUseCase consultarMontajeUseCase
    ) {
        this.configurarMontajeUseCase = configurarMontajeUseCase;
        this.consultarMontajeUseCase = consultarMontajeUseCase;
    }

    @PutMapping
    public MontajeResponse configurar(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable UUID reservaRaizId,
            @Valid @RequestBody ConfigurarMontajeRequest request
    ) {
        return toResponse(configurarMontajeUseCase.ejecutar(toCommand(reservaRaizId, usuario, request)));
    }

    @GetMapping
    public MontajeResponse obtener(@PathVariable UUID reservaRaizId) {
        return toResponse(consultarMontajeUseCase.obtenerPorReservaRaizId(reservaRaizId));
    }

    private ConfigurarMontajeCommand toCommand(UUID reservaRaizId, UsuarioAutenticado usuario, ConfigurarMontajeRequest request) {
        return new ConfigurarMontajeCommand(
                reservaRaizId,
                usuario.id(),
                request.observaciones(),
                request.mesas().stream().map(this::toCommand).toList(),
                toCommand(request.infraestructura()),
                request.adicionales() == null ? java.util.List.of() : request.adicionales().stream().map(this::toCommand).toList()
        );
    }

    private MontajeMesaReservaCommand toCommand(MontajeMesaReservaRequest request) {
        return new MontajeMesaReservaCommand(
                request.tipoMesaId(),
                request.tipoSillaId(),
                request.sillaPorMesa(),
                request.cantidadMesas(),
                request.mantelId(),
                request.sobremantelId(),
                request.vajilla(),
                request.fajon()
        );
    }

    private InfraestructuraReservaCommand toCommand(InfraestructuraReservaRequest request) {
        return new InfraestructuraReservaCommand(
                request.mesaPonque(),
                request.mesaRegalos(),
                request.espacioMusicos(),
                request.estanteBombas()
        );
    }

    private AdicionalEventoCommand toCommand(AdicionalEventoRequest request) {
        return new AdicionalEventoCommand(
                request.tipoAdicionalId(),
                request.cantidad()
        );
    }

    private MontajeResponse toResponse(MontajeView view) {
        return new MontajeResponse(
                view.id(),
                view.reservaId(),
                view.observaciones(),
                view.mesas().stream().map(this::toResponse).toList(),
                toResponse(view.infraestructura()),
                view.adicionales().stream().map(this::toResponse).toList()
        );
    }

    private MontajeMesaReservaResponse toResponse(MontajeMesaReservaView view) {
        return new MontajeMesaReservaResponse(
                view.id(),
                view.tipoMesaId(),
                view.tipoSillaId(),
                view.sillaPorMesa(),
                view.cantidadMesas(),
                view.mantelId(),
                view.sobremantelId(),
                view.vajilla(),
                view.fajon()
        );
    }

    private InfraestructuraReservaResponse toResponse(InfraestructuraReservaView view) {
        return new InfraestructuraReservaResponse(
                view.id(),
                view.mesaPonque(),
                view.mesaRegalos(),
                view.espacioMusicos(),
                view.estanteBombas()
        );
    }

    private AdicionalEventoResponse toResponse(AdicionalEventoView view) {
        return new AdicionalEventoResponse(
                view.id(),
                view.tipoAdicionalId(),
                view.cantidad()
        );
    }
}
