import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Container, Typography, Box, CircularProgress, Alert, Card, CardContent, Grid } from '@mui/material';
import { getTicketHoldersForEvent } from '../../api/client';
import useAuthStore from '../../store/authStore';
import { useNavigate } from 'react-router-dom';

interface PublicClientInfo {
  email: string;
  firstName?: string;
  lastName?: string;
  isNamePublic: boolean;
  socialMedia?: { [key: string]: string };
}

const TicketHoldersPage: React.FC = () => {
  const { eventId } = useParams<{ eventId: string }>();
  const { user, token } = useAuthStore();
  const navigate = useNavigate();
  const [ticketHolders, setTicketHolders] = useState<PublicClientInfo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!user?.email || !token || user.role !== 'owner-event') {
      navigate('/login'); // Redirect if not authenticated as owner
      return;
    }
    if (!eventId) {
        setError('Event ID is missing.');
        setLoading(false);
        return;
    }

    const fetchTicketHolders = async () => {
      try {
        setLoading(true);
        const data = await getTicketHoldersForEvent(parseInt(eventId), token);
        setTicketHolders(data);
      } catch (err: any) {
        setError(err.message || 'Failed to fetch ticket holders');
      } finally {
        setLoading(false);
      }
    };
    fetchTicketHolders();
  }, [eventId, user, token, navigate]);

  if (loading) {
    return (
      <Container maxWidth="md" sx={{ mt: 4, display: 'flex', justifyContent: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Alert severity="error">{error}</Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom align="center">
        Ticket Holders for Event ID: {eventId}
      </Typography>
      {ticketHolders.length === 0 ? (
        <Typography variant="h6" align="center" color="text.secondary">
          No ticket holders found for this event.
        </Typography>
      ) : (
        <Grid container spacing={3}>
          {ticketHolders.map((client, index) => (
            <Grid key={index} size={{ xs: 12, sm: 6, md: 4 }}>
              <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                <CardContent sx={{ flexGrow: 1 }}>
                  <Typography gutterBottom variant="h5" component="h2">
                    {client.isNamePublic ? `${client.firstName || ''} ${client.lastName || ''}`.trim() : 'Anonymous'}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Email: {client.email}
                  </Typography>
                  {client.isNamePublic && client.socialMedia && Object.keys(client.socialMedia).length > 0 && (
                    <Box sx={{ mt: 2 }}>
                      <Typography variant="subtitle1">Social Media:</Typography>
                      {Object.entries(client.socialMedia).map(([platform, link]) => (
                        link && <Typography variant="body2" key={platform}>
                          {platform.charAt(0).toUpperCase() + platform.slice(1)}: <a href={link} target="_blank" rel="noopener noreferrer">{link}</a>
                        </Typography>
                      ))}
                    </Box>
                  )}
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Container>
  );
};

export default TicketHoldersPage;
