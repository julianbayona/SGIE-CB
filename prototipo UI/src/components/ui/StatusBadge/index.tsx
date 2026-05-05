import React from 'react';
import type { EventStatus } from '@/features/events/types';
import type { QuoteStatus } from '@/features/quotes/types';

type StatusBadgeTone = 'neutral' | 'blue' | 'amber' | 'green' | 'red' | 'gray';

const toneClasses: Record<StatusBadgeTone, string> = {
  neutral: 'bg-stone-100 text-stone-700 border-stone-200',
  blue: 'bg-blue-bg text-blue-text border-blue-border',
  amber: 'bg-gold-bg text-gold-d border-gold/25',
  green: 'bg-green-bg text-green-text border-green-border',
  red: 'bg-red-bg text-red-text border-red-border',
  gray: 'bg-stone-100 text-stone-500 border-stone-200',
};

const eventStatusTone: Record<EventStatus, StatusBadgeTone> = {
  Pendiente: 'amber',
  'Esperando selección de menú': 'blue',
  'Cotización enviada': 'blue',
  'Cotización aprobada': 'amber',
  'Pendiente anticipo': 'red',
  Confirmado: 'green',
  Cancelado: 'gray',
};

const quoteStatusTone: Record<QuoteStatus, StatusBadgeTone> = {
  Borrador: 'gray',
  Generada: 'neutral',
  Enviada: 'blue',
  Aceptada: 'green',
  Rechazada: 'red',
  Desactualizada: 'amber',
};

interface StatusBadgeProps {
  status: EventStatus | QuoteStatus;
  type: 'event' | 'quote';
  size?: 'sm' | 'md';
  className?: string;
}

const sizeClasses = {
  sm: 'px-2 py-0.5 text-[10px]',
  md: 'px-2.5 py-1 text-xs',
};

export const StatusBadge: React.FC<StatusBadgeProps> = ({
  status,
  type,
  size = 'sm',
  className = '',
}) => {
  const tone =
    type === 'event'
      ? eventStatusTone[status as EventStatus]
      : quoteStatusTone[status as QuoteStatus];

  return (
    <span
      className={`inline-flex items-center rounded-full border font-bold leading-none ${sizeClasses[size]} ${toneClasses[tone]} ${className}`}
    >
      {status}
    </span>
  );
};

export const eventStatuses = Object.keys(eventStatusTone) as EventStatus[];
