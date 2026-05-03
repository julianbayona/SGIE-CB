package com.ejemplo.monolitomodular.montajes.presentacion.rest;

import com.ejemplo.monolitomodular.montajes.aplicacion.dto.AdicionalEventoView;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.InfraestructuraReservaView;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.MontajeMesaReservaView;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.MontajeView;
import com.ejemplo.monolitomodular.montajes.aplicacion.puerto.entrada.ConfigurarMontajeUseCase;
import com.ejemplo.monolitomodular.montajes.aplicacion.puerto.entrada.ConsultarMontajeUseCase;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para MontajeController.
 * Cubre el flujo REST → Controller → Service (mockeado) para subir coverage
 * del paquete com.ejemplo.monolitomodular.montajes.presentacion.rest (5% → ~80%).
 */
@WebMvcTest(MontajeController.class)
class MontajeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ConfigurarMontajeUseCase configurarMontajeUseCase;

    @MockBean
    ConsultarMontajeUseCase consultarMontajeUseCase;

    private static final UUID RESERVA_RAIZ_ID = UUID.randomUUID();
    private static final UUID MONTAJE_ID      = UUID.randomUUID();
    private static final UUID TIPO_MESA_ID    = UUID.randomUUID();
    private static final UUID TIPO_SILLA_ID   = UUID.randomUUID();
    private static final UUID MANTEL_ID       = UUID.randomUUID();
    private static final UUID TIPO_ADICIONAL_ID = UUID.randomUUID();
    private static final UUID USUARIO_ID      = UUID.randomUUID();

    private MontajeView montajeView() {
        InfraestructuraReservaView infra = new InfraestructuraReservaView(
                UUID.randomUUID(), true, false, false, true
        );
        MontajeMesaReservaView mesa = new MontajeMesaReservaView(
                UUID.randomUUID(), TIPO_MESA_ID, TIPO_SILLA_ID,
                6, 10, MANTEL_ID, null, true, false
        );
        AdicionalEventoView adicional = new AdicionalEventoView(
                UUID.randomUUID(), TIPO_ADICIONAL_ID, 2
        );
        return new MontajeView(
                MONTAJE_ID, UUID.randomUUID(), "Observaciones de prueba",
                List.of(mesa), infra, List.of(adicional)
        );
    }

    private String requestValido() {
        return String.format("""
                {
                    "usuarioId": "%s",
                    "observaciones": "Observaciones de prueba",
                    "mesas": [
                        {
                            "tipoMesaId": "%s",
                            "tipoSillaId": "%s",
                            "sillaPorMesa": 6,
                            "cantidadMesas": 10,
                            "mantelId": "%s",
                            "vajilla": true,
                            "fajon": false
                        }
                    ],
                    "infraestructura": {
                        "mesaPonque": true,
                        "mesaRegalos": false,
                        "espacioMusicos": false,
                        "estanteBombas": true
                    },
                    "adicionales": [
                        {
                            "tipoAdicionalId": "%s",
                            "cantidad": 2
                        }
                    ]
                }
                """, USUARIO_ID, TIPO_MESA_ID, TIPO_SILLA_ID, MANTEL_ID, TIPO_ADICIONAL_ID);
    }

    @Test
    void deberiaConfigurarMontajeYRetornar200() throws Exception {
        when(configurarMontajeUseCase.ejecutar(any())).thenReturn(montajeView());

        mockMvc.perform(put("/api/reservas/" + RESERVA_RAIZ_ID + "/montaje")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestValido()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(MONTAJE_ID.toString()))
                .andExpect(jsonPath("$.observaciones").value("Observaciones de prueba"))
                .andExpect(jsonPath("$.mesas[0].tipoMesaId").value(TIPO_MESA_ID.toString()))
                .andExpect(jsonPath("$.infraestructura.mesaPonque").value(true))
                .andExpect(jsonPath("$.adicionales[0].tipoAdicionalId").value(TIPO_ADICIONAL_ID.toString()));
    }

    @Test
    void deberiaRetornar400SiUsuarioIdEsNulo() throws Exception {
        mockMvc.perform(put("/api/reservas/" + RESERVA_RAIZ_ID + "/montaje")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "mesas": [
                                        {
                                            "tipoMesaId": "00000000-0000-0000-0000-000000000001",
                                            "tipoSillaId": "00000000-0000-0000-0000-000000000002",
                                            "sillaPorMesa": 6,
                                            "cantidadMesas": 10,
                                            "mantelId": "00000000-0000-0000-0000-000000000003",
                                            "vajilla": true,
                                            "fajon": false
                                        }
                                    ],
                                    "infraestructura": {
                                        "mesaPonque": true,
                                        "mesaRegalos": false,
                                        "espacioMusicos": false,
                                        "estanteBombas": true
                                    }
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400SiMesasEstaVacio() throws Exception {
        mockMvc.perform(put("/api/reservas/" + RESERVA_RAIZ_ID + "/montaje")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                    "usuarioId": "%s",
                                    "mesas": [],
                                    "infraestructura": {
                                        "mesaPonque": true,
                                        "mesaRegalos": false,
                                        "espacioMusicos": false,
                                        "estanteBombas": true
                                    }
                                }
                                """, USUARIO_ID)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400SiInfraestructuraEsNula() throws Exception {
        mockMvc.perform(put("/api/reservas/" + RESERVA_RAIZ_ID + "/montaje")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                    "usuarioId": "%s",
                                    "mesas": [
                                        {
                                            "tipoMesaId": "%s",
                                            "tipoSillaId": "%s",
                                            "sillaPorMesa": 6,
                                            "cantidadMesas": 10,
                                            "mantelId": "%s",
                                            "vajilla": true,
                                            "fajon": false
                                        }
                                    ]
                                }
                                """, USUARIO_ID, TIPO_MESA_ID, TIPO_SILLA_ID, MANTEL_ID)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoServicioLanzaDomainException() throws Exception {
        when(configurarMontajeUseCase.ejecutar(any()))
                .thenThrow(new DomainException("El tipo de mesa no existe o esta inactivo"));

        mockMvc.perform(put("/api/reservas/" + RESERVA_RAIZ_ID + "/montaje")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestValido()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El tipo de mesa no existe o esta inactivo"));
    }

    @Test
    void deberiaObtenerMontajePorReservaRaizId() throws Exception {
        when(consultarMontajeUseCase.obtenerPorReservaRaizId(RESERVA_RAIZ_ID)).thenReturn(montajeView());

        mockMvc.perform(get("/api/reservas/" + RESERVA_RAIZ_ID + "/montaje"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(MONTAJE_ID.toString()))
                .andExpect(jsonPath("$.mesas").isArray())
                .andExpect(jsonPath("$.infraestructura").exists())
                .andExpect(jsonPath("$.adicionales").isArray());
    }

    @Test
    void deberiaRetornar400AlObtenerMontajeConReservaInexistente() throws Exception {
        when(consultarMontajeUseCase.obtenerPorReservaRaizId(RESERVA_RAIZ_ID))
                .thenThrow(new DomainException("No existe una reserva vigente para el identificador indicado"));

        mockMvc.perform(get("/api/reservas/" + RESERVA_RAIZ_ID + "/montaje"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("No existe una reserva vigente para el identificador indicado"));
    }

    @Test
    void deberiaConfigurarMontajeSinAdicionales() throws Exception {
        MontajeView viewSinAdicionales = new MontajeView(
                MONTAJE_ID, UUID.randomUUID(), null,
                List.of(new MontajeMesaReservaView(
                        UUID.randomUUID(), TIPO_MESA_ID, TIPO_SILLA_ID,
                        4, 5, MANTEL_ID, null, false, false
                )),
                new InfraestructuraReservaView(UUID.randomUUID(), false, false, false, false),
                List.of()
        );
        when(configurarMontajeUseCase.ejecutar(any())).thenReturn(viewSinAdicionales);

        mockMvc.perform(put("/api/reservas/" + RESERVA_RAIZ_ID + "/montaje")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                    "usuarioId": "%s",
                                    "mesas": [
                                        {
                                            "tipoMesaId": "%s",
                                            "tipoSillaId": "%s",
                                            "sillaPorMesa": 4,
                                            "cantidadMesas": 5,
                                            "mantelId": "%s",
                                            "vajilla": false,
                                            "fajon": false
                                        }
                                    ],
                                    "infraestructura": {
                                        "mesaPonque": false,
                                        "mesaRegalos": false,
                                        "espacioMusicos": false,
                                        "estanteBombas": false
                                    }
                                }
                                """, USUARIO_ID, TIPO_MESA_ID, TIPO_SILLA_ID, MANTEL_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adicionales").isArray())
                .andExpect(jsonPath("$.adicionales").isEmpty());
    }
}