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

                Recibe un cordial saludo del Club Boyaca.

                Confirmamos que tu prueba de plato fue programada correctamente. Este espacio nos permitira revisar contigo la propuesta gastronomica y resolver cualquier ajuste antes del evento.

                Fecha y hora: %s

                Te recomendamos llegar unos minutos antes de la hora programada. Si necesitas reprogramar o hacer alguna observacion previa, puedes comunicarte con nuestro equipo administrativo.

                Gracias por confiar en el Club Boyaca.

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
                Se registro una prueba de plato en el sistema SGIE.

                Cliente: %s
                Fecha y hora: %s

                Acciones requeridas:
                - Verificar disponibilidad del equipo de cocina y servicio.
                - Revisar la propuesta gastronomica asociada al evento.
                - Preparar las observaciones necesarias para la atencion del cliente.

                Este correo es informativo para coordinacion interna del Club Boyaca.
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

                Recibe un cordial saludo del Club Boyaca.

                Nos complace confirmarte que tu evento quedo registrado como confirmado en nuestro sistema. Nuestro equipo continuara con la coordinacion operativa correspondiente para la fecha programada.

                Inicio: %s
                Fin: %s

                Si tienes alguna observacion adicional sobre menu, montaje o condiciones del servicio, puedes comunicarte con nuestro equipo administrativo para revisarla oportunamente.

                Gracias por elegir el Club Boyaca para la realizacion de tu evento.

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
                Se confirmo un evento en el sistema SGIE.

                Inicio: %s
                Fin: %s

                Acciones requeridas:
                - Revisar la reserva del salon y el horario confirmado.
                - Validar menu, montaje y adicionales asociados al evento.
                - Coordinar personal, cocina, servicio y requerimientos operativos.

                Este correo es informativo para la preparacion interna del Club Boyaca.
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

                Recibe un cordial saludo del Club Boyaca.

                Te recordamos que tu evento registra un saldo pendiente de anticipo. Este pago nos permite continuar con la reserva y coordinacion de los servicios acordados.

                Fecha del evento: %s
                Valor total: %s
                Total pagado: %s
                Saldo pendiente: %s

                Si ya realizaste el pago, por favor comparte el soporte con nuestro equipo administrativo para actualizar el estado del evento. Si tienes alguna inquietud, estamos atentos para ayudarte.

                Gracias por tu atencion.

                Club Boyaca
                """.formatted(cliente, fechaEvento, valorTotal, totalPagado, saldoPendiente).trim()
        );
    }

    private EmailMessage cotizacionCliente(JsonNode payload) {
        String cliente = valor(payload, "cliente");
        String fechaEvento = fecha(valor(payload, "fechaEvento"));
        String valorTotal = valor(payload, "valorTotal");
        return new EmailMessage(
                "Cotizacion de tu evento - Club Boyaca",
                """
                Hola %s,

                Recibe un cordial saludo del Club Boyaca.

                Adjuntamos la cotizacion formal de tu evento en formato Excel para que puedas revisar con claridad el detalle de servicios, cantidades y valores.

                Fecha del evento: %s
                Valor total: %s

                Si deseas aprobar la cotizacion o solicitar algun ajuste, puedes responder a este correo o comunicarte directamente con nuestro equipo administrativo.

                Gracias por confiar en el Club Boyaca para la realizacion de tu evento.

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
