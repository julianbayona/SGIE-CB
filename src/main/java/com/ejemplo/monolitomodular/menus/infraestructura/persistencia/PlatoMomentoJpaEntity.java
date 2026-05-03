package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "plato_momento")
@IdClass(PlatoMomentoJpaId.class)
public class PlatoMomentoJpaEntity {

    @Id
    @Column(name = "id_plato")
    private UUID platoId;

    @Id
    @Column(name = "id_tipo_momento")
    private UUID tipoMomentoId;

    protected PlatoMomentoJpaEntity() {
    }
}
