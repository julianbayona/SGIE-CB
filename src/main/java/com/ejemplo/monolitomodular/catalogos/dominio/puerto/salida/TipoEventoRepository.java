package com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoEvento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TipoEventoRepository {

    TipoEvento guardar(TipoEvento tipoEvento);

    Optional<TipoEvento> buscarPorId(UUID id);

    List<TipoEvento> listar();

    boolean existeActivoPorId(UUID id);

    boolean existePorNombre(String nombre);
}
