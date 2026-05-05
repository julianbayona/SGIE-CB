package com.ejemplo.monolitomodular.catalogos.aplicacion.servicio;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.PlatoView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.ConsultarPlatoUseCase;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Plato;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.PlatoRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ConsultarPlatoService implements ConsultarPlatoUseCase {

    private final PlatoRepository platoRepository;

    public ConsultarPlatoService(PlatoRepository platoRepository) {
        this.platoRepository = platoRepository;
    }

    @Override
    public List<PlatoView> listarActivos() {
        return platoRepository.listarActivos().stream()
                .map(this::toView)
                .toList();
    }

    @Override
    public PlatoView obtenerPorId(UUID id) {
        Plato plato = platoRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Plato no encontrado con id: " + id));
        return toView(plato);
    }

    private PlatoView toView(Plato plato) {
        return new PlatoView(
                plato.getId().toString(),
                plato.getNombre(),
                plato.getDescripcion(),
                plato.getPrecioBase(),
                plato.isActivo()
        );
    }
}
