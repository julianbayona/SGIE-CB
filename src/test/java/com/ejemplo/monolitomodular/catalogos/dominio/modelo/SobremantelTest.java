package com.ejemplo.monolitomodular.catalogos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SobremantelTest {

    private final UUID COLOR_ID = UUID.randomUUID();

    @Test
    void testCrearSobremantelNuevo() {
        Sobremantel sobremantel = Sobremantel.nuevo("Sobremantel Elegante", COLOR_ID);
        
        assertNotNull(sobremantel.getId());
        assertEquals("Sobremantel Elegante", sobremantel.getNombre());
        assertEquals(COLOR_ID, sobremantel.getColorId());
        assertTrue(sobremantel.isActivo());
    }

    @Test
    void testCrearSobremantelNuevoConNombreVacio() {
        assertThrows(DomainException.class, () -> Sobremantel.nuevo("", COLOR_ID));
    }

    @Test
    void testCrearSobremantelNuevoConColorIdNulo() {
        assertThrows(NullPointerException.class, () -> Sobremantel.nuevo("Sobremantel", null));
    }

    @Test
    void testReconstruirSobremantel() {
        UUID id = UUID.randomUUID();
        UUID colorId = UUID.randomUUID();
        Sobremantel sobremantel = Sobremantel.reconstruir(id, "Sobremantel Especial", colorId, false);
        
        assertEquals(id, sobremantel.getId());
        assertEquals("Sobremantel Especial", sobremantel.getNombre());
        assertEquals(colorId, sobremantel.getColorId());
        assertFalse(sobremantel.isActivo());
    }

    @Test
    void testActualizarSobremantel() {
        Sobremantel original = Sobremantel.nuevo("Sobremantel Original", COLOR_ID);
        UUID nuevoColorId = UUID.randomUUID();
        Sobremantel actualizado = original.actualizar("Sobremantel Actualizado", nuevoColorId);
        
        assertEquals(original.getId(), actualizado.getId());
        assertEquals("Sobremantel Actualizado", actualizado.getNombre());
        assertEquals(nuevoColorId, actualizado.getColorId());
        assertTrue(actualizado.isActivo());
    }

    @Test
    void testDesactivarSobremantel() {
        Sobremantel sobremantel = Sobremantel.nuevo("Sobremantel Activo", COLOR_ID);
        Sobremantel desactivado = sobremantel.desactivar();
        
        assertEquals(sobremantel.getId(), desactivado.getId());
        assertEquals(sobremantel.getNombre(), desactivado.getNombre());
        assertFalse(desactivado.isActivo());
    }

    @Test
    void testMultiplesSobremantelesConColoresDistintos() {
        UUID colorId1 = UUID.randomUUID();
        UUID colorId2 = UUID.randomUUID();
        
        Sobremantel sobremantel1 = Sobremantel.nuevo("Sobremantel 1", colorId1);
        Sobremantel sobremantel2 = Sobremantel.nuevo("Sobremantel 2", colorId2);
        
        assertNotEquals(sobremantel1.getId(), sobremantel2.getId());
        assertEquals(colorId1, sobremantel1.getColorId());
        assertEquals(colorId2, sobremantel2.getColorId());
    }
}
