import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { functionApi, pointsApi } from '../api';
import { Function, Point } from '../types';
import toast from 'react-hot-toast';

interface FunctionState {
    functions: Function[];
    currentFunction: Function | null;
    points: Point[];
    loading: boolean;
    error: string | null;
    selectedFunctionId: number | null;
}

const initialState: FunctionState = {
    functions: [],
    currentFunction: null,
    points: [],
    loading: false,
    error: null,
    selectedFunctionId: null,
};

// Асинхронные действия
export const fetchFunctions = createAsyncThunk(
    'functions/fetchFunctions',
    async () => {
        const response = await functionApi.getAllFunctions();
        return response;
    }
);

export const fetchFunction = createAsyncThunk(
    'functions/fetchFunction',
    async (id: number) => {
        const response = await functionApi.getFunction(id);
        return response;
    }
);

export const createFunction = createAsyncThunk(
    'functions/createFunction',
    async (funcData: Partial<Function>) => {
        const response = await functionApi.createFunction(funcData);
        return response;
    }
);

export const updateFunction = createAsyncThunk(
    'functions/updateFunction',
    async ({ id, data }: { id: number; data: Partial<Function> }) => {
        const response = await functionApi.updateFunction(id, data);
        return response;
    }
);

export const deleteFunction = createAsyncThunk(
    'functions/deleteFunction',
    async (id: number) => {
        await functionApi.deleteFunction(id);
        return id;
    }
);

export const fetchFunctionPoints = createAsyncThunk(
    'functions/fetchPoints',
    async (functionId: number) => {
        const response = await functionApi.getFunctionPoints(functionId);
        return { functionId, points: response };
    }
);

export const addPoint = createAsyncThunk(
    'functions/addPoint',
    async ({ functionId, point }: { functionId: number; point: Omit<Point, 'id' | 'functionId'> }) => {
        const response = await pointsApi.createPoint({ ...point, functionId });
        return response;
    }
);

export const updatePoint = createAsyncThunk(
    'functions/updatePoint',
    async ({ id, data }: { id: number; data: Partial<Point> }) => {
        const response = await pointsApi.updatePoint(id, data);
        return response;
    }
);

export const deletePoint = createAsyncThunk(
    'functions/deletePoint',
    async (id: number) => {
        await pointsApi.deletePoint(id);
        return id;
    }
);

const functionSlice = createSlice({
    name: 'functions',
    initialState,
    reducers: {
        clearError: (state) => {
            state.error = null;
        },
        setCurrentFunction: (state, action: PayloadAction<Function | null>) => {
            state.currentFunction = action.payload;
        },
        setSelectedFunctionId: (state, action: PayloadAction<number | null>) => {
            state.selectedFunctionId = action.payload;
        },
        clearPoints: (state) => {
            state.points = [];
        },
    },
    extraReducers: (builder) => {
        builder
            // Загрузка всех функций
            .addCase(fetchFunctions.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(fetchFunctions.fulfilled, (state, action: PayloadAction<Function[]>) => {
                state.loading = false;
                state.functions = action.payload;
            })
            .addCase(fetchFunctions.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message || 'Ошибка загрузки функций';
                toast.error(state.error);
            })

            // Загрузка конкретной функции
            .addCase(fetchFunction.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(fetchFunction.fulfilled, (state, action: PayloadAction<Function>) => {
                state.loading = false;
                state.currentFunction = action.payload;
                state.selectedFunctionId = action.payload.id;
            })
            .addCase(fetchFunction.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message || 'Ошибка загрузки функции';
                toast.error(state.error);
            })

            // Создание функции
            .addCase(createFunction.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(createFunction.fulfilled, (state, action: PayloadAction<Function>) => {
                state.loading = false;
                state.functions.push(action.payload);
                toast.success('Функция создана успешно');
            })
            .addCase(createFunction.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message || 'Ошибка создания функции';
                toast.error(state.error);
            })

            // Обновление функции
            .addCase(updateFunction.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(updateFunction.fulfilled, (state, action: PayloadAction<Function>) => {
                state.loading = false;
                const index = state.functions.findIndex(f => f.id === action.payload.id);
                if (index !== -1) {
                    state.functions[index] = action.payload;
                }
                if (state.currentFunction?.id === action.payload.id) {
                    state.currentFunction = action.payload;
                }
                toast.success('Функция обновлена успешно');
            })
            .addCase(updateFunction.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message || 'Ошибка обновления функции';
                toast.error(state.error);
            })

            // Удаление функции
            .addCase(deleteFunction.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(deleteFunction.fulfilled, (state, action: PayloadAction<number>) => {
                state.loading = false;
                state.functions = state.functions.filter(f => f.id !== action.payload);
                if (state.currentFunction?.id === action.payload) {
                    state.currentFunction = null;
                }
                if (state.selectedFunctionId === action.payload) {
                    state.selectedFunctionId = null;
                }
                toast.success('Функция удалена успешно');
            })
            .addCase(deleteFunction.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message || 'Ошибка удаления функции';
                toast.error(state.error);
            })

            // Загрузка точек функции
            .addCase(fetchFunctionPoints.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(fetchFunctionPoints.fulfilled, (state, action: PayloadAction<{ functionId: number; points: Point[] }>) => {
                state.loading = false;
                state.points = action.payload.points;
            })
            .addCase(fetchFunctionPoints.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message || 'Ошибка загрузки точек';
                toast.error(state.error);
            })

            // Добавление точки
            .addCase(addPoint.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(addPoint.fulfilled, (state, action: PayloadAction<Point>) => {
                state.loading = false;
                state.points.push(action.payload);
                if (state.currentFunction) {
                    state.currentFunction.pointCount = (state.currentFunction.pointCount || 0) + 1;
                }
                toast.success('Точка добавлена успешно');
            })
            .addCase(addPoint.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message || 'Ошибка добавления точки';
                toast.error(state.error);
            })

            // Обновление точки
            .addCase(updatePoint.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(updatePoint.fulfilled, (state, action: PayloadAction<Point>) => {
                state.loading = false;
                const index = state.points.findIndex(p => p.id === action.payload.id);
                if (index !== -1) {
                    state.points[index] = action.payload;
                }
                toast.success('Точка обновлена успешно');
            })
            .addCase(updatePoint.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message || 'Ошибка обновления точки';
                toast.error(state.error);
            })

            // Удаление точки
            .addCase(deletePoint.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(deletePoint.fulfilled, (state, action: PayloadAction<number>) => {
                state.loading = false;
                state.points = state.points.filter(p => p.id !== action.payload);
                if (state.currentFunction && state.currentFunction.pointCount) {
                    state.currentFunction.pointCount -= 1;
                }
                toast.success('Точка удалена успешно');
            })
            .addCase(deletePoint.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message || 'Ошибка удаления точки';
                toast.error(state.error);
            });
    },
});

export const { clearError, setCurrentFunction, setSelectedFunctionId, clearPoints } = functionSlice.actions;
export default functionSlice.reducer;