import React from 'react';

interface EventsTablePaginationProps {
  from: number;
  to: number;
  total: number;
}

const EventsTablePagination: React.FC<EventsTablePaginationProps> = ({ from, to, total }) => {
  return (
    <div className="p-6 bg-stone-50/50 flex flex-wrap justify-between items-center gap-3 border-t border-border">
      <p className="text-xs text-text3">
        Mostrando <span className="font-bold text-text1">{from} - {to}</span> de {total} eventos
      </p>

      <div className="flex gap-2">
        <button
          type="button"
          className="w-8 h-8 flex items-center justify-center rounded bg-white border border-border hover:bg-surface transition-colors"
        >
          <span className="material-symbols-outlined text-sm">chevron_left</span>
        </button>

        <button type="button" className="w-8 h-8 flex items-center justify-center rounded bg-gold text-white font-bold text-xs">
          1
        </button>

        <button
          type="button"
          className="w-8 h-8 flex items-center justify-center rounded bg-white border border-border hover:bg-surface transition-colors text-xs"
        >
          2
        </button>

        <button
          type="button"
          className="w-8 h-8 flex items-center justify-center rounded bg-white border border-border hover:bg-surface transition-colors text-xs"
        >
          3
        </button>

        <button
          type="button"
          className="w-8 h-8 flex items-center justify-center rounded bg-white border border-border hover:bg-surface transition-colors"
        >
          <span className="material-symbols-outlined text-sm">chevron_right</span>
        </button>
      </div>
    </div>
  );
};

export default EventsTablePagination;
