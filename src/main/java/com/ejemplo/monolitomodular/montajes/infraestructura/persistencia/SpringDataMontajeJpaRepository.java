package com.ejemplo.monolitomodular.montajes.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataMontajeJpaRepository extends JpaRepository<MontajeJpaEntity, UUID> {

    Optional<MontajeJpaEntity> findByReservaId(UUID reservaId);
}
