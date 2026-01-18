import axios from 'axios';

const CLIENT_API_BASE_URL = import.meta.env.VITE_CLIENT_API_BASE_URL;

const clientApi = axios.create({
  baseURL: CLIENT_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const getClientProfile = async (email: string, token: string) => {
  const response = await clientApi.get(`/clients/${email}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const updateClientProfile = async (email: string, profileData: any, token: string) => {
  const response = await clientApi.put(`/clients/${email}`, profileData, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const addTicketToClient = async (email: string, ticketCode: string, token: string) => {
  const response = await clientApi.put(`/clients/${email}/tickets/${ticketCode}`, {}, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const getTicketHoldersForEvent = async (eventId: number, token: string) => {
    const response = await clientApi.get(`/clients/ticket-holders/${eventId}`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });
    return response.data;
};
