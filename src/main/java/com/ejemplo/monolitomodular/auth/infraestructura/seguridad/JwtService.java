package com.ejemplo.monolitomodular.auth.infraestructura.seguridad;

import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtService {

    private final Clock clock;
    private final Key signingKey;
    private final long expirationMinutes;

    public JwtService(JwtProperties properties) {
        this.clock = Clock.systemUTC();
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        this.expirationMinutes = properties.expirationMinutes();
    }

    public TokenGenerado generar(Usuario usuario) {
        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        String token = Jwts.builder()
                .setSubject(usuario.getId().toString())
                .claim("nombre", usuario.getNombre())
                .claim("rol", usuario.getRol().name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
        return new TokenGenerado(token, expiresAt);
    }

    public Optional<UsuarioAutenticado> validar(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Date expiration = claims.getExpiration();
            if (expiration == null || Instant.now(clock).isAfter(expiration.toInstant())) {
                return Optional.empty();
            }
            return Optional.of(new UsuarioAutenticado(
                    UUID.fromString(claims.getSubject()),
                    claims.get("nombre", String.class),
                    RolUsuario.valueOf(claims.get("rol", String.class)),
                    expiration.toInstant()
            ));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public record TokenGenerado(String valor, Instant expiresAt) {
    }
}
