import React, { useEffect, useMemo, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import EventDetailHeaderTabs from '@/features/events/components/EventDetailHeaderTabs';
import eventosApi from '@/api/eventos';
import clientesApi from '@/api/clientes';
import salonesApi from '@/api/salones';
import catalogosApi from '@/api/catalogos';
import menusApi from '@/api/menus';
import cotizacionesApi from '@/api/cotizaciones';
import type {
  EventoResponse,
  ClienteResponse,
  SalonResponse,
  CatalogoBasicoResponse,
  PlatoResponse,
  TipoMomentoMenuResponse,
  EstadoCotizacion,
  PlatoMomentoResponse,
} from '@/api/types';

interface ItemLocal {
  localId: string;
  platoId: string;
  platoNombre: string;
  precioBase: number;
  cantidad: number;
  excepciones: string;
}

interface SeleccionLocal {
  tipoMomentoId: string;
  items: ItemLocal[];
}

const formatCurrency = (value: number) =>
  new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0,
  }).format(value);

const uid = () => `${Date.now()}-${Math.random().toString(36).slice(2, 7)}`;

const EventMenuPage: React.FC = () => {
  const { eventId } = useParams();

  const [evento, setEvento] = useState<EventoResponse | null>(null);
  const [cliente, setCliente] = useState<ClienteResponse | null>(null);
  const [salon, setSalon] = useState<SalonResponse | null>(null);
  const [tipoEvento, setTipoEvento] = useState<CatalogoBasicoResponse | null>(null);
  const [platos, setPlatos] = useState<PlatoResponse[]>([]);
  const [momentos, setMomentos] = useState<TipoMomentoMenuResponse[]>([]);
  const [platoMomentos, setPlatoMomentos] = useState<PlatoMomentoResponse[]>([]);
  const [selecciones, setSelecciones] = useState<SeleccionLocal[]>([]);
  const [notasGenerales, setNotasGenerales] = useState('');
  const [quoteState, setQuoteState] = useState<EstadoCotizacion | null>(null);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [savedOk, setSavedOk] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [addMomentoId, setAddMomentoId] = useState('');
  const [addPlatoId, setAddPlatoId] = useState('');
  const [addCantidad, setAddCantidad] = useState(1);
  const [addExcepciones, setAddExcepciones] = useState('');

  const guests = evento?.reservas.find((r) => r.vigente)?.numInvitados ?? 0;

  useEffect(() => {
    if (!eventId) return;

    let cancelled = false;

    (async () => {
      try {
        setLoading(true);
        setError(null);

        const eventoData = await eventosApi.obtenerPorId(eventId);
        if (cancelled) return;

        setEvento(eventoData);

        const [platosApiData, momentosApiData, platoMomentosApiData] = await Promise.all([
          catalogosApi.platos.listar(),
          catalogosApi.tiposMomentoMenu.listar(),
          catalogosApi.platoMomentos.obtener(),
        ]);

        const platosActivos = platosApiData.filter((plato) => plato.activo);
        const momentosActivos = momentosApiData.filter((momento) => momento.activo);

        if (cancelled) return;

        setPlatos(platosActivos);
        setMomentos(momentosActivos);
        setPlatoMomentos(platoMomentosApiData);

        if (momentosActivos.length > 0) setAddMomentoId(momentosActivos[0]!.id);
        if (momentosActivos.length > 0) {
          const primerMomentoId = momentosActivos[0]!.id;
          const primerPlatoAsociado = platoMomentosApiData.find((relacion) => relacion.tipoMomentoId === primerMomentoId);
          setAddPlatoId(primerPlatoAsociado?.platoId ?? '');
        }

        const reserva = eventoData.reservas.find((r) => r.vigente);
        if (!reserva) {
          setError('No hay reserva activa para este evento');
          setLoading(false);
          return;
        }

        const reservaId = reserva.reservaRaizId || reserva.id;

        const [clienteData, tipoEventoData, salonData] = await Promise.all([
          clientesApi.obtenerPorId(eventoData.clienteId),
          catalogosApi.tiposEvento.obtenerPorId(eventoData.tipoEventoId),
          salonesApi.obtenerPorId(reserva.salonId),
        ]);

        if (cancelled) return;

        setCliente(clienteData);
        setTipoEvento(tipoEventoData);
        setSalon(salonData);

        try {
          const menuExistente = await menusApi.obtener(reservaId);
          if (!cancelled) {
            setNotasGenerales(menuExistente.notasGenerales ?? '');
            setSelecciones(
              menuExistente.selecciones.map((seleccion) => ({
                tipoMomentoId: seleccion.tipoMomentoId,
                items: seleccion.items.map((item) => {
                  const plato = platosActivos.find((candidate) => candidate.id === item.platoId);
                  return {
                    localId: uid(),
                    platoId: item.platoId,
                    platoNombre: plato?.nombre ?? item.platoId,
                    precioBase: plato?.precioBase ?? 0,
                    cantidad: item.cantidad,
                    excepciones: item.excepciones ?? '',
                  };
                }),
              }))
            );
          }
        } catch {
          if (!cancelled) {
            setSelecciones([]);
          }
        }

        try {
          const cotizacionVigente = await cotizacionesApi.obtenerVigente(reservaId);
          if (!cancelled) {
            setQuoteState(cotizacionVigente.estado);
          }
        } catch {
          if (!cancelled) {
            setQuoteState(null);
          }
        }
      } catch (err) {
        if (!cancelled) {
          setError(err instanceof Error ? err.message : 'Error al cargar datos');
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [eventId]);

  const totalMenu = useMemo(
    () => selecciones.flatMap((seleccion) => seleccion.items).reduce((acc, item) => acc + item.precioBase * item.cantidad, 0),
    [selecciones]
  );

  const costoPorInvitado = guests > 0 ? Math.round(totalMenu / guests) : 0;

  const platosDisponiblesParaMomento = useMemo(() => {
    if (!addMomentoId) return [];

    const platoIdsPermitidos = new Set(
      platoMomentos
        .filter((relacion) => relacion.tipoMomentoId === addMomentoId)
        .map((relacion) => relacion.platoId)
    );

    return platos.filter((plato) => platoIdsPermitidos.has(plato.id));
  }, [addMomentoId, platoMomentos, platos]);

  useEffect(() => {
    if (!addMomentoId) {
      setAddPlatoId('');
      return;
    }

    if (!platosDisponiblesParaMomento.some((plato) => plato.id === addPlatoId)) {
      setAddPlatoId(platosDisponiblesParaMomento[0]?.id ?? '');
    }
  }, [addMomentoId, addPlatoId, platosDisponiblesParaMomento]);

  const agregarItem = () => {
    if (!addMomentoId || !addPlatoId) return;

    const plato = platosDisponiblesParaMomento.find((candidate) => candidate.id === addPlatoId);
    if (!plato) return;

    const nuevoItem: ItemLocal = {
      localId: uid(),
      platoId: plato.id,
      platoNombre: plato.nombre,
      precioBase: Number(plato.precioBase),
      cantidad: Math.max(1, addCantidad),
      excepciones: addExcepciones.trim(),
    };

    setSelecciones((prev) => {
      const current = prev.find((seleccion) => seleccion.tipoMomentoId === addMomentoId);
      if (!current) {
        return [...prev, { tipoMomentoId: addMomentoId, items: [nuevoItem] }];
      }

      return prev.map((seleccion) =>
        seleccion.tipoMomentoId === addMomentoId
          ? { ...seleccion, items: [...seleccion.items, nuevoItem] }
          : seleccion
      );
    });

    setAddCantidad(guests || 1);
    setAddExcepciones('');
    setError(null);
  };

  const quitarItem = (momentoId: string, localId: string) => {
    setSelecciones((prev) =>
      prev
        .map((seleccion) =>
          seleccion.tipoMomentoId === momentoId
            ? { ...seleccion, items: seleccion.items.filter((item) => item.localId !== localId) }
            : seleccion
        )
        .filter((seleccion) => seleccion.items.length > 0)
    );
  };

  const actualizarCantidad = (momentoId: string, localId: string, cantidad: number) => {
    setSelecciones((prev) =>
      prev.map((seleccion) =>
        seleccion.tipoMomentoId === momentoId
          ? {
              ...seleccion,
              items: seleccion.items.map((item) =>
                item.localId === localId ? { ...item, cantidad: Math.max(1, cantidad) } : item
              ),
            }
          : seleccion
      )
    );
  };

  const actualizarExcepciones = (momentoId: string, localId: string, excepciones: string) => {
    setSelecciones((prev) =>
      prev.map((seleccion) =>
        seleccion.tipoMomentoId === momentoId
          ? {
              ...seleccion,
              items: seleccion.items.map((item) =>
                item.localId === localId ? { ...item, excepciones } : item
              ),
            }
          : seleccion
      )
    );
  };

  const handleGuardarMenu = async () => {
    if (!evento) return;

    const reserva = evento.reservas.find((r) => r.vigente);
    if (!reserva) {
      setError('No hay reserva activa');
      return;
    }

    if (selecciones.length === 0) {
      setError('Agrega al menos un plato antes de guardar');
      return;
    }

    if (quoteState && quoteState !== 'BORRADOR') {
      const continuar = window.confirm(
        `Este evento ya tiene una cotización en estado ${quoteState}. Si guardas cambios en Menú, esa cotización dejará de estar vigente y tendrás que generar una nueva.`
      );

      if (!continuar) {
        return;
      }
    }

    try {
      setSaving(true);
      setSavedOk(false);
      setError(null);

      await menusApi.configurar(reserva.reservaRaizId || reserva.id, {
        notasGenerales: notasGenerales.trim() || undefined,
        selecciones: selecciones.map((seleccion) => ({
          tipoMomentoId: seleccion.tipoMomentoId,
          items: seleccion.items.map((item) => ({
            platoId: item.platoId,
            cantidad: item.cantidad,
            excepciones: item.excepciones || undefined,
          })),
        })),
      });

      setQuoteState(null);
      setSavedOk(true);
      window.setTimeout(() => setSavedOk(false), 3000);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al guardar menú');
    } finally {
      setSaving(false);
    }
  };

  const event = useMemo(() => {
    if (!evento) {
      return {
        id: eventId ?? '',
        title: 'Cargando...',
        dateLabel: '',
        timeLabel: '',
        status: 'Pendiente' as const,
        customerName: '',
        customerPhone: '',
        eventType: '',
        guests: 0,
        venue: '',
        venueCapacity: '',
        totalQuote: '$0',
      };
    }

    const reserva = evento.reservas.find((r) => r.vigente);
    const inicio = new Date(evento.fechaHoraInicio);

    return {
      id: evento.id,
      title: `${tipoEvento?.nombre ?? 'Evento'} - ${cliente?.nombreCompleto ?? 'Cliente'}`,
      dateLabel: inicio.toLocaleDateString('es-CO'),
      timeLabel: inicio.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' }),
      status: 'Pendiente' as const,
      customerName: cliente?.nombreCompleto ?? 'Cargando...',
      customerPhone: cliente?.telefono ?? '',
      eventType: tipoEvento?.nombre ?? 'Cargando...',
      guests: reserva?.numInvitados ?? 0,
      venue: salon?.nombre ?? 'Sin salón',
      venueCapacity: salon ? `Capacidad: ${salon.capacidad} pax` : '',
      totalQuote: '$0',
    };
  }, [cliente, eventId, evento, salon, tipoEvento]);

  const momentoNombre = (id: string) => momentos.find((momento) => momento.id === id)?.nombre ?? id;

  if (loading) {
    return (
      <section className="space-y-8 pb-32">
        <div className="flex items-center justify-center py-16 text-on-surface-variant">
          Cargando menú del evento...
        </div>
      </section>
    );
  }

  if (error && !evento) {
    return (
      <section className="space-y-8 pb-32">
        <div className="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>
      </section>
    );
  }

  return (
    <section className="space-y-8 pb-32">
      <EventDetailHeaderTabs event={event} activeTab="menu" />

      <div className="gap-6 lg:flex lg:items-start">
        <div className="mb-24 flex-1 space-y-6">
          {error && (
            <div className="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>
          )}

          {savedOk && (
            <div className="rounded-md border border-green-200 bg-green-50 px-4 py-3 text-sm font-semibold text-green-700">
              Menú guardado correctamente
            </div>
          )}

          <div className="rounded-lg border border-border bg-surface-container-lowest p-6 shadow-sm">
            <div className="flex flex-wrap items-start justify-between gap-4">
              <div>
                <p className="text-xs font-bold uppercase tracking-wider text-stone-500">Ficha gastronómica</p>
                <h3 className="mt-1 font-display text-2xl font-bold text-on-surface">Menú del evento</h3>
                <p className="mt-2 max-w-2xl text-sm text-on-surface-variant">
                  Aquí defines exactamente qué se pidió para la reserva. La cotización toma estos platos y cantidades
                  como fuente de verdad.
                </p>
              </div>
              <span className="rounded-full border border-amber-200 bg-amber-50 px-3 py-1 text-xs font-bold text-amber-700">
                {quoteState && quoteState !== 'BORRADOR' ? `Cotización ${quoteState.toLowerCase()}` : 'En edición'}
              </span>
            </div>
          </div>

          {quoteState && quoteState !== 'BORRADOR' && (
            <div className="rounded-md border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
              Este menú ya respalda una cotización en estado <strong>{quoteState}</strong>. Si cambias platos,
              cantidades o excepciones y guardas, esa versión se invalidará y tendrás que generar una nueva desde
              Cotización.
            </div>
          )}

          <div className="overflow-hidden rounded-lg border border-border bg-surface-container-lowest shadow-sm">
            <div className="border-b border-outline-variant/20 px-6 py-4">
              <h4 className="font-display text-lg font-bold text-on-surface">Items del menú</h4>
              <p className="mt-1 text-sm text-on-surface-variant">
                {selecciones.length === 0
                  ? 'Aún no hay platos. Usa el formulario de abajo para agregarlos.'
                  : `${selecciones.flatMap((seleccion) => seleccion.items).length} plato(s) en ${selecciones.length} momento(s)`}
              </p>
            </div>

            {selecciones.length > 0 && (
              <div className="overflow-x-auto">
                <table className="w-full min-w-[760px] text-left">
                  <thead className="bg-surface-container-low text-xs uppercase tracking-wider text-neutral-500">
                    <tr>
                      <th className="px-6 py-3">Momento</th>
                      <th className="px-4 py-3">Plato</th>
                      <th className="px-4 py-3 text-right">Precio base</th>
                      <th className="px-4 py-3 text-right">Cantidad</th>
                      <th className="px-4 py-3">Excepciones</th>
                      <th className="px-6 py-3 text-right">Acción</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-outline-variant/20">
                    {selecciones.flatMap((seleccion) =>
                      seleccion.items.map((item) => (
                        <tr key={item.localId}>
                          <td className="px-6 py-4 text-sm font-semibold text-on-surface">
                            {momentoNombre(seleccion.tipoMomentoId)}
                          </td>
                          <td className="px-4 py-4 text-sm font-semibold text-on-surface">{item.platoNombre}</td>
                          <td className="px-4 py-4 text-right text-sm text-on-surface-variant">
                            {formatCurrency(item.precioBase)}
                          </td>
                          <td className="px-4 py-4 text-right">
                            <input
                              className="w-20 rounded-md border border-outline-variant/40 bg-surface-container-low px-2 py-1.5 text-right text-sm"
                              type="number"
                              min={1}
                              value={item.cantidad}
                              onChange={(eventTarget) =>
                                actualizarCantidad(seleccion.tipoMomentoId, item.localId, Number(eventTarget.target.value))
                              }
                            />
                          </td>
                          <td className="px-4 py-4">
                            <input
                              className="w-full rounded-md border border-outline-variant/40 bg-surface-container-low px-3 py-1.5 text-sm"
                              type="text"
                              value={item.excepciones}
                              placeholder="Sin observaciones"
                              onChange={(eventTarget) =>
                                actualizarExcepciones(seleccion.tipoMomentoId, item.localId, eventTarget.target.value)
                              }
                            />
                          </td>
                          <td className="px-6 py-4 text-right">
                            <button
                              className="text-sm font-semibold text-red-700 hover:text-red-800"
                              type="button"
                              onClick={() => quitarItem(seleccion.tipoMomentoId, item.localId)}
                            >
                              Quitar
                            </button>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          <div className="grid grid-cols-1 gap-6 xl:grid-cols-[1.4fr_0.6fr]">
            <div className="space-y-5 rounded-lg border border-border bg-surface-container-lowest p-6 shadow-sm">
              <div>
                <h4 className="font-display text-lg font-bold text-on-surface">Agregar plato al menú</h4>
                <p className="mt-1 text-sm text-on-surface-variant">
                  Selecciona el momento, el plato y la cantidad solicitada para el evento.
                </p>
              </div>

              {platos.length === 0 || momentos.length === 0 ? (
                <div className="rounded-md border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-700">
                  {platos.length === 0
                    ? 'No hay platos en el catálogo. Agrégalos primero desde Catálogos.'
                    : 'No hay momentos de menú disponibles en el catálogo.'}
                </div>
              ) : (
                <div className="grid grid-cols-1 items-end gap-4 md:grid-cols-12">
                  <div className="md:col-span-3">
                    <label className="mb-2 block text-xs font-bold text-neutral-700">Momento</label>
                    <select
                      className="w-full rounded-md border border-outline-variant/40 bg-surface-container-low px-3 py-2.5 text-sm"
                      value={addMomentoId}
                      onChange={(eventTarget) => setAddMomentoId(eventTarget.target.value)}
                    >
                      {momentos.map((momento) => (
                        <option key={momento.id} value={momento.id}>
                          {momento.nombre}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="md:col-span-4">
                    <label className="mb-2 block text-xs font-bold text-neutral-700">Plato</label>
                    {platosDisponiblesParaMomento.length === 0 ? (
                      <div className="rounded-md border border-amber-200 bg-amber-50 px-3 py-2.5 text-sm text-amber-700">
                        No hay platos asociados a este momento.
                      </div>
                    ) : (
                      <select
                        className="w-full rounded-md border border-outline-variant/40 bg-surface-container-low px-3 py-2.5 text-sm"
                        value={addPlatoId}
                        onChange={(eventTarget) => setAddPlatoId(eventTarget.target.value)}
                      >
                        {platosDisponiblesParaMomento.map((plato) => (
                          <option key={plato.id} value={plato.id}>
                            {plato.nombre} - {formatCurrency(Number(plato.precioBase))}
                          </option>
                        ))}
                      </select>
                    )}
                  </div>

                  <div className="md:col-span-2">
                    <label className="mb-2 block text-xs font-bold text-neutral-700">Cantidad</label>
                    <input
                      className="w-full rounded-md border border-outline-variant/40 bg-surface-container-low px-3 py-2.5 text-sm"
                      type="number"
                      min={1}
                      value={addCantidad}
                      onChange={(eventTarget) => setAddCantidad(Number(eventTarget.target.value) || 1)}
                    />
                  </div>

                  <div className="md:col-span-3">
                    <button
                      className="w-full rounded-md bg-primary-gold px-4 py-2.5 text-sm font-bold text-white shadow-sm hover:bg-primary disabled:opacity-50"
                      type="button"
                      onClick={agregarItem}
                      disabled={!addMomentoId || !addPlatoId || platosDisponiblesParaMomento.length === 0}
                    >
                      Agregar
                    </button>
                  </div>

                  <div className="md:col-span-12">
                    <label className="mb-2 block text-xs font-bold text-neutral-700">Excepciones para este plato</label>
                    <input
                      className="w-full rounded-md border border-outline-variant/40 bg-surface-container-low px-3 py-2.5 text-sm"
                      type="text"
                      value={addExcepciones}
                      placeholder="Ej: sin cebolla, sin gluten"
                      onChange={(eventTarget) => setAddExcepciones(eventTarget.target.value)}
                    />
                  </div>
                </div>
              )}
            </div>

            <div className="space-y-4 rounded-lg border border-border bg-surface-container-lowest p-6 shadow-sm">
              <h4 className="font-display text-lg font-bold text-on-surface">Notas generales</h4>
              <textarea
                className="min-h-[140px] w-full rounded-md border border-outline-variant/40 bg-surface-container-low px-3 py-3 text-sm"
                value={notasGenerales}
                placeholder="Ej: menú infantil, personas vegetarianas, alergias"
                onChange={(eventTarget) => setNotasGenerales(eventTarget.target.value)}
              />
            </div>
          </div>
        </div>

        <aside className="space-y-6 lg:sticky lg:top-[92px] lg:w-[330px]">
          <div className="space-y-4 rounded-lg border border-border bg-surface-container-lowest p-5 shadow-sm">
            <h4 className="font-display text-lg font-bold text-on-surface">Resumen del menú</h4>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between gap-3">
                <span className="text-on-surface-variant">Invitados</span>
                <span className="font-semibold text-on-surface">{guests} pax</span>
              </div>
              <div className="flex justify-between gap-3">
                <span className="text-on-surface-variant">Platos definidos</span>
                <span className="font-semibold text-on-surface">
                  {selecciones.flatMap((seleccion) => seleccion.items).length}
                </span>
              </div>
              <div className="flex justify-between gap-3">
                <span className="text-on-surface-variant">Estimado por invitado</span>
                <span className="font-semibold text-on-surface">{formatCurrency(costoPorInvitado)}</span>
              </div>
              <div className="flex justify-between gap-3 border-t border-outline-variant/20 pt-3">
                <span className="text-xs font-bold uppercase tracking-wider text-neutral-500">Total menú</span>
                <span className="font-display text-lg font-bold text-primary-gold">{formatCurrency(totalMenu)}</span>
              </div>
            </div>
          </div>

          <div className="space-y-3 rounded-lg border border-amber-200 bg-amber-50 p-5 shadow-sm">
            <h4 className="font-display text-base font-bold text-amber-800">Impacto en cotización</h4>
            <p className="text-sm text-amber-800">
              Guarda el menú antes de ir a Cotización. Si ya existe una cotización generada o enviada, guardar aquí la
              dejará sin vigencia y luego tendrás que crear otra.
            </p>
            <Link
              className="inline-flex w-full items-center justify-center rounded-md border border-amber-300 bg-white px-4 py-2.5 text-sm font-bold text-amber-800 hover:bg-amber-100"
              to={`/events/${event.id}/cotizacion`}
            >
              Ir a Cotización
            </Link>
          </div>
        </aside>
      </div>

      <footer className="fixed bottom-0 right-0 z-[60] flex w-full items-center justify-between border-t border-surface-container bg-surface-container-lowest/90 px-6 py-4 backdrop-blur-md md:w-[calc(100%-16rem)]">
        <div className="hidden items-center gap-2 sm:flex">
          <span className="material-symbols-outlined text-lg text-neutral-400">info</span>
          <p className="text-[10px] font-bold uppercase tracking-wider text-neutral-500">
            Menú y cantidades se editan aquí, no dentro de la cotización
          </p>
        </div>
        <div className="flex w-full gap-3 sm:w-auto">
          <button
            className="flex-1 rounded-md bg-primary-gold px-8 py-2.5 text-sm font-bold text-white shadow-sm transition-colors hover:bg-primary disabled:opacity-50 sm:flex-none"
            type="button"
            onClick={handleGuardarMenu}
            disabled={saving || selecciones.length === 0}
          >
            {saving ? 'Guardando...' : 'Guardar menú'}
          </button>
        </div>
      </footer>
    </section>
  );
};

export default EventMenuPage;
