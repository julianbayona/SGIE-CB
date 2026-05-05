import React from 'react';
import { NavLink } from 'react-router-dom';

const navItems = [
  { to: '/', icon: 'calendar_month', label: 'Calendario' },
  { to: '/events', icon: 'event_available', label: 'Eventos' },
  { to: '/clients', icon: 'group', label: 'Clientes' },
  { to: '/quotes', icon: 'description', label: 'Cotizaciones' },
  { to: '/catalogs', icon: 'settings', label: 'Catálogos' },
];

const Sidebar: React.FC = () => {
  return (
    <aside className="fixed left-0 top-0 h-full w-64 bg-neutral-800 text-white flex flex-col z-40 border-r border-stone-200/20">
      <div className="px-4 py-5">
        <div className="text-2xl font-display text-primary-gold leading-none">CB</div>
        <p className="text-[10px] text-stone-400 font-semibold uppercase tracking-widest mt-1">SGIE</p>
      </div>

      <div className="px-4 py-2 mt-2">
        <p className="text-stone-500 text-[10px] uppercase tracking-widest font-medium">Gestión de eventos</p>
      </div>

      <nav className="flex-1 px-2 space-y-1">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-3 rounded font-medium tracking-wide transition-colors duration-200 ${
                isActive
                  ? 'text-yellow-500 bg-stone-700/40 font-bold border-r-4 border-yellow-600'
                  : 'text-stone-400 hover:text-stone-200 hover:bg-stone-800'
              }`
            }
          >
            <span className="material-symbols-outlined">{item.icon}</span>
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="p-4 border-t border-stone-700/30">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-full bg-stone-700 flex items-center justify-center text-xs text-yellow-500 font-bold">
            JD
          </div>
          <div className="overflow-hidden">
            <p className="text-stone-200 text-xs font-bold truncate">Julián Bayona</p>
            <p className="text-stone-500 text-[10px] truncate">Administrador</p>
          </div>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
