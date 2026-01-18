import { create } from 'zustand';
import { jwtDecode } from 'jwt-decode';

interface DecodedUser {
  id: string; // This is the username/email
  email: string;
  role: string;
  exp: number;
}

interface AuthState {
  token: string | null;
  user: DecodedUser | null;
  isAuthenticated: boolean;
  login: (token: string) => void;
  logout: () => void;
  getRole: () => string | null;
}

const useAuthStore = create<AuthState>((set, get) => ({
  token: localStorage.getItem('token'),
  user: localStorage.getItem('token') ? jwtDecode<DecodedUser>(localStorage.getItem('token') as string) : null,
  isAuthenticated: !!localStorage.getItem('token'),

  login: (token: string) => {
    localStorage.setItem('token', token);
    const decodedUser = jwtDecode<DecodedUser>(token);
    set({ token, user: decodedUser, isAuthenticated: true });
  },

  logout: () => {
    localStorage.removeItem('token');
    set({ token: null, user: null, isAuthenticated: false });
  },

  getRole: () => {
    return get().user?.role || null;
  },
}));

export default useAuthStore;
