package com.ejemplo.monolitomodular.catalogos.aplicacion.servicio;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarTipoComidaUseCase;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarTipoEventoUseCase;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoComida;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoEvento;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoComidaRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoEventoRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CatalogoApplicationService implements GestionarTipoEventoUseCase, GestionarTipoComidaUseCase {

    private final TipoEventoRepository tipoEventoRepository;
    private final TipoComidaRepository tipoComidaRepository;

    public CatalogoApplicationService(
            TipoEventoRepository tipoEventoRepository,
            TipoComidaRepository tipoComidaRepository
    ) {
        this.tipoEventoRepository = tipoEventoRepository;
        this.tipoComidaRepository = tipoComidaRepository;
    }

    @Override
    public CatalogoBasicoView crearTipoEvento(CatalogoBasicoCommand command) {
        validarNombreDisponibleTipoEvento(command.nombre());
        return toView(tipoEventoRepository.guardar(TipoEvento.nuevo(command.nombre(), command.descripcion())));
    }

    @Override
    public CatalogoBasicoView actualizarTipoEvento(UUID id, CatalogoBasicoCommand command) {
        TipoEvento tipoEvento = tipoEventoRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo de evento no encontrado"));
        validarNombreDisponibleTipoEvento(command.nombre(), tipoEvento.getNombre());
        return toView(tipoEventoRepository.guardar(tipoEvento.actualizar(command.nombre(), command.descripcion())));
    }

    @Override
    public CatalogoBasicoView desactivarTipoEvento(UUID id) {
        TipoEvento tipoEvento = tipoEventoRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo de evento no encontrado"));
        return toView(tipoEventoRepository.guardar(tipoEvento.desactivar()));
    }

    @Override
    public CatalogoBasicoView obtenerTipoEvento(UUID id) {
        return tipoEventoRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Tipo de evento no encontrado"));
    }

    @Override
    public List<CatalogoBasicoView> listarTiposEvento() {
        return tipoEventoRepository.listar().stream().map(this::toView).toList();
    }

    @Override
    public CatalogoBasicoView crearTipoComida(CatalogoBasicoCommand command) {
        validarNombreDisponibleTipoComida(command.nombre());
        return toView(tipoComidaRepository.guardar(TipoComida.nuevo(command.nombre(), command.descripcion())));
    }

    @Override
    public CatalogoBasicoView actualizarTipoComida(UUID id, CatalogoBasicoCommand command) {
        TipoComida tipoComida = tipoComidaRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo de comida no encontrado"));
        validarNombreDisponibleTipoComida(command.nombre(), tipoComida.getNombre());
        return toView(tipoComidaRepository.guardar(tipoComida.actualizar(command.nombre(), command.descripcion())));
    }

    @Override
    public CatalogoBasicoView desactivarTipoComida(UUID id) {
        TipoComida tipoComida = tipoComidaRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo de comida no encontrado"));
        return toView(tipoComidaRepository.guardar(tipoComida.desactivar()));
    }

    @Override
    public CatalogoBasicoView obtenerTipoComida(UUID id) {
        return tipoComidaRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Tipo de comida no encontrado"));
    }

    @Override
    public List<CatalogoBasicoView> listarTiposComida() {
        return tipoComidaRepository.listar().stream().map(this::toView).toList();
    }

    private void validarNombreDisponibleTipoEvento(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        if (tipoEventoRepository.existePorNombre(nombre)) {
            throw new DomainException("Ya existe un tipo de evento con el nombre indicado");
        }
    }

    private void validarNombreDisponibleTipoEvento(String nombre, String nombreActual) {
        if (nombre == null || !nombreActual.equalsIgnoreCase(nombre.trim())) {
            validarNombreDisponibleTipoEvento(nombre);
        }
    }

    private void validarNombreDisponibleTipoComida(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        if (tipoComidaRepository.existePorNombre(nombre)) {
            throw new DomainException("Ya existe un tipo de comida con el nombre indicado");
        }
    }

    private void validarNombreDisponibleTipoComida(String nombre, String nombreActual) {
        if (nombre == null || !nombreActual.equalsIgnoreCase(nombre.trim())) {
            validarNombreDisponibleTipoComida(nombre);
        }
    }

    private CatalogoBasicoView toView(TipoEvento tipoEvento) {
        return new CatalogoBasicoView(
                tipoEvento.getId(),
                tipoEvento.getNombre(),
                tipoEvento.getDescripcion(),
                tipoEvento.isActivo()
        );
    }

    private CatalogoBasicoView toView(TipoComida tipoComida) {
        return new CatalogoBasicoView(
                tipoComida.getId(),
                tipoComida.getNombre(),
                tipoComida.getDescripcion(),
                tipoComida.isActivo()
        );
    }
}
