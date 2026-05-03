package com.ejemplo.monolitomodular.montajes.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;

public class MontajeMesaReserva {

    private final UUID id;
    private final UUID montajeId;
    private final UUID tipoMesaId;
    private final UUID tipoSillaId;
    private final int sillaPorMesa;
    private final int cantidadMesas;
    private final UUID mantelId;
    private final UUID sobremantelId;
    private final boolean vajilla;
    private final boolean fajon;

    private MontajeMesaReserva(
            UUID id,
            UUID montajeId,
            UUID tipoMesaId,
            UUID tipoSillaId,
            int sillaPorMesa,
            int cantidadMesas,
            UUID mantelId,
            UUID sobremantelId,
            boolean vajilla,
            boolean fajon
    ) {
        this.id = Objects.requireNonNull(id, "El id del montaje de mesa es obligatorio");
        this.montajeId = Objects.requireNonNull(montajeId, "El montaje es obligatorio");
        this.tipoMesaId = Objects.requireNonNull(tipoMesaId, "El tipo de mesa es obligatorio");
        this.tipoSillaId = Objects.requireNonNull(tipoSillaId, "El tipo de silla es obligatorio");
        if (sillaPorMesa <= 0) {
            throw new DomainException("La cantidad de sillas por mesa debe ser mayor a cero");
        }
        if (cantidadMesas <= 0) {
            throw new DomainException("La cantidad de mesas debe ser mayor a cero");
        }
        this.sillaPorMesa = sillaPorMesa;
        this.cantidadMesas = cantidadMesas;
        this.mantelId = Objects.requireNonNull(mantelId, "El mantel es obligatorio");
        this.sobremantelId = sobremantelId;
        this.vajilla = vajilla;
        this.fajon = fajon;
    }

    public static MontajeMesaReserva nueva(
            UUID montajeId,
            UUID tipoMesaId,
            UUID tipoSillaId,
            int sillaPorMesa,
            int cantidadMesas,
            UUID mantelId,
            UUID sobremantelId,
            boolean vajilla,
            boolean fajon
    ) {
        return new MontajeMesaReserva(UUID.randomUUID(), montajeId, tipoMesaId, tipoSillaId, sillaPorMesa, cantidadMesas, mantelId, sobremantelId, vajilla, fajon);
    }

    public static MontajeMesaReserva reconstruir(
            UUID id,
            UUID montajeId,
            UUID tipoMesaId,
            UUID tipoSillaId,
            int sillaPorMesa,
            int cantidadMesas,
            UUID mantelId,
            UUID sobremantelId,
            boolean vajilla,
            boolean fajon
    ) {
        return new MontajeMesaReserva(id, montajeId, tipoMesaId, tipoSillaId, sillaPorMesa, cantidadMesas, mantelId, sobremantelId, vajilla, fajon);
    }

    public UUID getId() {
        return id;
    }

    public UUID getMontajeId() {
        return montajeId;
    }

    public UUID getTipoMesaId() {
        return tipoMesaId;
    }

    public UUID getTipoSillaId() {
        return tipoSillaId;
    }

    public int getSillaPorMesa() {
        return sillaPorMesa;
    }

    public int getCantidadMesas() {
        return cantidadMesas;
    }

    public UUID getMantelId() {
        return mantelId;
    }

    public UUID getSobremantelId() {
        return sobremantelId;
    }

    public boolean isVajilla() {
        return vajilla;
    }

    public boolean isFajon() {
        return fajon;
    }
}
