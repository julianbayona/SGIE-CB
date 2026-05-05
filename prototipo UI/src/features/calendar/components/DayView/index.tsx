import React from 'react';
import { format, getHours } from 'date-fns';
import { useCalendar } from '../../hooks/useCalendar';
import { Event, EventStatus } from '../../types';

const statusStyles: Record<EventStatus, { bg: string; border: string; text: string }> = {
  Confirmado: { bg: 'bg-green-bg', border: 'border-green', text: 'text-green-text' },
  Pendiente: { bg: 'bg-gold-bg', border: 'border-gold', text: 'text-gold-d' },
  'Cotización enviada': { bg: 'bg-blue-bg', border: 'border-blue', text: 'text-blue-text' },
  'Cotización aprobada': { bg: 'bg-gold-bg2', border: 'border-gold', text: 'text-gold-d' },
  'Pendiente anticipo': { bg: 'bg-red-bg', border: 'border-red', text: 'text-red-text' },
  'Esperando selección de menú': { bg: 'bg-blue-bg', border: 'border-blue', text: 'text-blue-text' },
  Cancelado: { bg: 'bg-stone-100', border: 'border-stone-400', text: 'text-stone-500' },
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
            <div className="calendar-cell p-4 text-[11px] font-bold text-stone-400 text-right pr-6 bg-stone-50/30">
              {`${hour.toString().padStart(2, '0')}:00`}
            </div>
            <div className="calendar-cell bg-white relative p-2 min-h-[64px]">
              {getEventsForHour(hour).map((event) => {
                const style = statusStyles[event.status];
                return (
                  <div key={event.id} className={`absolute inset-x-4 inset-y-2 rounded ${style.bg} ${style.border} border-l-4 p-3 shadow-sm group cursor-pointer transition-all`}>
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="text-xs font-bold text-on-surface mb-1">{event.title}</p>
                        <p className={`text-[10px] ${style.text} font-medium flex items-center gap-1`}>
                          <span className="material-symbols-outlined text-xs">location_on</span>
                          {event.salon}
                        </p>
                      </div>
                      <span className="text-[9px] font-bold text-stone-400 uppercase">
                        {format(event.start, 'HH:mm')} - {format(event.end, 'HH:mm')}
                      </span>
                    </div>
                  </div>
                );
              })}
            </div>
          </React.Fragment>
        ))
      )}
    </div>
  );
};

export default DayView;
