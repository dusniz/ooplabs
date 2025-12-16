import React, { useState, useEffect } from 'react';
import {
    Paper,
    Typography,
    Box,
    Grid,
    TextField,
    Button,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    LinearProgress,
    Alert,
    Slider,
    Chip,
} from '@mui/material';
import { IntegrationInstructions, Timeline } from '@mui/icons-material';
import { operationsApi } from '../../api/operations';
import { functionApi } from '../../api/functions';
import { Function } from '../../types';
import toast from 'react-hot-toast';

const Integration: React.FC = () => {
    const [functions, setFunctions] = useState<Function[]>([]);
    const [selectedFunctionId, setSelectedFunctionId] = useState<number | ''>('');
    const [variable, setVariable] = useState('x');
    const [lowerLimit, setLowerLimit] = useState(0);
    const [upperLimit, setUpperLimit] = useState(10);
    const [numThreads, setNumThreads] = useState(4);
    const [result, setResult] = useState<any>(null);
    const [loading, setLoading] = useState(false);
    const [loadingFunctions, setLoadingFunctions] = useState(true);

    useEffect(() => {
        loadFunctions();
    }, []);

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

    const handleIntegrate = async () => {
        if (!selectedFunctionId) {
            toast.error('Выберите функцию для интегрирования');
            return;
        }

        if (lowerLimit >= upperLimit) {
            toast.error('Верхний предел должен быть больше нижнего');
            return;
        }

        try {
            setLoading(true);
            const response = await operationsApi.integrate({
                functionId: selectedFunctionId,
                variable,
                lowerLimit,
                upperLimit,
            });
            setResult(response);
            toast.success('Интегрирование выполнено успешно!');
        } catch (error: any) {
            toast.error(error.message || 'Ошибка при интегрировании');
            setResult(null);
        } finally {
            setLoading(false);
        }
    };

    const selectedFunction = functions.find(f => f.id === selectedFunctionId);

    return (
        <Box>
            <Typography variant="h5" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <IntegrationInstructions /> Интегрирование функции
            </Typography>

            {loadingFunctions && <LinearProgress sx={{ mb: 3 }} />}

            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <Paper sx={{ p: 3 }}>
                        <Typography variant="h6" gutterBottom>
                            Параметры интегрирования
                        </Typography>

                        <FormControl fullWidth sx={{ mb: 3 }}>
                            <InputLabel>Функция</InputLabel>
                            <Select
                                value={selectedFunctionId}
                                label="Функция"
                                onChange={(e) => setSelectedFunctionId(e.target.value as number)}
                            >
                                {functions.map((func) => (
                                    <MenuItem key={func.id} value={func.id}>
                                        {func.name} ({func.type === 'TABULATED' ? 'Табулированная' : 'Математическая'})
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        <Grid container spacing={2} sx={{ mb: 3 }}>
                            <Grid item xs={6}>
                                <TextField
                                    fullWidth
                                    label="Нижний предел"
                                    type="number"
                                    value={lowerLimit}
                                    onChange={(e) => setLowerLimit(parseFloat(e.target.value))}
                                />
                            </Grid>
                            <Grid item xs={6}>
                                <TextField
                                    fullWidth
                                    label="Верхний предел"
                                    type="number"
                                    value={upperLimit}
                                    onChange={(e) => setUpperLimit(parseFloat(e.target.value))}
                                />
                            </Grid>
                        </Grid>

                        <TextField
                            fullWidth
                            label="Переменная интегрирования"
                            value={variable}
                            onChange={(e) => setVariable(e.target.value)}
                            sx={{ mb: 3 }}
                        />

                        <Box sx={{ mb: 3 }}>
                            <Typography gutterBottom>
                                Количество потоков: {numThreads}
                            </Typography>
                            <Slider
                                value={numThreads}
                                onChange={(_, value) => setNumThreads(value as number)}
                                min={1}
                                max={16}
                                marks={[
                                    { value: 1, label: '1' },
                                    { value: 4, label: '4' },
                                    { value: 8, label: '8' },
                                    { value: 12, label: '12' },
                                    { value: 16, label: '16' },
                                ]}
                                valueLabelDisplay="auto"
                            />
                        </Box>

                        <Button
                            fullWidth
                            variant="contained"
                            size="large"
                            onClick={handleIntegrate}
                            disabled={loading || !selectedFunctionId}
                            startIcon={<IntegrationInstructions />}
                        >
                            {loading ? 'Вычисление...' : 'Вычислить интеграл'}
                        </Button>
                    </Paper>

                    {selectedFunction && (
                        <Paper sx={{ p: 3, mt: 3 }}>
                            <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                <Timeline /> Функция для интегрирования
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Название:</strong> {selectedFunction.name}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Тип:</strong> {selectedFunction.type === 'TABULATED' ? 'Табулированная' : 'Математическая'}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Точек:</strong> {selectedFunction.pointCount}
                            </Typography>
                            <Typography variant="body1">
                                <strong>Описание:</strong> {selectedFunction.description || '—'}
                            </Typography>
                        </Paper>
                    )}
                </Grid>

                <Grid item xs={12} md={6}>
                    <Paper sx={{ p: 3, height: '100%' }}>
                        <Typography variant="h6" gutterBottom>
                            Результат интегрирования
                        </Typography>

                        {result ? (
                            <Box>
                                <Alert severity="success" sx={{ mb: 2 }}>
                                    Интеграл вычислен успешно!
                                </Alert>

                                <Box sx={{ mb: 3 }}>
                                    <Typography variant="subtitle1" gutterBottom>
                                        Определенный интеграл:
                                    </Typography>
                                    <Paper sx={{ p: 3, bgcolor: 'action.hover', textAlign: 'center' }}>
                                        <Typography variant="h4" gutterBottom>
                                            {result.value !== undefined ? result.value.toFixed(6) : 'N/A'}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            ∫<sub>{lowerLimit}</sub><sup>{upperLimit}</sup> f({variable}) d{variable}
                                        </Typography>
                                    </Paper>
                                </Box>

                                {result.details && (
                                    <Box sx={{ mb: 3 }}>
                                        <Typography variant="subtitle1" gutterBottom>
                                            Детали вычисления:
                                        </Typography>
                                        <Paper sx={{ p: 2 }}>
                                            <Grid container spacing={2}>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Метод:
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {result.details.method || 'Параллельное интегрирование'}
                                                    </Typography>
                                                </Grid>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Потоков использовано:
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {result.details.threadsUsed || numThreads}
                                                    </Typography>
                                                </Grid>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Время вычисления:
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {result.details.computationTime
                                                            ? `${result.details.computationTime.toFixed(3)} мс`
                                                            : '—'}
                                                    </Typography>
                                                </Grid>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Точность:
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {result.details.accuracy
                                                            ? `${result.details.accuracy.toFixed(8)}`
                                                            : '—'}
                                                    </Typography>
                                                </Grid>
                                            </Grid>
                                        </Paper>
                                    </Box>
                                )}

                                {result.intermediateResults && (
                                    <Box>
                                        <Typography variant="subtitle1" gutterBottom>
                                            Промежуточные результаты по потокам:
                                        </Typography>
                                        <Paper sx={{ p: 2, maxHeight: 200, overflow: 'auto' }}>
                                            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                                                <thead>
                                                <tr>
                                                    <th style={{ padding: '8px', borderBottom: '1px solid #444' }}>Поток</th>
                                                    <th style={{ padding: '8px', borderBottom: '1px solid #444' }}>Интервал</th>
                                                    <th style={{ padding: '8px', borderBottom: '1px solid #444' }}>Результат</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                {result.intermediateResults.map((intermediate: any, idx: number) => (
                                                    <tr key={idx}>
                                                        <td style={{ padding: '8px', borderBottom: '1px solid #333' }}>
                                                            <Chip label={idx + 1} size="small" />
                                                        </td>
                                                        <td style={{ padding: '8px', borderBottom: '1px solid #333' }}>
                                                            [{intermediate.lower?.toFixed(2) || '—'}, {intermediate.upper?.toFixed(2) || '—'}]
                                                        </td>
                                                        <td style={{ padding: '8px', borderBottom: '1px solid #333' }}>
                                                            {intermediate.value?.toFixed(6) || '—'}
                                                        </td>
                                                    </tr>
                                                ))}
                                                </tbody>
                                            </table>
                                        </Paper>
                                    </Box>
                                )}
                            </Box>
                        ) : (
                            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', py: 8 }}>
                                <IntegrationInstructions sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
                                <Typography color="text.secondary" align="center">
                                    Выполните интегрирование, чтобы увидеть результат
                                </Typography>
                                <Typography variant="body2" color="text.secondary" align="center" sx={{ mt: 1 }}>
                                    ∫<sub>a</sub><sup>b</sup> f(x) dx
                                </Typography>
                            </Box>
                        )}
                    </Paper>
                </Grid>
            </Grid>
        </Box>
    );
};

export default Integration;