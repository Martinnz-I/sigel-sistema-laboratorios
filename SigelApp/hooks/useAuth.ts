import { useEffect, useCallback } from 'react';
import { useAuthStore } from '../store/authStore';
import { handleApiError, devLog, errorLog } from '../utils/errorHandler';

/**
 * Hook personalizado para manejar autenticación
 * Proporciona métodos convenientes y manejo de errores mejorado
 */
export const useAuth = () => {
  const {
    user,
    token,
    isAuthenticated,
    isLoading,
    error,
    login: storeLogin,
    logout: storeLogout,
    refreshAccessToken,
    clearError,
    checkAuth,
  } = useAuthStore();

  // Verificar autenticación al montar
  useEffect(() => {
    checkAuth();
  }, []);

  /**
   * Login con manejo de errores mejorado
   */
  const login = useCallback(
    async (credenciales: string, password: string) => {
      const { error: storeError } = useAuthStore.getState(); // ✅ obtiene el error actual del store
      try {
        devLog('Intentando login...', { credenciales });
        await storeLogin(credenciales, password);
        devLog('Login exitoso', { user: user?.email });
        return { success: true };
      } catch (err: any) {
        // Usa el error del backend o el error guardado en el store
        const errorMessage = err?.response?.data?.message || storeError || 'Error al iniciar sesión';
        errorLog('Error en login', err);
        return { success: false, error: errorMessage };
      }
    },
    [storeLogin, user]
  );

  /**
   * Logout con confirmación
   */
  const logout = useCallback(async () => {
    try {
      devLog('Cerrando sesión...');
      await storeLogout();
      devLog('Sesión cerrada');
      return { success: true };
    } catch (err) {
      errorLog('Error en logout', err);
      // Aún así cerramos la sesión localmente
      await storeLogout();
      return { success: false, error: 'Error al cerrar sesión' };
    }
  }, [storeLogout]);

  /**
   * Refrescar token manualmente
   */
  const refreshToken = useCallback(async () => {
    try {
      devLog('Refrescando token...');
      await refreshAccessToken();
      devLog('Token refrescado');
      return { success: true };
    } catch (err) {
      errorLog('Error al refrescar token', err);
      return { success: false, error: 'Error al refrescar sesión' };
    }
  }, [refreshAccessToken]);

  /**
   * Verificar si el token está próximo a expirar
   */
  const shouldRefreshToken = useCallback((): boolean => {
    if (!token) return false;

    try {
      // Decodificar token para verificar expiración
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );

      const { exp } = JSON.parse(jsonPayload);
      const now = Date.now() / 1000;

      // Refrescar si faltan menos de 5 minutos
      return exp - now < 300;
    } catch (err) {
      errorLog('Error al verificar expiración del token', err);
      return false;
    }
  }, [token]);

  /**
   * Obtener nombre completo del usuario
   */
  const getFullName = useCallback((): string => {
    if (!user) return '';
    return `${user.nombre} ${user.apellidoPat} ${user.apellidoMat}`.trim();
  }, [user]);

  /**
   * Verificar si el usuario tiene un rol específico
   */
  const hasRole = useCallback(
    (role: string): boolean => {
      if (!user?.rol) return false;
      return user.rol.toLowerCase() === role.toLowerCase();
    },
    [user]
  );

  /**
   * Obtener iniciales del usuario para avatar
   */
  const getUserInitials = useCallback((): string => {
    if (!user) return '?';
    const firstInitial = user.nombre?.charAt(0) || '';
    const lastInitial = user.apellidoPat?.charAt(0) || '';
    return `${firstInitial}${lastInitial}`.toUpperCase();
  }, [user]);

  return {
    // Estado
    user,
    token,
    isAuthenticated,
    isLoading,
    error,

    // Acciones
    login,
    logout,
    refreshToken,
    clearError,

    // Utilidades
    shouldRefreshToken,
    getFullName,
    hasRole,
    getUserInitials,
  };
};