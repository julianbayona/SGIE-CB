package com.ejemplo.monolitomodular.productos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.productos.dominio.modelo.Producto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductoRepository {

    Producto guardar(Producto producto);

    Optional<Producto> buscarPorId(UUID id);

    List<Producto> listar();
}
