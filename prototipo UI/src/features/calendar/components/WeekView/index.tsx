import React from 'react';
import { format, startOfWeek, endOfWeek, eachDayOfInterval, getHours } from 'date-fns';
import { useCalendarStore } from '@/store/calendarStore';
import { useCalendar } from '../../hooks/useCalendar';
import { Event } from '../../types';
import EventItem from '../EventItem';

const WeekView: React.FC = () => {
  const { selectedDate } = useCalendarStore();
  const { events, loading } = useCalendar();

  const weekStart = startOfWeek(selectedDate, { weekStartsOn: 1 }); // Monday
  const weekEnd = endOfWeek(selectedDate, { weekStartsOn: 1 }); // Sunday
  const days = eachDayOfInterval({ start: weekStart, end: weekEnd });

  const hours = Array.from({ length: 24 }, (_, i) => i);

  const getEventsForHour = (day: Date, hour: number): Event[] => {
    return events.filter(event => {
      const eventDay = format(event.start, 'yyyy-MM-dd');
      const currentDay = format(day, 'yyyy-MM-dd');
      const eventHour = getHours(event.start);
      return eventDay === currentDay && eventHour === hour;
    });
  };

  return (
    <div className="grid" style={{ gridTemplateColumns: '60px repeat(7, 1fr)' }}>
      {/* Header */}
      <div className="calendar-header bg-stone-50"></div>
      {days.map(day => (
        <div key={day.toISOString()} className="calendar-header bg-stone-50 p-2 text-center border-b border-outline-variant/10">
          <p className="text-[8px] font-bold text-stone-400 uppercase">{format(day, 'eee')}</p>
          <p className="text-sm font-serif-italic text-on-surface">{format(day, 'd')}</p>
        </div>
      ))}

      {/* Body */}
      {loading ? (
        <div className="col-span-8 p-4 text-center">Cargando eventos...</div>
      ) : (
        hours.map(hour => (
          <React.Fragment key={hour}>
            <div className="calendar-cell p-2 text-[10px] font-bold text-stone-400 text-right pr-3">{`${hour.toString().padStart(2, '0')}:00`}</div>
            {days.map(day => {
              const hourEvents = getEventsForHour(day, hour);
              return (
                <div key={day.toISOString() + hour} className="calendar-cell bg-white relative p-1 min-h-[48px]">
                  {hourEvents.map(event => (
                    <EventItem key={event.id} event={event} />
                  ))}
                </div>
              );
            })}
          </React.Fragment>
        ))
      )}
    </div>
  );
};

export default WeekView;
