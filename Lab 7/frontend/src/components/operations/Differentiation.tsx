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
} from '@mui/material';
import { Calculate, ShowChart } from '@mui/icons-material';
import { operationsApi } from '../../api/operations';
import { functionApi } from '../../api/functions';
import { Function } from '../../types';
import toast from 'react-hot-toast';
import FunctionGraph from '../functions/FunctionGraph';

const Differentiation: React.FC = () => {
    const [functions, setFunctions] = useState<Function[]>([]);
    const [selectedFunctionId, setSelectedFunctionId] = useState<number | ''>('');
    const [variable, setVariable] = useState('x');
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

    const handleDifferentiate = async () => {
        if (!selectedFunctionId) {
            toast.error('Выберите функцию для дифференцирования');
            return;
        }

        try {
            setLoading(true);
            const response = await operationsApi.differentiate({
                functionId: selectedFunctionId,
                variable,
            });
            setResult(response);
            toast.success('Дифференцирование выполнено успешно!');
        } catch (error: any) {
            toast.error(error.message || 'Ошибка при дифференцировании');
            setResult(null);
        } finally {
            setLoading(false);
        }
    };

    const selectedFunction = functions.find(f => f.id === selectedFunctionId);

    return (
        <Box>
            <Typography variant="h5" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Calculate /> Дифференцирование функции
            </Typography>

            {loadingFunctions && <LinearProgress sx={{ mb: 3 }} />}

            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <Paper sx={{ p: 3 }}>
                        <Typography variant="h6" gutterBottom>
                            Параметры дифференцирования
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

                        <TextField
                            fullWidth
                            label="Переменная дифференцирования"
                            value={variable}
                            onChange={(e) => setVariable(e.target.value)}
                            sx={{ mb: 3 }}
                        />

                        <Button
                            fullWidth
                            variant="contained"
                            size="large"
                            onClick={handleDifferentiate}
                            disabled={loading || !selectedFunctionId}
                            startIcon={<Calculate />}
                        >
                            {loading ? 'Вычисление...' : 'Выполнить дифференцирование'}
                        </Button>
                    </Paper>

                    {selectedFunction && (
                        <Paper sx={{ p: 3, mt: 3 }}>
                            <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                <ShowChart /> Исходная функция
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Название:</strong> {selectedFunction.name}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Тип:</strong> {selectedFunction.type === 'TABULATED' ? 'Табулированная' : 'Математическая'}
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
                            Результат
                        </Typography>

                        {result ? (
                            <Box>
                                <Alert severity="success" sx={{ mb: 2 }}>
                                    Дифференцирование выполнено успешно!
                                </Alert>

                                <Box sx={{ mb: 3 }}>
                                    <Typography variant="subtitle1" gutterBottom>
                                        Производная функции:
                                    </Typography>
                                    <Paper sx={{ p: 2, bgcolor: 'action.hover' }}>
                                        <Typography variant="body1" fontFamily="monospace">
                                            f'({variable}) = {result.expression || 'Выражение производной'}
                                        </Typography>
                                    </Paper>
                                </Box>

                                {result.points && result.points.length > 0 && (
                                    <Box>
                                        <Typography variant="subtitle1" gutterBottom>
                                            Табулированные значения производной:
                                        </Typography>
                                        <Paper sx={{ p: 2, maxHeight: 300, overflow: 'auto' }}>
                                            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                                                <thead>
                                                <tr>
                                                    <th style={{ padding: '8px', borderBottom: '1px solid #444' }}>X</th>
                                                    <th style={{ padding: '8px', borderBottom: '1px solid #444' }}>Y'</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                {result.points.map((point: any, idx: number) => (
                                                    <tr key={idx}>
                                                        <td style={{ padding: '8px', borderBottom: '1px solid #333' }}>
                                                            {point.x.toFixed(4)}
                                                        </td>
                                                        <td style={{ padding: '8px', borderBottom: '1px solid #333' }}>
                                                            Number.isFinite(point.y) ? point.y.toFixed(4) : 0
                                                        </td>
                                                    </tr>
                                                ))}
                                                </tbody>
                                            </table>
                                        </Paper>
                                    </Box>
                                )}

                                <Box sx={{ mt: 3 }}>
                                    <Typography variant="subtitle1" gutterBottom>
                                        График производной:
                                    </Typography>
                                    <Box sx={{ height: 300 }}>
                                        <FunctionGraph
                                            points={result.points || []}
                                            title="График производной функции"
                                        />
                                    </Box>
                                </Box>
                            </Box>
                        ) : (
                            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', py: 8 }}>
                                <Calculate sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
                                <Typography color="text.secondary" align="center">
                                    Выполните дифференцирование, чтобы увидеть результат
                                </Typography>
                            </Box>
                        )}
                    </Paper>
                </Grid>
            </Grid>
        </Box>
    );
};

export default Differentiation;