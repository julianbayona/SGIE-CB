package com.ejemplo.monolitomodular.eventos.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataEventoJpaRepository extends JpaRepository<EventoJpaEntity, UUID> {

    List<EventoJpaEntity> findAllByOrderByCreatedAtAsc();
}
