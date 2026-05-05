package com.ejemplo.monolitomodular.auth.aplicacion.servicio;

import com.ejemplo.monolitomodular.auth.aplicacion.dto.AuthResponse;
import com.ejemplo.monolitomodular.auth.aplicacion.dto.LoginCommand;
import com.ejemplo.monolitomodular.auth.aplicacion.puerto.entrada.LoginUseCase;
import com.ejemplo.monolitomodular.auth.infraestructura.seguridad.JwtService;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthApplicationService implements LoginUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthApplicationService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginCommand command) {
        Usuario usuario = usuarioRepository.buscarPorNombre(command.nombre())
                .orElseThrow(() -> new DomainException("Credenciales invalidas"));
        if (!usuario.isActivo() || !passwordEncoder.matches(command.contrasena(), usuario.getContrasenaHash())) {
            throw new DomainException("Credenciales invalidas");
        }
        JwtService.TokenGenerado token = jwtService.generar(usuario);
        return new AuthResponse(
                "Bearer",
                token.valor(),
                token.expiresAt(),
                usuario.getId(),
                usuario.getNombre(),
                usuario.getRol()
        );
    }
}
