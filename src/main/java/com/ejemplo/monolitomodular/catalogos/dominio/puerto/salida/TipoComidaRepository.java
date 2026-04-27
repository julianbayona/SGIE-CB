package com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoComida;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TipoComidaRepository {

    TipoComida guardar(TipoComida tipoComida);

    Optional<TipoComida> buscarPorId(UUID id);

    List<TipoComida> listar();

    boolean existeActivoPorId(UUID id);

    boolean existePorNombre(String nombre);
}
