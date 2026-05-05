import React from 'react';
import { useNavigate } from 'react-router-dom';
import CalendarView from '@/features/calendar/components/CalendarView';
import AvailabilityPanel from '@/features/availability/components/AvailabilityPanel';
import { Button } from '@/components/ui/Button';

const CalendarPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <>
      <div className="flex justify-between items-end">
        <div>
          <h1 className="text-2xl font-display font-bold text-on-surface">Calendario de eventos</h1>
          <p className="text-on-surface-variant font-medium text-xs mt-1 uppercase tracking-widest opacity-80">
            {new Date().toLocaleDateString('es-ES', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
          </p>
        </div>
        <Button onClick={() => navigate('/events/request')}>
          <span className="material-symbols-outlined text-base mr-2">add_circle</span>
          Crear solicitud de evento
        </Button>
      </div>
      
      <div className="grid grid-cols-12 gap-6">
        <CalendarView />
        <AvailabilityPanel />
      </div>
    </>
  );
};

export default CalendarPage;
