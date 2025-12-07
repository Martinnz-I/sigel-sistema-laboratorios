import axios, { AxiosResponse } from 'axios';
import Config from 'react-native-config';
import { AuthResponse, LoginRequest, RegistroRequest, VerificarEmailResponse } from '../types/auth.types';
import { ApiResponse } from '../types/global.types';

const API_BASE_URL = `${Config.API_URL}/auth`;

export const authService = {
    /**
     * Iniciar sesión
     * @param credentials Objeto con credenciales (usuario/email y password)
     */
    login: async (credenciales: LoginRequest): Promise<AuthResponse> => {
        try {
            const response = await axios.post<ApiResponse<AuthResponse>>(`${API_BASE_URL}/login`, credenciales);

            console.log("Respuesta: " + JSON.stringify(response.data, null, 2));
            return response.data.data!;
        } catch (err: any) {
            // Lanza el error para que el store lo capture
            throw err;
        }
    },

    /**
 * Registrar nuevo usuario
 * @param datosRegistro Objeto con los datos del nuevo usuario
 */
    register: async (datosRegistro: RegistroRequest): Promise<ApiResponse<void>> => {
        try {
            const response = await axios.post<ApiResponse<void>>(
                `${API_BASE_URL}/registro`,
                datosRegistro
            );

            console.log("Respuesta de registro:", JSON.stringify(response.data, null, 2));

            return response.data;
        } catch (err: any) {
            // Lanza el error para que el store lo capture o lo maneje el frontend
            throw err;
        }
    },

    /**
     * Cerrar sesión
     * @param token Token de acceso
     */
    logout: async (token: string): Promise<void> => {
        try {
            await axios.post(`${API_BASE_URL}/logout`, null, {
                headers: { Authorization: `Bearer ${token}` },
            });
        } catch (err: any) {
            throw err;
        }
    },

    /**
     * Refrescar token de acceso
     * @param refreshToken Token de refresco
     */
    refreshToken: async (refreshToken: string): Promise<AuthResponse> => {
        try {
            const response = await axios.post<ApiResponse<AuthResponse>>(`${API_BASE_URL}/refresh`, {
                refreshToken,
            });
            return response.data.data!;
        } catch (err: any) {
            throw err;
        }
    },

    /**
     * Verificar si el token está expirado
     * @param token JWT de acceso
     */
    isTokenExpired: (token: string): boolean => {
        try {
            const payload = JSON.parse(atob(token.split('.')[1])); // decode JWT payload
            const exp = payload.exp * 1000; // exp en milisegundos
            return Date.now() > exp;
        } catch {
            return true; // Si el token no es válido, se considera expirado
        }
    },
};