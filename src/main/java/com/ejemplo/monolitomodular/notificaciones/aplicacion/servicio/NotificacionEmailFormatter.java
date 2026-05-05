package com.ejemplo.monolitomodular.notificaciones.aplicacion.servicio;

import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class NotificacionEmailFormatter {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ObjectMapper objectMapper = new ObjectMapper();

    EmailMessage formatear(TipoNotificacion tipo, String payloadJson) {
        try {
            JsonNode payload = objectMapper.readTree(payloadJson);
            return switch (tipo) {
                case PRUEBA_PLATO_CLIENTE -> pruebaPlatoCliente(payload);
                case PRUEBA_PLATO_PERSONAL -> pruebaPlatoPersonal(payload);
                case EVENTO_CONFIRMADO_CLIENTE -> eventoConfirmadoCliente(payload);
                case EVENTO_CONFIRMADO_PERSONAL -> eventoConfirmadoPersonal(payload);
                case RECORDATORIO_ANTICIPO -> recordatorioAnticipo(payload);
                case COTIZACION_CLIENTE -> cotizacionCliente(payload);
            };
        } catch (Exception ex) {
            return new EmailMessage("SGIE - " + tipo.name(), payloadJson);
        }
    }

    private EmailMessage pruebaPlatoCliente(JsonNode payload) {
        String cliente = valor(payload, "cliente");
        String fecha = fecha(valor(payload, "fechaRealizacion"));
        return new EmailMessage(
                "Prueba de plato programada - Club Boyaca",
                """
                Hola %s,

                Tu prueba de plato fue programada exitosamente.

                Fecha y hora: %s

                Club Boyaca
                """.formatted(cliente, fecha).trim()
        );
    }

    private EmailMessage pruebaPlatoPersonal(JsonNode payload) {
        String cliente = valor(payload, "cliente");
        String fecha = fecha(valor(payload, "fechaRealizacion"));
        return new EmailMessage(
                "Prueba de plato programada para coordinacion",
                """
                Se programo una prueba de plato.

                Cliente: %s
                Fecha y hora: %s

                Revisar disponibilidad y preparacion correspondiente.
                """.formatted(cliente, fecha).trim()
        );
    }

    private EmailMessage eventoConfirmadoCliente(JsonNode payload) {
        String cliente = valor(payload, "cliente");
        String inicio = fecha(valor(payload, "fechaInicio"));
        String fin = fecha(valor(payload, "fechaFin"));
        return new EmailMessage(
                "Evento confirmado - Club Boyaca",
                """
                Hola %s,

                Tu evento fue confirmado exitosamente.

                Inicio: %s
                Fin: %s

                Club Boyaca
                """.formatted(cliente, inicio, fin).trim()
        );
    }

    private EmailMessage eventoConfirmadoPersonal(JsonNode payload) {
        String inicio = fecha(valor(payload, "fechaInicio"));
        String fin = fecha(valor(payload, "fechaFin"));
        return new EmailMessage(
                "Evento confirmado para coordinacion",
                """
                Se confirmo un evento en el sistema.

                Inicio: %s
                Fin: %s

                Revisar montaje, menu y coordinacion operativa.
                """.formatted(inicio, fin).trim()
        );
    }

    private EmailMessage recordatorioAnticipo(JsonNode payload) {
        String cliente = valor(payload, "cliente");
        String fechaEvento = fecha(valor(payload, "fechaEvento"));
        String valorTotal = valor(payload, "valorTotal");
        String totalPagado = valor(payload, "totalPagado");
        String saldoPendiente = valor(payload, "saldoPendiente");
        return new EmailMessage(
                "Recordatorio de anticipo - Club Boyaca",
                """
                Hola %s,

                Te recordamos que el evento tiene saldo de anticipo pendiente.

                Fecha del evento: %s
                Valor total: %s
                Total pagado: %s
                Saldo pendiente: %s

                Club Boyaca
                """.formatted(cliente, fechaEvento, valorTotal, totalPagado, saldoPendiente).trim()
        );
    }

    private EmailMessage cotizacionCliente(JsonNode payload) {
        String cliente = valor(payload, "cliente");
        String fechaEvento = fecha(valor(payload, "fechaEvento"));
        String valorTotal = valor(payload, "valorTotal");
        return new EmailMessage(
                "Cotizacion de evento - Club Boyaca",
                """
                Hola %s,

                Te enviamos la cotizacion de tu evento.

                Fecha del evento: %s
                Valor total: %s

                Puedes comunicarte con el Club Boyaca para aprobarla o solicitar ajustes.

                Club Boyaca
                """.formatted(cliente, fechaEvento, valorTotal).trim()
        );
    }

    private String valor(JsonNode payload, String campo) {
        JsonNode valor = payload.get(campo);
        if (valor == null || valor.isNull() || valor.asText().isBlank()) {
            return "No registrado";
        }
        return valor.asText();
    }

    private String fecha(String valor) {
        try {
            return LocalDateTime.parse(valor).format(FORMATO_FECHA);
        } catch (RuntimeException ex) {
            return valor;
        }
    }

    record EmailMessage(String asunto, String cuerpo) {
    }
}
