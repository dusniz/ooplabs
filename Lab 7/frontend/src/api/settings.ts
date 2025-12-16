import axios from 'axios';
import { FactoryResponse, FactoryRequest } from '../types';

const API_URL = 'http://localhost:8080/api/v1/settings';

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

export const settingsApi = {
    getFactoryType: async (): Promise<FactoryResponse> => {
        const response = await api.get('/factory-type');
        return response.data;
    },

    setFactoryType: async (factoryType: string): Promise<void> => {
        const request: FactoryRequest = { factoryType };
        await api.post('/factory-type', request);
    },
};