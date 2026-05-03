package com.ejemplo.monolitomodular.eventos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Color;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Mantel;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.ColorRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.MantelRepository;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para gestión de eventos y reservas (PI016-PI025).
 * 
 * Tipo de integración: Top-down.
 * Flujo completo: REST → Controller → Service → Repository → BD.
 * Sin mocks para validar lógica de negocio real.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EventoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private MantelRepository mantelRepository;

    private UUID clienteId;
    private UUID usuarioId;
    private UUID salonId;
    private UUID tipoEventoId;
    private UUID tipoComidaId;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        setupTestData();
    }

    private void setupTestData() {
        // Crear usuario directamente en repositorio (no hay endpoint REST para usuarios)
        Usuario usuario = Usuario.nuevo("Usuario Test", "password123", RolUsuario.ADMINISTRADOR);
        usuarioRepository.guardar(usuario);
        usuarioId = usuario.getId();

        // Crear cliente vía endpoint REST
        try {
            String clienteRequest = String.format("""
                    {
                        "cedula": "123456",
                        "nombreCompleto": "Cliente Test",
                        "telefono": "3001234567",
                        "correo": "cliente@test.com",
                        "tipoCliente": "SOCIO"
                    }
                    """
            );

            String clienteResponse = mockMvc.perform(post("/api/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(clienteRequest))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            clienteId = UUID.fromString(objectMapper.readTree(clienteResponse).get("id").asText());

            // Crear salón vía endpoint REST
            String salonRequest = """
                    {
                        "nombre": "Salón Test",
                        "capacidad": 100,
                        "descripcion": "Salón de prueba"
                    }
                    """;

            String salonResponse = mockMvc.perform(post("/api/salones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(salonRequest))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            salonId = UUID.fromString(objectMapper.readTree(salonResponse).get("id").asText());

            // Crear tipo de evento vía endpoint REST
            String tipoEventoRequest = """
                    {
                        "nombre": "Boda",
                        "descripcion": "Tipo de evento para bodas"
                    }
                    """;

            String tipoEventoResponse = mockMvc.perform(post("/api/catalogos/tipos-evento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(tipoEventoRequest))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            tipoEventoId = UUID.fromString(objectMapper.readTree(tipoEventoResponse).get("id").asText());

            // Crear tipo de comida vía endpoint REST
            String tipoComidaRequest = """
                    {
                        "nombre": "Buffet",
                        "descripcion": "Servicio de buffet"
                    }
                    """;

            String tipoComidaResponse = mockMvc.perform(post("/api/catalogos/tipos-comida")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(tipoComidaRequest))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            tipoComidaId = UUID.fromString(objectMapper.readTree(tipoComidaResponse).get("id").asText());

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void confirmarEvento(UUID eventoId) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado para confirmar: " + eventoId));
        Evento confirmado = Evento.reconstruir(
                evento.getId(),
                evento.getClienteId(),
                evento.getTipoEventoId(),
                evento.getTipoComidaId(),
                evento.getUsuarioCreadorId(),
                evento.getFechaHoraInicio(),
                evento.getFechaHoraFin(),
                EstadoEvento.CONFIRMADO,
                evento.getGcalEventId()
        );
        eventoRepository.guardar(confirmado);
    }

    /**
     * PI016 — Crear evento sin reservas iniciales.
     */
    @Test
    void deberiaCrearEventoSinReservasInicialesExitosamente() throws Exception {
        LocalDateTime inicio = LocalDateTime.of(2026, 6, 15, 18, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 15, 22, 0);

        String requestBody = String.format("""
                {
                    "clienteId": "%s",
                    "tipoEventoId": "%s",
                    "tipoComidaId": "%s",
                    "usuarioCreadorId": "%s",
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, clienteId, tipoEventoId, tipoComidaId, usuarioId, inicio, fin);

        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.clienteId").value(clienteId.toString()))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(header().exists("Location"));
    }

    /**
     * PI017 — Crear evento con cliente inexistente.
     */
    @Test
    void deberiaRetornarErrorAlCrearEventoConClienteInexistente() throws Exception {
        UUID inexistentClienteId = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.of(2026, 6, 15, 18, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 15, 22, 0);

        String requestBody = String.format("""
                {
                    "clienteId": "%s",
                    "tipoEventoId": "%s",
                    "tipoComidaId": "%s",
                    "usuarioCreadorId": "%s",
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, inexistentClienteId, tipoEventoId, tipoComidaId, usuarioId, inicio, fin);

        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje", org.hamcrest.Matchers.containsString("Cliente")));
    }

    /**
     * PI018 — Crear evento con catálogo inactivo.
     */
    @Test
    void deberiaRetornarErrorAlCrearEventoConCatalogoInactivo() throws Exception {
        // Crear tipo de evento inactivo (no disponible) vía REST y desactivarlo
        String inactiveTipoResponse = mockMvc.perform(post("/api/catalogos/tipos-evento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Tipo Inactivo",
                                    "descripcion": "Será desactivado"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID inactiveTipoEventoId = UUID.fromString(objectMapper.readTree(inactiveTipoResponse).get("id").asText());

        mockMvc.perform(delete("/api/catalogos/tipos-evento/" + inactiveTipoEventoId))
                .andExpect(status().isOk());

        LocalDateTime inicio = LocalDateTime.of(2026, 6, 15, 18, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 15, 22, 0);

        String requestWithInactiveTipo = String.format("""
                {
                    "clienteId": "%s",
                    "tipoEventoId": "%s",
                    "tipoComidaId": "%s",
                    "usuarioCreadorId": "%s",
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, clienteId, inactiveTipoEventoId, tipoComidaId, usuarioId, inicio, fin);

        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithInactiveTipo))
                .andExpect(status().isBadRequest());
    }

    /**
     * PI019 — Crear evento con fechas inválidas.
     */
    @Test
    void deberiaRetornarErrorAlCrearEventoConFechasInvalidas_FinAnteriorAInicio() throws Exception {
        LocalDateTime inicio = LocalDateTime.of(2026, 6, 15, 18, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 15, 17, 0); // fin anterior a inicio

        String requestBody = String.format("""
                {
                    "clienteId": "%s",
                    "tipoEventoId": "%s",
                    "tipoComidaId": "%s",
                    "usuarioCreadorId": "%s",
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, clienteId, tipoEventoId, tipoComidaId, usuarioId, inicio, fin);

        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * PI020 — Crear reserva para salón en evento.
     */
    @Test
    void deberiaCrearReservaSalonParaEventoExitosamente() throws Exception {
        LocalDateTime inicio = LocalDateTime.of(2026, 6, 15, 18, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 15, 22, 0);

        String eventoRequest = String.format("""
                {
                    "clienteId": "%s",
                    "tipoEventoId": "%s",
                    "tipoComidaId": "%s",
                    "usuarioCreadorId": "%s",
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, clienteId, tipoEventoId, tipoComidaId, usuarioId, inicio, fin);

        String eventoResponse = mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventoRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID eventoId = UUID.fromString(objectMapper.readTree(eventoResponse).get("id").asText());

        String reservaRequest = String.format("""
                {
                    "usuarioId": "%s",
                    "salonId": "%s",
                    "numInvitados": 50,
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, usuarioId, salonId, inicio, fin);

        mockMvc.perform(post("/api/eventos/" + eventoId + "/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservaRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservas[0].salonId").value(salonId.toString()))
                .andExpect(jsonPath("$.reservas[0].vigente").value(true));
    }

    /**
     * PI021 — Crear reserva con salon no existente.
     */
    @Test
    void deberiaRetornarErrorAlCrearReservaSalonConSalonInexistente() throws Exception {
        LocalDateTime inicio = LocalDateTime.of(2026, 6, 15, 18, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 15, 22, 0);

        String eventoRequest = String.format("""
                {
                    "clienteId": "%s",
                    "tipoEventoId": "%s",
                    "tipoComidaId": "%s",
                    "usuarioCreadorId": "%s",
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, clienteId, tipoEventoId, tipoComidaId, usuarioId, inicio, fin);

        String eventoResponse = mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventoRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID eventoId = UUID.fromString(objectMapper.readTree(eventoResponse).get("id").asText());

        UUID inexistentSalonId = UUID.randomUUID();
        String reservaRequest = String.format("""
                {
                    "usuarioId": "%s",
                    "salonId": "%s",
                    "numInvitados": 50,
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, usuarioId, inexistentSalonId, inicio, fin);

        mockMvc.perform(post("/api/eventos/" + eventoId + "/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservaRequest))
                .andExpect(status().isBadRequest());
    }

    /**
     * PI022 — Crear reserva con conflicto de ocupacion.
     */
    @Test
    void deberiaRetornarErrorAlCrearReservaConConflictoDeOcupacion() throws Exception {
        // Crear primer evento con reserva
        LocalDateTime inicio = LocalDateTime.of(2026, 6, 15, 18, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 15, 22, 0);

        String evento1Request = String.format("""
                {
                    "clienteId": "%s",
                    "tipoEventoId": "%s",
                    "tipoComidaId": "%s",
                    "usuarioCreadorId": "%s",
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, clienteId, tipoEventoId, tipoComidaId, usuarioId, inicio, fin);

        String evento1Response = mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(evento1Request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID evento1Id = UUID.fromString(objectMapper.readTree(evento1Response).get("id").asText());

        // Crear primera reserva
        String reserva1Request = String.format("""
                {
                    "usuarioId": "%s",
                    "salonId": "%s",
                    "numInvitados": 50,
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, usuarioId, salonId, inicio, fin);

        mockMvc.perform(post("/api/eventos/" + evento1Id + "/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reserva1Request))
                .andExpect(status().isOk());

        // Confirmar el primer evento para que su reserva sea considerada en conflictos
        confirmarEvento(evento1Id);

        // Crear segundo evento que se solapa
        String evento2Request = String.format("""
                {
                    "clienteId": "%s",
                    "tipoEventoId": "%s",
                    "tipoComidaId": "%s",
                    "usuarioCreadorId": "%s",
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, clienteId, tipoEventoId, tipoComidaId, usuarioId, inicio, fin);

        String evento2Response = mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(evento2Request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID evento2Id = UUID.fromString(objectMapper.readTree(evento2Response).get("id").asText());

        // Intentar crear reserva para el mismo salón en el mismo rango (debe fallar)
        String reserva2Request = String.format("""
                {
                    "usuarioId": "%s",
                    "salonId": "%s",
                    "numInvitados": 60,
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, usuarioId, salonId, inicio, fin);

        mockMvc.perform(post("/api/eventos/" + evento2Id + "/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reserva2Request))
                .andExpect(status().isBadRequest());
    }

    /**
     * PI023 — Modificar reserva vigente creando nueva versión.
     */
    @Test
    void deberiaModificarReservaVigenteCreandoNuevaVersion() throws Exception {
        // Crear evento con reserva
        LocalDateTime inicio = LocalDateTime.of(2026, 6, 15, 18, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 15, 22, 0);

        String eventoRequest = String.format("""
                {
                    "clienteId": "%s",
                    "tipoEventoId": "%s",
                    "tipoComidaId": "%s",
                    "usuarioCreadorId": "%s",
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, clienteId, tipoEventoId, tipoComidaId, usuarioId, inicio, fin);

        String eventoResponse = mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventoRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID eventoId = UUID.fromString(objectMapper.readTree(eventoResponse).get("id").asText());

        // Crear reserva
        String reservaRequest = String.format("""
                {
                    "usuarioId": "%s",
                    "salonId": "%s",
                    "numInvitados": 50,
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, usuarioId, salonId, inicio, fin);

        String reservaResponse = mockMvc.perform(post("/api/eventos/" + eventoId + "/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservaRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID reservaId = UUID.fromString(objectMapper.readTree(reservaResponse).path("reservas").get(0).get("id").asText());

        // Modificar la reserva con nuevo rango horario
        LocalDateTime nuevoInicio = LocalDateTime.of(2026, 6, 15, 14, 0);
        LocalDateTime nuevoFin = LocalDateTime.of(2026, 6, 15, 18, 0);

        String modificarRequest = String.format("""
                {
                    "usuarioId": "%s",
                    "salonId": "%s",
                    "numInvitados": 70,
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, usuarioId, salonId, nuevoInicio, nuevoFin);

        mockMvc.perform(patch("/api/eventos/reservas/" + reservaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modificarRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservas[0].numInvitados").value(70))
                .andExpect(jsonPath("$.reservas[0].vigente").value(true));
    }

    /**
     * PI024 — Modificar reserva con rai inexistente.
     */
    @Test
    void deberiaRetornarErrorAlModificarReservaConRaizInexistente() throws Exception {
        UUID inexistentReservaId = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.of(2026, 6, 15, 18, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 15, 22, 0);

        String modificarRequest = String.format("""
                {
                    "usuarioId": "%s",
                    "salonId": "%s",
                    "numInvitados": 50,
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, usuarioId, salonId, inicio, fin);

        mockMvc.perform(patch("/api/eventos/reservas/" + inexistentReservaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modificarRequest))
                .andExpect(status().isBadRequest());
    }

    /**
     * PI025 — Modificar reserva con conflicto contra otra reserva.
     */
    @Test
    void deberiaRetornarErrorAlModificarReservaConConflictoContraOtraReserva() throws Exception {
        // Crear evento 1 con reserva
        LocalDateTime inicio1 = LocalDateTime.of(2026, 6, 15, 18, 0);
        LocalDateTime fin1 = LocalDateTime.of(2026, 6, 15, 22, 0);

        String evento1Request = String.format("""
                {
                    "clienteId": "%s",
                    "tipoEventoId": "%s",
                    "tipoComidaId": "%s",
                    "usuarioCreadorId": "%s",
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, clienteId, tipoEventoId, tipoComidaId, usuarioId, inicio1, fin1);

        String evento1Response = mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(evento1Request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID evento1Id = UUID.fromString(objectMapper.readTree(evento1Response).get("id").asText());

        // Crear segundo salón
        String salon2Request = """
                {
                    "nombre": "Salón B",
                    "capacidad": 150,
                    "descripcion": "Salón adicional"
                }
                """;

        String salon2Response = mockMvc.perform(post("/api/salones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(salon2Request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID salon2Id = UUID.fromString(objectMapper.readTree(salon2Response).get("id").asText());

        // Crear reserva 1
        String reserva1Request = String.format("""
                {
                    "usuarioId": "%s",
                    "salonId": "%s",
                    "numInvitados": 50,
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, usuarioId, salonId, inicio1, fin1);

        String reserva1Response = mockMvc.perform(post("/api/eventos/" + evento1Id + "/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reserva1Request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID reserva1Id = UUID.fromString(objectMapper.readTree(reserva1Response).path("reservas").get(0).get("id").asText());

        // Crear evento 2 con reserva
        LocalDateTime inicio2 = LocalDateTime.of(2026, 6, 15, 18, 30);
        LocalDateTime fin2 = LocalDateTime.of(2026, 6, 15, 21, 30);

        String evento2Request = String.format("""
                {
                    "clienteId": "%s",
                    "tipoEventoId": "%s",
                    "tipoComidaId": "%s",
                    "usuarioCreadorId": "%s",
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, clienteId, tipoEventoId, tipoComidaId, usuarioId, inicio2, fin2);

        String evento2Response = mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(evento2Request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID evento2Id = UUID.fromString(objectMapper.readTree(evento2Response).get("id").asText());

        // Crear reserva 2 en salon2
        String reserva2Request = String.format("""
                {
                    "usuarioId": "%s",
                    "salonId": "%s",
                    "numInvitados": 60,
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, usuarioId, salon2Id, inicio2, fin2);

        mockMvc.perform(post("/api/eventos/" + evento2Id + "/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reserva2Request))
                .andExpect(status().isOk());

        // Confirmar el evento 2 para que su reserva sea considerada en conflictos
        confirmarEvento(evento2Id);

        // Intentar modificar reserva 1 hacia el salón 2 que está ocupado
        String modificarRequest = String.format("""
                {
                    "usuarioId": "%s",
                    "salonId": "%s",
                    "numInvitados": 55,
                    "fechaHoraInicio": "%s",
                    "fechaHoraFin": "%s"
                }
                """, usuarioId, salon2Id, inicio2, fin2);

        mockMvc.perform(patch("/api/eventos/reservas/" + reserva1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modificarRequest))
                .andExpect(status().isBadRequest());
    }
}
