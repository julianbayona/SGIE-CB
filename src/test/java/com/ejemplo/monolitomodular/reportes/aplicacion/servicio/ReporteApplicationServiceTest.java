package com.ejemplo.monolitomodular.reportes.aplicacion.servicio;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.AnticipoPeriodoView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.AnticiposPorMetodoView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.DemandaSalonView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.EstadoEventoResumenView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.EventosMensualesView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.ReporteAnticiposView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.ResumenEventosView;
import com.ejemplo.monolitomodular.reportes.dominio.puerto.salida.ReporteRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReporteApplicationServiceTest {

    @Test
    void deberiaCalcularResumenEventosPorEstado() {
        ReporteApplicationService service = new ReporteApplicationService(new ReporteRepositoryStub());

        ResumenEventosView resumen = service.consultarResumenEventos(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31)
        );

        assertEquals(10, resumen.totalEventos());
        assertEquals(6, resumen.confirmados());
        assertEquals(1, resumen.cancelados());
        assertEquals(new BigDecimal("60.00"), resumen.porcentajeConfirmados());
        assertEquals(new BigDecimal("10.00"), resumen.porcentajeCancelados());
    }

    @Test
    void deberiaConsolidarAnticiposDelPeriodo() {
        ReporteApplicationService service = new ReporteApplicationService(new ReporteRepositoryStub());

        ReporteAnticiposView reporte = service.consultarAnticipos(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31)
        );

        assertEquals(2, reporte.cantidad());
        assertEquals(new BigDecimal("700000.00"), reporte.totalRecaudado());
        assertEquals(2, reporte.porMetodo().size());
    }

    @Test
    void noDeberiaAceptarRangoInvertido() {
        ReporteApplicationService service = new ReporteApplicationService(new ReporteRepositoryStub());

        assertThrows(DomainException.class, () -> service.consultarResumenEventos(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 5, 31)
        ));
    }

    private static class ReporteRepositoryStub implements ReporteRepository {

        @Override
        public List<EstadoEventoResumenView> contarEventosPorEstado(LocalDateTime desde, LocalDateTime hastaExclusivo) {
            return List.of(
                    new EstadoEventoResumenView(EstadoEvento.CONFIRMADO, 6),
                    new EstadoEventoResumenView(EstadoEvento.CANCELADO, 1),
                    new EstadoEventoResumenView(EstadoEvento.PENDIENTE_ANTICIPO, 3)
            );
        }

        @Override
        public List<EventosMensualesView> contarEventosMensuales(LocalDateTime desde, LocalDateTime hastaExclusivo) {
            return List.of(new EventosMensualesView(2026, 5, 6, 1, 10));
        }

        @Override
        public List<com.ejemplo.monolitomodular.reportes.aplicacion.dto.ReporteFinancieroEventoView> consultarFinancieroEventos(
                LocalDateTime desde,
                LocalDateTime hastaExclusivo
        ) {
            return List.of();
        }

        @Override
        public List<AnticipoPeriodoView> listarAnticipos(LocalDate desde, LocalDate hasta) {
            return List.of(
                    new AnticipoPeriodoView(
                            UUID.randomUUID(),
                            UUID.randomUUID(),
                            "Cliente Uno",
                            UUID.randomUUID(),
                            new BigDecimal("500000.00"),
                            "TRANSFERENCIA",
                            LocalDate.of(2026, 5, 10)
                    ),
                    new AnticipoPeriodoView(
                            UUID.randomUUID(),
                            UUID.randomUUID(),
                            "Cliente Dos",
                            UUID.randomUUID(),
                            new BigDecimal("200000.00"),
                            "EFECTIVO",
                            LocalDate.of(2026, 5, 11)
                    )
            );
        }

        @Override
        public List<AnticiposPorMetodoView> sumarAnticiposPorMetodo(LocalDate desde, LocalDate hasta) {
            return List.of(
                    new AnticiposPorMetodoView("TRANSFERENCIA", 1, new BigDecimal("500000.00")),
                    new AnticiposPorMetodoView("EFECTIVO", 1, new BigDecimal("200000.00"))
            );
        }

        @Override
        public List<DemandaSalonView> consultarDemandaSalones(LocalDateTime desde, LocalDateTime hastaExclusivo) {
            return List.of();
        }
    }
}
