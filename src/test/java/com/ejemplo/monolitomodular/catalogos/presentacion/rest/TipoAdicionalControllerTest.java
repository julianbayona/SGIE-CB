package com.ejemplo.monolitomodular.catalogos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.TipoAdicionalView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarTipoAdicionalUseCase;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.ModoCobroAdicional;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TipoAdicionalController.class)
class TipoAdicionalControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    GestionarTipoAdicionalUseCase gestionarTipoAdicionalUseCase;

    private static final UUID TIPO_ID = UUID.randomUUID();

    private TipoAdicionalView tipoAdicionalView() {
        return new TipoAdicionalView(TIPO_ID, "Decoración Floral", ModoCobroAdicional.SERVICIO,
                new BigDecimal("150000.00"), true);
    }

    private String bodyValido() {
        return "{\"nombre\":\"Decoración Floral\",\"modoCobro\":\"SERVICIO\",\"precioBase\":150000.00}";
    }

    @Test
    void deberiaCrearTipoAdicionalYRetornar201() throws Exception {
        when(gestionarTipoAdicionalUseCase.crearTipoAdicional(any())).thenReturn(tipoAdicionalView());

        mockMvc.perform(post("/api/catalogos/tipos-adicional")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TIPO_ID.toString()))
                .andExpect(jsonPath("$.nombre").value("Decoración Floral"))
                .andExpect(jsonPath("$.modoCobro").value("SERVICIO"))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void deberiaRetornar400CuandoNombreEsBlanco() throws Exception {
        mockMvc.perform(post("/api/catalogos/tipos-adicional")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"modoCobro\":\"SERVICIO\",\"precioBase\":150000.00}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoModoCobrosEsNulo() throws Exception {
        mockMvc.perform(post("/api/catalogos/tipos-adicional")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Decoración\",\"precioBase\":150000.00}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoPrecioBaseEsNegativo() throws Exception {
        mockMvc.perform(post("/api/catalogos/tipos-adicional")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Decoración\",\"modoCobro\":\"UNIDAD\",\"precioBase\":-1.00}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaActualizarTipoAdicionalYRetornar200() throws Exception {
        when(gestionarTipoAdicionalUseCase.actualizarTipoAdicional(eq(TIPO_ID), any()))
                .thenReturn(tipoAdicionalView());

        mockMvc.perform(put("/api/catalogos/tipos-adicional/" + TIPO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TIPO_ID.toString()));
    }

    @Test
    void deberiaDesactivarTipoAdicionalYRetornar200() throws Exception {
        TipoAdicionalView desactivado = new TipoAdicionalView(
                TIPO_ID, "Decoración Floral", ModoCobroAdicional.SERVICIO,
                new BigDecimal("150000.00"), false);
        when(gestionarTipoAdicionalUseCase.desactivarTipoAdicional(TIPO_ID)).thenReturn(desactivado);

        mockMvc.perform(delete("/api/catalogos/tipos-adicional/" + TIPO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void deberiaObtenerTipoAdicionalPorId() throws Exception {
        when(gestionarTipoAdicionalUseCase.obtenerTipoAdicional(TIPO_ID)).thenReturn(tipoAdicionalView());

        mockMvc.perform(get("/api/catalogos/tipos-adicional/" + TIPO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Decoración Floral"))
                .andExpect(jsonPath("$.modoCobro").value("SERVICIO"));
    }

    @Test
    void deberiaListarTiposAdicional() throws Exception {
        TipoAdicionalView porUnidad = new TipoAdicionalView(UUID.randomUUID(), "Silla Extra",
                ModoCobroAdicional.UNIDAD, new BigDecimal("20000.00"), true);
        when(gestionarTipoAdicionalUseCase.listarTiposAdicional())
                .thenReturn(List.of(tipoAdicionalView(), porUnidad));

        mockMvc.perform(get("/api/catalogos/tipos-adicional"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].modoCobro").value("SERVICIO"))
                .andExpect(jsonPath("$[1].modoCobro").value("UNIDAD"));
    }

    @Test
    void deberiaRetornar400CuandoServicioLanzaDomainException() throws Exception {
        when(gestionarTipoAdicionalUseCase.crearTipoAdicional(any()))
                .thenThrow(new DomainException("Ya existe un tipo adicional con ese nombre"));

        mockMvc.perform(post("/api/catalogos/tipos-adicional")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaCrearTipoAdicionalModoUnidad() throws Exception {
        TipoAdicionalView viewUnidad = new TipoAdicionalView(TIPO_ID, "Silla Extra",
                ModoCobroAdicional.UNIDAD, new BigDecimal("20000.00"), true);
        when(gestionarTipoAdicionalUseCase.crearTipoAdicional(any())).thenReturn(viewUnidad);

        mockMvc.perform(post("/api/catalogos/tipos-adicional")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Silla Extra\",\"modoCobro\":\"UNIDAD\",\"precioBase\":20000.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.modoCobro").value("UNIDAD"))
                .andExpect(jsonPath("$.nombre").value("Silla Extra"));
    }
}