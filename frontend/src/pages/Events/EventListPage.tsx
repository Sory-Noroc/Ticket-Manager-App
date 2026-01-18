import React, { useEffect, useState } from 'react';
import { Container, Typography, Box, CircularProgress, Alert, Grid, Card, CardContent, CardActions, Button } from '@mui/material';
import { getAllEvents } from '../../api/event';
import { Link } from 'react-router-dom';

interface Event {
  id: number;
  name: string;
  location: string;
  description: string;
  seats: number;
  ownerId: number;
  _links: any; // HATEOAS
}

const EventListPage: React.FC = () => {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        setLoading(true);
        const data = await getAllEvents();
        if (data && data._embedded) {
            const embedded = data._embedded;
            const eventsList = embedded.events || embedded.dataObjects || embedded.eventList || [];
            if (Array.isArray(eventsList)) {
                setEvents(eventsList);
            } else {
                const firstKey = Object.keys(embedded).find(key => Array.isArray(embedded[key]));
                setEvents(firstKey ? embedded[firstKey] : []);
            }
        } else {
          setEvents([]); 
        }
      } catch (err: any) {
        setError(err.message || 'Failed to fetch events');
      } finally {
        setLoading(false);
      }
    };
    fetchEvents();
  }, []);

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
        Available Events
      </Typography>
      {events.length === 0 ? (
        <Typography variant="h6" align="center" color="text.secondary">
          No events available at the moment.
        </Typography>
      ) : (
        <Grid container spacing={3}>
          {events.map((event) => (
            <Grid item key={event.id} xs={12} sm={6} md={4}>
              <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                <CardContent sx={{ flexGrow: 1 }}>
                  <Typography gutterBottom variant="h5" component="h2">
                    {event.name}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Location: {event.location}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Description: {event.description}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Available Seats: {event.seats || 'N/A'}
                  </Typography>
                </CardContent>
                <CardActions>
                  <Button size="small" component={Link} to={`/events/${event.id}`}>
                    View Details
                  </Button>
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Container>
  );
};

export default EventListPage;
