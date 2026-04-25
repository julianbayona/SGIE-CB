package com.ejemplo.monolitomodular.eventos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservaSalonRepository {

    List<ReservaSalon> guardarTodas(List<ReservaSalon> reservas);

    boolean existeConflicto(UUID salonId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<ReservaSalon> listarPorEvento(UUID eventoId);
}
