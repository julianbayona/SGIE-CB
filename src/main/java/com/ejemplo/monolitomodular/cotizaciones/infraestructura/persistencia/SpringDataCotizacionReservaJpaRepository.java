package com.ejemplo.monolitomodular.cotizaciones.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCotizacionReservaJpaRepository extends JpaRepository<CotizacionReservaJpaEntity, CotizacionReservaJpaId> {
}
