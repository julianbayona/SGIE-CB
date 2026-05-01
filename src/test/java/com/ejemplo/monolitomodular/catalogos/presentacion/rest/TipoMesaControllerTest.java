package com.ejemplo.monolitomodular.catalogos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarTipoMesaUseCase;
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

@WebMvcTest(TipoMesaController.class)
class TipoMesaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GestionarTipoMesaUseCase gestionarTipoMesaUseCase;

    private static final UUID TIPO_ID = UUID.randomUUID();

    private CatalogoBasicoView tipoMesaView() {
        return new CatalogoBasicoView(TIPO_ID, "Rectangular", "Para eventos medianos", true);
    }

    @Test
    void deberiaCrearTipoMesaYRetornar201() throws Exception {
        when(gestionarTipoMesaUseCase.crearTipoMesa(any())).thenReturn(tipoMesaView());

        mockMvc.perform(post("/api/catalogos/tipos-mesa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Rectangular\",\"descripcion\":\"Para eventos medianos\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/" + TIPO_ID)))
                .andExpect(jsonPath("$.id").value(TIPO_ID.toString()))
                .andExpect(jsonPath("$.nombre").value("Rectangular"));
    }

    @Test
    void deberiaRetornar400CuandoNombreEsBlanco() throws Exception {
        mockMvc.perform(post("/api/catalogos/tipos-mesa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"descripcion\":\"X\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaActualizarTipoMesaYRetornar200() throws Exception {
        when(gestionarTipoMesaUseCase.actualizarTipoMesa(eq(TIPO_ID), any())).thenReturn(tipoMesaView());

        mockMvc.perform(put("/api/catalogos/tipos-mesa/" + TIPO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Rectangular\",\"descripcion\":\"Para eventos medianos\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TIPO_ID.toString()));
    }

    @Test
    void deberiaDesactivarTipoMesaYRetornar200() throws Exception {
        CatalogoBasicoView desactivado = new CatalogoBasicoView(TIPO_ID, "Rectangular", "Para eventos medianos", false);
        when(gestionarTipoMesaUseCase.desactivarTipoMesa(TIPO_ID)).thenReturn(desactivado);

        mockMvc.perform(delete("/api/catalogos/tipos-mesa/" + TIPO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void deberiaObtenerTipoMesaPorId() throws Exception {
        when(gestionarTipoMesaUseCase.obtenerTipoMesa(TIPO_ID)).thenReturn(tipoMesaView());

        mockMvc.perform(get("/api/catalogos/tipos-mesa/" + TIPO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Rectangular"))
                .andExpect(jsonPath("$.descripcion").value("Para eventos medianos"));
    }

    @Test
    void deberiaListarTiposMesa() throws Exception {
        CatalogoBasicoView otro = new CatalogoBasicoView(UUID.randomUUID(), "Redonda", "Para grupos", true);
        when(gestionarTipoMesaUseCase.listarTiposMesa()).thenReturn(List.of(tipoMesaView(), otro));

        mockMvc.perform(get("/api/catalogos/tipos-mesa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Rectangular"))
                .andExpect(jsonPath("$[1].nombre").value("Redonda"));
    }

    @Test
    void deberiaRetornar400CuandoServicioLanzaDomainException() throws Exception {
        when(gestionarTipoMesaUseCase.crearTipoMesa(any()))
                .thenThrow(new DomainException("Ya existe un tipo de mesa con ese nombre"));

        mockMvc.perform(post("/api/catalogos/tipos-mesa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Rectangular\",\"descripcion\":\"Para eventos medianos\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un tipo de mesa con ese nombre"));
    }
}