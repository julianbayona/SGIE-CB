package com.ejemplo.monolitomodular.eventos.presentacion.rest;

import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.ReservaSalonView;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.ConsultarEventoUseCase;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.CrearEventoUseCase;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.CrearReservaSalonUseCase;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.ModificarReservaSalonUseCase;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventoController.class)
class EventoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CrearEventoUseCase crearEventoUseCase;

    @MockBean
    ConsultarEventoUseCase consultarEventoUseCase;

    @MockBean
    CrearReservaSalonUseCase crearReservaSalonUseCase;

    @MockBean
    ModificarReservaSalonUseCase modificarReservaSalonUseCase;

    private static final UUID EVENTO_ID     = UUID.randomUUID();
    private static final UUID CLIENTE_ID    = UUID.randomUUID();
    private static final UUID TIPO_EVENTO   = UUID.randomUUID();
    private static final UUID TIPO_COMIDA   = UUID.randomUUID();
    private static final UUID USUARIO_ID    = UUID.randomUUID();
    private static final UUID SALON_ID      = UUID.randomUUID();
    private static final UUID RESERVA_ID    = UUID.randomUUID();

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 6, 15, 18, 0);
    private static final LocalDateTime FIN    = LocalDateTime.of(2026, 6, 15, 22, 0);

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private EventoView eventoView() {
        ReservaSalonView reserva = new ReservaSalonView(
                RESERVA_ID, RESERVA_ID, SALON_ID, 100,
                INICIO, FIN, 1, true
        );
        return new EventoView(
                EVENTO_ID, CLIENTE_ID, TIPO_EVENTO, TIPO_COMIDA, USUARIO_ID,
                EstadoEvento.PENDIENTE, null, INICIO, FIN,
                List.of(reserva)
        );
    }

    @Test
    void deberiaCrearEventoYRetornar201() throws Exception {
        when(crearEventoUseCase.ejecutar(any())).thenReturn(eventoView());

        String body = String.format(
                "{\"clienteId\":\"%s\",\"tipoEventoId\":\"%s\",\"tipoComidaId\":\"%s\"," +
                "\"usuarioCreadorId\":\"%s\",\"fechaHoraInicio\":\"2026-06-15T18:00:00\"," +
                "\"fechaHoraFin\":\"2026-06-15T22:00:00\"}",
                CLIENTE_ID, TIPO_EVENTO, TIPO_COMIDA, USUARIO_ID
        );

        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(EVENTO_ID.toString()))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.reservas[0].salonId").value(SALON_ID.toString()));
    }

    @Test
    void deberiaRetornar400CuandoFaltaClienteId() throws Exception {
        String body = String.format(
                "{\"tipoEventoId\":\"%s\",\"tipoComidaId\":\"%s\"," +
                "\"usuarioCreadorId\":\"%s\",\"fechaHoraInicio\":\"2026-06-15T18:00:00\"," +
                "\"fechaHoraFin\":\"2026-06-15T22:00:00\"}",
                TIPO_EVENTO, TIPO_COMIDA, USUARIO_ID
        );

        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaObtenerEventoPorId() throws Exception {
        when(consultarEventoUseCase.obtenerPorId(EVENTO_ID)).thenReturn(eventoView());

        mockMvc.perform(get("/api/eventos/" + EVENTO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EVENTO_ID.toString()))
                .andExpect(jsonPath("$.clienteId").value(CLIENTE_ID.toString()));
    }

    @Test
    void deberiaListarEventos() throws Exception {
        when(consultarEventoUseCase.listar()).thenReturn(List.of(eventoView()));

        mockMvc.perform(get("/api/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(EVENTO_ID.toString()))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void deberiaCrearReservaEnEvento() throws Exception {
        when(crearReservaSalonUseCase.ejecutar(eq(EVENTO_ID), any())).thenReturn(eventoView());

        String body = String.format(
                "{\"usuarioId\":\"%s\",\"salonId\":\"%s\",\"numInvitados\":100," +
                "\"fechaHoraInicio\":\"2026-06-15T18:00:00\",\"fechaHoraFin\":\"2026-06-15T22:00:00\"}",
                USUARIO_ID, SALON_ID
        );

        mockMvc.perform(post("/api/eventos/" + EVENTO_ID + "/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EVENTO_ID.toString()));
    }

    @Test
    void deberiaModificarReserva() throws Exception {
        when(modificarReservaSalonUseCase.ejecutar(eq(RESERVA_ID), any())).thenReturn(eventoView());

        String body = String.format(
                "{\"usuarioId\":\"%s\",\"salonId\":\"%s\",\"numInvitados\":120," +
                "\"fechaHoraInicio\":\"2026-06-15T18:00:00\",\"fechaHoraFin\":\"2026-06-15T22:00:00\"}",
                USUARIO_ID, SALON_ID
        );

        mockMvc.perform(patch("/api/eventos/reservas/" + RESERVA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EVENTO_ID.toString()));
    }

    @Test
    void deberiaRetornar400AlCrearReservaConNumInvitadosCero() throws Exception {
        String body = String.format(
                "{\"usuarioId\":\"%s\",\"salonId\":\"%s\",\"numInvitados\":0," +
                "\"fechaHoraInicio\":\"2026-06-15T18:00:00\",\"fechaHoraFin\":\"2026-06-15T22:00:00\"}",
                USUARIO_ID, SALON_ID
        );

        mockMvc.perform(post("/api/eventos/" + EVENTO_ID + "/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

  @Test
    void deberiaRetornarEventoConReservasVigentesAlConsultarPorId() throws Exception {
        when(consultarEventoUseCase.obtenerPorId(EVENTO_ID)).thenReturn(eventoView());
 
        mockMvc.perform(get("/api/eventos/" + EVENTO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EVENTO_ID.toString()))
                .andExpect(jsonPath("$.reservas").isArray())
                .andExpect(jsonPath("$.reservas[0].vigente").value(true))
                .andExpect(jsonPath("$.reservas[0].salonId").value(SALON_ID.toString()));
    }
 
    // PI027 — Listar eventos con reservas
    @Test
    void deberiaRetornarListaDeEventosConSusReservasVigentes() throws Exception {
        when(consultarEventoUseCase.listar()).thenReturn(List.of(eventoView()));
 
        mockMvc.perform(get("/api/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(EVENTO_ID.toString()))
                .andExpect(jsonPath("$[0].reservas").isArray())
                .andExpect(jsonPath("$[0].reservas[0].vigente").value(true));
    }
 
    // PI028 — Manejo transversal de DomainException (complemento)
    @Test
    void deberiaRetornar400ConMensajeControladoCuandoCrearEventoLanzaDomainException() throws Exception {
        when(crearEventoUseCase.ejecutar(any()))
                .thenThrow(new DomainException("Cliente no encontrado"));
 
        String body = String.format(
                "{\"clienteId\":\"%s\",\"tipoEventoId\":\"%s\",\"tipoComidaId\":\"%s\"," +
                "\"usuarioCreadorId\":\"%s\",\"fechaHoraInicio\":\"2026-06-15T18:00:00\"," +
                "\"fechaHoraFin\":\"2026-06-15T22:00:00\"}",
                CLIENTE_ID, TIPO_EVENTO, TIPO_COMIDA, USUARIO_ID
        );
 
        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Cliente no encontrado"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
}