package com.ejemplo.monolitomodular.auth.infraestructura.seguridad;

import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtService {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final byte[] secret;
    private final long expirationMinutes;

    public JwtService(
            ObjectMapper objectMapper,
            @Value("${sgie.auth.jwt.secret}") String secret,
            @Value("${sgie.auth.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.objectMapper = objectMapper;
        this.clock = Clock.systemUTC();
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMinutes = expirationMinutes;
    }

    public TokenGenerado generar(Usuario usuario) {
        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", usuario.getId().toString());
        payload.put("nombre", usuario.getNombre());
        payload.put("rol", usuario.getRol().name());
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", expiresAt.getEpochSecond());
        String unsignedToken = base64Json(header) + "." + base64Json(payload);
        return new TokenGenerado(unsignedToken + "." + firmar(unsignedToken), expiresAt);
    }

    public Optional<UsuarioAutenticado> validar(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                return Optional.empty();
            }
            String unsignedToken = partes[0] + "." + partes[1];
            if (!firmar(unsignedToken).equals(partes[2])) {
                return Optional.empty();
            }
            Map<String, Object> payload = objectMapper.readValue(BASE64_URL_DECODER.decode(partes[1]), MAP_TYPE);
            Instant expiraEn = Instant.ofEpochSecond(((Number) payload.get("exp")).longValue());
            if (Instant.now(clock).isAfter(expiraEn)) {
                return Optional.empty();
            }
            return Optional.of(new UsuarioAutenticado(
                    UUID.fromString((String) payload.get("sub")),
                    (String) payload.get("nombre"),
                    RolUsuario.valueOf((String) payload.get("rol")),
                    expiraEn
            ));
        } catch (RuntimeException | java.io.IOException ex) {
            return Optional.empty();
        }
    }

    private String base64Json(Map<String, Object> value) {
        try {
            return BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (java.io.IOException ex) {
            throw new IllegalStateException("No se pudo construir el token JWT", ex);
        }
    }

    private String firmar(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return BASE64_URL_ENCODER.encodeToString(mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo firmar el token JWT", ex);
        }
    }

    public record TokenGenerado(String valor, Instant expiresAt) {
    }
}
