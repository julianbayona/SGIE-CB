package com.ejemplo.monolitomodular.salones.aplicacion.servicio;

import com.ejemplo.monolitomodular.salones.aplicacion.dto.RegistrarSalonCommand;
import com.ejemplo.monolitomodular.salones.aplicacion.dto.SalonView;
import com.ejemplo.monolitomodular.salones.aplicacion.puerto.entrada.ConsultarSalonUseCase;
import com.ejemplo.monolitomodular.salones.aplicacion.puerto.entrada.RegistrarSalonUseCase;
import com.ejemplo.monolitomodular.salones.dominio.modelo.Salon;
import com.ejemplo.monolitomodular.salones.dominio.puerto.salida.SalonRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SalonApplicationService implements RegistrarSalonUseCase, ConsultarSalonUseCase {

    private final SalonRepository salonRepository;

    public SalonApplicationService(SalonRepository salonRepository) {
        this.salonRepository = salonRepository;
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

    private SalonView toView(Salon salon) {
        return new SalonView(
                salon.getId(),
                salon.getNombre(),
                salon.getCapacidad(),
                salon.getDescripcion(),
                salon.isActivo()
        );
    }
}
