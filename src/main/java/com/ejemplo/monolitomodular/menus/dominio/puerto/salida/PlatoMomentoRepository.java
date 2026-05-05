package com.ejemplo.monolitomodular.menus.dominio.puerto.salida;

import java.util.List;
import java.util.UUID;

public interface PlatoMomentoRepository {

    void asociar(UUID platoId, UUID tipoMomentoId);

    void eliminar(UUID platoId, UUID tipoMomentoId);

    boolean existe(UUID platoId, UUID tipoMomentoId);

    List<RelacionPlatoMomento> listar();

    List<RelacionPlatoMomento> listarPorPlatoId(UUID platoId);

    List<RelacionPlatoMomento> listarPorTipoMomentoId(UUID tipoMomentoId);

    record RelacionPlatoMomento(UUID platoId, UUID tipoMomentoId) {
    }
}
