package com.ejemplo.monolitomodular.montajes.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "montaje_mesas_reserva")
public class MontajeMesaReservaJpaEntity {

    @Id
    @Column(name = "id_montaje_mesa")
    private UUID id;

    @Column(name = "id_montaje", nullable = false)
    private UUID montajeId;

    @Column(name = "id_tipo_mesa", nullable = false)
    private UUID tipoMesaId;

    @Column(name = "id_tipo_silla", nullable = false)
    private UUID tipoSillaId;

    @Column(name = "silla_por_mesa", nullable = false)
    private int sillaPorMesa;

    @Column(name = "cantidad_mesas", nullable = false)
    private int cantidadMesas;

    @Column(name = "id_mantel", nullable = false)
    private UUID mantelId;

    @Column(name = "id_sobremantel")
    private UUID sobremantelId;

    @Column(nullable = false)
    private boolean vajilla;

    @Column(nullable = false)
    private boolean fajon;

    protected MontajeMesaReservaJpaEntity() {
    }

    public MontajeMesaReservaJpaEntity(
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
        this.id = id;
        this.montajeId = montajeId;
        this.tipoMesaId = tipoMesaId;
        this.tipoSillaId = tipoSillaId;
        this.sillaPorMesa = sillaPorMesa;
        this.cantidadMesas = cantidadMesas;
        this.mantelId = mantelId;
        this.sobremantelId = sobremantelId;
        this.vajilla = vajilla;
        this.fajon = fajon;
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
