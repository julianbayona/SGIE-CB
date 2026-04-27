package com.ejemplo.monolitomodular.usuarios.infraestructura.persistencia;

import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuario")
public class UsuarioJpaEntity {

    @Id
    @Column(name = "id_usuario")
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "contrasena_hash", nullable = false, length = 255)
    private String contrasenaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RolUsuario rol;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected UsuarioJpaEntity() {
    }

    public UsuarioJpaEntity(
            UUID id,
            String nombre,
            String contrasenaHash,
            RolUsuario rol,
            boolean activo,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.nombre = nombre;
        this.contrasenaHash = contrasenaHash;
        this.rol = rol;
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

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }
}
