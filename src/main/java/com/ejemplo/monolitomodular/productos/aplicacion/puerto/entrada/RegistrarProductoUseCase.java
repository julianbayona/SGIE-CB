package com.ejemplo.monolitomodular.productos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.productos.aplicacion.dto.ProductoView;
import com.ejemplo.monolitomodular.productos.aplicacion.dto.RegistrarProductoCommand;

public interface RegistrarProductoUseCase {

    ProductoView ejecutar(RegistrarProductoCommand command);
}
