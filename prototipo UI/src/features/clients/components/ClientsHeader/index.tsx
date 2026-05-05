import React from 'react';
import type { ClientsTab } from '@/features/clients/types';

interface ClientsHeaderProps {
  activeTab: ClientsTab;
  searchQuery: string;
  onTabChange: (tab: ClientsTab) => void;
  onSearchChange: (value: string) => void;
  onCreateClient: () => void;
}

const tabs: ClientsTab[] = ['Todos', 'Socios', 'No Socios'];

const ClientsHeader: React.FC<ClientsHeaderProps> = ({
  activeTab,
  searchQuery,
  onTabChange,
  onSearchChange,
  onCreateClient,
}) => {
  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <h1 className="text-2xl font-display font-bold text-text1">Clientes</h1>
          <p className="text-sm text-text3 mt-1">Consulta y registro de socios y no socios para solicitudes de evento.</p>
        </div>

        <button
          type="button"
          onClick={onCreateClient}
          className="flex items-center gap-2 px-4 py-2 bg-gold text-white rounded-md text-sm font-bold shadow-sm hover:bg-gold-d transition-colors"
        >
          <span className="material-symbols-outlined text-lg">person_add</span>
          Nuevo Cliente
        </button>
      </div>

      <div className="flex flex-wrap items-center justify-between gap-4">
        <nav className="flex gap-8 border-b border-border/80">
          {tabs.map((tab) => {
            const isActive = activeTab === tab;

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

        <div className="relative w-full sm:w-80">
          <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-text3 text-lg">
            search
          </span>
          <input
            className="w-full bg-surface border border-border rounded-md pl-10 pr-3 py-2 text-sm focus:border-gold focus:ring-1 focus:ring-gold/20"
            placeholder="Buscar por cédula, nombre o teléfono"
            value={searchQuery}
            onChange={(event) => onSearchChange(event.target.value)}
          />
        </div>
      </div>
    </div>
  );
};

export default ClientsHeader;
