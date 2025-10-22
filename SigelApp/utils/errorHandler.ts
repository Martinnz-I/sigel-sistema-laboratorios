import { AxiosError } from 'axios';
import { ErrorResponse } from '../types/auth.types';

/**
 * Maneja errores de Axios y devuelve un mensaje amigable
 */
export const handleApiError = (error: unknown): string => {
  if (error instanceof AxiosError) {
    const axiosError = error as AxiosError<ErrorResponse>;
    
    // Error de respuesta del servidor
    if (axiosError.response) {
      const { status, data } = axiosError.response;
      
      switch (status) {
        case 400:
          return data?.message || 'Datos inválidos. Verifica la información ingresada.';
        
        case 401:
          return data?.message || 'Credenciales incorrectas. Verifica tu usuario y contraseña.';
        
        case 403:
          return 'No tienes permisos para realizar esta acción.';
        
        case 404:
          return 'El recurso solicitado no existe.';
        
        case 409:
          return data?.message || 'Ya existe un usuario con estas credenciales.';
        
        case 500:
          return 'Error interno del servidor. Intenta más tarde.';
        
        case 503:
          return 'Servicio no disponible. Intenta más tarde.';
        
        default:
          return data?.message || `Error ${status}: ${data?.error || 'Error desconocido'}`;
      }
    }
    
    // Error de red (sin respuesta)
    if (axiosError.request) {
      return 'No se pudo conectar con el servidor. Verifica tu conexión a internet.';
    }
    
    // Error en la configuración de la petición
    return 'Error al procesar la solicitud.';
  }
  
  // Error genérico
  if (error instanceof Error) {
    return error.message;
  }
  
  return 'Ha ocurrido un error inesperado.';
};

/**
 * Muestra mensajes de error apropiados según el campo
 */
export const getFieldErrorMessage = (field: string, error: string): string => {
  const messages: Record<string, Record<string, string>> = {
    credenciales: {
      required: 'Ingresa tu matrícula o email',
      invalid: 'Formato de email o matrícula inválido',
      min: 'Debe tener al menos 3 caracteres',
    },
    password: {
      required: 'Ingresa tu contraseña',
      min: 'La contraseña debe tener al menos 6 caracteres',
      weak: 'Contraseña muy débil',
    },
  };
  
  return messages[field]?.[error] || error;
};

/**
 * Valida formato de email
 */
export const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

/**
 * Valida formato de matrícula (ajusta según tus necesidades)
 */
export const isValidMatricula = (matricula: string): boolean => {
  // Ejemplo: matrícula debe ser numérica de 8-10 dígitos
  const matriculaRegex = /^[0-9]{8,10}$/;
  return matriculaRegex.test(matricula);
};

/**
 * Determina si las credenciales son email o matrícula
 */
export const getCredentialType = (credential: string): 'email' | 'matricula' | 'unknown' => {
  if (isValidEmail(credential)) {
    return 'email';
  }
  if (isValidMatricula(credential)) {
    return 'matricula';
  }
  return 'unknown';
};

/**
 * Formatea mensajes de éxito
 */
export const getSuccessMessage = (action: string, data?: any): string => {
  const messages: Record<string, string> = {
    login: `¡Bienvenido ${data?.nombre || ''}!`,
    logout: 'Sesión cerrada correctamente',
    refresh: 'Sesión renovada',
    register: 'Registro exitoso. Revisa tu email para verificar tu cuenta.',
  };
  
  return messages[action] || 'Operación exitosa';
};

/**
 * Logger para desarrollo (desactivar en producción)
 */
export const devLog = (message: string, data?: any) => {
  if (__DEV__) {
    console.log(`[AUTH] ${message}`, data || '');
  }
};

/**
 * Logger de errores
 */
export const errorLog = (message: string, error?: any) => {
  if (__DEV__) {
    console.error(`[AUTH ERROR] ${message}`, error || '');
  }
  // En producción, aquí podrías enviar a un servicio de logging como Sentry
};