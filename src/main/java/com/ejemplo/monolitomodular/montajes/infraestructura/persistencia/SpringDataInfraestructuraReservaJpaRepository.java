package com.ejemplo.monolitomodular.montajes.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataInfraestructuraReservaJpaRepository extends JpaRepository<InfraestructuraReservaJpaEntity, UUID> {

    Optional<InfraestructuraReservaJpaEntity> findByMontajeId(UUID montajeId);
}
