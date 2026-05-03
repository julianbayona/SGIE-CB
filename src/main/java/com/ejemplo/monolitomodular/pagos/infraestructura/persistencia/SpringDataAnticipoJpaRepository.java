package com.ejemplo.monolitomodular.pagos.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface SpringDataAnticipoJpaRepository extends JpaRepository<AnticipoJpaEntity, UUID> {

    List<AnticipoJpaEntity> findByCotizacionIdOrderByFechaPagoAsc(UUID cotizacionId);

    @Query("select coalesce(sum(a.valor), 0) from AnticipoJpaEntity a where a.cotizacionId = :cotizacionId")
    BigDecimal totalPorCotizacionId(UUID cotizacionId);
}
