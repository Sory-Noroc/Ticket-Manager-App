import React, { useEffect, useState } from 'react';
import { 
    Container, Typography, Box, CircularProgress, Alert, Button, Grid, Card, CardContent, CardActions,
    Dialog, DialogTitle, DialogContent, DialogActions, TextField
} from '@mui/material';
import { 
    getAllEvents, createEvent, updateEvent, deleteEvent, 
    getAllEventPackages, createEventPackage, updateEventPackage, deleteEventPackage 
} from '../../api/event';
import useAuthStore from '../../store/authStore';
import { useNavigate, Link } from 'react-router-dom';

interface Event {
    id: number;
    name: string;
    location: string;
    description: string;
    seats: number;
    ownerId: number;
    _links: any;
}

interface EventPackage {
    id: number;
    name: string;
    location: string;
    description: string;
    ownerId: number;
    _links: any;
}

const OwnerDashboardPage: React.FC = () => {
    const { user, token } = useAuthStore();
    const navigate = useNavigate();
    const [events, setEvents] = useState<Event[]>([]);
    const [packages, setPackages] = useState<EventPackage[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [openEventDialog, setOpenEventDialog] = useState(false);
    const [editingEvent, setEditingEvent] = useState<Event | null>(null);
    const [eventFormData, setEventFormData] = useState({
        name: '',
        location: '',
        description: '',
        seats: 0
    });

    const [openPackageDialog, setOpenPackageDialog] = useState(false);
    const [editingPackage, setEditingPackage] = useState<EventPackage | null>(null);
    const [packageFormData, setPackageFormData] = useState({
        name: '',
        location: '',
        description: ''
    });

    const fetchOwnerData = async () => {
        if (!user?.email || !token || user.role !== 'owner-event') {
            navigate('/login');
            return;
        }
        try {
            setLoading(true);

            const extractList = (data: any, defaultKey: string) => {
                if (!data || !data._embedded) return [];
                const embedded = data._embedded;
                if (embedded[defaultKey]) return embedded[defaultKey];
                if (embedded.dataObjects) return embedded.dataObjects;
                const firstKey = Object.keys(embedded).find(key => Array.isArray(embedded[key]));
                return firstKey ? embedded[firstKey] : [];
            };

            const allEventsData = await getAllEvents();
            const allEvents = extractList(allEventsData, 'events');
            
            // TODO: Filter by ownerId when available in JWT. Currently showing all.
            setEvents(allEvents); 

            const allPackagesData = await getAllEventPackages();
            const allPackages = extractList(allPackagesData, 'packets');

            setPackages(allPackages);

        } catch (err: any) {
            setError(err.message || 'Failed to fetch owner data');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (user) {
             fetchOwnerData();
        }
    }, [user, token, navigate]);

    const handleOpenCreateEvent = () => {
        setEditingEvent(null);
        setEventFormData({ name: '', location: '', description: '', seats: 0 });
        setOpenEventDialog(true);
    };

    const handleOpenEditEvent = (event: Event) => {
        setEditingEvent(event);
        setEventFormData({
            name: event.name,
            location: event.location,
            description: event.description,
            seats: event.seats
        });
        setOpenEventDialog(true);
    };

    const handleCloseEventDialog = () => {
        setOpenEventDialog(false);
        setEditingEvent(null);
    };

    const handleSaveEvent = async () => {
        if (!token) return;
        try {
            const eventPayload = { ...eventFormData, ownerId: 0 }; 
            
            if (editingEvent) {
                await updateEvent(editingEvent.id.toString(), eventPayload, token);
            } else {
                await createEvent(eventPayload, token);
            }
            fetchOwnerData();
            handleCloseEventDialog();
        } catch (err: any) {
            alert(`Failed to save event: ${err.message || err.response?.data?.detail}`);
        }
    };

    const handleDeleteEvent = async (eventId: number) => {
        if (!token) return;
        if (window.confirm(`Are you sure you want to delete event ${eventId}?`)) {
            try {
                await deleteEvent(eventId.toString(), token);
                fetchOwnerData();
            } catch (err: any) {
                alert(`Failed to delete event: ${err.message || err.response?.data?.detail}`);
            }
        }
    };

    // --- Package Handlers ---

    const handleOpenCreatePackage = () => {
        setEditingPackage(null);
        setPackageFormData({ name: '', location: '', description: '' });
        setOpenPackageDialog(true);
    };

    const handleOpenEditPackage = (pkg: EventPackage) => {
        setEditingPackage(pkg);
        setPackageFormData({
            name: pkg.name,
            location: pkg.location,
            description: pkg.description
        });
        setOpenPackageDialog(true);
    };

    const handleClosePackageDialog = () => {
        setOpenPackageDialog(false);
        setEditingPackage(null);
    };

    const handleSavePackage = async () => {
        if (!token) return;
        try {
            const packagePayload = { ...packageFormData, ownerId: 0 }; // ownerId handled by backend ideally
            
            if (editingPackage) {
                await updateEventPackage(editingPackage.id.toString(), packagePayload, token);
            } else {
                await createEventPackage(packagePayload, token);
            }
            fetchOwnerData();
            handleClosePackageDialog();
        } catch (err: any) {
            alert(`Failed to save package: ${err.message || err.response?.data?.detail}`);
        }
    };

    const handleDeletePackage = async (packageId: number) => {
        if (!token) return;
        if (window.confirm(`Are you sure you want to delete package ${packageId}?`)) {
            try {
                await deleteEventPackage(packageId.toString(), token);
                fetchOwnerData();
            } catch (err: any) {
                alert(`Failed to delete package: ${err.message || err.response?.data?.detail}`);
            }
        }
    };


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
                Owner Dashboard
            </Typography>

            <Box sx={{ my: 4 }}>
                <Typography variant="h5" component="h2" gutterBottom>
                    Your Events
                    <Button variant="contained" color="primary" sx={{ ml: 2 }} onClick={handleOpenCreateEvent}>
                        Create New Event
                    </Button>
                </Typography>
                {events.length === 0 ? (
                    <Typography>No events created yet.</Typography>
                ) : (
                    <Grid container spacing={3}>
                        {events.map((event) => (
                            <Grid item key={event.id} xs={12} sm={6} md={4}>
                                <Card>
                                    <CardContent>
                                        <Typography variant="h6">{event.name}</Typography>
                                        <Typography variant="body2">{event.location}</Typography>
                                        <Typography variant="body2">Seats: {event.seats}</Typography>
                                    </CardContent>
                                    <CardActions>
                                        <Button size="small" onClick={() => handleOpenEditEvent(event)}>Edit</Button>
                                        <Button size="small" color="error" onClick={() => handleDeleteEvent(event.id)}>Delete</Button>
                                        <Button size="small" component={Link} to={`/owner/events/${event.id}/ticket-holders`}>
                                            View Ticket Holders
                                        </Button>
                                    </CardActions>
                                </Card>
                            </Grid>
                        ))}
                    </Grid>
                )}
            </Box>

            <Box sx={{ my: 4 }}>
                <Typography variant="h5" component="h2" gutterBottom>
                    Your Packages
                    <Button variant="contained" color="primary" sx={{ ml: 2 }} onClick={handleOpenCreatePackage}>
                        Create New Package
                    </Button>
                </Typography>
                {packages.length === 0 ? (
                    <Typography>No packages created yet.</Typography>
                ) : (
                    <Grid container spacing={3}>
                        {packages.map((pkg) => (
                            <Grid item key={pkg.id} xs={12} sm={6} md={4}>
                                <Card>
                                    <CardContent>
                                        <Typography variant="h6">{pkg.name}</Typography>
                                        <Typography variant="body2">{pkg.location}</Typography>
                                    </CardContent>
                                    <CardActions>
                                        <Button size="small" onClick={() => handleOpenEditPackage(pkg)}>Edit</Button>
                                        <Button size="small" color="error" onClick={() => handleDeletePackage(pkg.id)}>Delete</Button>
                                    </CardActions>
                                </Card>
                            </Grid>
                        ))}
                    </Grid>
                )}
            </Box>

            {/* Event Dialog */}
            <Dialog open={openEventDialog} onClose={handleCloseEventDialog}>
                <DialogTitle>{editingEvent ? 'Edit Event' : 'Create New Event'}</DialogTitle>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="Event Name"
                        fullWidth
                        variant="outlined"
                        value={eventFormData.name}
                        onChange={(e) => setEventFormData({ ...eventFormData, name: e.target.value })}
                    />
                    <TextField
                        margin="dense"
                        label="Location"
                        fullWidth
                        variant="outlined"
                        value={eventFormData.location}
                        onChange={(e) => setEventFormData({ ...eventFormData, location: e.target.value })}
                    />
                    <TextField
                        margin="dense"
                        label="Description"
                        fullWidth
                        variant="outlined"
                        multiline
                        rows={3}
                        value={eventFormData.description}
                        onChange={(e) => setEventFormData({ ...eventFormData, description: e.target.value })}
                    />
                    <TextField
                        margin="dense"
                        label="Available Seats"
                        type="number"
                        fullWidth
                        variant="outlined"
                        value={eventFormData.seats}
                        onChange={(e) => setEventFormData({ ...eventFormData, seats: parseInt(e.target.value) })}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseEventDialog}>Cancel</Button>
                    <Button onClick={handleSaveEvent} variant="contained">Save</Button>
                </DialogActions>
            </Dialog>

            {/* Package Dialog */}
            <Dialog open={openPackageDialog} onClose={handleClosePackageDialog}>
                <DialogTitle>{editingPackage ? 'Edit Package' : 'Create New Package'}</DialogTitle>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="Package Name"
                        fullWidth
                        variant="outlined"
                        value={packageFormData.name}
                        onChange={(e) => setPackageFormData({ ...packageFormData, name: e.target.value })}
                    />
                    <TextField
                        margin="dense"
                        label="Location"
                        fullWidth
                        variant="outlined"
                        value={packageFormData.location}
                        onChange={(e) => setPackageFormData({ ...packageFormData, location: e.target.value })}
                    />
                    <TextField
                        margin="dense"
                        label="Description"
                        fullWidth
                        variant="outlined"
                        multiline
                        rows={3}
                        value={packageFormData.description}
                        onChange={(e) => setPackageFormData({ ...packageFormData, description: e.target.value })}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClosePackageDialog}>Cancel</Button>
                    <Button onClick={handleSavePackage} variant="contained">Save</Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default OwnerDashboardPage;
