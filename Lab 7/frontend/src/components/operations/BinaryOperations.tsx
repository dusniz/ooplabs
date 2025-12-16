import React, { useState, useEffect } from 'react';
import {
    Paper,
    Typography,
    Box,
    Grid,
    Button,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    LinearProgress,
    Alert,
    Tabs,
    Tab,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    IconButton,
    TextField, Chip,
} from '@mui/material';
import {
    Add,
    Remove,
    Close,
    Functions,
    Calculate,
    Edit,
    Save,
    Delete,
} from '@mui/icons-material';
import { operationsApi } from '../../api/operations';
import { functionApi } from '../../api/functions';
import { Function, Point, FunctionOperation } from '../../types';
import toast from 'react-hot-toast';
import FunctionGraph from '../functions/FunctionGraph';

interface TabPanelProps {
    children?: React.ReactNode;
    index: number;
    value: number;
}

const TabPanel: React.FC<TabPanelProps> = ({ children, value, index }) => {
    return (
        <div hidden={value !== index}>
            {value === index && <Box sx={{ py: 3 }}>{children}</Box>}
        </div>
    );
};

const BinaryOperations: React.FC = () => {
    const [functions, setFunctions] = useState<Function[]>([]);
    const [firstFunctionId, setFirstFunctionId] = useState<number | ''>('');
    const [secondFunctionId, setSecondFunctionId] = useState<number | ''>('');
    const [operation, setOperation] = useState<FunctionOperation>(FunctionOperation.ADD);
    const [result, setResult] = useState<any>(null);
    const [loading, setLoading] = useState(false);
    const [loadingFunctions, setLoadingFunctions] = useState(true);
    const [tabValue, setTabValue] = useState(0);
    const [editingPointIndex, setEditingPointIndex] = useState<number | null>(null);
    const [editedPoints, setEditedPoints] = useState<Point[]>([]);

    useEffect(() => {
        loadFunctions();
    }, []);

    useEffect(() => {
        if (result?.points) {
            setEditedPoints([...result.points]);
        }
    }, [result]);

    const loadFunctions = async () => {
        try {
            const data = await functionApi.getAllFunctions();
            setFunctions(data);
        } catch (error) {
            toast.error('Ошибка загрузки функций');
        } finally {
            setLoadingFunctions(false);
        }
    };

    const getOperationSymbol = (op: FunctionOperation) => {
        switch (op) {
            case FunctionOperation.ADD: return '+';
            case FunctionOperation.SUBTRACT: return '−';
            case FunctionOperation.MULTIPLY: return '×';
            case FunctionOperation.DIVIDE: return '÷';
            case FunctionOperation.COMPOSITE: return '∘';
            default: return op;
        }
    };

    const getOperationName = (op: FunctionOperation) => {
        switch (op) {
            case FunctionOperation.ADD: return 'Сложение';
            case FunctionOperation.SUBTRACT: return 'Вычитание';
            case FunctionOperation.MULTIPLY: return 'Умножение';
            case FunctionOperation.DIVIDE: return 'Деление';
            case FunctionOperation.COMPOSITE: return 'Композиция';
            default: return op;
        }
    };

    const handleOperation = async () => {
        if (!firstFunctionId || !secondFunctionId) {
            toast.error('Выберите обе функции для операции');
            return;
        }

        try {
            setLoading(true);
            const response = await operationsApi.binaryOperation({
                firstFunctionId,
                secondFunctionId,
                operation,
            });
            setResult(response);
            toast.success(`Операция "${getOperationName(operation)}" выполнена успешно!`);
        } catch (error: any) {
            toast.error(error.message || 'Ошибка при выполнении операции');
            setResult(null);
        } finally {
            setLoading(false);
        }
    };

    const handleSaveResult = async () => {
        if (!result) return;

        try {
            // TODO: Реализовать сохранение результата как новой функции
            toast.success('Результат сохранен как новая функция!');
        } catch (error) {
            toast.error('Ошибка сохранения результата');
        }
    };

    const handlePointEdit = (index: number) => {
        setEditingPointIndex(index);
    };

    const handlePointSave = (index: number) => {
        setEditingPointIndex(null);
        // TODO: Отправить изменения на сервер
    };

    const handlePointChange = (index: number, field: 'x' | 'y', value: number) => {
        const newPoints = [...editedPoints];
        newPoints[index] = { ...newPoints[index], [field]: value };
        setEditedPoints(newPoints);
    };

    const firstFunction = functions.find(f => f.id === firstFunctionId);
    const secondFunction = functions.find(f => f.id === secondFunctionId);

    return (
        <Box>
            <Typography variant="h5" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Calculate /> Бинарные операции над функциями
            </Typography>

            {loadingFunctions && <LinearProgress sx={{ mb: 3 }} />}

            <Grid container spacing={3}>
                {/* Левая панель - выбор функций и операции */}
                <Grid item xs={12} md={4}>
                    <Paper sx={{ p: 3, height: '100%' }}>
                        <Typography variant="h6" gutterBottom>
                            Выбор функций
                        </Typography>

                        <FormControl fullWidth sx={{ mb: 3 }}>
                            <InputLabel>Первая функция</InputLabel>
                            <Select
                                value={firstFunctionId}
                                label="Первая функция"
                                onChange={(e) => setFirstFunctionId(e.target.value as number)}
                            >
                                {functions.map((func) => (
                                    <MenuItem key={func.id} value={func.id}>
                                        {func.name}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        <FormControl fullWidth sx={{ mb: 3 }}>
                            <InputLabel>Операция</InputLabel>
                            <Select
                                value={operation}
                                label="Операция"
                                onChange={(e) => setOperation(e.target.value as FunctionOperation)}
                            >
                                <MenuItem value={FunctionOperation.ADD}>Сложение (f + g)</MenuItem>
                                <MenuItem value={FunctionOperation.SUBTRACT}>Вычитание (f - g)</MenuItem>
                                <MenuItem value={FunctionOperation.MULTIPLY}>Умножение (f × g)</MenuItem>
                                <MenuItem value={FunctionOperation.DIVIDE}>Деление (f ÷ g)</MenuItem>
                                <MenuItem value={FunctionOperation.COMPOSITE}>Композиция (f ∘ g)</MenuItem>
                            </Select>
                        </FormControl>

                        <FormControl fullWidth sx={{ mb: 3 }}>
                            <InputLabel>Вторая функция</InputLabel>
                            <Select
                                value={secondFunctionId}
                                label="Вторая функция"
                                onChange={(e) => setSecondFunctionId(e.target.value as number)}
                            >
                                {functions.map((func) => (
                                    <MenuItem key={func.id} value={func.id}>
                                        {func.name}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        <Button
                            fullWidth
                            variant="contained"
                            size="large"
                            onClick={handleOperation}
                            disabled={loading || !firstFunctionId || !secondFunctionId}
                            startIcon={<Calculate />}
                        >
                            {loading ? 'Вычисление...' : `Выполнить ${getOperationName(operation)}`}
                        </Button>

                        {firstFunction && secondFunction && (
                            <Box sx={{ mt: 3, p: 2, bgcolor: 'action.hover', borderRadius: 1 }}>
                                <Typography variant="body2" color="text.secondary">
                                    Выражение:
                                </Typography>
                                <Typography variant="h6" align="center" sx={{ fontFamily: 'monospace' }}>
                                    f({firstFunction.name}) {getOperationSymbol(operation)} g({secondFunction.name})
                                </Typography>
                            </Box>
                        )}
                    </Paper>
                </Grid>

                {/* Центральная панель - информация о функциях */}
                <Grid item xs={12} md={4}>
                    <Paper sx={{ p: 3, height: '100%' }}>
                        <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Functions /> Информация о функциях
                        </Typography>

                        <Tabs value={tabValue} onChange={(_, value) => setTabValue(value)} sx={{ mb: 2 }}>
                            <Tab label="Первая" />
                            <Tab label="Вторая" />
                        </Tabs>

                        <TabPanel value={tabValue} index={0}>
                            {firstFunction ? (
                                <Box>
                                    <Typography variant="subtitle1" gutterBottom>
                                        {firstFunction.name}
                                    </Typography>
                                    <Typography variant="body2" color="text.secondary" paragraph>
                                        {firstFunction.description || 'Без описания'}
                                    </Typography>
                                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 2 }}>
                                        <Chip
                                            label={firstFunction.type === 'TABULATED' ? 'Табулированная' : 'Математическая'}
                                            size="small"
                                            color="primary"
                                        />
                                        <Chip
                                            label={`${firstFunction.pointCount} точек`}
                                            size="small"
                                            variant="outlined"
                                        />
                                    </Box>
                                    {firstFunction.functionClass && (
                                        <Typography variant="body2">
                                            <strong>Класс:</strong> {firstFunction.functionClass}
                                        </Typography>
                                    )}
                                </Box>
                            ) : (
                                <Typography color="text.secondary" align="center" sx={{ py: 4 }}>
                                    Выберите первую функцию
                                </Typography>
                            )}
                        </TabPanel>

                        <TabPanel value={tabValue} index={1}>
                            {secondFunction ? (
                                <Box>
                                    <Typography variant="subtitle1" gutterBottom>
                                        {secondFunction.name}
                                    </Typography>
                                    <Typography variant="body2" color="text.secondary" paragraph>
                                        {secondFunction.description || 'Без описания'}
                                    </Typography>
                                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 2 }}>
                                        <Chip
                                            label={secondFunction.type === 'TABULATED' ? 'Табулированная' : 'Математическая'}
                                            size="small"
                                            color="primary"
                                        />
                                        <Chip
                                            label={`${secondFunction.pointCount} точек`}
                                            size="small"
                                            variant="outlined"
                                        />
                                    </Box>
                                    {secondFunction.functionClass && (
                                        <Typography variant="body2">
                                            <strong>Класс:</strong> {secondFunction.functionClass}
                                        </Typography>
                                    )}
                                </Box>
                            ) : (
                                <Typography color="text.secondary" align="center" sx={{ py: 4 }}>
                                    Выберите вторую функцию
                                </Typography>
                            )}
                        </TabPanel>
                    </Paper>
                </Grid>

                {/* Правая панель - результат */}
                <Grid item xs={12} md={4}>
                    <Paper sx={{ p: 3, height: '100%' }}>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                            <Typography variant="h6">
                                Результат
                            </Typography>
                            {result && (
                                <Button
                                    startIcon={<Save />}
                                    onClick={handleSaveResult}
                                    size="small"
                                >
                                    Сохранить
                                </Button>
                            )}
                        </Box>

                        {result ? (
                            <Box>
                                <Alert severity="success" sx={{ mb: 2 }}>
                                    Операция выполнена успешно!
                                </Alert>

                                <Box sx={{ mb: 3 }}>
                                    <Typography variant="body2" color="text.secondary">
                                        Тип результата:
                                    </Typography>
                                    <Typography variant="body1">
                                        {result.type === 'TABULATED' ? 'Табулированная функция' : 'Математическое выражение'}
                                    </Typography>
                                </Box>

                                {result.expression && (
                                    <Box sx={{ mb: 3 }}>
                                        <Typography variant="body2" color="text.secondary">
                                            Выражение:
                                        </Typography>
                                        <Paper sx={{ p: 2, bgcolor: 'action.hover' }}>
                                            <Typography fontFamily="monospace">
                                                {result.expression}
                                            </Typography>
                                        </Paper>
                                    </Box>
                                )}

                                {result.points && result.points.length > 0 && (
                                    <Box>
                                        <Typography variant="subtitle2" gutterBottom>
                                            Значения ({result.points.length} точек):
                                        </Typography>
                                        <Box sx={{ maxHeight: 200, overflow: 'auto', mb: 2 }}>
                                            <TableContainer>
                                                <Table size="small">
                                                    <TableHead>
                                                        <TableRow>
                                                            <TableCell>X</TableCell>
                                                            <TableCell>Y</TableCell>
                                                            <TableCell align="center">Действия</TableCell>
                                                        </TableRow>
                                                    </TableHead>
                                                    <TableBody>
                                                        {editedPoints.slice(0, 10).map((point, index) => (
                                                            <TableRow key={index}>
                                                                <TableCell>
                                                                    {editingPointIndex === index ? (
                                                                        <TextField
                                                                            type="number"
                                                                            value={point.x}
                                                                            onChange={(e) =>
                                                                                handlePointChange(index, 'x', parseFloat(e.target.value))
                                                                            }
                                                                            size="small"
                                                                            fullWidth
                                                                        />
                                                                    ) : (
                                                                        point.x.toFixed(4)
                                                                    )}
                                                                </TableCell>
                                                                <TableCell>
                                                                    {editingPointIndex === index ? (
                                                                        <TextField
                                                                            type="number"
                                                                            value={point.y}
                                                                            onChange={(e) =>
                                                                                handlePointChange(index, 'y', parseFloat(e.target.value))
                                                                            }
                                                                            size="small"
                                                                            fullWidth
                                                                        />
                                                                    ) : (
                                                                        Number.isFinite(point.y) ? point.y.toFixed(4) : 0
                                                                    )}
                                                                </TableCell>
                                                                <TableCell align="center">
                                                                    {editingPointIndex === index ? (
                                                                        <IconButton
                                                                            size="small"
                                                                            onClick={() => handlePointSave(index)}
                                                                        >
                                                                            <Save fontSize="small" />
                                                                        </IconButton>
                                                                    ) : (
                                                                        <IconButton
                                                                            size="small"
                                                                            onClick={() => handlePointEdit(index)}
                                                                        >
                                                                            <Edit fontSize="small" />
                                                                        </IconButton>
                                                                    )}
                                                                </TableCell>
                                                            </TableRow>
                                                        ))}
                                                    </TableBody>
                                                </Table>
                                            </TableContainer>
                                        </Box>
                                        {result.points.length > 10 && (
                                            <Typography variant="body2" color="text.secondary">
                                                ... и еще {result.points.length - 10} точек
                                            </Typography>
                                        )}
                                    </Box>
                                )}

                                <Box sx={{ mt: 2 }}>
                                    <Typography variant="subtitle2" gutterBottom>
                                        График результата:
                                    </Typography>
                                    <Box sx={{ height: 200 }}>
                                        <FunctionGraph
                                            points={result.points || []}
                                            title="Результат операции"
                                        />
                                    </Box>
                                </Box>
                            </Box>
                        ) : (
                            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', py: 8 }}>
                                <Calculate sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
                                <Typography color="text.secondary" align="center">
                                    Выполните операцию, чтобы увидеть результат
                                </Typography>
                            </Box>
                        )}
                    </Paper>
                </Grid>
            </Grid>
        </Box>
    );
};

export default BinaryOperations;