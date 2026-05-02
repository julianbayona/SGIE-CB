package com.ejemplo.monolitomodular.cotizaciones.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataCotizacionItemJpaRepository extends JpaRepository<CotizacionItemJpaEntity, UUID> {

    List<CotizacionItemJpaEntity> findByCotizacionId(UUID cotizacionId);
}
