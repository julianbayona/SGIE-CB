package com.ejemplo.monolitomodular.catalogos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.TipoMomentoMenuView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.ConsultarTipoMomentoMenuUseCase;
import com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto.TipoMomentoMenuResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalogos/tipos-momento-menu")
public class TipoMomentoMenuController {

    private final ConsultarTipoMomentoMenuUseCase consultarTipoMomentoMenuUseCase;

    public TipoMomentoMenuController(ConsultarTipoMomentoMenuUseCase consultarTipoMomentoMenuUseCase) {
        this.consultarTipoMomentoMenuUseCase = consultarTipoMomentoMenuUseCase;
    }

    @GetMapping
    public List<TipoMomentoMenuResponse> listar() {
        return consultarTipoMomentoMenuUseCase.listarActivos().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public TipoMomentoMenuResponse obtenerPorId(@PathVariable UUID id) {
        return toResponse(consultarTipoMomentoMenuUseCase.obtenerPorId(id));
    }

    private TipoMomentoMenuResponse toResponse(TipoMomentoMenuView view) {
        return new TipoMomentoMenuResponse(
                view.id(),
                view.nombre(),
                view.activo()
        );
    }
}
