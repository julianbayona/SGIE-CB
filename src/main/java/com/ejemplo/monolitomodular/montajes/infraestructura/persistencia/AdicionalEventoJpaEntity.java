package com.ejemplo.monolitomodular.montajes.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "adicional_evento")
public class AdicionalEventoJpaEntity {

    @Id
    @Column(name = "id_adicional_evento")
    private UUID id;

    @Column(name = "id_montaje", nullable = false)
    private UUID montajeId;

    @Column(name = "id_tipo_adicional", nullable = false)
    private UUID tipoAdicionalId;

    @Column(nullable = false)
    private int cantidad;

    protected AdicionalEventoJpaEntity() {
    }

    public AdicionalEventoJpaEntity(UUID id, UUID montajeId, UUID tipoAdicionalId, int cantidad) {
        this.id = id;
        this.montajeId = montajeId;
        this.tipoAdicionalId = tipoAdicionalId;
        this.cantidad = cantidad;
    }

    public UUID getId() {
        return id;
    }

    public UUID getMontajeId() {
        return montajeId;
    }

    public UUID getTipoAdicionalId() {
        return tipoAdicionalId;
    }

    public int getCantidad() {
        return cantidad;
    }
}
