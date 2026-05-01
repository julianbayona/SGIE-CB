package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import com.ejemplo.monolitomodular.menus.dominio.modelo.ItemMenu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Menu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.SeleccionMenu;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.MenuRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MenuJpaRepositoryAdapter implements MenuRepository {

    private final SpringDataMenuJpaRepository menuRepository;
    private final SpringDataSeleccionMenuJpaRepository seleccionRepository;
    private final SpringDataItemMenuJpaRepository itemRepository;

    public MenuJpaRepositoryAdapter(
            SpringDataMenuJpaRepository menuRepository,
            SpringDataSeleccionMenuJpaRepository seleccionRepository,
            SpringDataItemMenuJpaRepository itemRepository
    ) {
        this.menuRepository = menuRepository;
        this.seleccionRepository = seleccionRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Menu guardar(Menu menu) {
        LocalDateTime now = LocalDateTime.now();
        menuRepository.save(new MenuJpaEntity(menu.getId(), menu.getReservaId(), menu.getNotasGenerales(), now, now));
        seleccionRepository.saveAll(menu.getSelecciones().stream().map(this::toEntity).toList());
        itemRepository.saveAll(menu.getSelecciones().stream()
                .flatMap(seleccion -> seleccion.getItems().stream())
                .map(this::toEntity)
                .toList());
        return buscarPorReservaId(menu.getReservaId()).orElseThrow();
    }

    @Override
    public Optional<Menu> buscarPorReservaId(UUID reservaId) {
        return menuRepository.findByReservaId(reservaId).map(this::toDomain);
    }

    private Menu toDomain(MenuJpaEntity entity) {
        List<SeleccionMenu> selecciones = seleccionRepository.findByMenuId(entity.getId()).stream()
                .map(this::toDomain)
                .toList();
        return Menu.reconstruir(entity.getId(), entity.getReservaId(), entity.getNotasGenerales(), selecciones);
    }

    private SeleccionMenu toDomain(SeleccionMenuJpaEntity entity) {
        List<ItemMenu> items = itemRepository.findBySeleccionMenuId(entity.getId()).stream()
                .map(this::toDomain)
                .toList();
        return SeleccionMenu.reconstruir(entity.getId(), entity.getMenuId(), entity.getTipoMomentoId(), items);
    }

    private ItemMenu toDomain(ItemMenuJpaEntity entity) {
        return ItemMenu.reconstruir(
                entity.getId(),
                entity.getSeleccionMenuId(),
                entity.getPlatoId(),
                entity.getCantidad(),
                entity.getExcepciones(),
                entity.getPrecioOverride()
        );
    }

    private SeleccionMenuJpaEntity toEntity(SeleccionMenu seleccion) {
        return new SeleccionMenuJpaEntity(seleccion.getId(), seleccion.getMenuId(), seleccion.getTipoMomentoId());
    }

    private ItemMenuJpaEntity toEntity(ItemMenu item) {
        return new ItemMenuJpaEntity(
                item.getId(),
                item.getSeleccionMenuId(),
                item.getPlatoId(),
                item.getCantidad(),
                item.getExcepciones(),
                item.getPrecioOverride()
        );
    }
}
