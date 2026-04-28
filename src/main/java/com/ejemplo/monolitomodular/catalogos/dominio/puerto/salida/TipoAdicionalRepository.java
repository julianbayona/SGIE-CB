package com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoAdicional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TipoAdicionalRepository {

    TipoAdicional guardar(TipoAdicional tipoAdicional);

    Optional<TipoAdicional> buscarPorId(UUID id);

    List<TipoAdicional> listar();

    boolean existeActivoPorId(UUID id);

    boolean existePorNombre(String nombre);
}
