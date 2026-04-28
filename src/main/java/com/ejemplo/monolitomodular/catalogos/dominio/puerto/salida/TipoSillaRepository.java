package com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoSilla;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TipoSillaRepository {

    TipoSilla guardar(TipoSilla tipoSilla);

    Optional<TipoSilla> buscarPorId(UUID id);

    List<TipoSilla> listar();

    boolean existeActivoPorId(UUID id);

    boolean existePorNombre(String nombre);
}
