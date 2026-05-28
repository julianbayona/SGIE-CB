package com.ejemplo.monolitomodular.auth.infraestructura.seguridad;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sgie.security.rate-limit")
public record RateLimitProperties(
        boolean enabled,
        int capacity,
        int windowSeconds,
        int loginCapacity,
        int loginWindowSeconds
) {
    public RateLimitProperties {
        capacity = capacity <= 0 ? 120 : capacity;
        windowSeconds = windowSeconds <= 0 ? 60 : windowSeconds;
        loginCapacity = loginCapacity <= 0 ? 10 : loginCapacity;
        loginWindowSeconds = loginWindowSeconds <= 0 ? 60 : loginWindowSeconds;
    }
}
