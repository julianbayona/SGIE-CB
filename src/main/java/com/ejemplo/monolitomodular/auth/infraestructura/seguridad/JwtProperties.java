package com.ejemplo.monolitomodular.auth.infraestructura.seguridad;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sgie.auth.jwt")
public record JwtProperties(
        String secret,
        long expirationMinutes
) {
    public JwtProperties {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("La variable SGIE_AUTH_JWT_SECRET es obligatoria");
        }
        if (secret.getBytes(java.nio.charset.StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("SGIE_AUTH_JWT_SECRET debe tener al menos 32 bytes para HS256");
        }
        if (expirationMinutes <= 0) {
            throw new IllegalStateException("SGIE_AUTH_JWT_EXPIRATION_MINUTES debe ser mayor que cero");
        }
    }
}
