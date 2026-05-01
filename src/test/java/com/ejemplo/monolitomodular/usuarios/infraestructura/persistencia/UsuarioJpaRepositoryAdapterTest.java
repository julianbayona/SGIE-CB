package com.ejemplo.monolitomodular.usuarios.infraestructura.persistencia;

import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioJpaRepositoryAdapterTest {

    @Mock
    SpringDataUsuarioJpaRepository repository;

    @InjectMocks
    UsuarioJpaRepositoryAdapter adapter;

    private UUID usuarioId;
    private UsuarioJpaEntity entityBase;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        entityBase = new UsuarioJpaEntity(
                usuarioId,
                "Juan Perez",
                "$2a$10$hashedpassword",
                RolUsuario.ADMINISTRADOR,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void deberiaGuardarUsuarioYRetornarDominio() {
        when(repository.save(any())).thenReturn(entityBase);

        Usuario usuario = Usuario.reconstruir(
                usuarioId,
                "Juan Perez",
                "$2a$10$hashedpassword",
                RolUsuario.ADMINISTRADOR,
                true
        );

        Usuario resultado = adapter.guardar(usuario);

        assertNotNull(resultado);
        assertEquals(usuarioId, resultado.getId());
        assertEquals("Juan Perez", resultado.getNombre());
        assertEquals(RolUsuario.ADMINISTRADOR, resultado.getRol());
        assertTrue(resultado.isActivo());
        verify(repository, times(1)).save(any());
    }

    @Test
    void deberiaBuscarPorIdYRetornarPresente() {
        when(repository.findById(usuarioId)).thenReturn(Optional.of(entityBase));

        Optional<Usuario> resultado = adapter.buscarPorId(usuarioId);

        assertTrue(resultado.isPresent());
        assertEquals(usuarioId, resultado.get().getId());
        assertEquals("Juan Perez", resultado.get().getNombre());
        verify(repository).findById(usuarioId);
    }

    @Test
    void deberiaBuscarPorIdYRetornarVacioSiNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = adapter.buscarPorId(idInexistente);

        assertFalse(resultado.isPresent());
        verify(repository).findById(idInexistente);
    }

    @Test
    void deberiaGuardarUsuarioConDiferentesRoles() {
        for (RolUsuario rol : RolUsuario.values()) {
            UsuarioJpaEntity entityConRol = new UsuarioJpaEntity(
                    UUID.randomUUID(), "Nombre", "hash", rol, true,
                    LocalDateTime.now(), LocalDateTime.now()
            );
            when(repository.save(any())).thenReturn(entityConRol);

            Usuario usuario = Usuario.reconstruir(
                    entityConRol.getId(), "Nombre", "hash", rol, true
            );

            Usuario resultado = adapter.guardar(usuario);

            assertEquals(rol, resultado.getRol());
        }
    }

    @Test
    void deberiaGuardarUsuarioInactivo() {
        UsuarioJpaEntity entityInactivo = new UsuarioJpaEntity(
                usuarioId, "Ana Lopez", "$2a$10$hash2",
                RolUsuario.GERENTE, false,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.save(any())).thenReturn(entityInactivo);

        Usuario usuario = Usuario.reconstruir(
                usuarioId, "Ana Lopez", "$2a$10$hash2", RolUsuario.GERENTE, false
        );

        Usuario resultado = adapter.guardar(usuario);

        assertFalse(resultado.isActivo());
        assertEquals(RolUsuario.GERENTE, resultado.getRol());
    }

    @Test
    void deberiaConservarContrasenaHash() {
        String hashEsperado = "$2a$10$specificHashValue";
        UsuarioJpaEntity entityConHash = new UsuarioJpaEntity(
                usuarioId, "Carlos", hashEsperado,
                RolUsuario.TESORERO, true,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.findById(usuarioId)).thenReturn(Optional.of(entityConHash));

        Optional<Usuario> resultado = adapter.buscarPorId(usuarioId);

        assertTrue(resultado.isPresent());
        assertEquals(hashEsperado, resultado.get().getContrasenaHash());
    }
}