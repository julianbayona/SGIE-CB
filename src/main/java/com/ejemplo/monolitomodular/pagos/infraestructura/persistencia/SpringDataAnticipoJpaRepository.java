package com.ejemplo.monolitomodular.pagos.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataAnticipoJpaRepository extends JpaRepository<AnticipoJpaEntity, UUID> {

    List<AnticipoJpaEntity> findByCotizacionIdOrderByFechaPagoAsc(UUID cotizacionId);

    @Query("select coalesce(sum(a.valor), 0) from AnticipoJpaEntity a where a.cotizacionId = :cotizacionId")
    BigDecimal totalPorCotizacionId(UUID cotizacionId);

    @Query("""
            select coalesce(sum(a.valor), 0)
            from AnticipoJpaEntity a
            where exists (
                select 1
                from CotizacionJpaEntity c, ReservaSalonJpaEntity r
                where c.id = a.cotizacionId
                  and r.id = c.reservaId
                  and r.eventoId = :eventoId
            )
            """)
    BigDecimal totalPorEventoId(UUID eventoId);

    @Query("""
            select
                e.id as eventoId,
                c.id as cotizacionId,
                cliente.nombreCompleto as nombreCliente,
                cliente.telefono as telefonoCliente,
                cliente.correo as correoCliente,
                e.fechaHoraInicio as fechaHoraInicio,
                c.valorTotal as valorTotal,
                (
                    select coalesce(sum(a.valor), 0)
                    from AnticipoJpaEntity a
                    where exists (
                        select 1
                        from CotizacionJpaEntity cPago, ReservaSalonJpaEntity rPago
                        where cPago.id = a.cotizacionId
                          and rPago.id = cPago.reservaId
                          and rPago.eventoId = e.id
                    )
                ) as totalPagado
            from CotizacionJpaEntity c, ReservaSalonJpaEntity r, EventoJpaEntity e, ClienteJpaEntity cliente
            where r.id = c.reservaId
              and e.id = r.eventoId
              and cliente.id = e.clienteId
              and c.vigente = true
              and c.estado = com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion.ACEPTADA
              and r.vigente = true
              and e.estado <> com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento.CANCELADO
              and e.fechaHoraInicio >= :desde
              and e.fechaHoraInicio <= :hasta
              and c.valorTotal > (
                    select coalesce(sum(a.valor), 0)
                    from AnticipoJpaEntity a
                    where exists (
                        select 1
                        from CotizacionJpaEntity cPago, ReservaSalonJpaEntity rPago
                        where cPago.id = a.cotizacionId
                          and rPago.id = cPago.reservaId
                          and rPago.eventoId = e.id
                    )
              )
            order by e.fechaHoraInicio asc
            """)
    List<EventoAnticipoPendienteProjection> buscarEventosConAnticipoPendiente(
            LocalDateTime desde,
            LocalDateTime hasta,
            Pageable pageable
    );
}
