package com.ejemplo.monolitomodular.catalogos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarTipoEventoUseCase;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TipoEventoController.class)
class TipoEventoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GestionarTipoEventoUseCase gestionarTipoEventoUseCase;

    private static final UUID ID = UUID.randomUUID();

    private CatalogoBasicoView view() {
        return new CatalogoBasicoView(ID, "Cumpleaños", "Festejo de cumple", true);
    }

    @Test
    void deberiaCrearTipoEventoYRetornar201() throws Exception {
        when(gestionarTipoEventoUseCase.crearTipoEvento(any())).thenReturn(view());

        mockMvc.perform(post("/api/catalogos/tipos-evento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Cumpleaños\",\"descripcion\":\"Festejo de cumple\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Cumpleaños"))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void deberiaRetornar400SiNombreEsBlanco() throws Exception {
        mockMvc.perform(post("/api/catalogos/tipos-evento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"descripcion\":\"desc\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaActualizarTipoEvento() throws Exception {
        when(gestionarTipoEventoUseCase.actualizarTipoEvento(eq(ID), any())).thenReturn(view());

        mockMvc.perform(put("/api/catalogos/tipos-evento/" + ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Cumpleaños\",\"descripcion\":\"desc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID.toString()));
    }

    @Test
    void deberiaDesactivarTipoEvento() throws Exception {
        CatalogoBasicoView inactivo = new CatalogoBasicoView(ID, "Cumpleaños", "desc", false);
        when(gestionarTipoEventoUseCase.desactivarTipoEvento(ID)).thenReturn(inactivo);

        mockMvc.perform(delete("/api/catalogos/tipos-evento/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void deberiaObtenerTipoEventoPorId() throws Exception {
        when(gestionarTipoEventoUseCase.obtenerTipoEvento(ID)).thenReturn(view());

        mockMvc.perform(get("/api/catalogos/tipos-evento/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Cumpleaños"));
    }

    @Test
    void deberiaListarTiposEvento() throws Exception {
        when(gestionarTipoEventoUseCase.listarTiposEvento()).thenReturn(List.of(view()));

        mockMvc.perform(get("/api/catalogos/tipos-evento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Cumpleaños"));
    }

    @Test
    void deberiaRetornar400CuandoServicioLanzaDomainException() throws Exception {
        when(gestionarTipoEventoUseCase.crearTipoEvento(any()))
                .thenThrow(new DomainException("Ya existe un tipo de evento con el nombre indicado"));

        mockMvc.perform(post("/api/catalogos/tipos-evento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Cumpleaños\",\"descripcion\":\"desc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un tipo de evento con el nombre indicado"));
    }
}