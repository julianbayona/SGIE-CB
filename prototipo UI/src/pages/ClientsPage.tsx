import React, { useEffect, useMemo, useState } from 'react';
import ClientsHeader from '@/features/clients/components/ClientsHeader';
import ClientsTable from '@/features/clients/components/ClientsTable';
import ClientsTablePagination from '@/features/clients/components/ClientsTablePagination';
import ClientFormModal, { type ClientFormValues } from '@/features/clients/components/ClientFormModal';
import type { Client, ClientsTab } from '@/features/clients/types';
import clientesApi from '@/api/clientes';
import type { ClienteResponse } from '@/api/types';

/** Convierte la respuesta del backend al tipo que usa el frontend. */
function toClient(c: ClienteResponse): Client {
  return {
    idNumber: c.cedula,
    fullName: c.nombreCompleto,
    phone: c.telefono,
    email: c.correo,
    category: c.tipoCliente === 'SOCIO' ? 'Socio' : 'No Socio',
    status: c.activo ? 'Activo' : 'Suspendido',
    registeredAt: c.id, // se usa el id como referencia interna; la fecha no viene en el DTO
  };
}

const ClientsPage: React.FC = () => {
  const [clients, setClients] = useState<Client[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<ClientsTab>('Todos');
  const [searchQuery, setSearchQuery] = useState('');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingClientId, setEditingClientId] = useState<string | null>(null);

  // Carga inicial y búsqueda con debounce
  useEffect(() => {
    let cancelled = false;
    const timer = setTimeout(async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await clientesApi.listar(searchQuery.trim() || undefined);
        if (!cancelled) setClients(data.map(toClient));
      } catch (err) {
        if (!cancelled) setError(err instanceof Error ? err.message : 'Error al cargar clientes.');
      } finally {
        if (!cancelled) setLoading(false);
      }
    }, 300);

    return () => {
      cancelled = true;
      clearTimeout(timer);
    };
  }, [searchQuery]);

  const editingClient = useMemo(
    () => clients.find((c) => c.idNumber === editingClientId) ?? null,
    [clients, editingClientId]
  );

  const idNumbersInUse = useMemo(
    () =>
      clients
        .filter((c) => c.idNumber !== editingClientId)
        .map((c) => c.idNumber.replace(/[^\d]/g, '')),
    [clients, editingClientId]
  );

  const formattedToday = new Intl.DateTimeFormat('es-CO', { day: '2-digit', month: 'short', year: 'numeric' }).format(new Date());
  void formattedToday; // usado en saveClient para nuevos registros locales

  const openCreateForm = () => {
    setEditingClientId(null);
    setIsFormOpen(true);
  };

  const openEditForm = (client: Client) => {
    setEditingClientId(client.idNumber);
    setIsFormOpen(true);
  };

  const closeForm = () => {
    setIsFormOpen(false);
    setEditingClientId(null);
  };

  const saveClient = async (values: ClientFormValues) => {
    try {
      if (editingClientId) {
        // El backend no expone PUT /clientes/{id} aún; actualizamos localmente
        setClients((prev) =>
          prev.map((c) =>
            c.idNumber !== editingClientId
              ? c
              : {
                  ...c,
                  idNumber: values.idNumber,
                  fullName: values.fullName,
                  category: values.category,
                  phone: values.phone,
                  email: values.email,
                }
          )
        );
        closeForm();
        return;
      }

      const nuevo = await clientesApi.registrar({
        cedula: values.idNumber,
        nombreCompleto: values.fullName,
        telefono: values.phone,
        correo: values.email,
        tipoCliente: values.category === 'Socio' ? 'SOCIO' : 'NO_SOCIO',
      });

      setClients((prev) => [toClient(nuevo), ...prev]);
      closeForm();
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Error al guardar el cliente.');
    }
  };

  const visibleClients = useMemo(() => {
    return clients.filter((c) => {
      if (activeTab === 'Socios') return c.category === 'Socio';
      if (activeTab === 'No Socios') return c.category === 'No Socio';
      return true;
    });
  }, [activeTab, clients]);

  return (
    <section className="space-y-6 relative isolate min-h-[calc(100vh-10rem)]">
      <ClientsHeader
        activeTab={activeTab}
        searchQuery={searchQuery}
        onTabChange={setActiveTab}
        onSearchChange={setSearchQuery}
        onCreateClient={openCreateForm}
      />

      {error && (
        <div className="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      <div className="bg-surface rounded-lg shadow-sm border border-border overflow-hidden flex-1 flex flex-col">
        {loading ? (
          <div className="flex items-center justify-center py-16 text-on-surface-variant text-sm">
            Cargando clientes…
          </div>
        ) : (
          <ClientsTable clients={visibleClients} onEditClient={openEditForm} />
        )}
        <ClientsTablePagination from={1} to={visibleClients.length} total={visibleClients.length} />
      </div>

      <ClientFormModal
        isOpen={isFormOpen}
        mode={editingClient ? 'edit' : 'create'}
        initialClient={editingClient}
        idNumbersInUse={idNumbersInUse}
        onCancel={closeForm}
        onSubmit={saveClient}
      />
    </section>
  );
};

export default ClientsPage;
