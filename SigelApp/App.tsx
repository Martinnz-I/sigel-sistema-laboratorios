import React, { useEffect, useState } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { useAuthStore } from './store/useAuthStore';
import { tokenService } from './api/tokensjwt.service';
import { navigationRef } from './navigation/navigation.service';
import LoginScreen from './screens/LoginScreen/LoginScreen';
import RegisterScreen from './screens/RegisterScreen/RegisterScreen';
import MainLayout from './screens/MainLayout/MainLayout';

export type RootStackParamList = {
    Login: undefined;
    Register: undefined;
    Home: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();

export default function App() {
    const { isAuthenticated, checkAuth, usuario } = useAuthStore();
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const initAuth = async () => {
            await checkAuth(); // revisa tokens y refresca si es necesario
            setLoading(false);
            await tokenService.clearTokens(); // COMENTADO: solo para desarrollo
        };
        initAuth();
    }, []);

    if (loading) return null; // o splash screen

    return (
        <GestureHandlerRootView style={{ flex: 1 }}>
            <NavigationContainer ref={navigationRef}>
                <Stack.Navigator screenOptions={{ headerShown: false }}>
                    {isAuthenticated && usuario ? (
                        <Stack.Screen name="Home">
                            {() => <MainLayout />}
                        </Stack.Screen>
                    ) : (
                        <>
                            <Stack.Screen name="Login" component={LoginScreen} />
                            <Stack.Screen name="Register" component={RegisterScreen} />
                        </>
                    )}
                </Stack.Navigator>
            </NavigationContainer>
        </GestureHandlerRootView>
    );
}