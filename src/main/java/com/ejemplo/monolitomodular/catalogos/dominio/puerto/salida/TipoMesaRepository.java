package com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoMesa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TipoMesaRepository {

    TipoMesa guardar(TipoMesa tipoMesa);

    Optional<TipoMesa> buscarPorId(UUID id);

    List<TipoMesa> listar();

    boolean existeActivoPorId(UUID id);

    boolean existePorNombre(String nombre);
}
