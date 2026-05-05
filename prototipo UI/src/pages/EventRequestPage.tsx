import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import clientesApi from '@/api/clientes';
import eventosApi from '@/api/eventos';
import catalogosApi from '@/api/catalogos';
import salonesApi from '@/api/salones';
import type { ClienteResponse, SalonResponse, CatalogoBasicoResponse } from '@/api/types';

const labelClass =
  'block text-[10px] font-bold uppercase tracking-[0.15em] text-on-surface-variant mb-2';

const inputClass =
  'w-full bg-surface-container-lowest border border-outline-variant/30 px-4 py-2.5 text-sm rounded-md shadow-sm focus:border-primary-gold focus:ring-1 focus:ring-primary-gold/20';

const EventRequestPage: React.FC = () => {
  const navigate = useNavigate();

  const [customerQuery, setCustomerQuery] = useState('');
  const [selectedVenueId, setSelectedVenueId] = useState('');
  const [salones, setSalones] = useState<SalonResponse[]>([]);
  const [tiposEvento, setTiposEvento] = useState<CatalogoBasicoResponse[]>([]);
  const [tiposComida, setTiposComida] = useState<CatalogoBasicoResponse[]>([]);
  const [clienteEncontrado, setClienteEncontrado] = useState<ClienteResponse | null>(null);
  const [fechaHoraInicio, setFechaHoraInicio] = useState('');
  const [fechaHoraFin, setFechaHoraFin] = useState('');
  const [numPersonas, setNumPersonas] = useState(0);
  const [tipoEventoId, setTipoEventoId] = useState('');
  const [tipoComidaId, setTipoComidaId] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Carga catálogos y salones al montar
  useEffect(() => {
    Promise.all([
      catalogosApi.tiposEvento.listar(),
      catalogosApi.tiposComida.listar(),
      salonesApi.listar(),
    ]).then(([te, tc, sl]) => {
      setTiposEvento(te.filter((t) => t.activo));
      setTiposComida(tc.filter((t) => t.activo));
      setSalones(sl.filter((s) => s.activo));
      if (te.length > 0) setTipoEventoId(te[0]!.id);
      if (tc.length > 0) setTipoComidaId(tc[0]!.id);
      if (sl.length > 0) setSelectedVenueId(sl[0]!.id);
    }).catch(() => {
      // Si el backend no está disponible, continúa con listas vacías
    });
  }, []);

  // Búsqueda de cliente con debounce
  useEffect(() => {
    const q = customerQuery.trim();
    if (!q) { setClienteEncontrado(null); return; }
    const timer = setTimeout(async () => {
      try {
        const results = await clientesApi.listar(q);
        setClienteEncontrado(results[0] ?? null);
      } catch {
        setClienteEncontrado(null);
      }
    }, 400);
    return () => clearTimeout(timer);
  }, [customerQuery]);

  const selectedVenue = salones.find((s) => s.id === selectedVenueId);

  const toLocalDateTime = (value: string) => (value.length === 16 ? `${value}:00` : value);

  const consultarDisponibilidad = async () => {
    if (!fechaHoraInicio || !fechaHoraFin) {
      setError('Selecciona fecha/hora de inicio y fecha/hora fin para consultar disponibilidad.');
      return;
    }

    if (new Date(fechaHoraFin) <= new Date(fechaHoraInicio)) {
      setError('La fecha/hora fin debe ser posterior a la fecha/hora de inicio.');
      return;
    }

    try {
      setError(null);
      const disponibles = await salonesApi.consultarDisponibilidad({
        fechaHoraInicio: toLocalDateTime(fechaHoraInicio),
        fechaHoraFin: toLocalDateTime(fechaHoraFin),
        capacidadMinima: numPersonas || undefined,
      });
      const activos = disponibles.filter((s) => s.activo);
      setSalones(activos);
      setSelectedVenueId(activos[0]?.id ?? '');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error consultando disponibilidad.');
    }
  };

  const handleCrearEvento = async () => {
    if (!clienteEncontrado || !fechaHoraInicio || !fechaHoraFin || !selectedVenueId || !tipoEventoId || !tipoComidaId) {
      setError('Completa todos los campos obligatorios antes de continuar.');
      return;
    }

    if (new Date(fechaHoraFin) <= new Date(fechaHoraInicio)) {
      setError('La fecha/hora fin debe ser posterior a la fecha/hora de inicio.');
      return;
    }

    const inicio = toLocalDateTime(fechaHoraInicio);
    const fin = toLocalDateTime(fechaHoraFin);

    try {
      setSaving(true);
      setError(null);

      const evento = await eventosApi.crear({
        clienteId: clienteEncontrado.id,
        tipoEventoId,
        tipoComidaId,
        fechaHoraInicio: inicio,
        fechaHoraFin: fin,
      });

      // Crear reserva de salón
      await eventosApi.crearReserva(evento.id, {
        salonId: selectedVenueId,
        numInvitados: numPersonas || 1,
        fechaHoraInicio: inicio,
        fechaHoraFin: fin,
      });

      navigate(`/events/${evento.id}/menu`);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al crear el evento.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <section className="space-y-8 pb-36">
      <div>
        <p className="text-primary-gold tracking-widest text-xs uppercase mb-2">Solicitud de evento</p>
        <h1 className="text-2xl font-display font-bold text-on-surface">Crear solicitud de evento</h1>
        <p className="text-sm text-on-surface-variant mt-1">
          Registra cliente, horario y salón antes de completar menú, montaje y cotización.
        </p>
      </div>

      {error && (
        <div className="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      <div className="grid grid-cols-1 xl:grid-cols-[1fr_320px] gap-6">
        <div className="space-y-6">
          <section className="bg-surface-container-low p-6 rounded-lg border border-border">
            <div className="flex items-baseline gap-4 mb-5">
              <h2 className="text-lg font-display font-bold text-on-surface">Cliente</h2>
              <div className="flex-1 h-px bg-stone-200"></div>
            </div>

            <div className="space-y-5">
              <div className="w-full max-w-2xl">
                <label className={labelClass}>Búsqueda de socio / cliente</label>
                <div className="relative">
                  <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-primary-gold text-sm">
                    person_search
                  </span>
                  <input
                    className={`${inputClass} pl-10`}
                    placeholder="Buscar por nombre, cédula o teléfono"
                    type="text"
                    value={customerQuery}
                    onChange={(e) => setCustomerQuery(e.target.value)}
                  />
                </div>
              </div>

              {clienteEncontrado ? (
                <div className="flex items-center justify-between p-4 bg-primary-gold/10 border border-primary-gold/30 rounded-md">
                  <div className="flex items-center gap-3">
                    <span className="material-symbols-outlined text-primary-gold">person</span>
                    <div>
                      <p className="font-semibold text-on-surface">{clienteEncontrado.nombreCompleto}</p>
                      <span className="text-[10px] uppercase tracking-widest font-bold px-2 py-0.5 bg-primary-gold text-white rounded-full">
                        {clienteEncontrado.tipoCliente === 'SOCIO' ? 'Socio' : 'No Socio'}
                      </span>
                    </div>
                  </div>
                  <button className="text-on-surface-variant hover:text-primary-gold transition-colors" type="button" onClick={() => setClienteEncontrado(null)}>
                    <span className="material-symbols-outlined">close</span>
                  </button>
                </div>
              ) : (
                <button
                  type="button"
                  className="flex items-center gap-2 text-primary-gold font-bold text-sm hover:underline transition-all group"
                >
                  <span className="material-symbols-outlined text-lg group-hover:scale-110 transition-transform">
                    add_circle
                  </span>
                  Registrar nuevo cliente
                </button>
              )}
            </div>
          </section>

          <section className="bg-surface-container-low p-6 rounded-lg border border-border">
            <div className="flex items-baseline gap-4 mb-5">
              <h2 className="text-lg font-display font-bold text-on-surface">Detalles del evento</h2>
              <div className="flex-1 h-px bg-stone-200"></div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-x-6 gap-y-5">
              <div>
                <label className={labelClass}>Fecha y hora inicio</label>
                <input className={inputClass} type="datetime-local" value={fechaHoraInicio} onChange={(e) => setFechaHoraInicio(e.target.value)} />
              </div>

              <div>
                <label className={labelClass}>Fecha y hora fin</label>
                <input className={inputClass} type="datetime-local" value={fechaHoraFin} onChange={(e) => setFechaHoraFin(e.target.value)} />
              </div>

              <div>
                <label className={labelClass}>Número de personas</label>
                <input className={inputClass} placeholder="0" type="number" min={1} value={numPersonas || ''} onChange={(e) => setNumPersonas(Number(e.target.value))} />
              </div>

              <div>
                <label className={labelClass}>Tipo de evento</label>
                <select className={inputClass} value={tipoEventoId} onChange={(e) => setTipoEventoId(e.target.value)}>
                  {tiposEvento.length === 0 && <option value="">Cargando…</option>}
                  {tiposEvento.map((t) => (
                    <option key={t.id} value={t.id}>{t.nombre}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className={labelClass}>Tipo de comida</label>
                <select className={inputClass} value={tipoComidaId} onChange={(e) => setTipoComidaId(e.target.value)}>
                  {tiposComida.length === 0 && <option value="">Cargando…</option>}
                  {tiposComida.map((t) => (
                    <option key={t.id} value={t.id}>{t.nombre}</option>
                  ))}
                </select>
              </div>
            </div>
          </section>

          <section className="bg-surface-container-low p-6 rounded-lg border border-border">
            <div className="flex items-baseline gap-4 mb-5">
              <h2 className="text-lg font-display font-bold text-on-surface">Selección de salón</h2>
              <div className="flex-1 h-px bg-stone-200"></div>
              <button
                type="button"
                onClick={consultarDisponibilidad}
                className="text-xs font-bold text-primary-gold hover:underline"
              >
                Consultar disponibilidad
              </button>
            </div>

            {salones.length === 0 ? (
              <p className="text-sm text-on-surface-variant">Cargando salones…</p>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                {salones.map((salon) => {
                  const isSelected = selectedVenueId === salon.id;
                  return (
                    <button
                      key={salon.id}
                      type="button"
                      onClick={() => setSelectedVenueId(salon.id)}
                      className={`relative p-4 text-left rounded-lg border transition-colors ${
                        isSelected
                          ? 'bg-gold-bg border-gold shadow-sm'
                          : 'bg-surface-container-lowest border-border hover:border-gold/50'
                      }`}
                    >
                      <div className="flex items-start justify-between gap-3">
                        <div>
                          <h3 className="text-sm font-bold text-on-surface">{salon.nombre}</h3>
                          <p className="text-xs text-on-surface-variant mt-1">Hasta {salon.capacidad} personas</p>
                          {salon.descripcion && (
                            <p className="text-xs text-on-surface-variant mt-2 leading-snug">{salon.descripcion}</p>
                          )}
                        </div>
                        {isSelected ? (
                          <span className="material-symbols-outlined text-primary-gold">check_circle</span>
                        ) : null}
                      </div>
                      <div className="mt-3">
                        <span className="inline-flex items-center gap-1.5 rounded-full px-2 py-1 text-[10px] font-bold bg-green-bg text-green-text">
                          <span className="w-1.5 h-1.5 rounded-full bg-green"></span>
                          Disponible
                        </span>
                      </div>
                    </button>
                  );
                })}
              </div>
            )}
          </section>
        </div>

        <aside className="bg-surface-container-lowest border border-border rounded-lg p-5 h-fit sticky top-24 space-y-4">
          <h3 className="font-display font-bold text-lg text-on-surface">Resumen de solicitud</h3>
          <div className="space-y-3 text-sm">
            <div>
              <p className="text-xs uppercase tracking-wider text-text3 font-bold">Cliente</p>
              <p className="font-semibold text-text1">{clienteEncontrado?.nombreCompleto ?? 'Sin cliente seleccionado'}</p>
            </div>
            <div>
              <p className="text-xs uppercase tracking-wider text-text3 font-bold">Salón</p>
              <p className="font-semibold text-text1">{selectedVenue?.nombre ?? 'Sin salón'}</p>
              <p className="text-xs text-text3">{selectedVenue ? `Hasta ${selectedVenue.capacidad} personas` : ''}</p>
            </div>
          </div>
        </aside>
      </div>

      <footer className="fixed bottom-0 right-0 w-[calc(100%-16rem)] bg-white/90 backdrop-blur-md border-t border-outline-variant/30 py-4 px-8 flex items-center justify-between z-30">
        <button
          type="button"
          onClick={() => navigate('/events')}
          className="text-sm font-bold text-on-surface hover:bg-hover bg-[#f5f2ed] border border-outline-variant px-5 py-2.5 rounded-md transition-all flex items-center gap-2 active:scale-95"
        >
          <span className="material-symbols-outlined text-xl">close</span>
          Cancelar
        </button>

        <div className="flex items-center gap-4">
          <p className="text-xs text-on-surface-variant max-w-[260px] text-right leading-tight">
            Se creará el evento en estado Pendiente para continuar con menú y cotización.
          </p>
          <button
            type="button"
            onClick={handleCrearEvento}
            disabled={saving}
            className="bg-primary-gold text-white px-6 py-3 rounded-md font-bold flex items-center gap-3 hover:bg-primary transition-all shadow-sm active:scale-95 disabled:opacity-50"
          >
            {saving ? 'Creando…' : 'Crear evento y continuar'}
            <span className="material-symbols-outlined">chevron_right</span>
          </button>
        </div>
      </footer>
    </section>
  );
};

export default EventRequestPage;
