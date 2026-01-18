import React, { useEffect, useState } from 'react';
import { Container, Typography, Box, CircularProgress, Alert, Card, CardContent, Grid } from '@mui/material';
import { getClientProfile } from '../../api/client';
import useAuthStore from '../../store/authStore';
import { useNavigate } from 'react-router-dom';

interface Ticket {
    code: string;
    eventName: string;
    eventLocation: string;
    isPackage: boolean;
    includedEvents?: Array<{ name: string; location: string }>;
}

const ClientTicketsPage: React.FC = () => {
    const { user, token } = useAuthStore();
    const navigate = useNavigate();
    const [tickets, setTickets] = useState<Ticket[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!user?.email || !token) {
            navigate('/login');
            return;
        }

        const fetchTickets = async () => {
            try {
                setLoading(true);
                const clientProfile = await getClientProfile(user.email, token);
                setTickets(clientProfile.tickets || []);
            } catch (err: any) {
                setError(err.message || 'Failed to fetch tickets');
            } finally {
                setLoading(false);
            }
        };
        fetchTickets();
    }, [user?.email, token, navigate]);

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
                My Tickets
            </Typography>
            {tickets.length === 0 ? (
                <Typography variant="h6" align="center" color="text.secondary">
                    You don't have any tickets yet.
                </Typography>
            ) : (
                <Grid container spacing={3}>
                    {tickets.map((ticket) => (
                        <Grid item key={ticket.code} xs={12} sm={6} md={4}>
                            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                                <CardContent sx={{ flexGrow: 1 }}>
                                    <Typography gutterBottom variant="h5" component="h2">
                                        {ticket.eventName}
                                    </Typography>
                                    <Typography variant="body2" color="text.secondary">
                                        Code: {ticket.code}
                                    </Typography>
                                    <Typography variant="body2" color="text.secondary">
                                        Location: {ticket.eventLocation}
                                    </Typography>
                                    {ticket.isPackage && ticket.includedEvents && (
                                        <Box sx={{ mt: 2 }}>
                                            <Typography variant="subtitle1">Included Events:</Typography>
                                            {ticket.includedEvents.map((event, index) => (
                                                <Typography variant="body2" key={index}>
                                                    - {event.name} ({event.location})
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

export default ClientTicketsPage;
