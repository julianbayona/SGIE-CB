package com.ejemplo.monolitomodular.clientes.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void deberiaCrearClienteNuevo() {
        Cliente cliente = Cliente.nuevo("123", "Ana Perez", "3001112233", "ana@correo.com", TipoCliente.SOCIO, null);

        assertNotNull(cliente.getId());
        assertEquals("123", cliente.getCedula());
        assertEquals("Ana Perez", cliente.getNombreCompleto());
        assertEquals("3001112233", cliente.getTelefono());
        assertEquals("ana@correo.com", cliente.getCorreo());
        assertEquals(TipoCliente.SOCIO, cliente.getTipoCliente());
        assertTrue(cliente.isActivo());
        assertNull(cliente.getCreadoPor());
    }

    @Test
    void deberiaCrearClienteConUsuarioCreador() {
        UUID creadoPor = UUID.randomUUID();
        Cliente cliente = Cliente.nuevo("456", "Juan Lopez", "3109998877", "juan@test.com", TipoCliente.NO_SOCIO, creadoPor);

        assertEquals(creadoPor, cliente.getCreadoPor());
        assertEquals(TipoCliente.NO_SOCIO, cliente.getTipoCliente());
    }

    @Test
    void deberiaReconstruirClienteExistente() {
        UUID id = UUID.randomUUID();
        UUID creadoPor = UUID.randomUUID();
        Cliente cliente = Cliente.reconstruir(id, "789", "Maria Garcia", "3205556677", "maria@ejemplo.com",
                TipoCliente.SOCIO, false, creadoPor);

        assertEquals(id, cliente.getId());
        assertEquals("789", cliente.getCedula());
        assertEquals("Maria Garcia", cliente.getNombreCompleto());
        assertFalse(cliente.isActivo());
        assertEquals(creadoPor, cliente.getCreadoPor());
    }

    @Test
    void noDeberiaCrearClienteConCedulaNula() {
        assertThrows(DomainException.class, () ->
                Cliente.nuevo(null, "Ana Perez", "3001112233", "ana@correo.com", TipoCliente.SOCIO, null)
        );
    }

    @Test
    void noDeberiaCrearClienteConCedulaBlank() {
        assertThrows(DomainException.class, () ->
                Cliente.nuevo("   ", "Ana Perez", "3001112233", "ana@correo.com", TipoCliente.SOCIO, null)
        );
    }

    @Test
    void noDeberiaCrearClienteConNombreNulo() {
        assertThrows(DomainException.class, () ->
                Cliente.nuevo("123", null, "3001112233", "ana@correo.com", TipoCliente.SOCIO, null)
        );
    }

    @Test
    void noDeberiaCrearClienteConNombreBlank() {
        assertThrows(DomainException.class, () ->
                Cliente.nuevo("123", "   ", "3001112233", "ana@correo.com", TipoCliente.SOCIO, null)
        );
    }

    @Test
    void noDeberiaCrearClienteConTelefonoNulo() {
        assertThrows(DomainException.class, () ->
                Cliente.nuevo("123", "Ana Perez", null, "ana@correo.com", TipoCliente.SOCIO, null)
        );
    }

    @Test
    void noDeberiaCrearClienteConTelefonoBlank() {
        assertThrows(DomainException.class, () ->
                Cliente.nuevo("123", "Ana Perez", "   ", "ana@correo.com", TipoCliente.SOCIO, null)
        );
    }

    @Test
    void noDeberiaCrearClienteConCorreoNulo() {
        assertThrows(DomainException.class, () ->
                Cliente.nuevo("123", "Ana Perez", "3001112233", null, TipoCliente.SOCIO, null)
        );
    }

    @Test
    void noDeberiaCrearClienteConCorreoInvalido() {
        assertThrows(DomainException.class, () ->
                Cliente.nuevo("123", "Ana Perez", "3001112233", "correo-invalido", TipoCliente.SOCIO, null)
        );
    }

    @Test
    void noDeberiaCrearClienteConCorreoSinArroba() {
        assertThrows(DomainException.class, () ->
                Cliente.nuevo("123", "Ana Perez", "3001112233", "correocorreo.com", TipoCliente.SOCIO, null)
        );
    }

    @Test
    void noDeberiaCrearClienteConTipoClienteNulo() {
        assertThrows(NullPointerException.class, () ->
                Cliente.nuevo("123", "Ana Perez", "3001112233", "ana@correo.com", null, null)
        );
    }

    @Test
    void deberiaNormalizarCorreoAMinusculas() {
        Cliente cliente = Cliente.nuevo("123", "Ana Perez", "3001112233", "ANA@CORREO.COM", TipoCliente.SOCIO, null);
        assertEquals("ana@correo.com", cliente.getCorreo());
    }

    @Test
    void deberiaRecortarEspaciosEnCedula() {
        Cliente cliente = Cliente.nuevo("  123  ", "Ana Perez", "3001112233", "ana@correo.com", TipoCliente.SOCIO, null);
        assertEquals("123", cliente.getCedula());
    }

    @Test
    void deberiaRecortarEspaciosEnNombre() {
        Cliente cliente = Cliente.nuevo("123", "  Ana Perez  ", "3001112233", "ana@correo.com", TipoCliente.SOCIO, null);
        assertEquals("Ana Perez", cliente.getNombreCompleto());
    }
}