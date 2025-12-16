import axios from 'axios';
import { JwtAuthResponse, UserResponse, LoginRequest, RegisterRequest } from '../types';

const API_URL = 'http://localhost:8080/api/v1/';

const api = axios.create({
    baseURL: API_URL,
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export const authApi = {
    login: async (username: string, password: string): Promise<JwtAuthResponse> => {
        const request: LoginRequest = { username, password };
        const response = await api.post('/auth', request);
        return response.data;
    },

    register: async (username: string, password: string): Promise<UserResponse> => {
        const request: RegisterRequest = { username, password };
        const response = await api.post('/register', request);
        return response.data;
    },

    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    },

    getCurrentUser: (): UserResponse | null => {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    },

    isAuthenticated: (): boolean => {
        return !!localStorage.getItem('token');
    }
};