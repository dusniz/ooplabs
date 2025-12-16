import axios from 'axios';
import { Point, PointRequest } from '../types';

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

export const pointsApi = {
    getPoint: async (id: number): Promise<Point> => {
        const response = await api.get(`/points/${id}`);
        return response.data;
    },

    createPoint: async (pointData: Omit<Point, 'id'>): Promise<Point> => {
        const response = await api.post('/points', pointData);
        return response.data;
    },

    updatePoint: async (id: number, pointData: Partial<Point>): Promise<Point> => {
        const response = await api.put(`/points/${id}`, pointData);
        return response.data;
    },

    deletePoint: async (id: number): Promise<void> => {
        await api.delete(`/points/${id}`);
    },

    getFunctionPoints: async (functionId: number): Promise<Point[]> => {
        const response = await api.get(`/points/functionId/${functionId}`);
        return response.data;
    },

    addPointToFunction: async (functionId: number, point: PointRequest): Promise<Point> => {
        const response = await api.post(`/functions/${functionId}/points`, point);
        return response.data;
    },

    updateFunctionPoint: async (functionId: number, pointId: number, point: Partial<Point>): Promise<Point> => {
        const response = await api.put(`/functions/${functionId}/points/${pointId}`, point);
        return response.data;
    },

    deleteFunctionPoint: async (functionId: number, pointId: number): Promise<void> => {
        await api.delete(`/functions/${functionId}/points/${pointId}`);
    },
};