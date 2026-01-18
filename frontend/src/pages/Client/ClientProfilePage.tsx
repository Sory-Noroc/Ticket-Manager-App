import React, { useEffect, useState } from 'react';
import { Container, Typography, Box, TextField, Button, CircularProgress, Alert, Switch, FormControlLabel } from '@mui/material';
import { getClientProfile, updateClientProfile } from '../../api/client';
import useAuthStore from '../../store/authStore';
import { useNavigate } from 'react-router-dom';

interface ClientProfile {
  email: string;
  firstName: string;
  lastName: string;
  isNamePublic: boolean;
  socialMedia: { [key: string]: string };
}

const ClientProfilePage: React.FC = () => {
  const { user, token } = useAuthStore();
  const navigate = useNavigate();
  const [profile, setProfile] = useState<ClientProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    if (!user?.email || !token) {
      navigate('/login');
      return;
    }

    const fetchProfile = async () => {
      try {
        setLoading(true);
        const data = await getClientProfile(user.email, token);
        setProfile({
          ...data,
          socialMedia: data.socialMedia || {},  // Vedem ca socialMedia sa fie un obiect
        });
      } catch (err: any) {
        setError(err.message || 'Failed to fetch profile');
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, [user?.email, token, navigate]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setProfile((prev) => {
      if (!prev) return null;
      if (type === 'checkbox') {
        return { ...prev, [name]: checked };
      }
      return { ...prev, [name]: value };
    });
  };

  const handleSocialMediaChange = (platform: string, value: string) => {
    setProfile((prev) => {
      if (!prev) return null;
      return {
        ...prev,
        socialMedia: {
          ...prev.socialMedia,
          [platform]: value,
        },
      };
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user?.email || !token || !profile) return;

    setSaving(true);
    setError(null);
    setSuccess(null);
    try {
      await updateClientProfile(user.email, profile, token);
      setSuccess('Profile updated successfully!');
    } catch (err: any) {
      setError(err.response?.data?.detail || err.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <Container maxWidth="md" sx={{ mt: 4, display: 'flex', justifyContent: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  if (error && !profile) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Alert severity="error">{error}</Alert>
      </Container>
    );
  }

  if (!profile) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Alert severity="info">No profile data found.</Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom align="center">
        My Profile
      </Typography>
      <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
        <TextField
          margin="normal"
          fullWidth
          id="email"
          label="Email Address"
          name="email"
          value={profile.email}
          InputProps={{ readOnly: true }}
          sx={{ mb: 2 }}
        />
        <TextField
          margin="normal"
          fullWidth
          id="firstName"
          label="First Name"
          name="firstName"
          value={profile.firstName || ''}
          onChange={handleChange}
          sx={{ mb: 2 }}
        />
        <TextField
          margin="normal"
          fullWidth
          id="lastName"
          label="Last Name"
          name="lastName"
          value={profile.lastName || ''}
          onChange={handleChange}
          sx={{ mb: 2 }}
        />
        <FormControlLabel
          control={
            <Switch
              checked={profile.isNamePublic}
              onChange={handleChange}
              name="isNamePublic"
              color="primary"
            />
          }
          label="Make Name Public"
          sx={{ mb: 2 }}
        />

        <Typography variant="h6" component="h2" sx={{ mt: 3, mb: 2 }}>
          Social Media Links
        </Typography>
        <TextField
          margin="normal"
          fullWidth
          id="linkedin"
          label="LinkedIn"
          name="linkedin"
          value={profile.socialMedia.linkedin || ''}
          onChange={(e) => handleSocialMediaChange('linkedin', e.target.value)}
          sx={{ mb: 2 }}
        />
        <TextField
          margin="normal"
          fullWidth
          id="twitter"
          label="Twitter"
          name="twitter"
          value={profile.socialMedia.twitter || ''}
          onChange={(e) => handleSocialMediaChange('twitter', e.target.value)}
          sx={{ mb: 2 }}
        />

        {error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}
        {success && <Alert severity="success" sx={{ mt: 2 }}>{success}</Alert>}

        <Button
          type="submit"
          fullWidth
          variant="contained"
          sx={{ mt: 3, mb: 2 }}
          disabled={saving}
        >
          {saving ? <CircularProgress size={24} /> : 'Save Profile'}
        </Button>
      </Box>
    </Container>
  );
};

export default ClientProfilePage;
