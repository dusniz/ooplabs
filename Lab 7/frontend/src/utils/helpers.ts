import { Point } from '../types';

/**
 * Форматирование числа с заданной точностью
 */
export const formatNumber = (num: number, decimals: number = 4): string => {
    if (num === null || num === undefined || isNaN(num)) {
        return '—';
    }
    return num.toFixed(decimals);
};

/**
 * Генерация случайного цвета в hex формате
 */
export const generateRandomColor = (): string => {
    return `#${Math.floor(Math.random() * 16777215).toString(16).padStart(6, '0')}`;
};

/**
 * Вычисление расстояния между двумя точками
 */
export const calculateDistance = (x1: number, y1: number, x2: number, y2: number): number => {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
};

/**
 * Интерполяция значения между двумя точками
 */
export const interpolate = (
    x: number,
    x1: number,
    y1: number,
    x2: number,
    y2: number
): number => {
    if (x1 === x2) return y1;
    return y1 + ((x - x1) * (y2 - y1)) / (x2 - x1);
};

/**
 * Поиск ближайшей точки к заданному значению X
 */
export const findNearestPoint = (points: Point[], targetX: number): Point | null => {
    if (points.length === 0) return null;

    let nearest = points[0];
    let minDistance = Math.abs(points[0].x - targetX);

    for (let i = 1; i < points.length; i++) {
        const distance = Math.abs(points[i].x - targetX);
        if (distance < minDistance) {
            minDistance = distance;
            nearest = points[i];
        }
    }

    return nearest;
};

/**
 * Сортировка точек по значению X
 */
export const sortPointsByX = (points: Point[]): Point[] => {
    return [...points].sort((a, b) => a.x - b.x);
};

/**
 * Проверка на уникальность значений X в массиве точек
 */
export const hasUniqueXValues = (points: Point[]): boolean => {
    const xValues = points.map(p => p.x);
    return new Set(xValues).size === xValues.length;
};

/**
 * Проверка на возрастание значений X
 */
export const isXIncreasing = (points: Point[]): boolean => {
    for (let i = 1; i < points.length; i++) {
        if (points[i].x <= points[i - 1].x) {
            return false;
        }
    }
    return true;
};

/**
 * Вычисление статистики по точкам
 */
export const calculatePointsStats = (points: Point[]) => {
    if (points.length === 0) {
        return {
            count: 0,
            xMin: 0,
            xMax: 0,
            yMin: 0,
            yMax: 0,
            xAvg: 0,
            yAvg: 0,
            xRange: 0,
            yRange: 0,
        };
    }

    const xs = points.map(p => p.x);
    const ys = points.map(p => p.y);

    const xMin = Math.min(...xs);
    const xMax = Math.max(...xs);
    const yMin = Math.min(...ys);
    const yMax = Math.max(...ys);
    const xAvg = xs.reduce((a, b) => a + b) / xs.length;
    const yAvg = ys.reduce((a, b) => a + b) / ys.length;

    return {
        count: points.length,
        xMin,
        xMax,
        yMin,
        yMax,
        xAvg,
        yAvg,
        xRange: xMax - xMin,
        yRange: yMax - yMin,
    };
};

/**
 * Форматирование размера файла
 */
export const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes';

    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

/**
 * Задержка выполнения
 */
export const delay = (ms: number): Promise<void> => {
    return new Promise(resolve => setTimeout(resolve, ms));
};

/**
 * Безопасный парсинг JSON
 */
export const safeJsonParse = <T>(json: string, defaultValue: T): T => {
    try {
        return JSON.parse(json) as T;
    } catch {
        return defaultValue;
    }
};

/**
 * Валидация email
 */
export const isValidEmail = (email: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
};

/**
 * Обрезание текста с добавлением многоточия
 */
export const truncateText = (text: string, maxLength: number): string => {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength - 3) + '...';
};

/**
 * Генерация уникального ID
 */
export const generateId = (): string => {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
};

/**
 * Дебаунс функция
 */
export const debounce = <T extends (...args: any[]) => any>(
    func: T,
    wait: number
): ((...args: Parameters<T>) => void) => {
    let timeout: NodeJS.Timeout;

    return (...args: Parameters<T>) => {
        clearTimeout(timeout);
        timeout = setTimeout(() => func(...args), wait);
    };
};

/**
 * Троттлинг функция
 */
export const throttle = <T extends (...args: any[]) => any>(
    func: T,
    limit: number
): ((...args: Parameters<T>) => void) => {
    let inThrottle: boolean;

    return (...args: Parameters<T>) => {
        if (!inThrottle) {
            func(...args);
            inThrottle = true;
            setTimeout(() => (inThrottle = false), limit);
        }
    };
};