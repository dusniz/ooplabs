import React, { useState } from 'react';
import {
    Paper,
    Typography,
    Box,
    Grid,
    TextField,
    Button,
    IconButton,
    Tooltip,
    Alert,
    Stepper,
    Step,
    StepLabel,
    StepContent,
} from '@mui/material';
import {
    Add,
    Delete,
    Save,
    Cancel,
    ArrowUpward,
    ArrowDownward,
} from '@mui/icons-material';

interface Point {
    x: number;
    y: number;
}

interface PointFormProps {
    initialPoints?: Point[];
    onSave?: (points: Point[]) => void;
    onCancel?: () => void;
    title?: string;
    maxPoints?: number;
    minPoints?: number;
}

const PointForm: React.FC<PointFormProps> = ({
                                                 initialPoints = [{ x: 0, y: 0 }, { x: 1, y: 1 }],
                                                 onSave,
                                                 onCancel,
                                                 title = 'Редактирование точек функции',
                                                 maxPoints = 100,
                                                 minPoints = 2,
                                             }) => {
    const [points, setPoints] = useState<Point[]>(initialPoints);
    const [errors, setErrors] = useState<string[]>([]);
    const [activeStep, setActiveStep] = useState(0);

    const validatePoints = (): boolean => {
        const newErrors: string[] = [];

        // Проверка минимального количества точек
        if (points.length < minPoints) {
            newErrors.push(`Минимальное количество точек: ${minPoints}`);
        }

        // Проверка максимального количества точек
        if (points.length > maxPoints) {
            newErrors.push(`Максимальное количество точек: ${maxPoints}`);
        }

        // Проверка уникальности X значений
        const xValues = points.map(p => p.x);
        const uniqueXValues = new Set(xValues);
        if (uniqueXValues.size !== xValues.length) {
            newErrors.push('Значения X должны быть уникальными');
        }

        // Проверка сортировки X значений
        for (let i = 1; i < points.length; i++) {
            if (points[i].x <= points[i - 1].x) {
                newErrors.push('Значения X должны быть строго возрастающими');
                break;
            }
        }

        setErrors(newErrors);
        return newErrors.length === 0;
    };

    const handleAddPoint = () => {
        if (points.length >= maxPoints) {
            setErrors([...errors, `Достигнуто максимальное количество точек: ${maxPoints}`]);
            return;
        }

        const lastPoint = points[points.length - 1];
        const newX = lastPoint ? lastPoint.x + 1 : 0;
        setPoints([...points, { x: newX, y: 0 }]);
    };

    const handleRemovePoint = (index: number) => {
        if (points.length <= minPoints) {
            setErrors([...errors, `Минимальное количество точек: ${minPoints}`]);
            return;
        }

        const newPoints = [...points];
        newPoints.splice(index, 1);
        setPoints(newPoints);
    };

    const handlePointChange = (index: number, field: 'x' | 'y', value: number) => {
        const newPoints = [...points];
        newPoints[index] = { ...newPoints[index], [field]: value };
        setPoints(newPoints);

        // Очищаем ошибки при изменении
        if (errors.length > 0) {
            setErrors([]);
        }
    };

    const handleMoveUp = (index: number) => {
        if (index === 0) return;

        const newPoints = [...points];
        [newPoints[index], newPoints[index - 1]] = [newPoints[index - 1], newPoints[index]];
        setPoints(newPoints);
    };

    const handleMoveDown = (index: number) => {
        if (index === points.length - 1) return;

        const newPoints = [...points];
        [newPoints[index], newPoints[index + 1]] = [newPoints[index + 1], newPoints[index]];
        setPoints(newPoints);
    };

    const handleSave = () => {
        if (validatePoints() && onSave) {
            onSave(points);
        }
    };

    const steps = [
        {
            label: 'Ввод точек',
            description: 'Задайте значения X и Y для каждой точки функции',
        },
        {
            label: 'Проверка данных',
            description: 'Убедитесь, что точки правильно заданы и отсортированы',
        },
        {
            label: 'Сохранение',
            description: 'Сохраните функцию с заданными точками',
        },
    ];

    return (
        <Paper sx={{ p: 3 }}>
            <Typography variant="h5" gutterBottom>
                {title}
            </Typography>

            {/* Stepper для пошагового ввода */}
            <Stepper activeStep={activeStep} orientation="vertical" sx={{ mb: 3 }}>
                {steps.map((step, index) => (
                    <Step key={step.label}>
                        <StepLabel>{step.label}</StepLabel>
                        <StepContent>
                            <Typography variant="body2" color="text.secondary">
                                {step.description}
                            </Typography>
                        </StepContent>
                    </Step>
                ))}
            </Stepper>

            {/* Сообщения об ошибках */}
            {errors.length > 0 && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {errors.map((error, index) => (
                        <div key={index}>{error}</div>
                    ))}
                </Alert>
            )}

            {/* Информация о точках */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="body2" color="text.secondary">
                    Количество точек: {points.length}
                </Typography>
                <Button
                    startIcon={<Add />}
                    onClick={handleAddPoint}
                    size="small"
                    disabled={points.length >= maxPoints}
                >
                    Добавить точку
                </Button>
            </Box>

            {/* Таблица точек */}
            <Box sx={{ maxHeight: 400, overflow: 'auto', mb: 3 }}>
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                    <tr style={{ backgroundColor: '#2d2d2d' }}>
                        <th style={{ padding: '8px', textAlign: 'left', borderBottom: '1px solid #444' }}>#</th>
                        <th style={{ padding: '8px', textAlign: 'left', borderBottom: '1px solid #444' }}>Значение X</th>
                        <th style={{ padding: '8px', textAlign: 'left', borderBottom: '1px solid #444' }}>Значение Y</th>
                        <th style={{ padding: '8px', textAlign: 'center', borderBottom: '1px solid #444' }}>Действия</th>
                    </tr>
                    </thead>
                    <tbody>
                    {points.map((point, index) => (
                        <tr key={index} style={{ backgroundColor: index % 2 === 0 ? '#1e1e1e' : '#252525' }}>
                            <td style={{ padding: '8px', borderBottom: '1px solid #333' }}>
                                {index + 1}
                            </td>
                            <td style={{ padding: '8px', borderBottom: '1px solid #333' }}>
                                <TextField
                                    type="number"
                                    value={point.x}
                                    onChange={(e) => handlePointChange(index, 'x', parseFloat(e.target.value))}
                                    size="small"
                                    fullWidth
                                    error={index > 0 && point.x <= points[index - 1].x}
                                    helperText={index > 0 && point.x <= points[index - 1].x ? 'X должен быть больше предыдущего' : ''}
                                />
                            </td>
                            <td style={{ padding: '8px', borderBottom: '1px solid #333' }}>
                                <TextField
                                    type="number"
                                    value={point.y}
                                    onChange={(e) => handlePointChange(index, 'y', parseFloat(e.target.value))}
                                    size="small"
                                    fullWidth
                                />
                            </td>
                            <td style={{ padding: '8px', borderBottom: '1px solid #333', textAlign: 'center' }}>
                                <Box sx={{ display: 'flex', justifyContent: 'center', gap: 0.5 }}>
                                    <Tooltip title="Переместить вверх">
                                        <IconButton
                                            size="small"
                                            onClick={() => handleMoveUp(index)}
                                            disabled={index === 0}
                                        >
                                            <ArrowUpward fontSize="small" />
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title="Переместить вниз">
                                        <IconButton
                                            size="small"
                                            onClick={() => handleMoveDown(index)}
                                            disabled={index === points.length - 1}
                                        >
                                            <ArrowDownward fontSize="small" />
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title="Удалить">
                                        <IconButton
                                            size="small"
                                            onClick={() => handleRemovePoint(index)}
                                            disabled={points.length <= minPoints}
                                            color="error"
                                        >
                                            <Delete fontSize="small" />
                                        </IconButton>
                                    </Tooltip>
                                </Box>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </Box>

            {/* Статистика */}
            <Grid container spacing={2} sx={{ mb: 3 }}>
                <Grid item xs={6} sm={3}>
                    <Paper sx={{ p: 1, bgcolor: 'action.hover' }}>
                        <Typography variant="caption" color="text.secondary">X min</Typography>
                        <Typography variant="body2">
                            {Math.min(...points.map(p => p.x)).toFixed(4)}
                        </Typography>
                    </Paper>
                </Grid>
                <Grid item xs={6} sm={3}>
                    <Paper sx={{ p: 1, bgcolor: 'action.hover' }}>
                        <Typography variant="caption" color="text.secondary">X max</Typography>
                        <Typography variant="body2">
                            {Math.max(...points.map(p => p.x)).toFixed(4)}
                        </Typography>
                    </Paper>
                </Grid>
                <Grid item xs={6} sm={3}>
                    <Paper sx={{ p: 1, bgcolor: 'action.hover' }}>
                        <Typography variant="caption" color="text.secondary">Y min</Typography>
                        <Typography variant="body2">
                            {Math.min(...points.map(p => p.y)).toFixed(4)}
                        </Typography>
                    </Paper>
                </Grid>
                <Grid item xs={6} sm={3}>
                    <Paper sx={{ p: 1, bgcolor: 'action.hover' }}>
                        <Typography variant="caption" color="text.secondary">Y max</Typography>
                        <Typography variant="body2">
                            {Math.max(...points.map(p => p.y)).toFixed(4)}
                        </Typography>
                    </Paper>
                </Grid>
            </Grid>

            {/* Кнопки действий */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <Box>
                    {activeStep > 0 && (
                        <Button onClick={() => setActiveStep(activeStep - 1)}>
                            Назад
                        </Button>
                    )}
                    {activeStep < steps.length - 1 && (
                        <Button
                            onClick={() => setActiveStep(activeStep + 1)}
                            variant="contained"
                            sx={{ ml: 1 }}
                        >
                            Далее
                        </Button>
                    )}
                </Box>

                <Box sx={{ display: 'flex', gap: 1 }}>
                    {onCancel && (
                        <Button
                            startIcon={<Cancel />}
                            onClick={onCancel}
                            variant="outlined"
                        >
                            Отмена
                        </Button>
                    )}
                    <Button
                        startIcon={<Save />}
                        onClick={handleSave}
                        variant="contained"
                        disabled={!validatePoints()}
                    >
                        Сохранить точки
                    </Button>
                </Box>
            </Box>

            {/* Инструкция */}
            <Alert severity="info" sx={{ mt: 2 }}>
                <Typography variant="body2">
                    <strong>Инструкция:</strong> Задайте точки функции. Значения X должны быть уникальными
                    и строго возрастающими. Минимум {minPoints} точки, максимум {maxPoints} точек.
                </Typography>
            </Alert>
        </Paper>
    );
};

export default PointForm;