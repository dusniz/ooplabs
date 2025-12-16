// Константы приложения
export const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1';

// Типы функций
export const FUNCTION_TYPES = {
    TABULATED: 'TABULATED',
    MATH: 'MATH',
} as const;

// Операции над функциями
export const FUNCTION_OPERATIONS = {
    ADD: 'ADD',
    SUBTRACT: 'SUBTRACT',
    MULTIPLY: 'MULTIPLY',
    DIVIDE: 'DIVIDE',
    COMPOSITE: 'COMPOSITE',
} as const;

// Роли пользователей
export const USER_ROLES = {
    USER: 'USER',
    ADMIN: 'ADMIN',
} as const;

// Лимиты
export const LIMITS = {
    MAX_POINTS_PER_FUNCTION: 1000,
    MIN_POINTS_PER_FUNCTION: 2,
    MAX_FUNCTIONS_PER_USER: 100,
    MAX_THREADS: 16,
    MIN_THREADS: 1,
    DEFAULT_THREADS: 4,
} as const;

// Форматы файлов
export const FILE_FORMATS = {
    JSON: 'json',
    XML: 'xml',
    CSV: 'csv',
    TXT: 'txt',
} as const;

// Сообщения об ошибках
export const ERROR_MESSAGES = {
    NETWORK_ERROR: 'Ошибка сети. Проверьте подключение к интернету.',
    SERVER_ERROR: 'Ошибка сервера. Попробуйте позже.',
    AUTH_ERROR: 'Ошибка аутентификации. Проверьте логин и пароль.',
    VALIDATION_ERROR: 'Ошибка валидации данных.',
    NOT_FOUND: 'Ресурс не найден.',
    FORBIDDEN: 'Доступ запрещен.',
    UNKNOWN_ERROR: 'Неизвестная ошибка.',
} as const;

// Уведомления
export const NOTIFICATION_DURATION = {
    SUCCESS: 4000,
    ERROR: 6000,
    WARNING: 5000,
    INFO: 3000,
} as const;

// Локализация
export const LOCALE = {
    RU: 'ru-RU',
    EN: 'en-US',
} as const;

// Тема приложения
export const THEME = {
    DARK: 'dark',
    LIGHT: 'light',
} as const;

// Ключи localStorage
export const STORAGE_KEYS = {
    TOKEN: 'token',
    USER: 'user',
    THEME: 'theme',
    LANGUAGE: 'language',
    FACTORY_TYPE: 'factory_type',
} as const;