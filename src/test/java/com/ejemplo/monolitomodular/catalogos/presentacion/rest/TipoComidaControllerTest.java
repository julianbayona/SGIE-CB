package com.ejemplo.monolitomodular.catalogos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarTipoComidaUseCase;
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

@WebMvcTest(TipoComidaController.class)
class TipoComidaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GestionarTipoComidaUseCase gestionarTipoComidaUseCase;

    private static final UUID TIPO_ID = UUID.randomUUID();

    private CatalogoBasicoView tipoComidaView() {
        return new CatalogoBasicoView(TIPO_ID, "Buffet", "Estilo buffet", true);
    }

    @Test
    void deberiaCrearTipoComidaYRetornar201() throws Exception {
        when(gestionarTipoComidaUseCase.crearTipoComida(any())).thenReturn(tipoComidaView());

        mockMvc.perform(post("/api/catalogos/tipos-comida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Buffet\",\"descripcion\":\"Estilo buffet\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/" + TIPO_ID)))
                .andExpect(jsonPath("$.id").value(TIPO_ID.toString()))
                .andExpect(jsonPath("$.nombre").value("Buffet"));
    }

    @Test
    void deberiaRetornar400CuandoNombreEsBlanco() throws Exception {
        mockMvc.perform(post("/api/catalogos/tipos-comida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"descripcion\":\"X\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaActualizarTipoComidaYRetornar200() throws Exception {
        when(gestionarTipoComidaUseCase.actualizarTipoComida(eq(TIPO_ID), any())).thenReturn(tipoComidaView());

        mockMvc.perform(put("/api/catalogos/tipos-comida/" + TIPO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Buffet\",\"descripcion\":\"Estilo buffet\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TIPO_ID.toString()));
    }

    @Test
    void deberiaDesactivarTipoComidaYRetornar200() throws Exception {
        CatalogoBasicoView desactivado = new CatalogoBasicoView(TIPO_ID, "Buffet", "Estilo buffet", false);
        when(gestionarTipoComidaUseCase.desactivarTipoComida(TIPO_ID)).thenReturn(desactivado);

        mockMvc.perform(delete("/api/catalogos/tipos-comida/" + TIPO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void deberiaObtenerTipoComidaPorId() throws Exception {
        when(gestionarTipoComidaUseCase.obtenerTipoComida(TIPO_ID)).thenReturn(tipoComidaView());

        mockMvc.perform(get("/api/catalogos/tipos-comida/" + TIPO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Buffet"))
                .andExpect(jsonPath("$.descripcion").value("Estilo buffet"));
    }

    @Test
    void deberiaListarTiposComida() throws Exception {
        CatalogoBasicoView otro = new CatalogoBasicoView(UUID.randomUUID(), "Empanadas", "Pica pica", true);
        when(gestionarTipoComidaUseCase.listarTiposComida()).thenReturn(List.of(tipoComidaView(), otro));

        mockMvc.perform(get("/api/catalogos/tipos-comida"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Buffet"))
                .andExpect(jsonPath("$[1].nombre").value("Empanadas"));
    }

    @Test
    void deberiaRetornar400CuandoServicioLanzaDomainException() throws Exception {
        when(gestionarTipoComidaUseCase.crearTipoComida(any()))
                .thenThrow(new DomainException("Ya existe un tipo de comida con ese nombre"));

        mockMvc.perform(post("/api/catalogos/tipos-comida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Buffet\",\"descripcion\":\"Estilo buffet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un tipo de comida con ese nombre"));
    }
}