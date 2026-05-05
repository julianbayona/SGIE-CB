import React from 'react';

interface ClientsTablePaginationProps {
  from: number;
  to: number;
  total: number;
}

const ClientsTablePagination: React.FC<ClientsTablePaginationProps> = ({ from, to, total }) => {
  return (
    <div className="mt-auto bg-white border-t border-stone-100 px-8 py-4 flex flex-wrap items-center justify-between gap-3">
      <p className="text-xs text-stone-400 font-medium">
        Mostrando <span className="font-bold text-text1">{from} - {to}</span> de{' '}
        <span className="font-bold text-text1">{total.toLocaleString('es-CO')}</span> clientes
      </p>

      <div className="flex items-center gap-2">
        <button
          type="button"
          className="w-8 h-8 flex items-center justify-center rounded border border-stone-200 text-stone-400 hover:bg-stone-50 transition-colors"
        >
          <span className="material-symbols-outlined text-base">chevron_left</span>
        </button>
        <button
          type="button"
          className="w-8 h-8 flex items-center justify-center rounded bg-gold text-white text-xs font-bold shadow-sm"
        >
          1
        </button>
        <button
          type="button"
          className="w-8 h-8 flex items-center justify-center rounded text-xs font-bold text-stone-400 hover:bg-stone-50 transition-colors"
        >
          2
        </button>
        <button
          type="button"
          className="w-8 h-8 flex items-center justify-center rounded text-xs font-bold text-stone-400 hover:bg-stone-50 transition-colors"
        >
          3
        </button>
        <span className="px-2 text-stone-300">...</span>
        <button
          type="button"
          className="w-8 h-8 flex items-center justify-center rounded text-xs font-bold text-stone-400 hover:bg-stone-50 transition-colors"
        >
          125
        </button>
        <button
          type="button"
          className="w-8 h-8 flex items-center justify-center rounded border border-stone-200 text-stone-400 hover:bg-stone-50 transition-colors"
        >
          <span className="material-symbols-outlined text-base">chevron_right</span>
        </button>
      </div>
    </div>
  );
};

export default ClientsTablePagination;
