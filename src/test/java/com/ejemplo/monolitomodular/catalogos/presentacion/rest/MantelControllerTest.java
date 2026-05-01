package com.ejemplo.monolitomodular.catalogos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoConColorView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarMantelUseCase;
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

@WebMvcTest(MantelController.class)
class MantelControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    GestionarMantelUseCase gestionarMantelUseCase;

    private static final UUID MANTEL_ID = UUID.randomUUID();
    private static final UUID COLOR_ID  = UUID.randomUUID();

    private CatalogoConColorView mantelView() {
        return new CatalogoConColorView(MANTEL_ID, "Mantel Blanco", COLOR_ID, true);
    }

    private String bodyValido() {
        return String.format("{\"nombre\":\"Mantel Blanco\",\"colorId\":\"%s\"}", COLOR_ID);
    }

    @Test
    void deberiaCrearMantelYRetornar201() throws Exception {
        when(gestionarMantelUseCase.crearMantel(any())).thenReturn(mantelView());

        mockMvc.perform(post("/api/catalogos/manteles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(MANTEL_ID.toString()))
                .andExpect(jsonPath("$.nombre").value("Mantel Blanco"))
                .andExpect(jsonPath("$.colorId").value(COLOR_ID.toString()))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void deberiaRetornar400CuandoNombreEsBlanco() throws Exception {
        mockMvc.perform(post("/api/catalogos/manteles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"nombre\":\"\",\"colorId\":\"%s\"}", COLOR_ID)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoColorIdEsNulo() throws Exception {
        mockMvc.perform(post("/api/catalogos/manteles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Mantel Blanco\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaActualizarMantelYRetornar200() throws Exception {
        when(gestionarMantelUseCase.actualizarMantel(eq(MANTEL_ID), any())).thenReturn(mantelView());

        mockMvc.perform(put("/api/catalogos/manteles/" + MANTEL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(MANTEL_ID.toString()))
                .andExpect(jsonPath("$.nombre").value("Mantel Blanco"));
    }

    @Test
    void deberiaDesactivarMantelYRetornar200() throws Exception {
        CatalogoConColorView desactivado = new CatalogoConColorView(MANTEL_ID, "Mantel Blanco", COLOR_ID, false);
        when(gestionarMantelUseCase.desactivarMantel(MANTEL_ID)).thenReturn(desactivado);

        mockMvc.perform(delete("/api/catalogos/manteles/" + MANTEL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void deberiaObtenerMantelPorId() throws Exception {
        when(gestionarMantelUseCase.obtenerMantel(MANTEL_ID)).thenReturn(mantelView());

        mockMvc.perform(get("/api/catalogos/manteles/" + MANTEL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(MANTEL_ID.toString()))
                .andExpect(jsonPath("$.nombre").value("Mantel Blanco"));
    }

    @Test
    void deberiaListarManteles() throws Exception {
        when(gestionarMantelUseCase.listarManteles()).thenReturn(List.of(mantelView()));

        mockMvc.perform(get("/api/catalogos/manteles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(MANTEL_ID.toString()))
                .andExpect(jsonPath("$[0].nombre").value("Mantel Blanco"));
    }

    @Test
    void deberiaRetornar400CuandoServicioLanzaDomainException() throws Exception {
        when(gestionarMantelUseCase.crearMantel(any()))
                .thenThrow(new DomainException("Ya existe un mantel con ese nombre"));

        mockMvc.perform(post("/api/catalogos/manteles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido()))
                .andExpect(status().isBadRequest());
    }
}