import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Container, Typography, Box, CircularProgress, Alert, Card, CardContent, Button } from '@mui/material';
import { getEventById } from '../../api/event';
import { buyTicket } from '../../api/client';
import useAuthStore from '../../store/authStore';

interface Event {
  id: number;
  name: string;
  location: string;
  description: string;
  seats: number;
  ownerId: number;
  _links: any; // HATEOAS
}

const EventDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [event, setEvent] = useState<Event | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [purchaseStatus, setPurchaseStatus] = useState<string | null>(null);
  const { isAuthenticated, user, token } = useAuthStore(); // Get auth state

  const fetchEventDetails = async () => {
    if (!id) {
      setError('Event ID is missing.');
      setLoading(false);
      return;
    }
    try {
      // setLoading(true); // Don't reset loading on refresh to avoid flicker
      const data = await getEventById(id);
      setEvent(data);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch event details');
    } finally {
      setLoading(false);
    }
  };

  const handlePurchaseTicket = async () => {
    if (!user?.email || !token || !event?.id) {
      setPurchaseStatus('You must be logged in as a client to purchase tickets.');
      return;
    }
    setPurchaseStatus(null);
    try {
      await buyTicket(user.email, event.id, token);
      setPurchaseStatus('Ticket purchased successfully! Check your tickets page.');
      // Refresh event details to update seats
      fetchEventDetails();
    } catch (err: any) {
      console.error(err);
      setPurchaseStatus(err.response?.data?.detail || err.message || 'Failed to purchase ticket.');
    }
  };

  useEffect(() => {
    setLoading(true); // Initial load
    fetchEventDetails();
  }, [id]);

  if (loading) {
    return (
      <Container maxWidth="md" sx={{ mt: 4, display: 'flex', justifyContent: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  if (error && !event) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Alert severity="error">{error}</Alert>
      </Container>
    );
  }

  if (!event) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Alert severity="info">Event not found.</Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom align="center">
        {event.name}
      </Typography>
      <Card>
        <CardContent>
          <Typography variant="h6" component="h2" gutterBottom>
            Details
          </Typography>
          <Typography variant="body1">
            <strong>Location:</strong> {event.location}
          </Typography>
          <Typography variant="body1">
            <strong>Description:</strong> {event.description}
          </Typography>
          <Typography variant="body1">
            <strong>Available Seats:</strong> {event.seats || 'N/A'}
          </Typography>
          
          {isAuthenticated && user?.role === 'client' && (
            <Box sx={{ mt: 3 }}>
              <Button variant="contained" color="primary" onClick={handlePurchaseTicket}>
                Buy Ticket
              </Button>
            </Box>
          )}

          {purchaseStatus && (
            <Alert severity={purchaseStatus.includes('successfully') ? 'success' : 'error'} sx={{ mt: 2 }}>
              {purchaseStatus}
            </Alert>
          )}

        </CardContent>
      </Card>
    </Container>
  );
};

export default EventDetailsPage;
