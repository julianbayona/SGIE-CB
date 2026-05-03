package com.ejemplo.monolitomodular.cotizaciones.presentacion.rest;

import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.CotizacionItemView;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.CotizacionView;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.ActualizarItemCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.ConsultarCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.EnviarCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.GenerarCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.GenerarDocumentoCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para CotizacionController.
 * Cubre el flujo REST → Controller → Service (mockeado) para subir coverage
 * del paquete com.ejemplo.monolitomodular.cotizaciones.presentacion.rest (13% → ~80%).
 */
@WebMvcTest(CotizacionController.class)
class CotizacionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GenerarCotizacionUseCase generarCotizacionUseCase;

    @MockBean
    ConsultarCotizacionUseCase consultarCotizacionUseCase;

    @MockBean
    ActualizarItemCotizacionUseCase actualizarItemCotizacionUseCase;

    @MockBean
    GenerarDocumentoCotizacionUseCase generarDocumentoCotizacionUseCase;

    @MockBean
    EnviarCotizacionUseCase enviarCotizacionUseCase;

    private static final UUID COTIZACION_ID   = UUID.randomUUID();
    private static final UUID RESERVA_RAIZ_ID = UUID.randomUUID();
    private static final UUID USUARIO_ID      = UUID.randomUUID();
    private static final UUID ITEM_ID         = UUID.randomUUID();
    private static final UUID ORIGEN_ID       = UUID.randomUUID();

    private CotizacionView cotizacionView(EstadoCotizacion estado) {
        CotizacionItemView item = new CotizacionItemView(
                ITEM_ID, "MENU", ORIGEN_ID, "Plato especial",
                new BigDecimal("50000"), null, 2, new BigDecimal("100000")
        );
        return new CotizacionView(
                COTIZACION_ID, UUID.randomUUID(), USUARIO_ID,
                estado,
                new BigDecimal("100000"),
                new BigDecimal("5000"),
                new BigDecimal("95000"),
                "Observaciones de prueba",
                List.of(item)
        );
    }

    // ─────────────────────────────────────────────────────────────────────
    // POST /reservas/{reservaRaizId}/cotizaciones — generar cotización
    // ─────────────────────────────────────────────────────────────────────

    @Test
    void deberiaGenerarCotizacionYRetornar200() throws Exception {
        when(generarCotizacionUseCase.ejecutar(any())).thenReturn(cotizacionView(EstadoCotizacion.BORRADOR));

        mockMvc.perform(post("/api/reservas/" + RESERVA_RAIZ_ID + "/cotizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                    "usuarioId": "%s",
                                    "descuento": 5000.00,
                                    "observaciones": "Observaciones de prueba"
                                }
                                """, USUARIO_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COTIZACION_ID.toString()))
                .andExpect(jsonPath("$.estado").value("BORRADOR"))
                .andExpect(jsonPath("$.valorTotal").value(95000))
                .andExpect(jsonPath("$.items[0].tipoConcepto").value("MENU"));
    }

    @Test
    void deberiaRetornar400SiUsuarioIdEsNuloAlGenerarCotizacion() throws Exception {
        mockMvc.perform(post("/api/reservas/" + RESERVA_RAIZ_ID + "/cotizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "descuento": 5000.00
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400SiDescuentoEsNegativoAlGenerarCotizacion() throws Exception {
        mockMvc.perform(post("/api/reservas/" + RESERVA_RAIZ_ID + "/cotizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                    "usuarioId": "%s",
                                    "descuento": -1000.00
                                }
                                """, USUARIO_ID)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoServicioLanzaDomainExceptionAlGenerar() throws Exception {
        when(generarCotizacionUseCase.ejecutar(any()))
                .thenThrow(new DomainException("No existe una reserva vigente para el identificador indicado"));

        mockMvc.perform(post("/api/reservas/" + RESERVA_RAIZ_ID + "/cotizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                    "usuarioId": "%s"
                                }
                                """, USUARIO_ID)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("No existe una reserva vigente para el identificador indicado"));
    }

    // ─────────────────────────────────────────────────────────────────────
    // GET /cotizaciones/{id} — obtener cotización
    // ─────────────────────────────────────────────────────────────────────

    @Test
    void deberiaObtenerCotizacionPorId() throws Exception {
        when(consultarCotizacionUseCase.obtenerPorId(COTIZACION_ID))
                .thenReturn(cotizacionView(EstadoCotizacion.BORRADOR));

        mockMvc.perform(get("/api/cotizaciones/" + COTIZACION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COTIZACION_ID.toString()))
                .andExpect(jsonPath("$.estado").value("BORRADOR"))
                .andExpect(jsonPath("$.usuarioId").value(USUARIO_ID.toString()));
    }

    @Test
    void deberiaRetornar400AlObtenerCotizacionInexistente() throws Exception {
        when(consultarCotizacionUseCase.obtenerPorId(COTIZACION_ID))
                .thenThrow(new DomainException("Cotizacion no encontrada"));

        mockMvc.perform(get("/api/cotizaciones/" + COTIZACION_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Cotizacion no encontrada"));
    }

    // ─────────────────────────────────────────────────────────────────────
    // PATCH /cotizaciones/{cotizacionId}/items/{itemId} — actualizar ítem
    // ─────────────────────────────────────────────────────────────────────

    @Test
    void deberiaActualizarItemCotizacionYRetornar200() throws Exception {
        when(actualizarItemCotizacionUseCase.ejecutar(any()))
                .thenReturn(cotizacionView(EstadoCotizacion.BORRADOR));

        mockMvc.perform(patch("/api/cotizaciones/" + COTIZACION_ID + "/items/" + ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "precioOverride": 45000.00
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COTIZACION_ID.toString()));
    }

    @Test
    void deberiaRetornar400SiPrecioOverrideEsNegativo() throws Exception {
        mockMvc.perform(patch("/api/cotizaciones/" + COTIZACION_ID + "/items/" + ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "precioOverride": -500.00
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────────────────────────────
    // PATCH /cotizaciones/{id}/generar — generar documento
    // ─────────────────────────────────────────────────────────────────────

    @Test
    void deberiaGenerarDocumentoCotizacionYRetornar200() throws Exception {
        when(generarDocumentoCotizacionUseCase.generar(COTIZACION_ID))
                .thenReturn(cotizacionView(EstadoCotizacion.GENERADA));

        mockMvc.perform(patch("/api/cotizaciones/" + COTIZACION_ID + "/generar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("GENERADA"));
    }

    @Test
    void deberiaRetornar400AlGenerarDocumentoDeCotizacionInexistente() throws Exception {
        when(generarDocumentoCotizacionUseCase.generar(COTIZACION_ID))
                .thenThrow(new DomainException("Cotizacion no encontrada"));

        mockMvc.perform(patch("/api/cotizaciones/" + COTIZACION_ID + "/generar"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Cotizacion no encontrada"));
    }

    // ─────────────────────────────────────────────────────────────────────
    // PATCH /cotizaciones/{id}/enviar — enviar cotización
    // ─────────────────────────────────────────────────────────────────────

    @Test
    void deberiaEnviarCotizacionYRetornar200() throws Exception {
        when(enviarCotizacionUseCase.enviar(COTIZACION_ID))
                .thenReturn(cotizacionView(EstadoCotizacion.ENVIADA));

        mockMvc.perform(patch("/api/cotizaciones/" + COTIZACION_ID + "/enviar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENVIADA"));
    }

    // ─────────────────────────────────────────────────────────────────────
    // PATCH /cotizaciones/{id}/aceptar — aceptar cotización
    // ─────────────────────────────────────────────────────────────────────

    @Test
    void deberiaAceptarCotizacionYRetornar200() throws Exception {
        when(enviarCotizacionUseCase.aceptar(COTIZACION_ID))
                .thenReturn(cotizacionView(EstadoCotizacion.ACEPTADA));

        mockMvc.perform(patch("/api/cotizaciones/" + COTIZACION_ID + "/aceptar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACEPTADA"));
    }

    // ─────────────────────────────────────────────────────────────────────
    // PATCH /cotizaciones/{id}/rechazar — rechazar cotización
    // ─────────────────────────────────────────────────────────────────────

    @Test
    void deberiaRechazarCotizacionYRetornar200() throws Exception {
        when(enviarCotizacionUseCase.rechazar(COTIZACION_ID))
                .thenReturn(cotizacionView(EstadoCotizacion.RECHAZADA));

        mockMvc.perform(patch("/api/cotizaciones/" + COTIZACION_ID + "/rechazar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECHAZADA"));
    }

    @Test
    void deberiaRetornar400AlRechazarCotizacionInexistente() throws Exception {
        when(enviarCotizacionUseCase.rechazar(COTIZACION_ID))
                .thenThrow(new DomainException("Cotizacion no encontrada"));

        mockMvc.perform(patch("/api/cotizaciones/" + COTIZACION_ID + "/rechazar"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Cotizacion no encontrada"));
    }
}