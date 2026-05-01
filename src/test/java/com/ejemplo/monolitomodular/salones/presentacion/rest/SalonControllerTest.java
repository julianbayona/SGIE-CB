package com.ejemplo.monolitomodular.salones.presentacion.rest;

import com.ejemplo.monolitomodular.salones.aplicacion.dto.SalonView;
import com.ejemplo.monolitomodular.salones.aplicacion.puerto.entrada.ConsultarSalonUseCase;
import com.ejemplo.monolitomodular.salones.aplicacion.puerto.entrada.RegistrarSalonUseCase;
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

@WebMvcTest(SalonController.class)
class SalonControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RegistrarSalonUseCase registrarSalonUseCase;

    @MockBean
    ConsultarSalonUseCase consultarSalonUseCase;

    private static final UUID SALON_ID = UUID.randomUUID();

    private SalonView salonView() {
        return new SalonView(SALON_ID, "Salon Principal", 100, "Salon grande", true);
    }

    @Test
    void deberiaCrearSalonYRetornar201() throws Exception {
        when(registrarSalonUseCase.ejecutar(any())).thenReturn(salonView());

        mockMvc.perform(post("/api/salones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Salon Principal\",\"capacidad\":100,\"descripcion\":\"Salon grande\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Salon Principal"))
                .andExpect(jsonPath("$.capacidad").value(100))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void deberiaRetornar400SiNombreEsBlanco() throws Exception {
        mockMvc.perform(post("/api/salones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"capacidad\":100}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400SiCapacidadEsCero() throws Exception {
        mockMvc.perform(post("/api/salones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Salon A\",\"capacidad\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaObtenerSalonPorId() throws Exception {
        when(consultarSalonUseCase.obtenerPorId(SALON_ID)).thenReturn(salonView());

        mockMvc.perform(get("/api/salones/" + SALON_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SALON_ID.toString()))
                .andExpect(jsonPath("$.nombre").value("Salon Principal"));
    }

    @Test
    void deberiaListarSalones() throws Exception {
        when(consultarSalonUseCase.listar()).thenReturn(List.of(salonView()));

        mockMvc.perform(get("/api/salones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Salon Principal"));
    }

    @Test
    void deberiaConsultarDisponibilidad() throws Exception {
        when(consultarSalonUseCase.consultarDisponibilidad(any())).thenReturn(List.of(salonView()));

        mockMvc.perform(get("/api/salones/disponibilidad")
                        .param("fechaHoraInicio", "2025-06-01T10:00:00")
                        .param("fechaHoraFin", "2025-06-01T14:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Salon Principal"));
    }

    @Test
    void deberiaRetornar400CuandoServicioLanzaDomainException() throws Exception {
        when(registrarSalonUseCase.ejecutar(any()))
                .thenThrow(new DomainException("Ya existe un salon con el nombre indicado"));

        mockMvc.perform(post("/api/salones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Salon Principal\",\"capacidad\":100}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un salon con el nombre indicado"));
    }
}