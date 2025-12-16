import * as yup from 'yup';
import { Point } from '../types';

// Схема валидации функции
export const functionSchema = yup.object().shape({
    name: yup
        .string()
        .required('Название функции обязательно')
        .min(2, 'Минимум 2 символа')
        .max(50, 'Максимум 50 символов'),
    description: yup
        .string()
        .max(500, 'Максимум 500 символов'),
    type: yup
        .string()
        .oneOf(['TABULATED', 'MATH'], 'Некорректный тип функции')
        .required('Тип функции обязателен'),
    functionClass: yup
        .string()
        .max(100, 'Максимум 100 символов'),
    pointCount: yup
        .number()
        .min(0, 'Количество точек не может быть отрицательным')
        .max(1000, 'Максимум 1000 точек'),
});

// Схема валидации точки
export const pointSchema = yup.object().shape({
    x: yup
        .number()
        .required('Значение X обязательно')
        .typeError('X должен быть числом'),
    y: yup
        .number()
        .required('Значение Y обязательно')
        .typeError('Y должен быть числом'),
    index: yup
        .number()
        .required('Индекс обязателен')
        .integer('Индекс должен быть целым числом')
        .min(0, 'Индекс не может быть отрицательным'),
});

// Схема валидации запроса на дифференцирование
export const differentiationSchema = yup.object().shape({
    functionId: yup
        .number()
        .required('ID функции обязателен')
        .positive('ID функции должен быть положительным числом'),
    variable: yup
        .string()
        .required('Переменная обязательна')
        .matches(/^[a-zA-Z]$/, 'Переменная должна быть одной буквой'),
});

// Схема валидации запроса на интегрирование
export const integrationSchema = yup.object().shape({
    functionId: yup
        .number()
        .required('ID функции обязателен')
        .positive('ID функции должен быть положительным числом'),
    variable: yup
        .string()
        .required('Переменная обязательна')
        .matches(/^[a-zA-Z]$/, 'Переменная должна быть одной буквой'),
    lowerLimit: yup
        .number()
        .required('Нижний предел обязателен')
        .typeError('Нижний предел должен быть числом'),
    upperLimit: yup
        .number()
        .required('Верхний предел обязателен')
        .typeError('Верхний предел должен быть числом')
        .test('upper-gt-lower', 'Верхний предел должен быть больше нижнего', function(value) {
            const { lowerLimit } = this.parent;
            return value > lowerLimit;
        }),
});

// Схема валидации бинарной операции
export const binaryOperationSchema = yup.object().shape({
    firstFunctionId: yup
        .number()
        .required('ID первой функции обязательно')
        .positive('ID первой функции должен быть положительным числом'),
    secondFunctionId: yup
        .number()
        .required('ID второй функции обязательно')
        .positive('ID второй функции должен быть положительным числом')
        .test('different-ids', 'Функции должны быть разными', function(value) {
            const { firstFunctionId } = this.parent;
            return value !== firstFunctionId;
        }),
    operation: yup
        .string()
        .oneOf(['ADD', 'SUBTRACT', 'MULTIPLY', 'DIVIDE', 'COMPOSITE'], 'Некорректная операция')
        .required('Операция обязательна'),
});

// Валидация массива точек
export const validatePointsArray = (points: Point[], minPoints: number = 2, maxPoints: number = 1000): string[] => {
    const errors: string[] = [];

    // Проверка количества точек
    if (points.length < minPoints) {
        errors.push(`Минимальное количество точек: ${minPoints}`);
    }
    if (points.length > maxPoints) {
        errors.push(`Максимальное количество точек: ${maxPoints}`);
    }

    // Проверка уникальности X
    const xValues = points.map(p => p.x);
    const uniqueXValues = new Set(xValues);
    if (uniqueXValues.size !== xValues.length) {
        errors.push('Значения X должны быть уникальными');
    }

    // Проверка сортировки X
    for (let i = 1; i < points.length; i++) {
        if (points[i].x <= points[i - 1].x) {
            errors.push('Значения X должны быть строго возрастающими');
            break;
        }
    }

    return errors;
};

// Валидация числового диапазона
export const validateNumberRange = (
    value: number,
    min: number,
    max: number,
    fieldName: string
): string | null => {
    if (value < min) {
        return `${fieldName} не может быть меньше ${min}`;
    }
    if (value > max) {
        return `${fieldName} не может быть больше ${max}`;
    }
    return null;
};

// Валидация имени файла
export const validateFilename = (filename: string): string | null => {
    if (!filename) {
        return 'Имя файла обязательно';
    }

    const invalidChars = /[<>:"/\\|?*]/;
    if (invalidChars.test(filename)) {
        return 'Имя файла содержит недопустимые символы: < > : " / \\ | ? *';
    }

    if (filename.length > 255) {
        return 'Имя файла слишком длинное (максимум 255 символов)';
    }

    return null;
};

// Валидация пароля
export const validatePassword = (password: string): string[] => {
    const errors: string[] = [];

    if (password.length < 6) {
        errors.push('Минимум 6 символов');
    }
    if (!/[A-Z]/.test(password)) {
        errors.push('Должна быть хотя бы одна заглавная буква');
    }
    if (!/[0-9]/.test(password)) {
        errors.push('Должна быть хотя бы одна цифра');
    }
    if (!/[!@#$%^&*]/.test(password)) {
        errors.push('Должен быть хотя бы один специальный символ (!@#$%^&*)');
    }

    return errors;
};

// Валидация email
export const validateEmail = (email: string): string | null => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        return 'Некорректный формат email';
    }
    return null;
};

// Валидация URL
export const validateUrl = (url: string): string | null => {
    try {
        new URL(url);
        return null;
    } catch {
        return 'Некорректный URL';
    }
};

// Валидация JSON
export const validateJson = (jsonString: string): string | null => {
    try {
        JSON.parse(jsonString);
        return null;
    } catch (error: any) {
        return `Некорректный JSON: ${error.message}`;
    }
};

// Валидация даты
export const validateDate = (dateString: string): string | null => {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
        return 'Некорректная дата';
    }
    return null;
};

// Комплексная валидация данных функции
export const validateFunctionData = (
    name: string,
    type: string,
    points?: Point[]
): string[] => {
    const errors: string[] = [];

    // Валидация имени
    if (!name || name.trim().length === 0) {
        errors.push('Название функции обязательно');
    } else if (name.length > 50) {
        errors.push('Название функции не может превышать 50 символов');
    }

    // Валидация типа
    if (!type || !['TABULATED', 'MATH'].includes(type)) {
        errors.push('Некорректный тип функции');
    }

    // Валидация точек для табулированных функций
    if (type === 'TABULATED' && points) {
        const pointErrors = validatePointsArray(points);
        errors.push(...pointErrors);
    }

    return errors;
};