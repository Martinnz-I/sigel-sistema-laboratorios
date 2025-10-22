import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { authService } from '../api/auth.service';
import { AuthResponse } from '../types/auth.types';

interface AuthState {
  // Estado
  user: AuthResponse | null;
  token: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;

  // Acciones
  login: (credenciales: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  refreshAccessToken: () => Promise<void>;
  clearError: () => void;
  setUser: (user: AuthResponse) => void;
  checkAuth: () => Promise<void>;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      // Estado inicial
      user: null,
      token: null,
      refreshToken: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      // Login
      login: async (credenciales: string, password: string) => {
        set({ isLoading: true, error: null });
        try {
          const response = await authService.login(credenciales, password);
          set({
            user: response,
            token: response.token,
            refreshToken: response.refreshToken,
            isAuthenticated: true,
            isLoading: false,
            error: null,
          });
          // no return
        } catch (error: any) {
          const errorMessage =
            error.response?.data?.message ||
            error.message ||
            'Error al iniciar sesión';
          set({
            error: errorMessage,
            isLoading: false,
            isAuthenticated: false,
          });
          // no throw
        }
      },


      // Logout
      logout: async () => {
        set({ isLoading: true });
        try {
          const { token } = get();
          if (token) {
            await authService.logout(token);
          }
        } catch (error) {
          console.error('Error al cerrar sesión:', error);
        } finally {
          set({
            user: null,
            token: null,
            refreshToken: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,
          });
        }
      },

      // Refrescar token
      refreshAccessToken: async () => {
        const { refreshToken } = get();
        if (!refreshToken) {
          set({ isAuthenticated: false });
          return;
        }

        try {
          const response = await authService.refreshToken(refreshToken);
          set({
            user: response,
            token: response.token,
            refreshToken: response.refreshToken,
            isAuthenticated: true,
            error: null,
          });
        } catch (error) {
          console.error('Error al refrescar token:', error);
          set({
            user: null,
            token: null,
            refreshToken: null,
            isAuthenticated: false,
          });
        }
      },

      // Verificar autenticación
      checkAuth: async () => {
        const { token, refreshToken } = get();

        if (!token) {
          set({ isAuthenticated: false });
          return;
        }

        // Verificar si el token está expirado
        const isExpired = authService.isTokenExpired(token);

        if (isExpired && refreshToken) {
          // Intentar refrescar el token
          await get().refreshAccessToken();
        } else if (isExpired) {
          // Token expirado y no hay refresh token
          set({
            user: null,
            token: null,
            refreshToken: null,
            isAuthenticated: false,
          });
        } else {
          set({ isAuthenticated: true });
        }
      },

      // Limpiar error
      clearError: () => set({ error: null }),

      // Establecer usuario
      setUser: (user: AuthResponse) => set({ user }),
    }),
    {
      name: 'auth-storage',
      storage: createJSONStorage(() => AsyncStorage),
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        refreshToken: state.refreshToken,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);