package com.ejemplo.monolitomodular.auth.infraestructura.seguridad;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;
    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    public RateLimitingFilter(RateLimitProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!properties.enabled() || HttpMethod.OPTIONS.matches(request.getMethod()) || !request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        Limit limit = limitFor(request);
        String key = clientKey(request) + ":" + limit.name();
        long now = System.currentTimeMillis();
        WindowCounter counter = counters.compute(key, (ignored, current) -> {
            if (current == null || now >= current.resetAtMillis()) {
                return new WindowCounter(new AtomicInteger(1), now + limit.windowMillis());
            }
            current.count().incrementAndGet();
            return current;
        });

        if (counter.count().get() > limit.capacity()) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", Long.toString(Math.max(1, (counter.resetAtMillis() - now) / 1000)));
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "message", "Demasiadas solicitudes. Intenta de nuevo mas tarde.",
                    "timestamp", Instant.now().toString()
            ));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Limit limitFor(HttpServletRequest request) {
        if ("/api/auth/login".equals(request.getRequestURI())) {
            return new Limit("login", properties.loginCapacity(), properties.loginWindowSeconds() * 1000L);
        }
        return new Limit("api", properties.capacity(), properties.windowSeconds() * 1000L);
    }

    private String clientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private record Limit(String name, int capacity, long windowMillis) {
    }

    private record WindowCounter(AtomicInteger count, long resetAtMillis) {
    }
}
