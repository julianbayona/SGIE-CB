package com.ejemplo.monolitomodular.calendario.infraestructura.google;

import com.ejemplo.monolitomodular.calendario.aplicacion.dto.SincronizarGoogleCalendarCommand;
import com.ejemplo.monolitomodular.calendario.aplicacion.dto.SincronizarGoogleCalendarResult;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.GoogleCalendarPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@ConditionalOnProperty(name = "sgie.calendario.google.enabled", havingValue = "true")
@EnableConfigurationProperties(GoogleCalendarProperties.class)
public class GoogleCalendarApiAdapter implements GoogleCalendarPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleCalendarApiAdapter.class);

    private final GoogleCalendarProperties properties;
    private final ObjectMapper objectMapper;

    public GoogleCalendarApiAdapter(
            GoogleCalendarProperties properties,
            ObjectMapper objectMapper
    ) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public SincronizarGoogleCalendarResult sincronizar(SincronizarGoogleCalendarCommand command) {
        try {
            return switch (command.tipo()) {
                case CREAR -> crear(command);
                case ACTUALIZAR -> actualizar(command);
                case CANCELAR -> cancelar(command);
            };
        } catch (Exception ex) {
            LOGGER.error("Fallo sincronizando Google Calendar. eventoCalendarId={}", command.eventoCalendarId(), ex);
            return SincronizarGoogleCalendarResult.error(ex.getMessage());
        }
    }

    private SincronizarGoogleCalendarResult crear(SincronizarGoogleCalendarCommand command)
            throws GeneralSecurityException, IOException {
        Event creado = calendar().events()
                .insert(properties.calendarId(), toGoogleEvent(command.payloadJson()))
                .setSendUpdates(properties.sendUpdates())
                .execute();
        return SincronizarGoogleCalendarResult.ok(creado.getId());
    }

    private SincronizarGoogleCalendarResult actualizar(SincronizarGoogleCalendarCommand command)
            throws GeneralSecurityException, IOException {
        validarGoogleEventId(command.googleEventId());
        Event actualizado = calendar().events()
                .update(properties.calendarId(), command.googleEventId(), toGoogleEvent(command.payloadJson()))
                .setSendUpdates(properties.sendUpdates())
                .execute();
        return SincronizarGoogleCalendarResult.ok(actualizado.getId());
    }

    private SincronizarGoogleCalendarResult cancelar(SincronizarGoogleCalendarCommand command)
            throws GeneralSecurityException, IOException {
        validarGoogleEventId(command.googleEventId());
        calendar().events()
                .delete(properties.calendarId(), command.googleEventId())
                .setSendUpdates(properties.sendUpdates())
                .execute();
        return SincronizarGoogleCalendarResult.ok(command.googleEventId());
    }

    private Calendar calendar() throws GeneralSecurityException, IOException {
        GoogleCredentials credentials = oauthCredentials();
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer
        )
                .setApplicationName(properties.applicationName())
                .build();
    }

    private GoogleCredentials oauthCredentials() {
        validarConfiguracionOauth();
        return UserCredentials.newBuilder()
                .setClientId(properties.oauthClientId())
                .setClientSecret(properties.oauthClientSecret())
                .setRefreshToken(properties.oauthRefreshToken())
                .build()
                .createScoped(List.of(CalendarScopes.CALENDAR));
    }

    private Event toGoogleEvent(String payloadJson) throws IOException {
        CalendarPayload payload = objectMapper.readValue(payloadJson, CalendarPayload.class);
        ZoneId zoneId = ZoneId.of(properties.timeZone());
        Event event = new Event()
                .setSummary(payload.resumen())
                .setDescription(payload.origen())
                .setStart(new EventDateTime()
                        .setDateTime(new DateTime(toEpochMillis(payload.fechaInicio(), zoneId)))
                        .setTimeZone(properties.timeZone()))
                .setEnd(new EventDateTime()
                        .setDateTime(new DateTime(toEpochMillis(payload.fechaFin(), zoneId)))
                        .setTimeZone(properties.timeZone()));
        if (properties.includeAttendees()) {
            event.setAttendees(payload.attendees().stream()
                    .map(attendee -> new EventAttendee().setEmail(attendee.email()))
                    .toList());
        }
        return event;
    }

    private long toEpochMillis(LocalDateTime dateTime, ZoneId zoneId) {
        return dateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

    private void validarGoogleEventId(String googleEventId) {
        if (googleEventId == null || googleEventId.isBlank()) {
            throw new IllegalArgumentException("googleEventId es obligatorio para actualizar o cancelar en Google Calendar");
        }
    }

    private void validarConfiguracionOauth() {
        validarNoVacio(properties.oauthClientId(), "oauthClientId");
        validarNoVacio(properties.oauthClientSecret(), "oauthClientSecret");
        validarNoVacio(properties.oauthRefreshToken(), "oauthRefreshToken");
    }

    private void validarNoVacio(String valor, String nombre) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("La propiedad Google Calendar " + nombre + " es obligatoria");
        }
    }

    private record CalendarPayload(
            String resumen,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            String origen,
            List<CalendarAttendeePayload> attendees
    ) {
        private CalendarPayload {
            attendees = attendees == null ? List.of() : attendees;
        }
    }

    private record CalendarAttendeePayload(String email) {
    }
}
