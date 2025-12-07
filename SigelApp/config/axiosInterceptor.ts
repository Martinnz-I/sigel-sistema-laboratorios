import axios, { AxiosInstance, AxiosError, InternalAxiosRequestConfig } from 'axios';
import Config from 'react-native-config';
import { useAuthStore } from '../store/useAuthStore';
import { tokenService } from '../api/tokensjwt.service';

// URL base de tu API
export const API_BASE_URL = Config.API_URL;

// Crear instancia de axios
export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - Añadir token automáticamente
apiClient.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    const token = await tokenService.getAccessToken();
    
    // Si existe un token, lo añadimos al header
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Manejar refresh token automáticamente
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // Si el error es 401 y no hemos intentado refrescar el token
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // Intentar refrescar el token
        const { refreshAccessToken } = useAuthStore.getState();
        const token = await tokenService.getAccessToken();
        await refreshAccessToken();

        // Obtener el nuevo token
        const newToken = await tokenService.getAccessToken();
        
        if (newToken && originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
        }

        // Reintentar la petición original
        return apiClient(originalRequest);
      } catch (refreshError) {
        // Si falla el refresh, hacer logout
        const { logout } = useAuthStore.getState();
        await logout();
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;