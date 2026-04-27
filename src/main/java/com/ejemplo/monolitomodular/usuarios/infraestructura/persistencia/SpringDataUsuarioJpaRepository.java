package com.ejemplo.monolitomodular.usuarios.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataUsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
}
