import React from 'react';

interface QuotesTablePaginationProps {
  from: number;
  to: number;
  total: number;
}

const QuotesTablePagination: React.FC<QuotesTablePaginationProps> = ({ from, to, total }) => {
  return (
    <div className="px-6 py-4 bg-stone-50 border-t border-stone-100 flex items-center justify-between">
      <span className="text-xs font-medium text-stone-500 uppercase tracking-widest">
        Mostrando {from}-{to} de {total} Cotizaciones
      </span>

      <div className="flex gap-1">
        <button
          type="button"
          className="w-8 h-8 flex items-center justify-center rounded border border-stone-200 bg-white text-stone-400 hover:bg-stone-50 transition-colors"
        >
          <span className="material-symbols-outlined text-lg">chevron_left</span>
        </button>

        <button type="button" className="w-8 h-8 flex items-center justify-center rounded bg-primary-gold text-white font-bold text-xs">
          1
        </button>

        <button
          type="button"
          className="w-8 h-8 flex items-center justify-center rounded border border-stone-200 bg-white text-stone-600 font-bold text-xs hover:bg-stone-50 transition-colors"
        >
          2
        </button>

        <button
          type="button"
          className="w-8 h-8 flex items-center justify-center rounded border border-stone-200 bg-white text-stone-600 font-bold text-xs hover:bg-stone-50 transition-colors"
        >
          3
        </button>

        <button
          type="button"
          className="w-8 h-8 flex items-center justify-center rounded border border-stone-200 bg-white text-stone-400 hover:bg-stone-50 transition-colors"
        >
          <span className="material-symbols-outlined text-lg">chevron_right</span>
        </button>
      </div>
    </div>
  );
};

export default QuotesTablePagination;
