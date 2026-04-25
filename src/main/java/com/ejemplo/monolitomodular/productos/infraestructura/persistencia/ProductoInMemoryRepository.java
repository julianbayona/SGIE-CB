package com.ejemplo.monolitomodular.productos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.productos.dominio.modelo.Producto;
import com.ejemplo.monolitomodular.productos.dominio.puerto.salida.ProductoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class ProductoInMemoryRepository implements ProductoRepository {

    private final ConcurrentMap<UUID, Producto> storage = new ConcurrentHashMap<>();

    @Override
    public Producto guardar(Producto producto) {
        storage.put(producto.getId(), producto);
        return producto;
    }

    @Override
    public Optional<Producto> buscarPorId(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Producto> listar() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Producto::getNombre))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
