package com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida;

import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(UUID id);

    default List<Usuario> listar() {
        return List.of();
    }

    default long contarAdministradoresActivos() {
        return 0;
    }

    default Optional<Usuario> buscarPorNombre(String nombre) {
        return Optional.empty();
    }
}
