package com.ejemplo.monolitomodular.catalogos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.ColorView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarColorUseCase;
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

@WebMvcTest(ColorController.class)
class ColorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    GestionarColorUseCase gestionarColorUseCase;

    private static final UUID COLOR_ID = UUID.randomUUID();

    private ColorView colorView() {
        return new ColorView(COLOR_ID, "Rojo", "#FF0000", true);
    }

    @Test
    void deberiaCrearColorYRetornar201() throws Exception {
        when(gestionarColorUseCase.crearColor(any())).thenReturn(colorView());

        mockMvc.perform(post("/api/catalogos/colores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Rojo\",\"codigoHex\":\"#FF0000\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Rojo"))
                .andExpect(jsonPath("$.codigoHex").value("#FF0000"))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void deberiaRetornar400CuandoNombreEsBlanco() throws Exception {
        mockMvc.perform(post("/api/catalogos/colores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"codigoHex\":\"#FF0000\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoCodigoHexEsBlanco() throws Exception {
        mockMvc.perform(post("/api/catalogos/colores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Rojo\",\"codigoHex\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaActualizarColorYRetornar200() throws Exception {
        when(gestionarColorUseCase.actualizarColor(eq(COLOR_ID), any())).thenReturn(colorView());

        mockMvc.perform(put("/api/catalogos/colores/" + COLOR_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Rojo\",\"codigoHex\":\"#FF0000\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COLOR_ID.toString()));
    }

    @Test
    void deberiaDesactivarColorYRetornar200() throws Exception {
        ColorView desactivado = new ColorView(COLOR_ID, "Rojo", "#FF0000", false);
        when(gestionarColorUseCase.desactivarColor(COLOR_ID)).thenReturn(desactivado);

        mockMvc.perform(delete("/api/catalogos/colores/" + COLOR_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void deberiaObtenerColorPorId() throws Exception {
        when(gestionarColorUseCase.obtenerColor(COLOR_ID)).thenReturn(colorView());

        mockMvc.perform(get("/api/catalogos/colores/" + COLOR_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Rojo"));
    }

    @Test
    void deberiaListarColores() throws Exception {
        when(gestionarColorUseCase.listarColores()).thenReturn(List.of(colorView()));

        mockMvc.perform(get("/api/catalogos/colores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Rojo"));
    }

    @Test
    void deberiaRetornar400CuandoServicioLanzaDomainException() throws Exception {
        when(gestionarColorUseCase.crearColor(any()))
                .thenThrow(new DomainException("Ya existe un color con el nombre indicado"));

        mockMvc.perform(post("/api/catalogos/colores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Rojo\",\"codigoHex\":\"#FF0000\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un color con el nombre indicado"));
    }
}