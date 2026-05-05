package com.ejemplo.monolitomodular.menus.aplicacion.servicio;

import com.ejemplo.monolitomodular.menus.aplicacion.dto.PlatoMomentoCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.PlatoMomentoView;
import com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada.GestionarPlatoMomentoUseCase;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Plato;
import com.ejemplo.monolitomodular.menus.dominio.modelo.TipoMomentoMenu;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.PlatoMomentoRepository;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.PlatoRepository;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.TipoMomentoMenuRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PlatoMomentoApplicationService implements GestionarPlatoMomentoUseCase {

    private final PlatoRepository platoRepository;
    private final TipoMomentoMenuRepository tipoMomentoMenuRepository;
    private final PlatoMomentoRepository platoMomentoRepository;

    public PlatoMomentoApplicationService(
            PlatoRepository platoRepository,
            TipoMomentoMenuRepository tipoMomentoMenuRepository,
            PlatoMomentoRepository platoMomentoRepository
    ) {
        this.platoRepository = platoRepository;
        this.tipoMomentoMenuRepository = tipoMomentoMenuRepository;
        this.platoMomentoRepository = platoMomentoRepository;
    }

    @Override
    @Transactional
    public PlatoMomentoView asociar(PlatoMomentoCommand command) {
        Plato plato = platoRepository.buscarPorId(command.platoId())
                .orElseThrow(() -> new DomainException("Plato no encontrado"));
        TipoMomentoMenu tipoMomento = tipoMomentoMenuRepository.buscarPorId(command.tipoMomentoId())
                .orElseThrow(() -> new DomainException("Tipo momento de menu no encontrado"));
        if (!plato.isActivo()) {
            throw new DomainException("No se puede asociar un plato inactivo");
        }
        if (!tipoMomento.isActivo()) {
            throw new DomainException("No se puede asociar un tipo momento de menu inactivo");
        }
        if (platoMomentoRepository.existe(command.platoId(), command.tipoMomentoId())) {
            throw new DomainException("El plato ya esta asociado al momento de menu indicado");
        }
        platoMomentoRepository.asociar(command.platoId(), command.tipoMomentoId());
        return new PlatoMomentoView(command.platoId(), command.tipoMomentoId());
    }

    @Override
    @Transactional
    public void eliminar(UUID platoId, UUID tipoMomentoId) {
        if (!platoMomentoRepository.existe(platoId, tipoMomentoId)) {
            throw new DomainException("La asociacion plato-momento no existe");
        }
        platoMomentoRepository.eliminar(platoId, tipoMomentoId);
    }

    @Override
    public List<PlatoMomentoView> listar(UUID platoId, UUID tipoMomentoId) {
        if (platoId != null && tipoMomentoId != null) {
            return platoMomentoRepository.existe(platoId, tipoMomentoId)
                    ? List.of(new PlatoMomentoView(platoId, tipoMomentoId))
                    : List.of();
        }
        if (platoId != null) {
            return platoMomentoRepository.listarPorPlatoId(platoId).stream().map(this::toView).toList();
        }
        if (tipoMomentoId != null) {
            return platoMomentoRepository.listarPorTipoMomentoId(tipoMomentoId).stream().map(this::toView).toList();
        }
        return platoMomentoRepository.listar().stream().map(this::toView).toList();
    }

    private PlatoMomentoView toView(PlatoMomentoRepository.RelacionPlatoMomento relacion) {
        return new PlatoMomentoView(relacion.platoId(), relacion.tipoMomentoId());
    }
}
