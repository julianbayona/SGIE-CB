package com.ejemplo.monolitomodular.salones.aplicacion.servicio;

import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.salones.aplicacion.dto.ConsultarDisponibilidadSalonesQuery;
import com.ejemplo.monolitomodular.salones.aplicacion.dto.RegistrarSalonCommand;
import com.ejemplo.monolitomodular.salones.aplicacion.dto.SalonView;
import com.ejemplo.monolitomodular.salones.aplicacion.puerto.entrada.ConsultarSalonUseCase;
import com.ejemplo.monolitomodular.salones.aplicacion.puerto.entrada.RegistrarSalonUseCase;
import com.ejemplo.monolitomodular.salones.dominio.modelo.Salon;
import com.ejemplo.monolitomodular.salones.dominio.puerto.salida.SalonRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class SalonApplicationService implements RegistrarSalonUseCase, ConsultarSalonUseCase {

    private final SalonRepository salonRepository;
    private final ReservaSalonRepository reservaSalonRepository;

    public SalonApplicationService(
            SalonRepository salonRepository,
            ReservaSalonRepository reservaSalonRepository
    ) {
        this.salonRepository = salonRepository;
        this.reservaSalonRepository = reservaSalonRepository;
    }

    @Override
    public SalonView ejecutar(RegistrarSalonCommand command) {
        if (salonRepository.existePorNombre(command.nombre())) {
            throw new DomainException("Ya existe un salon con el nombre indicado");
        }

        return toView(salonRepository.guardar(
                Salon.nuevo(command.nombre(), command.capacidad(), command.descripcion())
        ));
    }

    @Override
    public SalonView obtenerPorId(UUID id) {
        return salonRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Salon no encontrado"));
    }

    @Override
    public List<SalonView> listar() {
        return salonRepository.listar().stream()
                .map(this::toView)
                .toList();
    }

    @Override
    public List<SalonView> consultarDisponibilidad(ConsultarDisponibilidadSalonesQuery query) {
        validarConsultaDisponibilidad(query);

        Set<UUID> salonesOcupados = reservaSalonRepository.buscarSalonesOcupados(
                query.fechaHoraInicio(),
                query.fechaHoraFin()
        );

        return salonRepository.listar().stream()
                .filter(Salon::isActivo)
                .filter(salon -> query.capacidadMinima() == null || salon.getCapacidad() >= query.capacidadMinima())
                .filter(salon -> !salonesOcupados.contains(salon.getId()))
                .map(this::toView)
                .toList();
    }

    private SalonView toView(Salon salon) {
        return new SalonView(
                salon.getId(),
                salon.getNombre(),
                salon.getCapacidad(),
                salon.getDescripcion(),
                salon.isActivo()
        );
    }

    private void validarConsultaDisponibilidad(ConsultarDisponibilidadSalonesQuery query) {
        if (query.fechaHoraInicio() == null) {
            throw new DomainException("La fecha y hora de inicio es obligatoria");
        }
        if (query.fechaHoraFin() == null) {
            throw new DomainException("La fecha y hora de fin es obligatoria");
        }
        if (!query.fechaHoraFin().isAfter(query.fechaHoraInicio())) {
            throw new DomainException("La fecha y hora de fin debe ser posterior a la fecha y hora de inicio");
        }
        if (query.capacidadMinima() != null && query.capacidadMinima() <= 0) {
            throw new DomainException("La capacidad minima debe ser mayor a cero");
        }
    }
}
