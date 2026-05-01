package com.ejemplo.monolitomodular.catalogos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoConColorView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarSobremantelUseCase;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SobremantelController.class)
class SobremantelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GestionarSobremantelUseCase gestionarSobremantelUseCase;

    private static final UUID SOBREMANTEL_ID = UUID.randomUUID();
    private static final UUID COLOR_ID = UUID.randomUUID();

    private CatalogoConColorView sobremantelView() {
        return new CatalogoConColorView(SOBREMANTEL_ID, "Elegante", COLOR_ID, true);
    }

    @Test
    void deberiaCrearSobremantelYRetornar201() throws Exception {
        when(gestionarSobremantelUseCase.crearSobremantel(any())).thenReturn(sobremantelView());

        mockMvc.perform(post("/api/catalogos/sobremanteles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Elegante\",\"colorId\":\"" + COLOR_ID + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/" + SOBREMANTEL_ID)))
                .andExpect(jsonPath("$.id").value(SOBREMANTEL_ID.toString()))
                .andExpect(jsonPath("$.colorId").value(COLOR_ID.toString()));
    }

    @Test
    void deberiaRetornar400CuandoNombreEsBlanco() throws Exception {
        mockMvc.perform(post("/api/catalogos/sobremanteles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"colorId\":\"" + COLOR_ID + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaActualizarSobremantelYRetornar200() throws Exception {
        when(gestionarSobremantelUseCase.actualizarSobremantel(eq(SOBREMANTEL_ID), any())).thenReturn(sobremantelView());

        mockMvc.perform(put("/api/catalogos/sobremanteles/" + SOBREMANTEL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Elegante\",\"colorId\":\"" + COLOR_ID + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SOBREMANTEL_ID.toString()));
    }

    @Test
    void deberiaDesactivarSobremantelYRetornar200() throws Exception {
        CatalogoConColorView desactivado = new CatalogoConColorView(SOBREMANTEL_ID, "Elegante", COLOR_ID, false);
        when(gestionarSobremantelUseCase.desactivarSobremantel(SOBREMANTEL_ID)).thenReturn(desactivado);

        mockMvc.perform(delete("/api/catalogos/sobremanteles/" + SOBREMANTEL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void deberiaObtenerSobremantelPorId() throws Exception {
        when(gestionarSobremantelUseCase.obtenerSobremantel(SOBREMANTEL_ID)).thenReturn(sobremantelView());

        mockMvc.perform(get("/api/catalogos/sobremanteles/" + SOBREMANTEL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Elegante"))
                .andExpect(jsonPath("$.colorId").value(COLOR_ID.toString()));
    }

    @Test
    void deberiaListarSobremanteles() throws Exception {
        CatalogoConColorView otro = new CatalogoConColorView(UUID.randomUUID(), "Fiesta", UUID.randomUUID(), true);
        when(gestionarSobremantelUseCase.listarSobremanteles()).thenReturn(List.of(sobremantelView(), otro));

        mockMvc.perform(get("/api/catalogos/sobremanteles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Elegante"))
                .andExpect(jsonPath("$[1].nombre").value("Fiesta"));
    }

    @Test
    void deberiaRetornar400CuandoServicioLanzaDomainException() throws Exception {
        when(gestionarSobremantelUseCase.crearSobremantel(any()))
                .thenThrow(new DomainException("Ya existe un sobremantel con ese nombre"));

        mockMvc.perform(post("/api/catalogos/sobremanteles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Elegante\",\"colorId\":\"" + COLOR_ID + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un sobremantel con ese nombre"));
    }
}