package com.ejemplo.monolitomodular.clientes.infraestructura.persistencia;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cliente")
public class ClienteJpaEntity {

    @Id
    @Column(name = "id_cliente")
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String nombreCompleto;

    @Column(nullable = false, length = 30)
    private String telefono;

    @Column(nullable = false, length = 120)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente", nullable = false, length = 20)
    private TipoCliente tipoCliente;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "creado_por")
    private UUID creadoPor;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ClienteJpaEntity() {
    }

    public ClienteJpaEntity(
            UUID id,
            String cedula,
            String nombreCompleto,
            String telefono,
            String correo,
            TipoCliente tipoCliente,
            boolean activo,
            UUID creadoPor,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.cedula = cedula;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.correo = correo;
        this.tipoCliente = tipoCliente;
        this.activo = activo;
        this.creadoPor = creadoPor;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public boolean isActivo() {
        return activo;
    }

    public UUID getCreadoPor() {
        return creadoPor;
    }
}
