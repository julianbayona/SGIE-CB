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

/**
 * PI029 — Persistencia de cliente con adaptador JPA
 * Tipo: Integración Bottom-up (Caja gris)
 * Requisito: DP-GESTION_CLIENTES-02
 *
 * Valida la traducción dominio ↔ entidad JPA y los métodos:
 * guardar(), buscarPorId(), buscarPorCedula() y buscarPorFiltro().
 */
@ExtendWith(MockitoExtension.class)
class ClienteJpaRepositoryAdapterIntegrationTest {

    @Mock
    SpringDataClienteJpaRepository repository;

    @InjectMocks
    ClienteJpaRepositoryAdapter adapter;

    private static final UUID CLIENTE_ID     = UUID.randomUUID();
    private static final UUID CREADO_POR_ID  = UUID.randomUUID();

    private ClienteJpaEntity entityBase;

    @BeforeEach
    void setUp() {
        entityBase = new ClienteJpaEntity(
                CLIENTE_ID, "123", "Ana Pérez", "3001234567",
                "ana@mail.com", TipoCliente.SOCIO, true, CREADO_POR_ID,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // ──────────────────────────────────────────────────────────────────────
    // guardar()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarClienteYReconstruirDominioSinPerdidaDeDatos() {
        when(repository.save(any())).thenReturn(entityBase);

        Cliente cliente = Cliente.reconstruir(
                CLIENTE_ID, "123", "Ana Pérez", "3001234567",
                "ana@mail.com", TipoCliente.SOCIO, true, CREADO_POR_ID
        );

        Cliente resultado = adapter.guardar(cliente);

        assertNotNull(resultado);
        assertEquals(CLIENTE_ID, resultado.getId());
        assertEquals("123", resultado.getCedula());
        assertEquals("Ana Pérez", resultado.getNombreCompleto());
        assertEquals("3001234567", resultado.getTelefono());
        assertEquals("ana@mail.com", resultado.getCorreo());
        assertEquals(TipoCliente.SOCIO, resultado.getTipoCliente());
        assertTrue(resultado.isActivo());
        assertEquals(CREADO_POR_ID, resultado.getCreadoPor());
        verify(repository, times(1)).save(any());
    }

    @Test
    void deberiaGuardarClienteSinUsuarioCreador() {
        ClienteJpaEntity entitySinCreador = new ClienteJpaEntity(
                CLIENTE_ID, "999", "Pedro López", "3009999999",
                "pedro@mail.com", TipoCliente.NO_SOCIO, true, null,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.save(any())).thenReturn(entitySinCreador);

        Cliente cliente = Cliente.reconstruir(
                CLIENTE_ID, "999", "Pedro López", "3009999999",
                "pedro@mail.com", TipoCliente.NO_SOCIO, true, null
        );

        Cliente resultado = adapter.guardar(cliente);

        assertNotNull(resultado);
        assertNull(resultado.getCreadoPor());
        assertEquals(TipoCliente.NO_SOCIO, resultado.getTipoCliente());
        verify(repository, times(1)).save(any());
    }

    // ──────────────────────────────────────────────────────────────────────
    // buscarPorId()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarPorIdYRetornarClientePresente() {
        when(repository.findById(CLIENTE_ID)).thenReturn(Optional.of(entityBase));

        Optional<Cliente> resultado = adapter.buscarPorId(CLIENTE_ID);

        assertTrue(resultado.isPresent());
        assertEquals(CLIENTE_ID, resultado.get().getId());
        assertEquals("123", resultado.get().getCedula());
        assertEquals("Ana Pérez", resultado.get().getNombreCompleto());
    }

    @Test
    void deberiaBuscarPorIdYRetornarVacioCuandoNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = adapter.buscarPorId(idInexistente);

        assertFalse(resultado.isPresent());
    }

    // ──────────────────────────────────────────────────────────────────────
    // buscarPorCedula()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarPorCedulaYRetornarClientePresente() {
        when(repository.findByCedulaIgnoreCase("123")).thenReturn(Optional.of(entityBase));

        Optional<Cliente> resultado = adapter.buscarPorCedula("123");

        assertTrue(resultado.isPresent());
        assertEquals("123", resultado.get().getCedula());
        assertEquals("Ana Pérez", resultado.get().getNombreCompleto());
    }

    @Test
    void deberiaBuscarPorCedulaYRetornarVacioCuandoNoExiste() {
        when(repository.findByCedulaIgnoreCase("cedula-inexistente")).thenReturn(Optional.empty());

        Optional<Cliente> resultado = adapter.buscarPorCedula("cedula-inexistente");

        assertFalse(resultado.isPresent());
    }

    // ──────────────────────────────────────────────────────────────────────
    // buscarPorFiltro()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarPorFiltroYRetornarCoincidencias() {
        when(repository.buscarPorFiltro("Ana")).thenReturn(List.of(entityBase));

        List<Cliente> resultado = adapter.buscarPorFiltro("Ana");

        assertEquals(1, resultado.size());
        assertEquals("Ana Pérez", resultado.get(0).getNombreCompleto());
        verify(repository, times(1)).buscarPorFiltro("Ana");
    }

    @Test
    void deberiaBuscarPorFiltroYRetornarListaVaciaSinCoincidencias() {
        when(repository.buscarPorFiltro("NoExiste")).thenReturn(List.of());

        List<Cliente> resultado = adapter.buscarPorFiltro("NoExiste");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deberiaBuscarPorFiltroNuloYDelegarAListar() {
        ClienteJpaEntity entityB = new ClienteJpaEntity(
                UUID.randomUUID(), "456", "Carlos Ruiz", "3007654321",
                "carlos@mail.com", TipoCliente.NO_SOCIO, true, null,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.findAll()).thenReturn(List.of(entityBase, entityB));

        List<Cliente> resultado = adapter.buscarPorFiltro(null);

        assertEquals(2, resultado.size());
        verify(repository, never()).buscarPorFiltro(any());
    }

    @Test
    void deberiaBuscarPorFiltroBlancoYDelegarAListar() {
        when(repository.findAll()).thenReturn(List.of(entityBase));

        List<Cliente> resultado = adapter.buscarPorFiltro("   ");

        assertEquals(1, resultado.size());
        verify(repository, never()).buscarPorFiltro(any());
    }

    // ──────────────────────────────────────────────────────────────────────
    // listar() — ordenamiento
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaListarClientesOrdenadosAlfabeticamentePorNombre() {
        ClienteJpaEntity entityZara = new ClienteJpaEntity(
                UUID.randomUUID(), "789", "Zara Gómez", "3005555555",
                "zara@mail.com", TipoCliente.SOCIO, true, null,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.findAll()).thenReturn(List.of(entityZara, entityBase));

        List<Cliente> resultado = adapter.listar();

        assertEquals(2, resultado.size());
        assertEquals("Ana Pérez", resultado.get(0).getNombreCompleto());
        assertEquals("Zara Gómez", resultado.get(1).getNombreCompleto());
    }
}