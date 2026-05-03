package com.ejemplo.monolitomodular.montajes.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "infraestructura_reserva")
public class InfraestructuraReservaJpaEntity {

    @Id
    @Column(name = "id_infra_reserva")
    private UUID id;

    @Column(name = "id_montaje", nullable = false)
    private UUID montajeId;

    @Column(name = "mesa_ponque", nullable = false)
    private boolean mesaPonque;

    @Column(name = "mesa_regalos", nullable = false)
    private boolean mesaRegalos;

    @Column(name = "espacio_musicos", nullable = false)
    private boolean espacioMusicos;

    @Column(name = "estante_bombas", nullable = false)
    private boolean estanteBombas;

    protected InfraestructuraReservaJpaEntity() {
    }

    public InfraestructuraReservaJpaEntity(
            UUID id,
            UUID montajeId,
            boolean mesaPonque,
            boolean mesaRegalos,
            boolean espacioMusicos,
            boolean estanteBombas
    ) {
        this.id = id;
        this.montajeId = montajeId;
        this.mesaPonque = mesaPonque;
        this.mesaRegalos = mesaRegalos;
        this.espacioMusicos = espacioMusicos;
        this.estanteBombas = estanteBombas;
    }

    public UUID getId() {
        return id;
    }

    public UUID getMontajeId() {
        return montajeId;
    }

    public boolean isMesaPonque() {
        return mesaPonque;
    }

    public boolean isMesaRegalos() {
        return mesaRegalos;
    }

    public boolean isEspacioMusicos() {
        return espacioMusicos;
    }

    public boolean isEstanteBombas() {
        return estanteBombas;
    }
}
