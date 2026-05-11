package com.ejemplo.monolitomodular.cotizaciones.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SpringDataCotizacionReservaJpaRepository extends JpaRepository<CotizacionReservaJpaEntity, CotizacionReservaJpaId> {

    @Query("""
            select cr.reservaId
            from CotizacionReservaJpaEntity cr
            where cr.cotizacionId = :cotizacionId
            order by cr.createdAt asc
            """)
    List<UUID> findReservaIdsByCotizacionId(UUID cotizacionId);
}
