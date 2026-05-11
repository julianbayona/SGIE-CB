package com.ejemplo.monolitomodular.reportes.infraestructura.persistencia;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.AnticipoPeriodoView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.AnticiposPorMetodoView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.DemandaSalonView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.EstadoEventoResumenView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.EventosMensualesView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.ReporteFinancieroEventoView;
import com.ejemplo.monolitomodular.reportes.dominio.puerto.salida.ReporteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class ReporteJpaRepositoryAdapter implements ReporteRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<EstadoEventoResumenView> contarEventosPorEstado(LocalDateTime desde, LocalDateTime hastaExclusivo) {
        return entityManager.createQuery("""
                        select e.estado, count(e)
                        from EventoJpaEntity e
                        where e.fechaHoraInicio >= :desde
                          and e.fechaHoraInicio < :hasta
                        group by e.estado
                        order by e.estado
                        """, Object[].class)
                .setParameter("desde", desde)
                .setParameter("hasta", hastaExclusivo)
                .getResultList()
                .stream()
                .map(row -> new EstadoEventoResumenView((EstadoEvento) row[0], ((Number) row[1]).longValue()))
                .toList();
    }

    @Override
    public List<EventosMensualesView> contarEventosMensuales(LocalDateTime desde, LocalDateTime hastaExclusivo) {
        return entityManager.createNativeQuery("""
                        select
                            extract(year from e.fecha_hora_inicio)::int as anio,
                            extract(month from e.fecha_hora_inicio)::int as mes,
                            sum(case when e.estado = 'CONFIRMADO' then 1 else 0 end) as confirmados,
                            sum(case when e.estado = 'CANCELADO' then 1 else 0 end) as cancelados,
                            count(e.id_evento) as total
                        from evento e
                        where e.fecha_hora_inicio >= :desde
                          and e.fecha_hora_inicio < :hasta
                        group by anio, mes
                        order by anio asc, mes asc
                        """)
                .setParameter("desde", desde)
                .setParameter("hasta", hastaExclusivo)
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] values = (Object[]) row;
                    return new EventosMensualesView(
                            ((Number) values[0]).intValue(),
                            ((Number) values[1]).intValue(),
                            ((Number) values[2]).longValue(),
                            ((Number) values[3]).longValue(),
                            ((Number) values[4]).longValue()
                    );
                })
                .toList();
    }

    @Override
    public List<ReporteFinancieroEventoView> consultarFinancieroEventos(LocalDateTime desde, LocalDateTime hastaExclusivo) {
        return entityManager.createNativeQuery("""
                        select
                            e.id_evento,
                            cl.nombre_completo,
                            e.fecha_hora_inicio,
                            e.estado,
                            cot.id_cotizacion,
                            coalesce(cot.valor_total, 0) as valor_total,
                            coalesce((
                                select sum(a.valor)
                                from anticipo a
                                join cotizacion c_pago on c_pago.id_cotizacion = a.id_cotizacion
                                join reserva_salon r_pago on r_pago.id_reserva = c_pago.id_reserva
                                where r_pago.id_evento = e.id_evento
                            ), 0) as total_pagado
                        from evento e
                        join cliente cl on cl.id_cliente = e.id_cliente
                        left join lateral (
                            select c.id_cotizacion, c.valor_total
                            from cotizacion c
                            join reserva_salon r on r.id_reserva = c.id_reserva
                            where r.id_evento = e.id_evento
                              and r.vigente = true
                              and r.activa = true
                              and c.vigente = true
                              and c.estado = 'ACEPTADA'
                            order by c.created_at desc
                            limit 1
                        ) cot on true
                        where e.fecha_hora_inicio >= :desde
                          and e.fecha_hora_inicio < :hasta
                        order by e.fecha_hora_inicio asc
                        """)
                .setParameter("desde", desde)
                .setParameter("hasta", hastaExclusivo)
                .getResultList()
                .stream()
                .map(row -> toFinancieroEvento((Object[]) row))
                .toList();
    }

    @Override
    public List<AnticipoPeriodoView> listarAnticipos(LocalDate desde, LocalDate hasta) {
        return entityManager.createNativeQuery("""
                        select
                            a.id_anticipo,
                            e.id_evento,
                            cl.nombre_completo,
                            c.id_cotizacion,
                            a.valor,
                            a.metodo_pago,
                            a.fecha_pago
                        from anticipo a
                        join cotizacion c on c.id_cotizacion = a.id_cotizacion
                        join reserva_salon r on r.id_reserva = c.id_reserva
                        join evento e on e.id_evento = r.id_evento
                        join cliente cl on cl.id_cliente = e.id_cliente
                        where a.fecha_pago >= :desde
                          and a.fecha_pago <= :hasta
                        order by a.fecha_pago asc, cl.nombre_completo asc
                        """)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList()
                .stream()
                .map(row -> toAnticipoPeriodo((Object[]) row))
                .toList();
    }

    @Override
    public List<AnticiposPorMetodoView> sumarAnticiposPorMetodo(LocalDate desde, LocalDate hasta) {
        return entityManager.createNativeQuery("""
                        select a.metodo_pago, count(a.id_anticipo), coalesce(sum(a.valor), 0)
                        from anticipo a
                        where a.fecha_pago >= :desde
                          and a.fecha_pago <= :hasta
                        group by a.metodo_pago
                        order by sum(a.valor) desc
                        """)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] values = (Object[]) row;
                    return new AnticiposPorMetodoView(
                            (String) values[0],
                            ((Number) values[1]).longValue(),
                            (BigDecimal) values[2]
                    );
                })
                .toList();
    }

    @Override
    public List<DemandaSalonView> consultarDemandaSalones(LocalDateTime desde, LocalDateTime hastaExclusivo) {
        return entityManager.createNativeQuery("""
                        select
                            s.id_salon,
                            s.nombre,
                            count(r.id_reserva) as total_reservas,
                            count(distinct r.id_evento) as total_eventos,
                            coalesce(sum(r.num_invitados), 0) as total_invitados
                        from reserva_salon r
                        join salon s on s.id_salon = r.id_salon
                        where r.vigente = true
                          and r.activa = true
                          and r.fecha_hora_inicio >= :desde
                          and r.fecha_hora_inicio < :hasta
                        group by s.id_salon, s.nombre
                        order by total_reservas desc, s.nombre asc
                        """)
                .setParameter("desde", desde)
                .setParameter("hasta", hastaExclusivo)
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] values = (Object[]) row;
                    return new DemandaSalonView(
                            (UUID) values[0],
                            (String) values[1],
                            ((Number) values[2]).longValue(),
                            ((Number) values[3]).longValue(),
                            ((Number) values[4]).longValue()
                    );
                })
                .toList();
    }

    private static ReporteFinancieroEventoView toFinancieroEvento(Object[] row) {
        BigDecimal valorTotal = (BigDecimal) row[5];
        BigDecimal totalPagado = (BigDecimal) row[6];
        BigDecimal saldoPendiente = valorTotal.subtract(totalPagado);
        return new ReporteFinancieroEventoView(
                (UUID) row[0],
                (String) row[1],
                toLocalDateTime(row[2]),
                EstadoEvento.valueOf((String) row[3]),
                (UUID) row[4],
                valorTotal,
                totalPagado,
                saldoPendiente,
                saldoPendiente.compareTo(BigDecimal.ZERO) == 0 && valorTotal.compareTo(BigDecimal.ZERO) > 0
        );
    }

    private static AnticipoPeriodoView toAnticipoPeriodo(Object[] row) {
        return new AnticipoPeriodoView(
                (UUID) row[0],
                (UUID) row[1],
                (String) row[2],
                (UUID) row[3],
                (BigDecimal) row[4],
                (String) row[5],
                toLocalDate(row[6])
        );
    }

    private static LocalDateTime toLocalDateTime(Object value) {
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        return ((Timestamp) value).toLocalDateTime();
    }

    private static LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        return ((Date) value).toLocalDate();
    }
}
