package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "seleccion_menu")
public class SeleccionMenuJpaEntity {

    @Id
    @Column(name = "id_seleccion_menu")
    private UUID id;

    @Column(name = "id_menu", nullable = false)
    private UUID menuId;

    @Column(name = "id_tipo_momento", nullable = false)
    private UUID tipoMomentoId;

    protected SeleccionMenuJpaEntity() {
    }

    public SeleccionMenuJpaEntity(UUID id, UUID menuId, UUID tipoMomentoId) {
        this.id = id;
        this.menuId = menuId;
        this.tipoMomentoId = tipoMomentoId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getMenuId() {
        return menuId;
    }

    public UUID getTipoMomentoId() {
        return tipoMomentoId;
    }
}
