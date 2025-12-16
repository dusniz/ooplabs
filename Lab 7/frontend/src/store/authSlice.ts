import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { authApi } from '../api/auth';
import { User, UserResponse, Role, AuthState } from '../types';
import toast from 'react-hot-toast';

const initialState: AuthState = {
    user: authApi.getCurrentUser(),
    token: localStorage.getItem('token'),
    isAuthenticated: !!localStorage.getItem('token'),
    loading: false,
    error: null,
};

export const login = createAsyncThunk(
    'auth/login',
    async ({ username, password }: { username: string; password: string }) => {
        const response = await authApi.login(username, password);
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(response.user));
        return response;
    }
);

export const register = createAsyncThunk(
    'auth/register',
    async ({ username, password }: { username: string; password: string }) => {
        const response = await authApi.register(username, password);
        return response;
    }
);

export const logout = createAsyncThunk('auth/logout', async () => {
    authApi.logout();
    return true;
});

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        clearError: (state) => {
            state.error = null;
        },
        updateUser: (state, action: PayloadAction<Partial<UserResponse>>) => {
            if (state.user) {
                state.user = { ...state.user, ...action.payload };
                localStorage.setItem('user', JSON.stringify(state.user));
            }
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(login.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(login.fulfilled, (state, action: PayloadAction<any>) => {
                state.loading = false;
                state.isAuthenticated = true;
                state.user = action.payload.user;
                state.token = action.payload.token;
                toast.success('Вход выполнен успешно!');
            })
            .addCase(login.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message || 'Ошибка входа';
                toast.error(state.error);
            })

            .addCase(register.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(register.fulfilled, (state) => {
                state.loading = false;
                toast.success('Регистрация успешна! Теперь вы можете войти.');
            })
            .addCase(register.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message || 'Ошибка регистрации';
                toast.error(state.error);
            })

            .addCase(logout.fulfilled, (state) => {
                state.user = null;
                state.token = null;
                state.isAuthenticated = false;
                toast.success('Выход выполнен');
            });
    },
});

export const { clearError, updateUser } = authSlice.actions;
export default authSlice.reducer;