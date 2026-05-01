package com.ejemplo.monolitomodular.montajes.dominio.modelo;

import java.util.UUID;

public class InfraestructuraReserva {

    private final UUID id;
    private final UUID montajeId;
    private final boolean mesaPonque;
    private final boolean mesaRegalos;
    private final boolean espacioMusicos;
    private final boolean estanteBombas;

    private InfraestructuraReserva(
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

    public static InfraestructuraReserva nueva(
            UUID montajeId,
            boolean mesaPonque,
            boolean mesaRegalos,
            boolean espacioMusicos,
            boolean estanteBombas
    ) {
        return new InfraestructuraReserva(UUID.randomUUID(), montajeId, mesaPonque, mesaRegalos, espacioMusicos, estanteBombas);
    }

    public static InfraestructuraReserva reconstruir(
            UUID id,
            UUID montajeId,
            boolean mesaPonque,
            boolean mesaRegalos,
            boolean espacioMusicos,
            boolean estanteBombas
    ) {
        return new InfraestructuraReserva(id, montajeId, mesaPonque, mesaRegalos, espacioMusicos, estanteBombas);
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
