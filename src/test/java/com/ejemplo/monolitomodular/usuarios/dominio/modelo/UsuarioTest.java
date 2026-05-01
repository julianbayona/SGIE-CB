package com.ejemplo.monolitomodular.usuarios.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void deberiaCrearUsuarioNuevo() {
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);

        assertNotNull(usuario.getId());
        assertEquals("Admin", usuario.getNombre());
        assertEquals("$2a$hash", usuario.getContrasenaHash());
        assertEquals(RolUsuario.ADMINISTRADOR, usuario.getRol());
        assertTrue(usuario.isActivo());
    }

    @Test
    void deberiaReconstruirUsuarioExistente() {
        UUID id = UUID.randomUUID();
        Usuario usuario = Usuario.reconstruir(id, "Juan", "$2a$hash", RolUsuario.ADMINISTRADOR, false);

        assertEquals(id, usuario.getId());
        assertEquals("Juan", usuario.getNombre());
        assertFalse(usuario.isActivo());
    }

    @Test
    void noDeberiaCrearUsuarioConNombreNulo() {
        assertThrows(DomainException.class, () ->
                Usuario.nuevo(null, "$2a$hash", RolUsuario.ADMINISTRADOR)
        );
    }

    @Test
    void noDeberiaCrearUsuarioConNombreBlank() {
        assertThrows(DomainException.class, () ->
                Usuario.nuevo("   ", "$2a$hash", RolUsuario.ADMINISTRADOR)
        );
    }

    @Test
    void noDeberiaCrearUsuarioConContrasenaHashNula() {
        assertThrows(DomainException.class, () ->
                Usuario.nuevo("Admin", null, RolUsuario.ADMINISTRADOR)
        );
    }

    @Test
    void noDeberiaCrearUsuarioConContrasenaHashBlank() {
        assertThrows(DomainException.class, () ->
                Usuario.nuevo("Admin", "   ", RolUsuario.ADMINISTRADOR)
        );
    }

    @Test
    void noDeberiaCrearUsuarioConRolNulo() {
        assertThrows(NullPointerException.class, () ->
                Usuario.nuevo("Admin", "$2a$hash", null)
        );
    }

    @Test
    void deberiaRecortarEspaciosEnNombre() {
        Usuario usuario = Usuario.nuevo("  Admin  ", "$2a$hash", RolUsuario.ADMINISTRADOR);
        assertEquals("Admin", usuario.getNombre());
    }
}