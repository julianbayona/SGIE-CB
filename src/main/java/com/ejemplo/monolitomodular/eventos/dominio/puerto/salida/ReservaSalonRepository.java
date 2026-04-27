package com.ejemplo.monolitomodular.eventos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ReservaSalonRepository {

    List<ReservaSalon> guardarTodas(List<ReservaSalon> reservas);

    ReservaSalon guardar(ReservaSalon reserva);

    boolean existeConflicto(UUID salonId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin);

    boolean existeConflicto(UUID salonId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, UUID reservaRaizIdExcluida);

    List<ReservaSalon> listarPorEvento(UUID eventoId);

    Set<UUID> buscarSalonesOcupados(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin);

    Optional<ReservaSalon> buscarVigentePorEventoYSalon(UUID eventoId, UUID salonId);

    Optional<ReservaSalon> buscarVigentePorRaizId(UUID reservaRaizId);

    void desactivarReservaVigente(UUID reservaRaizId);
}
