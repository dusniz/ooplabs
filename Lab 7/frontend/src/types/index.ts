// Типы пользователей
export interface User {
    id: number;
    username: string;
    passwordHash: string;
    role: Role;
}

export enum Role {
    USER = 'USER',
    ADMIN = 'ADMIN'
}

export interface UserResponse {
    id: number;
    username: string;
    role: Role;
}

export interface UserRequest {
    username: string;
    passwordHash: string;
    role: Role;
}

// Типы функций
export interface Function {
    id: number;
    userId: number;
    name: string;
    description?: string;
    type: FunctionType;
    pointCount: number;
    functionClass?: string;
}

export enum FunctionType {
    TABULATED = 'TABULATED',
    MATH = 'MATH'
}

export enum FactoryType {
    ARRAY = 'ARRAY',
    LIST = 'LIST'
}

export enum FunctionClass {
    LINEAR = 'IdentityFunction',
    SQR = 'SqrFunction',
    ZERO = 'ZeroFunction',
    UNIT = 'UnitFunction',
    NATURAL_LOG = 'NaturalLogarithmFunction',
    SIN = 'SineFunction',
    COS = 'CosineFunction',
    TAN = 'TangentFunction',
    COT = 'CotangentFunction',
}

export interface FunctionResponse {
    id: number;
    userId: number;
    name: string;
    description?: string;
    type: FunctionType;
    pointCount: number;
    functionClass?: string;
}

export interface FunctionListResponse {
    availableFunctions: string[];
    totalCount: number;
}

// Типы точек
export interface Point {
    id: number;
    functionId: number;
    x: number;
    y: number;
    index: number;
}

export interface PointResponse {
    id: number;
    functionId: number;
    x: number;
    y: number;
    index: number;
}

export interface PointRequest {
    functionId: number;
    x: number;
    y: number;
    index: number;
}

// Запросы для операций
export interface DifferentiationRequest {
    functionId: number;
    variable: string;
}

export interface IntegrationRequest {
    functionId: number;
    variable: string;
    lowerLimit: number;
    upperLimit: number;
}

export interface FunctionOperationRequest {
    firstFunctionId: number;
    secondFunctionId: number;
    operation: FunctionOperation;
}

export enum FunctionOperation {
    ADD = 'ADD',
    SUBTRACT = 'SUBTRACT',
    MULTIPLY = 'MULTIPLY',
    DIVIDE = 'DIVIDE',
    COMPOSITE = 'COMPOSITE'
}

// Типы для создания функций
export interface FunctionFromPointsRequest {
    userId: number;
    name: string;
    description?: string;
    type: FunctionType;
    points: PointRequest[];
}

// Типы для фабрики
export interface FactoryResponse {
    factoryType: string;
    description: string;
}

export interface FactoryRequest {
    factoryType: string;
}

// Типы аутентификации
export interface JwtAuthResponse {
    token: string;
    user: UserResponse;
}

export interface LoginRequest {
    username: string;
    password: string;
}

export interface RegisterRequest {
    username: string;
    password: string;
}

// Состояния Redux
export interface AuthState {
    user: UserResponse | null;
    token: string | null;
    isAuthenticated: boolean;
    loading: boolean;
    error: string | null;
}

export interface FunctionState {
    functions: Function[];
    currentFunction: Function | null;
    points: Point[];
    loading: boolean;
    error: string | null;
    selectedFunctionId: number | null;
}

// Результаты операций
export interface DifferentiationResult {
    success: boolean;
    expression?: string;
    points?: Point[];
    error?: string;
}

export interface IntegrationResult {
    success: boolean;
    value?: number;
    details?: {
        method: string;
        threadsUsed: number;
        computationTime: number;
        accuracy: number;
    };
    intermediateResults?: Array<{
        thread: number;
        lower: number;
        upper: number;
        value: number;
    }>;
    error?: string;
}

export interface BinaryOperationResult {
    success: boolean;
    resultFunction?: Function;
    points?: Point[];
    expression?: string;
    error?: string;
}

// Пагинация
export interface PaginationParams {
    page: number;
    size: number;
    sortBy?: string;
    sortDirection?: 'ASC' | 'DESC';
}

export interface PaginatedResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
}

// Фильтры
export interface FunctionFilter {
    type?: FunctionType;
    userId?: number;
    search?: string;
    minPoints?: number;
    maxPoints?: number;
}

// Настройки
export interface AppSettings {
    factoryType: FunctionType;
    theme: 'dark' | 'light';
    language: 'ru' | 'en';
    defaultThreads: number;
    maxPointsPerFunction: number;
    autoSave: boolean;
}

// Графики
export interface ChartData {
    x: number;
    y: number;
    [key: string]: number;
}

export interface ChartConfig {
    title: string;
    xLabel: string;
    yLabel: string;
    showGrid: boolean;
    showLegend: boolean;
    showTooltip: boolean;
    animationDuration: number;
}

// Файлы
export interface FileInfo {
    name: string;
    size: number;
    type: string;
    lastModified: Date;
}

// Уведомления
export interface Notification {
    id: string;
    type: 'success' | 'error' | 'warning' | 'info';
    title: string;
    message: string;
    timestamp: Date;
    read: boolean;
}

// История операций
export interface OperationHistory {
    id: number;
    userId: number;
    operationType: string;
    parameters: any;
    result: any;
    timestamp: Date;
    duration: number;
}

// Экспорт/импорт
export interface ExportOptions {
    format: 'json' | 'xml' | 'csv';
    includePoints: boolean;
    includeMetadata: boolean;
}

// Валидация
export interface ValidationError {
    field: string;
    message: string;
    code: string;
}

// API Response типы
export interface ApiResponse<T> {
    data: T;
    success: boolean;
    message?: string;
    timestamp: Date;
}

export interface ApiError {
    status: number;
    message: string;
    errors?: ValidationError[];
    path: string;
    timestamp: Date;
}

// Компоненты пропсы
export interface ModalProps {
    open: boolean;
    onClose: () => void;
    title?: string;
    children: React.ReactNode;
    actions?: React.ReactNode;
    maxWidth?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
    fullWidth?: boolean;
}

export interface TableColumn<T> {
    field: keyof T;
    headerName: string;
    width?: number;
    renderCell?: (value: any, row: T) => React.ReactNode;
    sortable?: boolean;
    filterable?: boolean;
}

// Формы
export interface FormField {
    name: string;
    label: string;
    type: 'text' | 'number' | 'email' | 'password' | 'select' | 'textarea';
    required: boolean;
    defaultValue?: any;
    options?: Array<{ value: any; label: string }>;
    validation?: {
        min?: number;
        max?: number;
        pattern?: RegExp;
        custom?: (value: any) => string | null;
    };
}

// События
export interface FunctionEvent {
    type: 'CREATE' | 'UPDATE' | 'DELETE' | 'OPERATION';
    functionId: number;
    userId: number;
    timestamp: Date;
    details: any;
}

// Статистика
export interface AppStatistics {
    totalFunctions: number;
    totalUsers: number;
    totalOperations: number;
    totalPoints: number;
    averagePointsPerFunction: number;
    mostUsedOperation: string;
    functionsByType: Record<FunctionType, number>;
    dailyActivity: Array<{ date: string; count: number }>;
}