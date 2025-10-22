/**
 * Tipos para la autenticación
 */

export interface LoginRequest {
  credenciales: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  tokenExpira: string; // LocalDateTime en formato ISO
  email: string;
  nombre: string;
  apellidoPat: string;
  apellidoMat: string;
  rol: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
  error?: string;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}