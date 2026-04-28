package com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Color;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ColorRepository {

    Color guardar(Color color);

    Optional<Color> buscarPorId(UUID id);

    List<Color> listar();

    boolean existeActivoPorId(UUID id);

    boolean existePorNombre(String nombre);
}
