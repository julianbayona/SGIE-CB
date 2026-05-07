import React from 'react';
import { format, getHours } from 'date-fns';
import { useCalendar } from '../../hooks/useCalendar';
import { Event, EventStatus } from '../../types';

const statusStyles: Record<EventStatus, { bg: string; border: string; text: string }> = {
  Confirmado: { bg: 'bg-emerald-100', border: 'border-emerald-600', text: 'text-emerald-900' },
  Pendiente: { bg: 'bg-amber-100', border: 'border-amber-600', text: 'text-amber-900' },
  'Cotización enviada': { bg: 'bg-cyan-100', border: 'border-cyan-600', text: 'text-cyan-900' },
  'Cotización aprobada': { bg: 'bg-indigo-100', border: 'border-indigo-600', text: 'text-indigo-900' },
  'Pendiente anticipo': { bg: 'bg-orange-100', border: 'border-orange-600', text: 'text-orange-900' },
  'Esperando selección de menú': { bg: 'bg-violet-100', border: 'border-violet-600', text: 'text-violet-900' },
  Cancelado: { bg: 'bg-slate-200', border: 'border-slate-500', text: 'text-slate-700' },
};

const DayView: React.FC = () => {
  const { events, loading } = useCalendar();
  const hours = Array.from({ length: 24 }, (_, index) => index);

  const getEventsForHour = (hour: number): Event[] => {
    return events.filter((event) => getHours(event.start) === hour);
  };

  return (
    <div className="grid" style={{ gridTemplateColumns: '80px 1fr' }}>
      {loading ? (
        <div className="col-span-2 p-4 text-center">Cargando eventos...</div>
      ) : (
        hours.map((hour) => (
          <React.Fragment key={hour}>
            <div className="calendar-cell p-4 text-[11px] font-bold text-stone-500 text-right pr-6 bg-stone-50 border-r border-b border-outline-variant/35">
              {`${hour.toString().padStart(2, '0')}:00`}
            </div>
            <div className="calendar-cell bg-white p-2 min-h-[64px] border-b border-outline-variant/30">
              {(() => {
                const hourEvents = getEventsForHour(hour);
                const columnCount = Math.min(hourEvents.length || 1, 3);

                return (
                  <div
                    className="grid gap-2"
                    style={{ gridTemplateColumns: `repeat(${columnCount}, minmax(0, 1fr))` }}
                  >
                    {hourEvents.map((event) => {
                      const style = statusStyles[event.status];
                      return (
                        <div key={event.id} className={`rounded ${style.bg} ${style.border} border-l-4 p-3 shadow-sm group cursor-pointer transition-all min-w-0`}>
                          <div className="flex justify-between items-start gap-2">
                            <div className="min-w-0">
                              <p className="text-xs font-bold text-on-surface mb-1 truncate">{event.title}</p>
                              <p className={`text-[10px] ${style.text} font-medium flex items-center gap-1 truncate`}>
                                <span className="material-symbols-outlined text-xs">location_on</span>
                                <span className="truncate">{event.salon}</span>
                              </p>
                            </div>
                            <span className="text-[9px] font-bold text-stone-400 uppercase whitespace-nowrap">
                              {format(event.start, 'HH:mm')} - {format(event.end, 'HH:mm')}
                            </span>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                );
              })()}
            </div>
          </React.Fragment>
        ))
      )}
    </div>
  );
};

export default DayView;
