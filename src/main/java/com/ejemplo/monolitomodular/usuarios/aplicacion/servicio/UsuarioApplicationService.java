package com.ejemplo.monolitomodular.usuarios.aplicacion.servicio;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.aplicacion.dto.CrearUsuarioCommand;
import com.ejemplo.monolitomodular.usuarios.aplicacion.dto.UsuarioView;
import com.ejemplo.monolitomodular.usuarios.aplicacion.puerto.entrada.GestionarUsuarioUseCase;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UsuarioApplicationService implements GestionarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioApplicationService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioView crear(CrearUsuarioCommand command) {
        validarContrasenaPlano(command.contrasena());
        usuarioRepository.buscarPorNombre(command.nombre())
                .ifPresent(usuario -> {
                    throw new DomainException("Ya existe un usuario con ese nombre");
                });

        Usuario usuario = Usuario.nuevo(
                command.nombre(),
                passwordEncoder.encode(command.contrasena()),
                command.rol()
        );

        return toView(usuarioRepository.guardar(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioView> listar() {
        return usuarioRepository.listar().stream()
                .sorted(Comparator.comparing(Usuario::getNombre, String.CASE_INSENSITIVE_ORDER))
                .map(this::toView)
                .toList();
    }

    @Override
    public UsuarioView activar(UUID id) {
        Usuario usuario = obtener(id);
        return toView(usuarioRepository.guardar(usuario.activar()));
    }

    @Override
    public UsuarioView desactivar(UUID id) {
        Usuario usuario = obtener(id);
        if (usuario.getRol() == RolUsuario.ADMINISTRADOR && usuario.isActivo()
                && usuarioRepository.contarAdministradoresActivos() <= 1) {
            throw new DomainException("No se puede desactivar el ultimo Administrador activo");
        }
        return toView(usuarioRepository.guardar(usuario.desactivar()));
    }

    private Usuario obtener(UUID id) {
        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Usuario no encontrado"));
    }

    private void validarContrasenaPlano(String contrasena) {
        if (contrasena == null || contrasena.isBlank()) {
            throw new DomainException("La contrasena es obligatoria");
        }
        if (contrasena.length() < 6) {
            throw new DomainException("La contrasena debe tener minimo 6 caracteres");
        }
    }

    private UsuarioView toView(Usuario usuario) {
        return new UsuarioView(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getRol(),
                usuario.isActivo()
        );
    }
}
