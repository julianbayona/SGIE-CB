package com.ejemplo.monolitomodular.menus.presentacion.rest;

import com.ejemplo.monolitomodular.menus.aplicacion.dto.ItemMenuView;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.MenuView;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.SeleccionMenuView;
import com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada.ConfigurarMenuUseCase;
import com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada.ConsultarMenuUseCase;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
class MenuControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ConfigurarMenuUseCase configurarMenuUseCase;

    @MockBean
    ConsultarMenuUseCase consultarMenuUseCase;

    private static final UUID RESERVA_ID = UUID.randomUUID();
    private static final UUID MENU_ID = UUID.randomUUID();
    private static final UUID SELECCION_ID = UUID.randomUUID();
    private static final UUID TIPO_MOMENTO_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final UUID PLATO_ID = UUID.randomUUID();
    private static final UUID USUARIO_ID = UUID.randomUUID();

    private MenuView menuView() {
        ItemMenuView item = new ItemMenuView(ITEM_ID, PLATO_ID, 3, "Sin sal");
        SeleccionMenuView seleccion = new SeleccionMenuView(SELECCION_ID, TIPO_MOMENTO_ID, List.of(item));
        return new MenuView(MENU_ID, RESERVA_ID, "Sin lactosa", List.of(seleccion));
    }

    private String bodyConfigurar() {
        return String.format("""
                {
                  "usuarioId": "%s",
                  "notasGenerales": "Sin lactosa",
                  "selecciones": [
                    {
                      "tipoMomentoId": "%s",
                      "items": [
                        { "platoId": "%s", "cantidad": 3, "excepciones": "Sin sal" }
                      ]
                    }
                  ]
                }
                """, USUARIO_ID, TIPO_MOMENTO_ID, PLATO_ID);
    }

    @Test
    void deberiaConfigurarMenuYRetornar200() throws Exception {
        when(configurarMenuUseCase.ejecutar(any())).thenReturn(menuView());

        mockMvc.perform(put("/api/reservas/" + RESERVA_ID + "/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyConfigurar()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(MENU_ID.toString()))
                .andExpect(jsonPath("$.reservaId").value(RESERVA_ID.toString()))
                .andExpect(jsonPath("$.notasGenerales").value("Sin lactosa"))
                .andExpect(jsonPath("$.selecciones[0].tipoMomentoId").value(TIPO_MOMENTO_ID.toString()))
                .andExpect(jsonPath("$.selecciones[0].items[0].platoId").value(PLATO_ID.toString()))
                .andExpect(jsonPath("$.selecciones[0].items[0].cantidad").value(3))
                .andExpect(jsonPath("$.selecciones[0].items[0].excepciones").value("Sin sal"));
    }

    @Test
    void deberiaObtenerMenuYRetornar200() throws Exception {
        when(consultarMenuUseCase.obtenerPorReservaRaizId(RESERVA_ID)).thenReturn(menuView());

        mockMvc.perform(get("/api/reservas/" + RESERVA_ID + "/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(MENU_ID.toString()))
                .andExpect(jsonPath("$.reservaId").value(RESERVA_ID.toString()))
                .andExpect(jsonPath("$.selecciones").isArray());
    }

    @Test
    void deberiaRetornar400CuandoFaltaUsuarioId() throws Exception {
        String body = String.format("""
                {
                  "notasGenerales": "Sin lactosa",
                  "selecciones": [
                    {
                      "tipoMomentoId": "%s",
                      "items": [
                        { "platoId": "%s", "cantidad": 3 }
                      ]
                    }
                  ]
                }
                """, TIPO_MOMENTO_ID, PLATO_ID);

        mockMvc.perform(put("/api/reservas/" + RESERVA_ID + "/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoSeleccionesEstaVacia() throws Exception {
        String body = String.format("""
                {
                  "usuarioId": "%s",
                  "notasGenerales": "Sin lactosa",
                  "selecciones": []
                }
                """, USUARIO_ID);

        mockMvc.perform(put("/api/reservas/" + RESERVA_ID + "/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoFaltaTipoMomentoIdEnSeleccion() throws Exception {
        String body = String.format("""
                {
                  "usuarioId": "%s",
                  "notasGenerales": "Sin lactosa",
                  "selecciones": [
                    {
                      "items": [
                        { "platoId": "%s", "cantidad": 3 }
                      ]
                    }
                  ]
                }
                """, USUARIO_ID, PLATO_ID);

        mockMvc.perform(put("/api/reservas/" + RESERVA_ID + "/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoItemsEstaVacio() throws Exception {
        String body = String.format("""
                {
                  "usuarioId": "%s",
                  "notasGenerales": "Sin lactosa",
                  "selecciones": [
                    {
                      "tipoMomentoId": "%s",
                      "items": []
                    }
                  ]
                }
                """, USUARIO_ID, TIPO_MOMENTO_ID);

        mockMvc.perform(put("/api/reservas/" + RESERVA_ID + "/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoCantidadEsMenorAUno() throws Exception {
        String body = String.format("""
                {
                  "usuarioId": "%s",
                  "selecciones": [
                    {
                      "tipoMomentoId": "%s",
                      "items": [
                        { "platoId": "%s", "cantidad": 0 }
                      ]
                    }
                  ]
                }
                """, USUARIO_ID, TIPO_MOMENTO_ID, PLATO_ID);

        mockMvc.perform(put("/api/reservas/" + RESERVA_ID + "/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoFaltaPlatoId() throws Exception {
        String body = String.format("""
                {
                  "usuarioId": "%s",
                  "selecciones": [
                    {
                      "tipoMomentoId": "%s",
                      "items": [
                        { "cantidad": 2 }
                      ]
                    }
                  ]
                }
                """, USUARIO_ID, TIPO_MOMENTO_ID);

        mockMvc.perform(put("/api/reservas/" + RESERVA_ID + "/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoServicioLanzaDomainException() throws Exception {
        when(configurarMenuUseCase.ejecutar(any()))
                .thenThrow(new DomainException("El plato no esta activo para el momento indicado"));

        mockMvc.perform(put("/api/reservas/" + RESERVA_ID + "/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyConfigurar()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El plato no esta activo para el momento indicado"));
    }

    @Test
    void deberiaRetornar400CuandoConsultaLanzaDomainException() throws Exception {
        when(consultarMenuUseCase.obtenerPorReservaRaizId(any()))
                .thenThrow(new DomainException("No se encontro menu para la reserva"));

        mockMvc.perform(get("/api/reservas/" + RESERVA_ID + "/menu"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("No se encontro menu para la reserva"));
    }

    @Test
    void deberiaConfigurarMenuSinNotasGenerales() throws Exception {
        MenuView viewSinNotas = new MenuView(MENU_ID, RESERVA_ID, null,
                List.of(new SeleccionMenuView(SELECCION_ID, TIPO_MOMENTO_ID,
                        List.of(new ItemMenuView(ITEM_ID, PLATO_ID, 1, null)))));
        when(configurarMenuUseCase.ejecutar(any())).thenReturn(viewSinNotas);

        String body = String.format("""
                {
                  "usuarioId": "%s",
                  "selecciones": [
                    {
                      "tipoMomentoId": "%s",
                      "items": [
                        { "platoId": "%s", "cantidad": 1 }
                      ]
                    }
                  ]
                }
                """, USUARIO_ID, TIPO_MOMENTO_ID, PLATO_ID);

        mockMvc.perform(put("/api/reservas/" + RESERVA_ID + "/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(MENU_ID.toString()));
    }
}