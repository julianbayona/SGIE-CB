package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "item_menu")
public class ItemMenuJpaEntity {

    @Id
    @Column(name = "id_item_menu")
    private UUID id;

    @Column(name = "id_seleccion_menu", nullable = false)
    private UUID seleccionMenuId;

    @Column(name = "id_plato", nullable = false)
    private UUID platoId;

    @Column(nullable = false)
    private int cantidad;

    @Column(length = 500)
    private String excepciones;

    protected ItemMenuJpaEntity() {
    }

    public ItemMenuJpaEntity(UUID id, UUID seleccionMenuId, UUID platoId, int cantidad, String excepciones) {
        this.id = id;
        this.seleccionMenuId = seleccionMenuId;
        this.platoId = platoId;
        this.cantidad = cantidad;
        this.excepciones = excepciones;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSeleccionMenuId() {
        return seleccionMenuId;
    }

    public UUID getPlatoId() {
        return platoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getExcepciones() {
        return excepciones;
    }
}
