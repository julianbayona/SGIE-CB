package com.ejemplo.monolitomodular.salones.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SalonTest {

    @Test
    void deberiaCrearSalonNuevo() {
        Salon salon = Salon.nuevo("Salon Republica", 120, "Principal");

        assertNotNull(salon.getId());
        assertEquals("Salon Republica", salon.getNombre());
        assertEquals(120, salon.getCapacidad());
        assertEquals("Principal", salon.getDescripcion());
        assertTrue(salon.isActivo());
    }

    @Test
    void deberiaCrearSalonConDescripcionNula() {
        Salon salon = Salon.nuevo("Salon Test", 50, null);
        assertEquals("", salon.getDescripcion());
    }

    @Test
    void deberiaCrearSalonConDescripcionBlank() {
        Salon salon = Salon.nuevo("Salon Test", 50, "   ");
        assertEquals("", salon.getDescripcion());
    }

    @Test
    void deberiaReconstruirSalonExistente() {
        UUID id = UUID.randomUUID();
        Salon salon = Salon.reconstruir(id, "Salon Colonial", 80, "Segundo piso", false);

        assertEquals(id, salon.getId());
        assertEquals("Salon Colonial", salon.getNombre());
        assertEquals(80, salon.getCapacidad());
        assertEquals("Segundo piso", salon.getDescripcion());
        assertFalse(salon.isActivo());
    }

    @Test
    void noDeberiaCrearSalonConNombreNulo() {
        assertThrows(DomainException.class, () -> Salon.nuevo(null, 100, "Test"));
    }

    @Test
    void noDeberiaCrearSalonConNombreBlank() {
        assertThrows(DomainException.class, () -> Salon.nuevo("   ", 100, "Test"));
    }

    @Test
    void noDeberiaCrearSalonConCapacidadCero() {
        assertThrows(DomainException.class, () -> Salon.nuevo("Salon Test", 0, "Test"));
    }

    @Test
    void noDeberiaCrearSalonConCapacidadNegativa() {
        assertThrows(DomainException.class, () -> Salon.nuevo("Salon Test", -5, "Test"));
    }

    @Test
    void deberiaRecortarEspaciosEnNombre() {
        Salon salon = Salon.nuevo("  Salon Republica  ", 100, "Test");
        assertEquals("Salon Republica", salon.getNombre());
    }

    @Test
    void deberiaCrearSalonConCapacidadMinima() {
        Salon salon = Salon.nuevo("Salon Mini", 1, "Pequeño");
        assertEquals(1, salon.getCapacidad());
    }
}