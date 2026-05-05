import React from 'react';
import { Link } from 'react-router-dom';

const EventsPageHeader: React.FC = () => {
  return (
    <div className="flex flex-wrap items-end justify-between gap-4">
      <div>
        <nav className="flex items-center gap-2 text-[10px] uppercase tracking-widest text-text3 mb-2">
          <span>Gestión</span>
          <span className="material-symbols-outlined text-[12px]">chevron_right</span>
          <span className="text-gold font-bold">Eventos</span>
        </nav>
        <h1 className="text-2xl font-display font-bold text-text1">Gestión de eventos</h1>
        <p className="text-sm text-text3 mt-1">Seguimiento del flujo cliente, menú, cotización, anticipo y confirmación.</p>
      </div>

      <Link
        to="/events/request"
        className="flex items-center gap-2 bg-gold text-white px-5 py-2.5 rounded-md shadow-sm hover:bg-gold-d active:scale-[0.98] transition-all font-bold text-sm"
      >
        <span className="material-symbols-outlined text-lg">add_circle</span>
        Crear solicitud de evento
      </Link>
    </div>
  );
};

export default EventsPageHeader;
