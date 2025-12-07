import { UserRole } from "./enums";

export interface UserDto {
  matricula?: string,
  claveDocente?: string,
  email: string,
  nombre: string,
  apellidos: string,
  rol: UserRole,
  semestre?: number,
  grupo?: string,
  especialidad?: string,
}

export interface RegistroRequest {
  matricula?: string
  email: string
  password: string
  confirmarPassword: string
  nombre: string
  apellidoPaterno: string
  apellidoMaterno: string
  grupoId?: number
  claveDocente?: string
  rol: UserRole
}

export interface LoginRequest {
  credenciales: string
  password: string
}

export interface VerificarEmailResponse {
  success: boolean,
  message: string
}

export interface RefreshTokenRequest {
  token: string
}

export interface VerificacionEmailRequest {
  token: string
}

export interface ReenviarVerificacionRequest {
  email: string
}

export interface RecuperarPasswordRequest {
  email: string
}

export interface AuthResponse {
  token: string
  refreshToken: string
  tokenExpira: string
  usuario: UserDto
}

export interface RefreshTokenRequest {
  refreshToken: string;
}