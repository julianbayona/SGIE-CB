package com.ejemplo.monolitomodular.menus.presentacion.rest;

import com.ejemplo.monolitomodular.menus.aplicacion.dto.ConfigurarMenuCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.ItemMenuCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.ItemMenuView;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.MenuView;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.SeleccionMenuCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.SeleccionMenuView;
import com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada.ConfigurarMenuUseCase;
import com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada.ConsultarMenuUseCase;
import com.ejemplo.monolitomodular.menus.presentacion.rest.dto.ConfigurarMenuRequest;
import com.ejemplo.monolitomodular.menus.presentacion.rest.dto.ItemMenuRequest;
import com.ejemplo.monolitomodular.menus.presentacion.rest.dto.ItemMenuResponse;
import com.ejemplo.monolitomodular.menus.presentacion.rest.dto.MenuResponse;
import com.ejemplo.monolitomodular.menus.presentacion.rest.dto.SeleccionMenuRequest;
import com.ejemplo.monolitomodular.menus.presentacion.rest.dto.SeleccionMenuResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/reservas/{reservaRaizId}/menu")
public class MenuController {

    private final ConfigurarMenuUseCase configurarMenuUseCase;
    private final ConsultarMenuUseCase consultarMenuUseCase;

    public MenuController(ConfigurarMenuUseCase configurarMenuUseCase, ConsultarMenuUseCase consultarMenuUseCase) {
        this.configurarMenuUseCase = configurarMenuUseCase;
        this.consultarMenuUseCase = consultarMenuUseCase;
    }

    @PutMapping
    public MenuResponse configurar(@PathVariable UUID reservaRaizId, @Valid @RequestBody ConfigurarMenuRequest request) {
        return toResponse(configurarMenuUseCase.ejecutar(toCommand(reservaRaizId, request)));
    }

    @GetMapping
    public MenuResponse obtener(@PathVariable UUID reservaRaizId) {
        return toResponse(consultarMenuUseCase.obtenerPorReservaRaizId(reservaRaizId));
    }

    private ConfigurarMenuCommand toCommand(UUID reservaRaizId, ConfigurarMenuRequest request) {
        return new ConfigurarMenuCommand(
                reservaRaizId,
                request.usuarioId(),
                request.notasGenerales(),
                request.selecciones().stream().map(this::toCommand).toList()
        );
    }

    private SeleccionMenuCommand toCommand(SeleccionMenuRequest request) {
        return new SeleccionMenuCommand(
                request.tipoMomentoId(),
                request.items().stream().map(this::toCommand).toList()
        );
    }

    private ItemMenuCommand toCommand(ItemMenuRequest request) {
        return new ItemMenuCommand(request.platoId(), request.cantidad(), request.excepciones());
    }

    private MenuResponse toResponse(MenuView view) {
        return new MenuResponse(
                view.id(),
                view.reservaId(),
                view.notasGenerales(),
                view.selecciones().stream().map(this::toResponse).toList()
        );
    }

    private SeleccionMenuResponse toResponse(SeleccionMenuView view) {
        return new SeleccionMenuResponse(
                view.id(),
                view.tipoMomentoId(),
                view.items().stream().map(this::toResponse).toList()
        );
    }

    private ItemMenuResponse toResponse(ItemMenuView view) {
        return new ItemMenuResponse(view.id(), view.platoId(), view.cantidad(), view.excepciones());
    }
}
