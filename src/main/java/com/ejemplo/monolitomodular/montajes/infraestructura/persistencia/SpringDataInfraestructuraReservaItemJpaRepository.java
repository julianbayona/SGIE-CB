package com.ejemplo.monolitomodular.montajes.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataInfraestructuraReservaItemJpaRepository extends JpaRepository<InfraestructuraReservaItemJpaEntity, UUID> {

    List<InfraestructuraReservaItemJpaEntity> findByInfraestructuraReservaId(UUID infraestructuraReservaId);

    void deleteByInfraestructuraReservaId(UUID infraestructuraReservaId);
}
