package com.ejemplo.monolitomodular.productos.aplicacion.servicio;

import com.ejemplo.monolitomodular.productos.aplicacion.dto.ProductoView;
import com.ejemplo.monolitomodular.productos.aplicacion.dto.RegistrarProductoCommand;
import com.ejemplo.monolitomodular.productos.aplicacion.puerto.entrada.ConsultarProductoUseCase;
import com.ejemplo.monolitomodular.productos.aplicacion.puerto.entrada.RegistrarProductoUseCase;
import com.ejemplo.monolitomodular.productos.dominio.modelo.Producto;
import com.ejemplo.monolitomodular.productos.dominio.puerto.salida.ProductoRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductoApplicationService implements RegistrarProductoUseCase, ConsultarProductoUseCase {

    private final ProductoRepository productoRepository;

    public ProductoApplicationService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public ProductoView ejecutar(RegistrarProductoCommand command) {
        Producto producto = Producto.nuevo(command.nombre(), command.precio());
        Producto guardado = productoRepository.guardar(producto);
        return toView(guardado);
    }

    @Override
    public ProductoView obtenerPorId(UUID id) {
        return productoRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Producto no encontrado"));
    }

    @Override
    public List<ProductoView> listar() {
        return productoRepository.listar().stream()
                .map(this::toView)
                .toList();
    }

    private ProductoView toView(Producto producto) {
        return new ProductoView(producto.getId(), producto.getNombre(), producto.getPrecio());
    }
}
