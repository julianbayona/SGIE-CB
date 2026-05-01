package com.ejemplo.monolitomodular.clientes.presentacion.rest;

import com.ejemplo.monolitomodular.clientes.aplicacion.dto.ClienteView;
import com.ejemplo.monolitomodular.clientes.aplicacion.puerto.entrada.ConsultarClienteUseCase;
import com.ejemplo.monolitomodular.clientes.aplicacion.puerto.entrada.RegistrarClienteUseCase;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;
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

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RegistrarClienteUseCase registrarClienteUseCase;

    @MockBean
    ConsultarClienteUseCase consultarClienteUseCase;

    private static final UUID CLIENTE_ID = UUID.randomUUID();

    private ClienteView clienteView() {
        return new ClienteView(CLIENTE_ID, "1234567890", "Juan Perez", "3001234567",
                "juan@mail.com", TipoCliente.SOCIO, true, null);
    }

    private String requestValido() {
        return """
                {
                    "cedula": "1234567890",
                    "nombreCompleto": "Juan Perez",
                    "telefono": "3001234567",
                    "correo": "juan@mail.com",
                    "tipoCliente": "SOCIO"
                }
                """;
    }

    @Test
    void deberiaRegistrarClienteYRetornar201() throws Exception {
        when(registrarClienteUseCase.ejecutar(any())).thenReturn(clienteView());

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestValido()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cedula").value("1234567890"))
                .andExpect(jsonPath("$.nombreCompleto").value("Juan Perez"))
                .andExpect(jsonPath("$.tipoCliente").value("SOCIO"));
    }

    @Test
    void deberiaRetornar400SiCedulaEsBlanca() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "cedula": "",
                                    "nombreCompleto": "Juan Perez",
                                    "telefono": "3001234567",
                                    "correo": "juan@mail.com",
                                    "tipoCliente": "SOCIO"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400SiCorreoEsInvalido() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "cedula": "123",
                                    "nombreCompleto": "Juan",
                                    "telefono": "300",
                                    "correo": "no-es-un-correo",
                                    "tipoCliente": "SOCIO"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaObtenerClientePorId() throws Exception {
        when(consultarClienteUseCase.obtenerPorId(CLIENTE_ID)).thenReturn(clienteView());

        mockMvc.perform(get("/api/clientes/" + CLIENTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CLIENTE_ID.toString()));
    }

    @Test
    void deberiaListarClientesSinFiltro() throws Exception {
        when(consultarClienteUseCase.listar()).thenReturn(List.of(clienteView()));

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cedula").value("1234567890"));
    }

    @Test
    void deberiaListarClientesConFiltro() throws Exception {
        when(consultarClienteUseCase.buscar("Juan")).thenReturn(List.of(clienteView()));

        mockMvc.perform(get("/api/clientes").param("q", "Juan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCompleto").value("Juan Perez"));
    }

    @Test
    void deberiaRetornar400CuandoServicioLanzaDomainException() throws Exception {
        when(registrarClienteUseCase.ejecutar(any()))
                .thenThrow(new DomainException("Ya existe un cliente con la cedula indicada"));

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestValido()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un cliente con la cedula indicada"));
    }

    @Test
    void deberiaRetornarListaVaciaConFiltroBlanqueado() throws Exception {
        when(consultarClienteUseCase.listar()).thenReturn(List.of());

        mockMvc.perform(get("/api/clientes").param("q", "  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}