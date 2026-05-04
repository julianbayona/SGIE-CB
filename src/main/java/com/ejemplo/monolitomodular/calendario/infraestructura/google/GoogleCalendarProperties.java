package com.ejemplo.monolitomodular.calendario.infraestructura.google;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sgie.calendario.google")
public record GoogleCalendarProperties(
        boolean enabled,
        String calendarId,
        String oauthClientId,
        String oauthClientSecret,
        String oauthRefreshToken,
        String applicationName,
        String timeZone,
        String sendUpdates,
        boolean includeAttendees
) {
}
