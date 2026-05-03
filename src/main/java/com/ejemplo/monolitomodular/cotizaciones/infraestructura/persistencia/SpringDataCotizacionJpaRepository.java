package com.ejemplo.monolitomodular.cotizaciones.infraestructura.persistencia;

import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataCotizacionJpaRepository extends JpaRepository<CotizacionJpaEntity, UUID> {

    List<CotizacionJpaEntity> findByReservaIdAndEstadoNotInOrderByCreatedAtDesc(UUID reservaId, List<EstadoCotizacion> estados);

    @Query("""
            select c
            from CotizacionJpaEntity c
            where exists (
                select 1
                from ReservaSalonJpaEntity r
                where r.id = c.reservaId
                  and r.reservaRaizId = :reservaRaizId
            )
            order by c.createdAt desc
            """)
    List<CotizacionJpaEntity> findByReservaRaizIdOrderByCreatedAtDesc(UUID reservaRaizId);

    @Modifying
    @Query("""
            update CotizacionJpaEntity c
               set c.estado = com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion.DESACTUALIZADA,
                   c.updatedAt = :updatedAt
             where c.reservaId = :reservaId
               and c.estado not in (
                   com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion.RECHAZADA,
                   com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion.DESACTUALIZADA
               )
            """)
    int desactualizarActivasPorReservaId(UUID reservaId, LocalDateTime updatedAt);
}
