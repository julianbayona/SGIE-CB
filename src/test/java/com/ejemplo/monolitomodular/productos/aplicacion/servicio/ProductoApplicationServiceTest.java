package com.ejemplo.monolitomodular.productos.aplicacion.servicio;

import com.ejemplo.monolitomodular.productos.aplicacion.dto.ProductoView;
import com.ejemplo.monolitomodular.productos.aplicacion.dto.RegistrarProductoCommand;
import com.ejemplo.monolitomodular.productos.dominio.modelo.Producto;
import com.ejemplo.monolitomodular.productos.dominio.puerto.salida.ProductoRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductoApplicationServiceTest {

    @Test
    void deberiaRegistrarProducto() {
        ProductoApplicationService service = new ProductoApplicationService(new InMemoryProductoRepositoryStub());

        ProductoView producto = service.ejecutar(new RegistrarProductoCommand("Teclado", new BigDecimal("149.90")));

        assertNotNull(producto.id());
        assertEquals("Teclado", producto.nombre());
        assertEquals(new BigDecimal("149.90"), producto.precio());
    }

    @Test
    void noDeberiaPermitirPrecioInvalido() {
        ProductoApplicationService service = new ProductoApplicationService(new InMemoryProductoRepositoryStub());

        assertThrows(
                DomainException.class,
                () -> service.ejecutar(new RegistrarProductoCommand("Teclado", BigDecimal.ZERO))
        );
    }

    private static class InMemoryProductoRepositoryStub implements ProductoRepository {

        private final List<Producto> productos = new ArrayList<>();

        @Override
        public Producto guardar(Producto producto) {
            productos.add(producto);
            return producto;
        }

        @Override
        public Optional<Producto> buscarPorId(UUID id) {
            return productos.stream().filter(producto -> producto.getId().equals(id)).findFirst();
        }

        @Override
        public List<Producto> listar() {
            return List.copyOf(productos);
        }
    }
}
