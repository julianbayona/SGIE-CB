package com.ejemplo.monolitomodular.shared.presentacion;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void deberiaRetornar400CuandoDomainException() {
        DomainException ex = new DomainException("Error de dominio");

        ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Error de dominio", response.getBody().mensaje());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void deberiaRetornar400CuandoIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Argumento invalido");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Argumento invalido", response.getBody().mensaje());
    }

    @Test
    void deberiaRetornar400CuandoValidationException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "nombre", "El nombre es obligatorio");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().mensaje().contains("nombre"));
        assertTrue(response.getBody().mensaje().contains("El nombre es obligatorio"));
    }

    @Test
    void deberiaRetornarMensajeGenericoSiNoHayFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Solicitud inválida", response.getBody().mensaje());
    }

    @Test
    void errorResponseDeberiaAlmacenarMensajeYTimestamp() {
        Instant ahora = Instant.now();
        ErrorResponse errorResponse = new ErrorResponse("mensaje de error", ahora);

        assertEquals("mensaje de error", errorResponse.mensaje());
        assertEquals(ahora, errorResponse.timestamp());
    }
}