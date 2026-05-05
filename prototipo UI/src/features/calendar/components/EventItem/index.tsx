import React from 'react';
import { Event, EventStatus } from '../../types';

const statusStyles: Record<EventStatus, { bg: string; dot: string; text: string }> = {
  Confirmado: { bg: 'bg-green-bg', dot: 'bg-green', text: 'text-green-text' },
  Pendiente: { bg: 'bg-gold-bg', dot: 'bg-gold', text: 'text-gold-d' },
  'Cotización enviada': { bg: 'bg-blue-bg', dot: 'bg-blue', text: 'text-blue-text' },
  'Cotización aprobada': { bg: 'bg-gold-bg2', dot: 'bg-gold', text: 'text-gold-d' },
  'Pendiente anticipo': { bg: 'bg-red-bg', dot: 'bg-red', text: 'text-red-text' },
  'Esperando selección de menú': { bg: 'bg-blue-bg', dot: 'bg-blue', text: 'text-blue-text' },
  Cancelado: { bg: 'bg-stone-100', dot: 'bg-stone-400', text: 'text-stone-500' },
};

interface EventItemProps {
  event: Event;
}

const EventItem: React.FC<EventItemProps> = ({ event }) => {
  const style = statusStyles[event.status] || statusStyles.Pendiente;

  return (
    <div className={`flex items-center gap-1.5 ${style.bg} px-2 py-1 rounded text-[10px] font-semibold ${style.text} truncate`}>
      <span className={`w-1.5 h-1.5 rounded-full ${style.dot}`}></span>
      <span className="truncate">{event.title}</span>
    </div>
  );
};

export default EventItem;
