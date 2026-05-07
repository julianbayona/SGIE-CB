import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { StatusBadge } from '@/components/ui/StatusBadge';
import type { EventSummaryData } from '@/features/events/data/eventSummary';
import { formatShortId } from '@/utils/formatters';
import eventosApi from '@/api/eventos';
import type { EventoResponse } from '@/api/types';
import { useAuth } from '@/contexts/AuthContext';
import CancelEventModal from '@/features/events/components/CancelEventModal';

export type EventDetailTab =
  | 'summary'
  | 'menu'
  | 'agenda'
  | 'montaje'
  | 'cotizacion'
  | 'pagos';

interface EventDetailHeaderTabsProps {
  event: EventSummaryData;
  activeTab: EventDetailTab;
  onEventCancelled?: (evento: EventoResponse) => void;
}

const tabs: Array<{ key: EventDetailTab; label: string; getPath: (eventId: string) => string }> = [
  { key: 'summary', label: 'Resumen', getPath: (eventId) => `/events/${eventId}` },
  { key: 'menu', label: 'Menú', getPath: (eventId) => `/events/${eventId}/menu` },
  { key: 'agenda', label: 'Agenda', getPath: (eventId) => `/events/${eventId}/agenda` },
  { key: 'montaje', label: 'Montaje', getPath: (eventId) => `/events/${eventId}/montaje` },
  { key: 'cotizacion', label: 'Cotización', getPath: (eventId) => `/events/${eventId}/cotizacion` },
  { key: 'pagos', label: 'Pagos', getPath: (eventId) => `/events/${eventId}/pagos` },
];

const EventDetailHeaderTabs: React.FC<EventDetailHeaderTabsProps> = ({
  event,
  activeTab,
  onEventCancelled,
}) => {
  const navigate = useNavigate();
  const { hasRole } = useAuth();
  const [cancelModalOpen, setCancelModalOpen] = useState(false);
  const [cancelError, setCancelError] = useState<string | null>(null);
  const [cancelling, setCancelling] = useState(false);

  const isCancelled = event.status === 'Cancelado';
  const isAdmin = hasRole('ADMINISTRADOR');
  const canCancel = isAdmin && !isCancelled;

  const openCancelModal = () => {
    if (!canCancel) return;
    setCancelError(null);
    setCancelModalOpen(true);
  };

  const closeCancelModal = () => {
    if (cancelling) return;
    setCancelModalOpen(false);
    setCancelError(null);
  };

  const handleCancel = async (motivo: string) => {
    try {
      setCancelling(true);
      setCancelError(null);
      const actualizado = await eventosApi.cancelar(event.id, { motivo });
      setCancelModalOpen(false);
      onEventCancelled?.(actualizado);
      if (activeTab !== 'summary') {
        navigate(`/events/${event.id}`, { replace: true });
      }
    } catch (err) {
      setCancelError(err instanceof Error ? err.message : 'No fue posible cancelar el evento.');
    } finally {
      setCancelling(false);
    }
  };

  return (
    <>
      <section className="bg-surface-container-lowest border border-border rounded-lg p-5 shadow-sm">
        <div className="flex flex-col lg:flex-row lg:items-start justify-between gap-5">
          <div className="min-w-0">
            <div className="flex items-center gap-3 mb-3 flex-wrap">
              <span className="text-xs font-bold tracking-widest text-stone-500 uppercase">
                {formatShortId(event.id, 'EV-')}
              </span>
              <StatusBadge type="event" status={event.status} size="md" />
            </div>
            <h2 className="text-2xl font-display font-bold text-on-surface">{event.title.replace(' - ', ' · ')}</h2>
            <div className="flex items-center gap-5 text-on-surface-variant font-medium text-sm flex-wrap mt-3">
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-base">calendar_today</span>
                {event.dateLabel}
              </div>
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-base">schedule</span>
                {event.timeLabel}
              </div>
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-base">meeting_room</span>
                {event.venue}
              </div>
            </div>
          </div>

          <div className="flex flex-wrap gap-2">
            <button
              type="button"
              className="border border-border bg-surface text-text2 px-3 py-2 rounded-md flex items-center gap-2 text-sm font-semibold hover:bg-hover transition-colors"
            >
              <span className="material-symbols-outlined text-lg">public</span>
              Enlace público
            </button>
            <button
              type="button"
              className="border border-border bg-surface text-text2 px-3 py-2 rounded-md flex items-center gap-2 text-sm font-semibold hover:bg-hover transition-colors"
            >
              <span className="material-symbols-outlined text-lg">edit</span>
              Editar
            </button>
            <button
              type="button"
              onClick={openCancelModal}
              disabled={!canCancel}
              title={!isAdmin ? 'Solo un administrador puede cancelar eventos' : undefined}
              className="border border-red-border bg-red-bg text-red-text px-3 py-2 rounded-md flex items-center gap-2 text-sm font-semibold hover:bg-red-bg/80 transition-colors disabled:cursor-not-allowed disabled:opacity-50"
            >
              <span className="material-symbols-outlined text-lg">cancel</span>
              Cancelar
            </button>
          </div>
        </div>
      </section>

      <nav className="flex gap-7 border-b border-outline-variant/30 overflow-x-auto">
        {tabs.map((tab) => {
          const isActive = tab.key === activeTab;

          return (
            <Link
              key={tab.key}
              to={tab.getPath(event.id)}
              className={`pb-3 px-1 whitespace-nowrap text-sm transition-colors ${
                isActive
                  ? 'text-primary-gold font-semibold border-b-2 border-primary-gold'
                  : 'text-gray-500 font-medium hover:text-primary-gold'
              }`}
            >
              {tab.label}
            </Link>
          );
        })}
      </nav>

      <CancelEventModal
        open={cancelModalOpen}
        eventTitle={event.title.replace(' - ', ' / ')}
        submitting={cancelling}
        error={cancelError}
        onClose={closeCancelModal}
        onConfirm={handleCancel}
      />
    </>
  );
};

export default EventDetailHeaderTabs;
