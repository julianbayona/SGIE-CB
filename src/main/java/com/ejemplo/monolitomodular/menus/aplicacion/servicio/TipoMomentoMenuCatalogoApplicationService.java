package com.ejemplo.monolitomodular.menus.aplicacion.servicio;

import com.ejemplo.monolitomodular.menus.aplicacion.dto.TipoMomentoMenuCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.TipoMomentoMenuView;
import com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada.GestionarTipoMomentoMenuUseCase;
import com.ejemplo.monolitomodular.menus.dominio.modelo.TipoMomentoMenu;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.TipoMomentoMenuRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TipoMomentoMenuCatalogoApplicationService implements GestionarTipoMomentoMenuUseCase {

    private final TipoMomentoMenuRepository repository;

    public TipoMomentoMenuCatalogoApplicationService(TipoMomentoMenuRepository repository) {
        this.repository = repository;
    }

    @Override
    public TipoMomentoMenuView crear(TipoMomentoMenuCommand command) {
        validarNombreDisponible(command.nombre());
        return toView(repository.guardar(TipoMomentoMenu.nuevo(command.nombre())));
    }

    @Override
    public TipoMomentoMenuView actualizar(UUID id, TipoMomentoMenuCommand command) {
        TipoMomentoMenu tipoMomento = repository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo momento de menu no encontrado"));
        validarNombreDisponible(command.nombre(), tipoMomento.getNombre());
        return toView(repository.guardar(tipoMomento.actualizar(command.nombre())));
    }

    @Override
    public TipoMomentoMenuView desactivar(UUID id) {
        TipoMomentoMenu tipoMomento = repository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo momento de menu no encontrado"));
        return toView(repository.guardar(tipoMomento.desactivar()));
    }

    @Override
    public TipoMomentoMenuView obtener(UUID id) {
        return repository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Tipo momento de menu no encontrado"));
    }

    @Override
    public List<TipoMomentoMenuView> listar() {
        return repository.listar().stream().map(this::toView).toList();
    }

    private void validarNombreDisponible(String nombre) {
        validarNombreDisponible(nombre, null);
    }

    private void validarNombreDisponible(String nombre, String nombreActual) {
        if (nombreActual != null && nombreActual.equalsIgnoreCase(nombre)) {
            return;
        }
        if (repository.existePorNombre(nombre)) {
            throw new DomainException("Ya existe un tipo momento de menu con el nombre indicado");
        }
    }

    private TipoMomentoMenuView toView(TipoMomentoMenu tipoMomentoMenu) {
        return new TipoMomentoMenuView(tipoMomentoMenu.getId(), tipoMomentoMenu.getNombre(), tipoMomentoMenu.isActivo());
    }
}
