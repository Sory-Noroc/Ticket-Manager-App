import axios from 'axios';

const CLIENT_API_BASE_URL = import.meta.env.VITE_CLIENT_API_BASE_URL;

const authApi = axios.create({
  baseURL: CLIENT_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const login = async (username: string, password: string) => {
  const response = await authApi.post('/auth/login', { username, password });
  return response.data;
};

export const register = async (username: string, password: string, email: string, role?: string) => {
  const response = await authApi.post('/auth/register', { username, password, email, role });
  return response.data;
};
