package com.ejemplo.monolitomodular.catalogos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MantelTest {

    private final UUID COLOR_ID = UUID.randomUUID();

    @Test
    void testCrearMantelNuevo() {
        Mantel mantel = Mantel.nuevo("Mantel Clásico", COLOR_ID);
        
        assertNotNull(mantel.getId());
        assertEquals("Mantel Clásico", mantel.getNombre());
        assertEquals(COLOR_ID, mantel.getColorId());
        assertTrue(mantel.isActivo());
    }

    @Test
    void testCrearMantelNuevoConNombreVacio() {
        assertThrows(DomainException.class, () -> Mantel.nuevo("", COLOR_ID));
    }

    @Test
    void testCrearMantelNuevoConColorIdNulo() {
        assertThrows(NullPointerException.class, () -> Mantel.nuevo("Mantel", null));
    }

    @Test
    void testReconstruirMantel() {
        UUID id = UUID.randomUUID();
        UUID colorId = UUID.randomUUID();
        Mantel mantel = Mantel.reconstruir(id, "Mantel Premium", colorId, false);
        
        assertEquals(id, mantel.getId());
        assertEquals("Mantel Premium", mantel.getNombre());
        assertEquals(colorId, mantel.getColorId());
        assertFalse(mantel.isActivo());
    }

    @Test
    void testActualizarMantel() {
        Mantel original = Mantel.nuevo("Mantel Original", COLOR_ID);
        UUID nuevoColorId = UUID.randomUUID();
        Mantel actualizado = original.actualizar("Mantel Actualizado", nuevoColorId);
        
        assertEquals(original.getId(), actualizado.getId());
        assertEquals("Mantel Actualizado", actualizado.getNombre());
        assertEquals(nuevoColorId, actualizado.getColorId());
        assertTrue(actualizado.isActivo());
    }

    @Test
    void testDesactivarMantel() {
        Mantel mantel = Mantel.nuevo("Mantel Activo", COLOR_ID);
        Mantel desactivado = mantel.desactivar();
        
        assertEquals(mantel.getId(), desactivado.getId());
        assertEquals(mantel.getNombre(), desactivado.getNombre());
        assertFalse(desactivado.isActivo());
    }

    @Test
    void testNombreVacio() {
        assertThrows(DomainException.class, () -> Mantel.nuevo("", COLOR_ID));
    }

    @Test
    void testNombreConEspacios() {
        Mantel mantel = Mantel.nuevo("  Mantel con espacios  ", COLOR_ID);
        assertNotNull(mantel.getNombre());
    }
}
