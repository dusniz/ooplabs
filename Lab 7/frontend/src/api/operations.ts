import axios from 'axios';
import { DifferentiationRequest, IntegrationRequest, FunctionOperationRequest } from '../types';

const API_URL = 'http://localhost:8080/api/v1/functions';

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

export const operationsApi = {
    differentiate: async (request: DifferentiationRequest): Promise<any> => {
        const response = await api.post('/differentiate', request);
        return response.data;
    },

    integrate: async (request: IntegrationRequest): Promise<any> => {
        const response = await api.post('/integrate', request);
        return response.data;
    },

    binaryOperation: async (request: FunctionOperationRequest): Promise<any> => {
        const response = await api.post('/binary', request);
        return response.data;
    }
};