package com.ejemplo.monolitomodular.catalogos.aplicacion.servicio;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoConColorCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoConColorView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.ColorCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.ColorView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.TipoAdicionalCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.TipoAdicionalView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarColorUseCase;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarMantelUseCase;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarSobremantelUseCase;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarTipoAdicionalUseCase;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarTipoComidaUseCase;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarTipoEventoUseCase;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarTipoMesaUseCase;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarTipoSillaUseCase;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Color;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Mantel;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Sobremantel;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoAdicional;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoComida;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoEvento;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoMesa;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoSilla;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.ColorRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.MantelRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.SobremantelRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoAdicionalRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoComidaRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoEventoRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoMesaRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoSillaRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CatalogoApplicationService implements
        GestionarTipoEventoUseCase,
        GestionarTipoComidaUseCase,
        GestionarColorUseCase,
        GestionarTipoMesaUseCase,
        GestionarTipoSillaUseCase,
        GestionarMantelUseCase,
        GestionarSobremantelUseCase,
        GestionarTipoAdicionalUseCase {

    private final TipoEventoRepository tipoEventoRepository;
    private final TipoComidaRepository tipoComidaRepository;
    private final ColorRepository colorRepository;
    private final TipoMesaRepository tipoMesaRepository;
    private final TipoSillaRepository tipoSillaRepository;
    private final MantelRepository mantelRepository;
    private final SobremantelRepository sobremantelRepository;
    private final TipoAdicionalRepository tipoAdicionalRepository;

    public CatalogoApplicationService(
            TipoEventoRepository tipoEventoRepository,
            TipoComidaRepository tipoComidaRepository,
            ColorRepository colorRepository,
            TipoMesaRepository tipoMesaRepository,
            TipoSillaRepository tipoSillaRepository,
            MantelRepository mantelRepository,
            SobremantelRepository sobremantelRepository,
            TipoAdicionalRepository tipoAdicionalRepository
    ) {
        this.tipoEventoRepository = tipoEventoRepository;
        this.tipoComidaRepository = tipoComidaRepository;
        this.colorRepository = colorRepository;
        this.tipoMesaRepository = tipoMesaRepository;
        this.tipoSillaRepository = tipoSillaRepository;
        this.mantelRepository = mantelRepository;
        this.sobremantelRepository = sobremantelRepository;
        this.tipoAdicionalRepository = tipoAdicionalRepository;
    }

    @Override
    public CatalogoBasicoView crearTipoEvento(CatalogoBasicoCommand command) {
        validarNombreDisponibleTipoEvento(command.nombre());
        return toView(tipoEventoRepository.guardar(TipoEvento.nuevo(command.nombre(), command.descripcion())));
    }

    @Override
    public CatalogoBasicoView actualizarTipoEvento(UUID id, CatalogoBasicoCommand command) {
        TipoEvento tipoEvento = tipoEventoRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo de evento no encontrado"));
        validarNombreDisponibleTipoEvento(command.nombre(), tipoEvento.getNombre());
        return toView(tipoEventoRepository.guardar(tipoEvento.actualizar(command.nombre(), command.descripcion())));
    }

    @Override
    public CatalogoBasicoView desactivarTipoEvento(UUID id) {
        TipoEvento tipoEvento = tipoEventoRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo de evento no encontrado"));
        return toView(tipoEventoRepository.guardar(tipoEvento.desactivar()));
    }

    @Override
    public CatalogoBasicoView obtenerTipoEvento(UUID id) {
        return tipoEventoRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Tipo de evento no encontrado"));
    }

    @Override
    public List<CatalogoBasicoView> listarTiposEvento() {
        return tipoEventoRepository.listar().stream().map(this::toView).toList();
    }

    @Override
    public CatalogoBasicoView crearTipoComida(CatalogoBasicoCommand command) {
        validarNombreDisponibleTipoComida(command.nombre());
        return toView(tipoComidaRepository.guardar(TipoComida.nuevo(command.nombre(), command.descripcion())));
    }

    @Override
    public CatalogoBasicoView actualizarTipoComida(UUID id, CatalogoBasicoCommand command) {
        TipoComida tipoComida = tipoComidaRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo de comida no encontrado"));
        validarNombreDisponibleTipoComida(command.nombre(), tipoComida.getNombre());
        return toView(tipoComidaRepository.guardar(tipoComida.actualizar(command.nombre(), command.descripcion())));
    }

    @Override
    public CatalogoBasicoView desactivarTipoComida(UUID id) {
        TipoComida tipoComida = tipoComidaRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo de comida no encontrado"));
        return toView(tipoComidaRepository.guardar(tipoComida.desactivar()));
    }

    @Override
    public CatalogoBasicoView obtenerTipoComida(UUID id) {
        return tipoComidaRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Tipo de comida no encontrado"));
    }

    @Override
    public List<CatalogoBasicoView> listarTiposComida() {
        return tipoComidaRepository.listar().stream().map(this::toView).toList();
    }

    @Override
    public ColorView crearColor(ColorCommand command) {
        validarNombreDisponibleColor(command.nombre());
        return toView(colorRepository.guardar(Color.nuevo(command.nombre(), command.codigoHex())));
    }

    @Override
    public ColorView actualizarColor(UUID id, ColorCommand command) {
        Color color = colorRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Color no encontrado"));
        validarNombreDisponibleColor(command.nombre(), color.getNombre());
        return toView(colorRepository.guardar(color.actualizar(command.nombre(), command.codigoHex())));
    }

    @Override
    public ColorView desactivarColor(UUID id) {
        Color color = colorRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Color no encontrado"));
        return toView(colorRepository.guardar(color.desactivar()));
    }

    @Override
    public ColorView obtenerColor(UUID id) {
        return colorRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Color no encontrado"));
    }

    @Override
    public List<ColorView> listarColores() {
        return colorRepository.listar().stream().map(this::toView).toList();
    }

    @Override
    public CatalogoBasicoView crearTipoMesa(CatalogoBasicoCommand command) {
        validarNombreDisponibleTipoMesa(command.nombre());
        return toView(tipoMesaRepository.guardar(TipoMesa.nuevo(command.nombre())));
    }

    @Override
    public CatalogoBasicoView actualizarTipoMesa(UUID id, CatalogoBasicoCommand command) {
        TipoMesa tipoMesa = tipoMesaRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo de mesa no encontrado"));
        validarNombreDisponibleTipoMesa(command.nombre(), tipoMesa.getNombre());
        return toView(tipoMesaRepository.guardar(tipoMesa.actualizar(command.nombre())));
    }

    @Override
    public CatalogoBasicoView desactivarTipoMesa(UUID id) {
        TipoMesa tipoMesa = tipoMesaRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo de mesa no encontrado"));
        return toView(tipoMesaRepository.guardar(tipoMesa.desactivar()));
    }

    @Override
    public CatalogoBasicoView obtenerTipoMesa(UUID id) {
        return tipoMesaRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Tipo de mesa no encontrado"));
    }

    @Override
    public List<CatalogoBasicoView> listarTiposMesa() {
        return tipoMesaRepository.listar().stream().map(this::toView).toList();
    }

    @Override
    public CatalogoBasicoView crearTipoSilla(CatalogoBasicoCommand command) {
        validarNombreDisponibleTipoSilla(command.nombre());
        return toView(tipoSillaRepository.guardar(TipoSilla.nuevo(command.nombre())));
    }

    @Override
    public CatalogoBasicoView actualizarTipoSilla(UUID id, CatalogoBasicoCommand command) {
        TipoSilla tipoSilla = tipoSillaRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo de silla no encontrado"));
        validarNombreDisponibleTipoSilla(command.nombre(), tipoSilla.getNombre());
        return toView(tipoSillaRepository.guardar(tipoSilla.actualizar(command.nombre())));
    }

    @Override
    public CatalogoBasicoView desactivarTipoSilla(UUID id) {
        TipoSilla tipoSilla = tipoSillaRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo de silla no encontrado"));
        return toView(tipoSillaRepository.guardar(tipoSilla.desactivar()));
    }

    @Override
    public CatalogoBasicoView obtenerTipoSilla(UUID id) {
        return tipoSillaRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Tipo de silla no encontrado"));
    }

    @Override
    public List<CatalogoBasicoView> listarTiposSilla() {
        return tipoSillaRepository.listar().stream().map(this::toView).toList();
    }

    @Override
    public CatalogoConColorView crearMantel(CatalogoConColorCommand command) {
        validarColorActivo(command.colorId());
        validarNombreDisponibleMantel(command.nombre());
        return toView(mantelRepository.guardar(Mantel.nuevo(command.nombre(), command.colorId())));
    }

    @Override
    public CatalogoConColorView actualizarMantel(UUID id, CatalogoConColorCommand command) {
        Mantel mantel = mantelRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Mantel no encontrado"));
        validarColorActivo(command.colorId());
        validarNombreDisponibleMantel(command.nombre(), mantel.getNombre());
        return toView(mantelRepository.guardar(mantel.actualizar(command.nombre(), command.colorId())));
    }

    @Override
    public CatalogoConColorView desactivarMantel(UUID id) {
        Mantel mantel = mantelRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Mantel no encontrado"));
        return toView(mantelRepository.guardar(mantel.desactivar()));
    }

    @Override
    public CatalogoConColorView obtenerMantel(UUID id) {
        return mantelRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Mantel no encontrado"));
    }

    @Override
    public List<CatalogoConColorView> listarManteles() {
        return mantelRepository.listar().stream().map(this::toView).toList();
    }

    @Override
    public CatalogoConColorView crearSobremantel(CatalogoConColorCommand command) {
        validarColorActivo(command.colorId());
        validarNombreDisponibleSobremantel(command.nombre());
        return toView(sobremantelRepository.guardar(Sobremantel.nuevo(command.nombre(), command.colorId())));
    }

    @Override
    public CatalogoConColorView actualizarSobremantel(UUID id, CatalogoConColorCommand command) {
        Sobremantel sobremantel = sobremantelRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Sobremantel no encontrado"));
        validarColorActivo(command.colorId());
        validarNombreDisponibleSobremantel(command.nombre(), sobremantel.getNombre());
        return toView(sobremantelRepository.guardar(sobremantel.actualizar(command.nombre(), command.colorId())));
    }

    @Override
    public CatalogoConColorView desactivarSobremantel(UUID id) {
        Sobremantel sobremantel = sobremantelRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Sobremantel no encontrado"));
        return toView(sobremantelRepository.guardar(sobremantel.desactivar()));
    }

    @Override
    public CatalogoConColorView obtenerSobremantel(UUID id) {
        return sobremantelRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Sobremantel no encontrado"));
    }

    @Override
    public List<CatalogoConColorView> listarSobremanteles() {
        return sobremantelRepository.listar().stream().map(this::toView).toList();
    }

    @Override
    public TipoAdicionalView crearTipoAdicional(TipoAdicionalCommand command) {
        validarNombreDisponibleTipoAdicional(command.nombre());
        return toView(tipoAdicionalRepository.guardar(
                TipoAdicional.nuevo(command.nombre(), command.modoCobro(), command.precioBase())
        ));
    }

    @Override
    public TipoAdicionalView actualizarTipoAdicional(UUID id, TipoAdicionalCommand command) {
        TipoAdicional tipoAdicional = tipoAdicionalRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo adicional no encontrado"));
        validarNombreDisponibleTipoAdicional(command.nombre(), tipoAdicional.getNombre());
        return toView(tipoAdicionalRepository.guardar(
                tipoAdicional.actualizar(command.nombre(), command.modoCobro(), command.precioBase())
        ));
    }

    @Override
    public TipoAdicionalView desactivarTipoAdicional(UUID id) {
        TipoAdicional tipoAdicional = tipoAdicionalRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Tipo adicional no encontrado"));
        return toView(tipoAdicionalRepository.guardar(tipoAdicional.desactivar()));
    }

    @Override
    public TipoAdicionalView obtenerTipoAdicional(UUID id) {
        return tipoAdicionalRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Tipo adicional no encontrado"));
    }

    @Override
    public List<TipoAdicionalView> listarTiposAdicional() {
        return tipoAdicionalRepository.listar().stream().map(this::toView).toList();
    }

    private void validarNombreDisponibleTipoEvento(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        if (tipoEventoRepository.existePorNombre(nombre)) {
            throw new DomainException("Ya existe un tipo de evento con el nombre indicado");
        }
    }

    private void validarNombreDisponibleTipoEvento(String nombre, String nombreActual) {
        if (nombre == null || !nombreActual.equalsIgnoreCase(nombre.trim())) {
            validarNombreDisponibleTipoEvento(nombre);
        }
    }

    private void validarNombreDisponibleTipoComida(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        if (tipoComidaRepository.existePorNombre(nombre)) {
            throw new DomainException("Ya existe un tipo de comida con el nombre indicado");
        }
    }

    private void validarNombreDisponibleTipoComida(String nombre, String nombreActual) {
        if (nombre == null || !nombreActual.equalsIgnoreCase(nombre.trim())) {
            validarNombreDisponibleTipoComida(nombre);
        }
    }

    private void validarNombreDisponibleColor(String nombre) {
        if (nombre != null && !nombre.isBlank() && colorRepository.existePorNombre(nombre)) {
            throw new DomainException("Ya existe un color con el nombre indicado");
        }
    }

    private void validarNombreDisponibleColor(String nombre, String nombreActual) {
        if (nombre == null || !nombreActual.equalsIgnoreCase(nombre.trim())) {
            validarNombreDisponibleColor(nombre);
        }
    }

    private void validarNombreDisponibleTipoSilla(String nombre) {
        if (nombre != null && !nombre.isBlank() && tipoSillaRepository.existePorNombre(nombre)) {
            throw new DomainException("Ya existe un tipo de silla con el nombre indicado");
        }
    }

    private void validarNombreDisponibleTipoMesa(String nombre) {
        if (nombre != null && !nombre.isBlank() && tipoMesaRepository.existePorNombre(nombre)) {
            throw new DomainException("Ya existe un tipo de mesa con el nombre indicado");
        }
    }

    private void validarNombreDisponibleTipoMesa(String nombre, String nombreActual) {
        if (nombre == null || !nombreActual.equalsIgnoreCase(nombre.trim())) {
            validarNombreDisponibleTipoMesa(nombre);
        }
    }

    private void validarNombreDisponibleTipoSilla(String nombre, String nombreActual) {
        if (nombre == null || !nombreActual.equalsIgnoreCase(nombre.trim())) {
            validarNombreDisponibleTipoSilla(nombre);
        }
    }

    private void validarNombreDisponibleMantel(String nombre) {
        if (nombre != null && !nombre.isBlank() && mantelRepository.existePorNombre(nombre)) {
            throw new DomainException("Ya existe un mantel con el nombre indicado");
        }
    }

    private void validarNombreDisponibleMantel(String nombre, String nombreActual) {
        if (nombre == null || !nombreActual.equalsIgnoreCase(nombre.trim())) {
            validarNombreDisponibleMantel(nombre);
        }
    }

    private void validarNombreDisponibleSobremantel(String nombre) {
        if (nombre != null && !nombre.isBlank() && sobremantelRepository.existePorNombre(nombre)) {
            throw new DomainException("Ya existe un sobremantel con el nombre indicado");
        }
    }

    private void validarNombreDisponibleSobremantel(String nombre, String nombreActual) {
        if (nombre == null || !nombreActual.equalsIgnoreCase(nombre.trim())) {
            validarNombreDisponibleSobremantel(nombre);
        }
    }

    private void validarColorActivo(UUID colorId) {
        if (!colorRepository.existeActivoPorId(colorId)) {
            throw new DomainException("El color no existe o esta inactivo");
        }
    }

    private void validarNombreDisponibleTipoAdicional(String nombre) {
        if (nombre != null && !nombre.isBlank() && tipoAdicionalRepository.existePorNombre(nombre)) {
            throw new DomainException("Ya existe un tipo adicional con el nombre indicado");
        }
    }

    private void validarNombreDisponibleTipoAdicional(String nombre, String nombreActual) {
        if (nombre == null || !nombreActual.equalsIgnoreCase(nombre.trim())) {
            validarNombreDisponibleTipoAdicional(nombre);
        }
    }

    private CatalogoBasicoView toView(TipoEvento tipoEvento) {
        return new CatalogoBasicoView(
                tipoEvento.getId(),
                tipoEvento.getNombre(),
                tipoEvento.getDescripcion(),
                tipoEvento.isActivo()
        );
    }

    private CatalogoBasicoView toView(TipoComida tipoComida) {
        return new CatalogoBasicoView(
                tipoComida.getId(),
                tipoComida.getNombre(),
                tipoComida.getDescripcion(),
                tipoComida.isActivo()
        );
    }

    private ColorView toView(Color color) {
        return new ColorView(
                color.getId(),
                color.getNombre(),
                color.getCodigoHex(),
                color.isActivo()
        );
    }

    private CatalogoBasicoView toView(TipoSilla tipoSilla) {
        return new CatalogoBasicoView(
                tipoSilla.getId(),
                tipoSilla.getNombre(),
                "",
                tipoSilla.isActivo()
        );
    }

    private CatalogoBasicoView toView(TipoMesa tipoMesa) {
        return new CatalogoBasicoView(
                tipoMesa.getId(),
                tipoMesa.getNombre(),
                "",
                tipoMesa.isActivo()
        );
    }

    private CatalogoConColorView toView(Mantel mantel) {
        return new CatalogoConColorView(
                mantel.getId(),
                mantel.getNombre(),
                mantel.getColorId(),
                mantel.isActivo()
        );
    }

    private CatalogoConColorView toView(Sobremantel sobremantel) {
        return new CatalogoConColorView(
                sobremantel.getId(),
                sobremantel.getNombre(),
                sobremantel.getColorId(),
                sobremantel.isActivo()
        );
    }

    private TipoAdicionalView toView(TipoAdicional tipoAdicional) {
        return new TipoAdicionalView(
                tipoAdicional.getId(),
                tipoAdicional.getNombre(),
                tipoAdicional.getModoCobro(),
                tipoAdicional.getPrecioBase(),
                tipoAdicional.isActivo()
        );
    }
}
