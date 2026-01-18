import axios from 'axios';

const EVENT_API_BASE_URL = import.meta.env.VITE_EVENT_API_BASE_URL;

const eventApi = axios.create({
  baseURL: EVENT_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const createTicket = async (ticketData: any) => {
    const response = await eventApi.post("/api/event-manager/ticket", ticketData);
    return response.data;
};

export const getAllEvents = async () => {
  const response = await eventApi.get('/api/event-manager/events');
  return response.data;
};

export const getEventById = async (id: string) => {
  const response = await eventApi.get(`/api/event-manager/events/${id}`);
  return response.data;
};

export const getAllEventPackages = async () => {
    const response = await eventApi.get('/api/event-manager/event-packets');
    return response.data;
  };
  
export const getEventPackageById = async (id: string) => {
    const response = await eventApi.get(`/api/event-manager/event-packets/${id}`);
    return response.data;
};

// Cereri cu token
export const createEvent = async (eventData: any, token: string) => {
  const response = await eventApi.post('/api/event-manager/events', eventData, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const updateEvent = async (id: string, eventData: any, token: string) => {
  const response = await eventApi.put(`/api/event-manager/events/${id}`, eventData, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const deleteEvent = async (id: string, token: string) => {
  const response = await eventApi.delete(`/api/event-manager/events/${id}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

// Event Package
export const createEventPackage = async (packageData: any, token: string) => {
    const response = await eventApi.post('/api/event-manager/event-packets', packageData, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  };
  
export const updateEventPackage = async (id: string, packageData: any, token: string) => {
    const response = await eventApi.put(`/api/event-manager/event-packets/${id}`, packageData, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
};

export const deleteEventPackage = async (id: string, token: string) => {
    const response = await eventApi.delete(`/api/event-manager/event-packets/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
};
