package com.ejemplo.monolitomodular.pruebasplato.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataPruebaPlatoJpaRepository extends JpaRepository<PruebaPlatoJpaEntity, UUID> {
}
