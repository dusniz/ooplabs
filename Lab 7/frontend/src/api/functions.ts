import axios from 'axios';
import { Function, Point, FunctionFromPointsRequest } from '../types';

const API_URL = 'http://localhost:8080/api/v1';

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

export const functionApi = {
    getAllFunctions: async (): Promise<Function[]> => {
        const response = await api.get('/functions/');
        return response.data;
    },

    getFunction: async (id: number): Promise<Function> => {
        const response = await api.get(`/functions/${id}`);
        return response.data;
    },

    createFunction: async (funcData: Partial<Function>): Promise<Function> => {
        const response = await api.post('/functions/create/from-math', funcData);
        return response.data;
    },

    createFunctionFromPoints: async (request: FunctionFromPointsRequest): Promise<Function> => {
        const response = await api.post('/functions/create/from-points', request);
        return response.data;
    },

    updateFunction: async (id: number, funcData: Partial<Function>): Promise<Function> => {
        const response = await api.put(`/functions/${id}`, funcData);
        return response.data;
    },

    deleteFunction: async (id: number): Promise<void> => {
        await api.delete(`/functions/${id}`);
    },

    getFunctionPoints: async (functionId: number): Promise<Point[]> => {
        const response = await api.get(`/points/functionId/${functionId}`);
        return response.data;
    },

    addPointToFunction: async (functionId: number, point: Omit<Point, 'id' | 'functionId'>): Promise<Point> => {
        const response = await api.post(`/functions/${functionId}/points`, point);
        return response.data;
    }
};