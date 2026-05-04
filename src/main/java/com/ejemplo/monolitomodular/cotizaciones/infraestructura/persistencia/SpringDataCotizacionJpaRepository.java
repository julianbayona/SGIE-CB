package com.ejemplo.monolitomodular.cotizaciones.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataCotizacionJpaRepository extends JpaRepository<CotizacionJpaEntity, UUID> {

    List<CotizacionJpaEntity> findByReservaIdAndVigenteTrueOrderByCreatedAtDesc(UUID reservaId);

    @Query("""
            select c
            from CotizacionJpaEntity c
            where c.vigente = true
              and c.estado = com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion.ACEPTADA
              and exists (
                  select 1
                  from ReservaSalonJpaEntity r
                  where r.id = c.reservaId
                    and r.eventoId = :eventoId
                    and r.vigente = true
              )
            order by c.createdAt desc
            """)
    List<CotizacionJpaEntity> findAceptadaVigenteByEventoIdOrderByCreatedAtDesc(UUID eventoId);

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
               set c.estado =
                   case
                       when c.estado = com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion.ACEPTADA
                       then com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion.ACEPTADA
                       else com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion.DESACTUALIZADA
                   end,
                   c.vigente = false,
                   c.updatedAt = :updatedAt
             where c.reservaId = :reservaId
               and c.vigente = true
            """)
    int desactualizarActivasPorReservaId(UUID reservaId, LocalDateTime updatedAt);
}
