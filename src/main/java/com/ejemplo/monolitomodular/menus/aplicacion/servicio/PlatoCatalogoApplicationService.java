package com.ejemplo.monolitomodular.menus.aplicacion.servicio;

import com.ejemplo.monolitomodular.menus.aplicacion.dto.PlatoCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.PlatoView;
import com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada.GestionarPlatoUseCase;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Plato;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.PlatoRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PlatoCatalogoApplicationService implements GestionarPlatoUseCase {

    private final PlatoRepository platoRepository;

    public PlatoCatalogoApplicationService(PlatoRepository platoRepository) {
        this.platoRepository = platoRepository;
    }

    @Override
    public PlatoView crear(PlatoCommand command) {
        validarNombreDisponible(command.nombre());
        return toView(platoRepository.guardar(Plato.nuevo(command.nombre(), command.descripcion(), command.precioBase())));
    }

    @Override
    public PlatoView actualizar(UUID id, PlatoCommand command) {
        Plato plato = platoRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Plato no encontrado"));
        validarNombreDisponible(command.nombre(), plato.getNombre());
        return toView(platoRepository.guardar(plato.actualizar(command.nombre(), command.descripcion(), command.precioBase())));
    }

    @Override
    public PlatoView desactivar(UUID id) {
        Plato plato = platoRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Plato no encontrado"));
        return toView(platoRepository.guardar(plato.desactivar()));
    }

    @Override
    public PlatoView obtener(UUID id) {
        return platoRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Plato no encontrado"));
    }

    @Override
    public List<PlatoView> listar() {
        return platoRepository.listar().stream().map(this::toView).toList();
    }

    private void validarNombreDisponible(String nombre) {
        validarNombreDisponible(nombre, null);
    }

    private void validarNombreDisponible(String nombre, String nombreActual) {
        if (nombreActual != null && nombreActual.equalsIgnoreCase(nombre)) {
            return;
        }
        if (platoRepository.existePorNombre(nombre)) {
            throw new DomainException("Ya existe un plato con el nombre indicado");
        }
    }

    private PlatoView toView(Plato plato) {
        return new PlatoView(plato.getId(), plato.getNombre(), plato.getDescripcion(), plato.getPrecioBase(), plato.isActivo());
    }
}
