import React from 'react';
import type { QuotesTab } from '@/features/quotes/types';

interface QuotesHeaderProps {
  activeTab: QuotesTab;
  onTabChange: (tab: QuotesTab) => void;
}

const tabs: QuotesTab[] = ['Recientes', 'Pendientes', 'Aprobadas'];

const QuotesHeader: React.FC<QuotesHeaderProps> = ({ activeTab, onTabChange }) => {
  return (
    <div className="space-y-4">
      <div className="flex flex-wrap justify-between items-end gap-4">
        <div>
          <h1 className="text-2xl font-display font-bold text-text1">Cotizaciones</h1>
          <p className="text-sm text-text3 mt-1">Seguimiento económico de eventos y propuestas enviadas al cliente.</p>
        </div>

        <div className="flex gap-3">
          <button
            type="button"
            className="px-4 py-2 bg-surface border border-border text-text2 text-sm font-semibold rounded-md flex items-center gap-2 hover:bg-hover transition-colors"
          >
            <span className="material-symbols-outlined text-lg">filter_list</span>
            Filtros
          </button>
          <button
            type="button"
            className="px-4 py-2 bg-surface border border-border text-text2 text-sm font-semibold rounded-md flex items-center gap-2 hover:bg-hover transition-colors"
          >
            <span className="material-symbols-outlined text-lg">download</span>
            Exportar
          </button>
        </div>
      </div>

      <nav className="flex gap-8 border-b border-border/80">
        {tabs.map((tab) => {
          const isActive = tab === activeTab;

          return (
            <button
              key={tab}
              type="button"
              onClick={() => onTabChange(tab)}
              className={`pb-3 px-1 border-b-2 text-sm transition-colors ${
                isActive
                  ? 'border-gold text-gold font-bold'
                  : 'border-transparent text-text2 font-medium hover:text-gold'
              }`}
            >
              {tab}
            </button>
          );
        })}
      </nav>
    </div>
  );
};

export default QuotesHeader;
