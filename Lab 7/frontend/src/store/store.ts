import { configureStore } from '@reduxjs/toolkit';
import authReducer from './authSlice';
import functionReducer from './functionSlice';

export const store = configureStore({
    reducer: {
        auth: authReducer,
        functions: functionReducer,
    },
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
            serializableCheck: false,
        }),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;