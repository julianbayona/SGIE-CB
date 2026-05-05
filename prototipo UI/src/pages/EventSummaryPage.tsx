import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import EventDetailHeaderTabs from '@/features/events/components/EventDetailHeaderTabs';
import eventosApi from '@/api/eventos';
import clientesApi from '@/api/clientes';
import salonesApi from '@/api/salones';
import catalogosApi from '@/api/catalogos';
import pagosApi from '@/api/pagos';
import type { EventoResponse, ClienteResponse, SalonResponse, CatalogoBasicoResponse, EstadoEvento } from '@/api/types';

const estadoLabels: Record<EstadoEvento, string> = {
  PENDIENTE: 'Pendiente',
  COTIZACION_ENVIADA: 'Cotización enviada',
  COTIZACION_APROBADA: 'Cotización aprobada',
  PENDIENTE_ANTICIPO: 'Pendiente anticipo',
  CONFIRMADO: 'Confirmado',
  CANCELADO: 'Cancelado',
};

const lifecycleSteps = [
  'PENDIENTE',
  'COTIZACION_ENVIADA',
  'COTIZACION_APROBADA',
  'PENDIENTE_ANTICIPO',
  'CONFIRMADO',
];

const EventSummaryPage: React.FC = () => {
  const navigate = useNavigate();
  const { eventId } = useParams();

  const [evento, setEvento] = useState<EventoResponse | null>(null);
  const [cliente, setCliente] = useState<ClienteResponse | null>(null);
  const [salon, setSalon] = useState<SalonResponse | null>(null);
  const [tipoEvento, setTipoEvento] = useState<CatalogoBasicoResponse | null>(null);
  const [valorTotal, setValorTotal] = useState(0);
  const [saldoPendiente, setSaldoPendiente] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!eventId) return;

    let cancelled = false;

    (async () => {
      try {
        setLoading(true);
        setError(null);

        // Cargar evento
        const eventoData = await eventosApi.obtenerPorId(eventId);
        if (cancelled) return;
        setEvento(eventoData);

        // Cargar datos relacionados en paralelo
        const reservaActual = eventoData.reservas.find(r => r.vigente);
        
        const [clienteData, tipoEventoData, salonData] = await Promise.all([
          clientesApi.obtenerPorId(eventoData.clienteId),
          catalogosApi.tiposEvento.obtenerPorId(eventoData.tipoEventoId),
          reservaActual ? salonesApi.obtenerPorId(reservaActual.salonId) : Promise.resolve(null),
        ]);

        if (cancelled) return;
        setCliente(clienteData);
        setTipoEvento(tipoEventoData);
        setSalon(salonData);

        try {
          const financiero = await pagosApi.estadoFinanciero(eventId);
          if (!cancelled) {
            setValorTotal(Number(financiero.valorTotal) || 0);
            setSaldoPendiente(Number(financiero.saldoPendiente) || 0);
          }
        } catch {
          if (!cancelled) {
            setValorTotal(0);
            setSaldoPendiente(0);
          }
        }
      } catch (err) {
        if (!cancelled) {
          setError(err instanceof Error ? err.message : 'Error al cargar evento');
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();

    return () => { cancelled = true; };
  }, [eventId]);

  const event = useMemo(() => {
    if (!evento) {
      return {
        id: eventId || '',
        title: 'Cargando...',
        dateLabel: '',
        timeLabel: '',
        status: 'Pendiente' as const,
        customerName: '',
        customerPhone: '',
        eventType: '',
        guests: 0,
        venue: '',
        venueCapacity: '',
        totalQuote: '$0',
      };
    }

    const reserva = evento.reservas.find(r => r.vigente);
    const inicio = new Date(evento.fechaHoraInicio);
    
    return {
      id: evento.id,
      title: `${tipoEvento?.nombre || 'Evento'} - ${cliente?.nombreCompleto || 'Cliente'}`,
      dateLabel: inicio.toLocaleDateString('es-CO', { day: '2-digit', month: 'long', year: 'numeric' }),
      timeLabel: `${inicio.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' })} - ${new Date(evento.fechaHoraFin).toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' })}`,
      status: estadoLabels[evento.estado] as any,
      customerName: cliente?.nombreCompleto || 'Cargando...',
      customerPhone: cliente?.telefono || '',
      eventType: tipoEvento?.nombre || 'Cargando...',
      guests: reserva?.numInvitados || 0,
      venue: salon?.nombre || 'Sin salón',
      venueCapacity: salon ? `Capacidad: ${salon.capacidad} pax` : '',
      totalQuote: new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(valorTotal),
    };
  }, [evento, cliente, salon, tipoEvento, eventId, valorTotal]);

  const currentStepIndex = evento ? lifecycleSteps.indexOf(evento.estado) : -1;

  if (loading) {
    return (
      <section className="space-y-8 pb-28">
        <div className="flex items-center justify-center py-16 text-on-surface-variant">
          Cargando información del evento...
        </div>
      </section>
    );
  }

  if (error) {
    return (
      <section className="space-y-8 pb-28">
        <div className="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      </section>
    );
  }

  return (
    <section className="space-y-8 pb-28">
      <EventDetailHeaderTabs event={event} activeTab="summary" />

      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
        <div className="bg-surface-container-lowest p-5 rounded-lg shadow-sm border border-border">
          <span className="text-xs text-stone-500 uppercase tracking-wider font-bold">Cliente principal</span>
          <p className="text-lg font-display font-bold mt-2">{event.customerName}</p>
          <p className="text-sm text-on-surface-variant mt-1">{event.customerPhone || 'Sin teléfono'}</p>
          {cliente && (
            <p className="text-xs text-on-surface-variant mt-1">{cliente.correo}</p>
          )}
        </div>

        <div className="bg-surface-container-lowest p-5 rounded-lg shadow-sm border border-border">
          <span className="text-xs text-stone-500 uppercase tracking-wider font-bold">Tipo de evento</span>
          <p className="text-lg font-display font-bold mt-2">{event.eventType}</p>
          <p className="text-sm text-on-surface-variant mt-1">{event.guests} invitados</p>
        </div>

        <div className="bg-surface-container-lowest p-5 rounded-lg shadow-sm border border-border">
          <span className="text-xs text-stone-500 uppercase tracking-wider font-bold">Salón reservado</span>
          <p className="text-lg font-display font-bold mt-2">{event.venue}</p>
          <p className="text-sm text-on-surface-variant mt-1">{event.venueCapacity}</p>
        </div>

        <div className="bg-surface-container-lowest p-5 rounded-lg shadow-sm border border-border">
          <span className="text-xs text-stone-500 uppercase tracking-wider font-bold">Total cotizado</span>
          <p className="text-lg font-display font-bold mt-2">{event.totalQuote}</p>
          <p className="text-sm text-on-surface-variant mt-1">
            Saldo: {new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(saldoPendiente)}
          </p>
        </div>
      </div>

      <section className="bg-surface-container-lowest p-6 rounded-lg shadow-sm border border-border">
        <div className="flex items-center justify-between gap-4 flex-wrap mb-6">
          <div>
            <h3 className="text-lg font-display font-bold text-on-surface">Estado del proceso</h3>
            <p className="text-sm text-on-surface-variant mt-1">Transiciones automáticas según acciones del usuario.</p>
          </div>
          <button
            type="button"
            onClick={() => navigate(`/events/${event.id}/pagos`)}
            className="bg-primary-gold text-white font-bold px-4 py-2.5 rounded-md shadow-sm hover:bg-primary transition-colors flex items-center gap-2"
          >
            <span className="material-symbols-outlined text-lg">payments</span>
            Registrar anticipo
          </button>
        </div>

        <div className="overflow-x-auto">
          <div className="relative flex justify-between items-start min-w-[760px]">
            <div className="absolute top-5 left-0 w-full h-0.5 bg-border -z-0"></div>
            {lifecycleSteps.map((step, index) => {
              const isCurrent = index === currentStepIndex;
              const isDone = currentStepIndex > index;

              return (
                <div key={step} className="relative z-10 flex flex-col items-center text-center w-32">
                  <div
                    className={`w-10 h-10 rounded-full flex items-center justify-center mb-3 border ${
                      isCurrent
                        ? 'bg-white border-primary-gold text-primary-gold ring-4 ring-primary-gold/15'
                        : isDone
                          ? 'bg-primary-gold border-primary-gold text-white'
                          : 'bg-surface-container-low border-border text-text3'
                    }`}
                  >
                    {isDone ? <span className="material-symbols-outlined text-lg">check</span> : null}
                    {isCurrent ? <div className="w-2.5 h-2.5 rounded-full bg-primary-gold"></div> : null}
                  </div>
                  <span className={`text-[11px] font-bold leading-tight ${isCurrent ? 'text-primary-gold' : 'text-on-surface-variant'}`}>
                    {estadoLabels[step as EstadoEvento]}
                  </span>
                </div>
              );
            })}
          </div>
        </div>
      </section>
    </section>
  );
};

export default EventSummaryPage;
