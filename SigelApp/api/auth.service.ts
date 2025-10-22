import axios, { AxiosError } from 'axios';
import { AuthResponse, LoginRequest, ApiResponse, ErrorResponse } from '../types/auth.types';
import { jwtDecode } from 'jwt-decode';

// Configura tu URL base del backend
const API_BASE_URL = 'http://192.168.0.8:8080'; // Cambia esto por tu URL

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para manejar errores de respuesta
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ErrorResponse>) => {
    if (error.response) {
      // El servidor respondió con un código de estado fuera del rango 2xx
      const errorData = error.response.data;
      throw {
        message: errorData.message || 'Error en la solicitud',
        status: errorData.status,
        error: errorData.error,
      };
    } else if (error.request) {
      // La solicitud se hizo pero no se recibió respuesta
      throw {
        message: 'No se pudo conectar con el servidor',
        status: 0,
        error: 'Network Error',
      };
    } else {
      // Algo sucedió al configurar la solicitud
      throw {
        message: error.message || 'Error desconocido',
        status: 0,
        error: 'Unknown Error',
      };
    }
  }
);

export const authService = {
  /**
   * Login de usuario
   */
  login: async (credenciales: string, password: string): Promise<AuthResponse> => {
  const loginData: LoginRequest = {
    credenciales,
    password,
  };

  const response = await api.post<ApiResponse<AuthResponse>>(
    '/auth/login',
    loginData
  );

  if (response.data.success && response.data.data) {
    return response.data.data;
  } else {
    throw new Error(response.data.message || 'Error al iniciar sesión');
  }
},

  logout: async (token: string): Promise<void> => {
    try {
      await api.post(
        '/auth/logout',
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
    } catch (error) {
      console.error('Error en logout:', error);
      throw error;
    }
  },

  /**
   * Refrescar token de acceso
   */
  refreshToken: async (refreshToken: string): Promise<AuthResponse> => {
    try {
      const response = await api.post<ApiResponse<AuthResponse>>(
        '/auth/refresh',
        { refreshToken }
      );

      if (response.data.success && response.data.data) {
        return response.data.data;
      } else {
        throw new Error(response.data.message || 'Error al refrescar token');
      }
    } catch (error) {
      console.error('Error al refrescar token:', error);
      throw error;
    }
  },

  /**
   * Verificar si el token está expirado
   */
  isTokenExpired: (token: string): boolean => {
    try {
      const decoded: any = jwtDecode(token);
      const currentTime = Date.now() / 1000;
      
      // Considerar expirado si faltan menos de 5 minutos
      return decoded.exp < currentTime + 300;
    } catch (error) {
      console.error('Error al decodificar token:', error);
      return true;
    }
  },

  /**
   * Obtener información del usuario desde el token
   */
  getUserFromToken: (token: string): any => {
    try {
      return jwtDecode(token);
    } catch (error) {
      console.error('Error al obtener usuario del token:', error);
      return null;
    }
  },
};