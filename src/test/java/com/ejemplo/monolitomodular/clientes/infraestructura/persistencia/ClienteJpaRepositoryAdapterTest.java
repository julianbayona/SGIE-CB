package com.ejemplo.monolitomodular.clientes.infraestructura.persistencia;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteJpaRepositoryAdapterTest {

    @Mock
    SpringDataClienteJpaRepository repository;

    @InjectMocks
    ClienteJpaRepositoryAdapter adapter;

    private UUID clienteId;
    private ClienteJpaEntity entityBase;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        entityBase = new ClienteJpaEntity(
                clienteId, "1234567890", "Juan Perez", "3001234567",
                "juan@mail.com", TipoCliente.SOCIO, true, null,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void deberiaGuardarClienteYRetornarDominio() {
        when(repository.save(any())).thenReturn(entityBase);

        Cliente cliente = Cliente.reconstruir(clienteId, "1234567890", "Juan Perez",
                "3001234567", "juan@mail.com", TipoCliente.SOCIO, true, null);

        Cliente resultado = adapter.guardar(cliente);

        assertNotNull(resultado);
        assertEquals(clienteId, resultado.getId());
        assertEquals("1234567890", resultado.getCedula());
        assertEquals("Juan Perez", resultado.getNombreCompleto());
        verify(repository, times(1)).save(any());
    }

    @Test
    void deberiaBuscarPorIdYRetornarPresente() {
        when(repository.findById(clienteId)).thenReturn(Optional.of(entityBase));

        Optional<Cliente> resultado = adapter.buscarPorId(clienteId);

        assertTrue(resultado.isPresent());
        assertEquals(clienteId, resultado.get().getId());
    }

    @Test
    void deberiaBuscarPorIdYRetornarVacio() {
        when(repository.findById(clienteId)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = adapter.buscarPorId(clienteId);

        assertFalse(resultado.isPresent());
    }

    @Test
    void deberiaBuscarPorCedula() {
        when(repository.findByCedulaIgnoreCase("1234567890")).thenReturn(Optional.of(entityBase));

        Optional<Cliente> resultado = adapter.buscarPorCedula("1234567890");

        assertTrue(resultado.isPresent());
        assertEquals("1234567890", resultado.get().getCedula());
    }

    @Test
    void deberiaListarClientesOrdenadosPorNombre() {
        ClienteJpaEntity entityB = new ClienteJpaEntity(UUID.randomUUID(), "987", "Zara Gomez",
                "300", "zara@mail.com", TipoCliente.NO_SOCIO, true, null,
                LocalDateTime.now(), LocalDateTime.now());
        when(repository.findAll()).thenReturn(List.of(entityB, entityBase));

        List<Cliente> resultado = adapter.listar();

        assertEquals(2, resultado.size());
        // Debe estar ordenado por nombre (Juan < Zara)
        assertEquals("Juan Perez", resultado.get(0).getNombreCompleto());
        assertEquals("Zara Gomez", resultado.get(1).getNombreCompleto());
    }

    @Test
    void deberiaBuscarPorFiltroConFiltroValido() {
        when(repository.buscarPorFiltro("Juan")).thenReturn(List.of(entityBase));

        List<Cliente> resultado = adapter.buscarPorFiltro("Juan");

        assertEquals(1, resultado.size());
        assertEquals("Juan Perez", resultado.get(0).getNombreCompleto());
    }

    @Test
    void deberiaBuscarPorFiltroNuloRetornaListar() {
        when(repository.findAll()).thenReturn(List.of(entityBase));

        List<Cliente> resultado = adapter.buscarPorFiltro(null);

        assertEquals(1, resultado.size());
    }

    @Test
    void deberiaBuscarPorFiltroBlancoRetornaListar() {
        when(repository.findAll()).thenReturn(List.of(entityBase));

        List<Cliente> resultado = adapter.buscarPorFiltro("  ");

        assertEquals(1, resultado.size());
    }

    @Test
    void clienteJpaEntityDeberiaExponer_getters() {
        assertEquals(clienteId, entityBase.getId());
        assertEquals("1234567890", entityBase.getCedula());
        assertEquals("Juan Perez", entityBase.getNombreCompleto());
        assertEquals("3001234567", entityBase.getTelefono());
        assertEquals("juan@mail.com", entityBase.getCorreo());
        assertEquals(TipoCliente.SOCIO, entityBase.getTipoCliente());
        assertTrue(entityBase.isActivo());
        assertNull(entityBase.getCreadoPor());
    }
}