package com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Sobremantel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SobremantelRepository {

    Sobremantel guardar(Sobremantel sobremantel);

    Optional<Sobremantel> buscarPorId(UUID id);

    List<Sobremantel> listar();

    boolean existeActivoPorId(UUID id);

    boolean existePorNombre(String nombre);
}
