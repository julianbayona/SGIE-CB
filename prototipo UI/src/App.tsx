import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/auth/ProtectedRoute';
import MainLayout from './layouts/MainLayout';
import LoginPage from './pages/LoginPage';
import CalendarPage from './pages/CalendarPage';
import ClientsPage from './pages/ClientsPage';
import EventsPage from './pages/EventsPage';
import QuotesPage from './pages/QuotesPage';
import EventRequestPage from './pages/EventRequestPage';
import EventSummaryPage from './pages/EventSummaryPage';
import EventMenuPage from './pages/EventMenuPage';
import EventSectionPlaceholderPage from './pages/EventSectionPlaceholderPage';
import EventMontagePage from './pages/EventMontagePage';
import EventQuotePage from './pages/EventQuotePage';
import EventPaymentsPage from './pages/EventPaymentsPage';
import EventAgendaPage from './pages/EventAgendaPage';
import CatalogsPage from './pages/CatalogsPage';

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          {/* Ruta pública de login */}
          <Route path="/login" element={<LoginPage />} />

          {/* Rutas protegidas */}
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <MainLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<CalendarPage />} />
            <Route path="quotes" element={<QuotesPage />} />
            <Route path="clients" element={<ClientsPage />} />
            
            {/* Catálogos - Solo ADMINISTRADOR y GERENTE */}
            <Route
              path="catalogs"
              element={
                <ProtectedRoute requiredRoles={['ADMINISTRADOR', 'GERENTE']}>
                  <CatalogsPage />
                </ProtectedRoute>
              }
            />
            
            <Route path="events" element={<EventsPage />} />
            <Route path="events/request" element={<EventRequestPage />} />
            <Route path="events/:eventId" element={<EventSummaryPage />} />
            <Route path="events/:eventId/menu" element={<EventMenuPage />} />
            <Route path="events/:eventId/agenda" element={<EventAgendaPage />} />
            <Route path="events/:eventId/montaje" element={<EventMontagePage />} />
            <Route path="events/:eventId/cotizacion" element={<EventQuotePage />} />
            
            {/* Pagos - Solo ADMINISTRADOR, GERENTE y TESORERO */}
            <Route
              path="events/:eventId/pagos"
              element={
                <ProtectedRoute requiredRoles={['ADMINISTRADOR', 'GERENTE', 'TESORERO']}>
                  <EventPaymentsPage />
                </ProtectedRoute>
              }
            />
            
            <Route path="events/:eventId/:section" element={<EventSectionPlaceholderPage />} />
          </Route>

          {/* Redirigir cualquier ruta no encontrada al inicio */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
