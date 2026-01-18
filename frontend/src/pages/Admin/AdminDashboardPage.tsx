import React, { useState } from 'react';
import { Container, Typography, TextField, Button, Select, MenuItem, FormControl, InputLabel, Box, Alert, Card, CardContent } from '@mui/material';
import { register } from '../../api/auth';
import useAuthStore from '../../store/authStore';

const AdminDashboardPage: React.FC = () => {
    const { user } = useAuthStore();
    
    // Redirect if not admin (double check besides route protection)
    if (user?.role !== 'admin') {
        return <Alert severity="error">Access Denied. Admins only.</Alert>;
    }

    const [formData, setFormData] = useState({
        username: '',
        password: '',
        email: '',
        role: 'client' // default
    });
    const [status, setStatus] = useState<{ type: 'success' | 'error', message: string } | null>(null);

    const handleChange = (e: any) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setStatus(null);
        try {
            await register(formData.username, formData.password, formData.email, formData.role);
            setStatus({ type: 'success', message: `User ${formData.username} created successfully as ${formData.role}!` });
            setFormData({ username: '', password: '', email: '', role: 'client' }); // Reset form
        } catch (err: any) {
            setStatus({ type: 'error', message: err.response?.data?.detail || 'Failed to create user.' });
        }
    };

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Typography variant="h4" component="h1" gutterBottom align="center">
                Admin Dashboard
            </Typography>
            
            <Card sx={{ mt: 4 }}>
                <CardContent>
                    <Typography variant="h5" gutterBottom>Create New User</Typography>
                    <Typography variant="body2" color="text.secondary" paragraph>
                        Use this form to manually create accounts for Event Owners or Clients.
                    </Typography>

                    {status && (
                        <Alert severity={status.type} sx={{ mb: 2 }}>
                            {status.message}
                        </Alert>
                    )}

                    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <TextField
                            label="Username"
                            name="username"
                            value={formData.username}
                            onChange={handleChange}
                            required
                        />
                        <TextField
                            label="Email"
                            name="email"
                            type="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />
                        <TextField
                            label="Password"
                            name="password"
                            type="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                        />
                        <FormControl fullWidth>
                            <InputLabel>Role</InputLabel>
                            <Select
                                name="role"
                                value={formData.role}
                                label="Role"
                                onChange={handleChange}
                            >
                                <MenuItem value="client">Client</MenuItem>
                                <MenuItem value="owner-event">Event Owner</MenuItem>
                                <MenuItem value="admin">Admin</MenuItem>
                            </Select>
                        </FormControl>
                        <Button type="submit" variant="contained" color="primary" size="large">
                            Create User
                        </Button>
                    </Box>
                </CardContent>
            </Card>
        </Container>
    );
};

export default AdminDashboardPage;
