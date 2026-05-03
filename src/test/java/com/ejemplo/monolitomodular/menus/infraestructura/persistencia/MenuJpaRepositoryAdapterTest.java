package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import com.ejemplo.monolitomodular.menus.dominio.modelo.ItemMenu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Menu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.SeleccionMenu;
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
class MenuJpaRepositoryAdapterTest {

    @Mock
    SpringDataMenuJpaRepository menuRepository;

    @Mock
    SpringDataSeleccionMenuJpaRepository seleccionRepository;

    @Mock
    SpringDataItemMenuJpaRepository itemRepository;

    @InjectMocks
    MenuJpaRepositoryAdapter adapter;

    private UUID menuId;
    private UUID reservaId;
    private UUID seleccionId;
    private UUID tipoMomentoId;
    private UUID itemId;
    private UUID platoId;

    private MenuJpaEntity menuEntity;
    private SeleccionMenuJpaEntity seleccionEntity;
    private ItemMenuJpaEntity itemEntity;

    @BeforeEach
    void setUp() {
        menuId = UUID.randomUUID();
        reservaId = UUID.randomUUID();
        seleccionId = UUID.randomUUID();
        tipoMomentoId = UUID.randomUUID();
        itemId = UUID.randomUUID();
        platoId = UUID.randomUUID();

        LocalDateTime now = LocalDateTime.now();
        menuEntity = new MenuJpaEntity(menuId, reservaId, "Sin lactosa", now, now);
        seleccionEntity = new SeleccionMenuJpaEntity(seleccionId, menuId, tipoMomentoId);
        itemEntity = new ItemMenuJpaEntity(itemId, seleccionId, platoId, 2, "Sin sal");
    }

    @Test
    void deberiaGuardarMenuYRetornarDominio() {
        ItemMenu item = ItemMenu.reconstruir(itemId, seleccionId, platoId, 2, "Sin sal");
        SeleccionMenu seleccion = SeleccionMenu.reconstruir(seleccionId, menuId, tipoMomentoId, List.of(item));
        Menu menu = Menu.reconstruir(menuId, reservaId, "Sin lactosa", List.of(seleccion));

        when(menuRepository.save(any())).thenReturn(menuEntity);
        when(seleccionRepository.saveAll(any())).thenReturn(List.of(seleccionEntity));
        when(itemRepository.saveAll(any())).thenReturn(List.of(itemEntity));
        when(menuRepository.findByReservaId(reservaId)).thenReturn(Optional.of(menuEntity));
        when(seleccionRepository.findByMenuId(menuId)).thenReturn(List.of(seleccionEntity));
        when(itemRepository.findBySeleccionMenuId(seleccionId)).thenReturn(List.of(itemEntity));

        Menu resultado = adapter.guardar(menu);

        assertNotNull(resultado);
        assertEquals(menuId, resultado.getId());
        assertEquals(reservaId, resultado.getReservaId());
        assertEquals("Sin lactosa", resultado.getNotasGenerales());
        assertEquals(1, resultado.getSelecciones().size());
        assertEquals(1, resultado.getSelecciones().get(0).getItems().size());

        verify(menuRepository).save(any());
        verify(seleccionRepository).saveAll(any());
        verify(itemRepository).saveAll(any());
    }

    @Test
    void deberiaBuscarPorReservaIdYRetornarPresente() {
        when(menuRepository.findByReservaId(reservaId)).thenReturn(Optional.of(menuEntity));
        when(seleccionRepository.findByMenuId(menuId)).thenReturn(List.of(seleccionEntity));
        when(itemRepository.findBySeleccionMenuId(seleccionId)).thenReturn(List.of(itemEntity));

        Optional<Menu> resultado = adapter.buscarPorReservaId(reservaId);

        assertTrue(resultado.isPresent());
        assertEquals(menuId, resultado.get().getId());
        assertEquals(reservaId, resultado.get().getReservaId());
    }

    @Test
    void deberiaBuscarPorReservaIdYRetornarVacio() {
        when(menuRepository.findByReservaId(reservaId)).thenReturn(Optional.empty());

        Optional<Menu> resultado = adapter.buscarPorReservaId(reservaId);

        assertFalse(resultado.isPresent());
    }

    @Test
    void deberiaMappearSeleccionConMultiplesItems() {
        UUID itemId2 = UUID.randomUUID();
        UUID platoId2 = UUID.randomUUID();
        ItemMenuJpaEntity itemEntity2 = new ItemMenuJpaEntity(itemId2, seleccionId, platoId2, 5, null);

        when(menuRepository.findByReservaId(reservaId)).thenReturn(Optional.of(menuEntity));
        when(seleccionRepository.findByMenuId(menuId)).thenReturn(List.of(seleccionEntity));
        when(itemRepository.findBySeleccionMenuId(seleccionId)).thenReturn(List.of(itemEntity, itemEntity2));

        Optional<Menu> resultado = adapter.buscarPorReservaId(reservaId);

        assertTrue(resultado.isPresent());
        assertEquals(2, resultado.get().getSelecciones().get(0).getItems().size());
    }

    @Test
    void deberiaMappearMenuConMultiplesSelecciones() {
        UUID seleccionId2 = UUID.randomUUID();
        UUID tipoMomentoId2 = UUID.randomUUID();
        UUID itemId2 = UUID.randomUUID();
        UUID platoId2 = UUID.randomUUID();
        SeleccionMenuJpaEntity seleccionEntity2 = new SeleccionMenuJpaEntity(seleccionId2, menuId, tipoMomentoId2);
        ItemMenuJpaEntity itemEntity2 = new ItemMenuJpaEntity(itemId2, seleccionId2, platoId2, 1, null);

        when(menuRepository.findByReservaId(reservaId)).thenReturn(Optional.of(menuEntity));
        when(seleccionRepository.findByMenuId(menuId)).thenReturn(List.of(seleccionEntity, seleccionEntity2));
        when(itemRepository.findBySeleccionMenuId(seleccionId)).thenReturn(List.of(itemEntity));
        when(itemRepository.findBySeleccionMenuId(seleccionId2)).thenReturn(List.of(itemEntity2));

        Optional<Menu> resultado = adapter.buscarPorReservaId(reservaId);

        assertTrue(resultado.isPresent());
        assertEquals(2, resultado.get().getSelecciones().size());
    }

    @Test
    void menuJpaEntityDeberiaExponerGetters() {
        assertEquals(menuId, menuEntity.getId());
        assertEquals(reservaId, menuEntity.getReservaId());
        assertEquals("Sin lactosa", menuEntity.getNotasGenerales());
    }

    @Test
    void seleccionMenuJpaEntityDeberiaExponerGetters() {
        assertEquals(seleccionId, seleccionEntity.getId());
        assertEquals(menuId, seleccionEntity.getMenuId());
        assertEquals(tipoMomentoId, seleccionEntity.getTipoMomentoId());
    }

    @Test
    void itemMenuJpaEntityDeberiaExponerGetters() {
        assertEquals(itemId, itemEntity.getId());
        assertEquals(seleccionId, itemEntity.getSeleccionMenuId());
        assertEquals(platoId, itemEntity.getPlatoId());
        assertEquals(2, itemEntity.getCantidad());
        assertEquals("Sin sal", itemEntity.getExcepciones());
    }
}