import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useNavigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import ProtectedRoute from './components/ProtectedRoute';
import { AppBar, Toolbar, Typography, Button, Box, Container } from '@mui/material';
import useAuthStore from './store/authStore';
import EventListPage from './pages/Events/EventListPage';
import EventDetailsPage from './pages/Events/EventDetailsPage';
import ClientProfilePage from './pages/Client/ClientProfilePage';
import ClientTicketsPage from './pages/Client/ClientTicketsPage';
import OwnerDashboardPage from './pages/Owner/OwnerDashboardPage';
import TicketHoldersPage from './pages/Owner/TicketHoldersPage';

const DashboardClientLanding = () => (
    <Container sx={{ mt: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>Client Dashboard</Typography>
        <ul>
            <li><Link to="/client-profile">Manage your profile</Link></li>
            <li><Link to="/my-tickets">View your purchased tickets</Link></li>
        </ul>
    </Container>
);

// AppContent contains the main logic and can use hooks that depend on the router context
const AppContent = () => {
  const { isAuthenticated, logout, user } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <>
      <AppBar>
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Event Management
          </Typography>
          <Box>
            <Button color="inherit" component={Link} to="/events">
              Events
            </Button>
            {isAuthenticated ? (
              <>
                <Button color="inherit">Welcome, {user?.email}</Button>
                {user?.role === 'client' && (
                    <>
                        <Button color="inherit" component={Link} to="/client-dashboard">
                            My Dashboard
                        </Button>
                        <Button color="inherit" component={Link} to="/client-profile">
                            Profile
                        </Button>
                        <Button color="inherit" component={Link} to="/my-tickets">
                            My Tickets
                        </Button>
                    </>
                )}
                {user?.role === 'owner-event' && (
                    <Button color="inherit" component={Link} to="/owner-dashboard">
                        Owner Dashboard
                    </Button>
                )}
                <Button color="inherit" onClick={handleLogout}>
                  Logout
                </Button>
              </>
            ) : (
              <>
                <Button color="inherit" component={Link} to="/login">
                  Login
                </Button>
                <Button color="inherit" component={Link} to="/register">
                  Register
                </Button>
              </>
            )}
          </Box>
        </Toolbar>
      </AppBar>
      <Container component="main" sx={{ p: 3, pt: 10 }}>
        <Routes>
          <Route path="/" element={<EventListPage />} />
          <Route path="/events" element={<EventListPage />} />
          <Route path="/events/:id" element={<EventDetailsPage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          {/* Protected client routes */}
          <Route
            path="/client-dashboard"
            element={
              <ProtectedRoute allowedRoles={['client']}>
                <DashboardClientLanding />
              </ProtectedRoute>
            }
          />
          <Route
            path="/client-profile"
            element={
              <ProtectedRoute allowedRoles={['client']}>
                <ClientProfilePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/my-tickets"
            element={
              <ProtectedRoute allowedRoles={['client']}>
                <ClientTicketsPage />
              </ProtectedRoute>
            }
          />

           {/* Protected owner routes */}
           <Route
            path="/owner-dashboard"
            element={
              <ProtectedRoute allowedRoles={['owner-event']}>
                <OwnerDashboardPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/owner/events/:eventId/ticket-holders"
            element={
              <ProtectedRoute allowedRoles={['owner-event']}>
                <TicketHoldersPage />
              </ProtectedRoute>
            }
          />
        </Routes>
      </Container>
    </>
  );
}

// The main App component only sets up the Router
function App() {
    return (
        <Router>
            <AppContent />
        </Router>
    )
}

export default App;