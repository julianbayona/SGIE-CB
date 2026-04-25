package com.ejemplo.monolitomodular.eventos.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataHistorialEstadoEventoJpaRepository extends JpaRepository<HistorialEstadoEventoJpaEntity, UUID> {
}
