package com.ejemplo.monolitomodular.montajes.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataMontajeMesaReservaJpaRepository extends JpaRepository<MontajeMesaReservaJpaEntity, UUID> {

    List<MontajeMesaReservaJpaEntity> findByMontajeId(UUID montajeId);
}
