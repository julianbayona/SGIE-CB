package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.ModoCobroAdicional;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tipo_adicional")
public class TipoAdicionalJpaEntity {

    @Id
    @Column(name = "id_tipo_adicional")
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "modo_cobro", nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private ModoCobroAdicional modoCobro;

    @Column(name = "precio_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioBase;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected TipoAdicionalJpaEntity() {
    }

    public TipoAdicionalJpaEntity(
            UUID id,
            String nombre,
            ModoCobroAdicional modoCobro,
            BigDecimal precioBase,
            boolean activo,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.nombre = nombre;
        this.modoCobro = modoCobro;
        this.precioBase = precioBase;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public ModoCobroAdicional getModoCobro() {
        return modoCobro;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public boolean isActivo() {
        return activo;
    }
}
