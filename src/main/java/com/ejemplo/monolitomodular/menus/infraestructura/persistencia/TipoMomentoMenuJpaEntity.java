package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "tipo_momento_menu")
public class TipoMomentoMenuJpaEntity {

    @Id
    @Column(name = "id_tipo_momento")
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false)
    private boolean activo;

    protected TipoMomentoMenuJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isActivo() {
        return activo;
    }
}
