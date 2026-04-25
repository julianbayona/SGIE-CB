package com.ejemplo.monolitomodular.productos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.productos.aplicacion.dto.ProductoView;

import java.util.List;
import java.util.UUID;

public interface ConsultarProductoUseCase {

    ProductoView obtenerPorId(UUID id);

    List<ProductoView> listar();
}
