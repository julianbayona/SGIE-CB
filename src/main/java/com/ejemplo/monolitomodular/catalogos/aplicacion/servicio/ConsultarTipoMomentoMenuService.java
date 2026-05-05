package com.ejemplo.monolitomodular.catalogos.aplicacion.servicio;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.TipoMomentoMenuView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.ConsultarTipoMomentoMenuUseCase;
import com.ejemplo.monolitomodular.menus.dominio.modelo.TipoMomentoMenu;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.TipoMomentoMenuRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ConsultarTipoMomentoMenuService implements ConsultarTipoMomentoMenuUseCase {

    private final TipoMomentoMenuRepository tipoMomentoMenuRepository;

    public ConsultarTipoMomentoMenuService(TipoMomentoMenuRepository tipoMomentoMenuRepository) {
        this.tipoMomentoMenuRepository = tipoMomentoMenuRepository;
    }

    @Override
    public List<TipoMomentoMenuView> listarActivos() {
        return tipoMomentoMenuRepository.listarActivos().stream()
                .map(this::toView)
                .toList();
    }

    @Override
    public TipoMomentoMenuView obtenerPorId(UUID id) {
        TipoMomentoMenu tipoMomento = tipoMomentoMenuRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo de momento de menú no encontrado con id: " + id));
        return toView(tipoMomento);
    }

    private TipoMomentoMenuView toView(TipoMomentoMenu tipoMomento) {
        return new TipoMomentoMenuView(
                tipoMomento.getId().toString(),
                tipoMomento.getNombre(),
                tipoMomento.isActivo()
        );
    }
}
