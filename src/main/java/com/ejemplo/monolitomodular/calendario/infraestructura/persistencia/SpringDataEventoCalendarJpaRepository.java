package com.ejemplo.monolitomodular.calendario.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataEventoCalendarJpaRepository extends JpaRepository<EventoCalendarJpaEntity, UUID> {
}
