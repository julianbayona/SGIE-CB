package com.ejemplo.monolitomodular.pruebasplato.dominio.puerto.salida;

import com.ejemplo.monolitomodular.pruebasplato.dominio.modelo.PruebaPlato;

import java.util.List;
import java.util.UUID;

public interface PruebaPlatoRepository {

    PruebaPlato guardar(PruebaPlato pruebaPlato);

    default List<PruebaPlato> buscarProgramadasPorEventoId(UUID eventoId) {
        return List.of();
    }
}
