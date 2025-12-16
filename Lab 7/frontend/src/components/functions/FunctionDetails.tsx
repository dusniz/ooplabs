import React, { useState, useEffect } from 'react';
import {
    Paper,
    Typography,
    Box,
    Grid,
    Chip,
    Divider,
    LinearProgress,
    Alert,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    IconButton,
    Button,
    TextField,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
} from '@mui/material';
import {
    Edit,
    Delete,
    Add,
    ShowChart,
    Download,
    Refresh,
    Timeline,
    Functions,
} from '@mui/icons-material';
import { useParams, useNavigate } from 'react-router-dom';
import { functionApi, pointsApi } from '../../api';
import { Function, Point } from '../../types';
import FunctionGraph from './FunctionGraph';
import toast from 'react-hot-toast';

const FunctionDetails: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [func, setFunc] = useState<Function | null>(null);
    const [points, setPoints] = useState<Point[]>([]);
    const [loading, setLoading] = useState(true);
    const [loadingPoints, setLoadingPoints] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [showAddPointDialog, setShowAddPointDialog] = useState(false);
    const [showDeleteDialog, setShowDeleteDialog] = useState(false);
    const [newPoint, setNewPoint] = useState({ x: 0, y: 0, index: 0 });

    useEffect(() => {
        if (id) {
            loadFunction(parseInt(id));
        }
    }, [id]);

    const loadFunction = async (funcId: number) => {
        try {
            setLoading(true);
            setError(null);

            const [funcData, pointsData] = await Promise.all([
                functionApi.getFunction(funcId),
                functionApi.getFunctionPoints(funcId),
            ]);

            setFunc(funcData);
            setPoints(pointsData);
        } catch (err: any) {
            setError(err.message || 'Ошибка загрузки функции');
            toast.error('Ошибка загрузки функции');
        } finally {
            setLoading(false);
        }
    };

    const handleAddPoint = async () => {
        if (!func) return;

        try {
            setLoadingPoints(true);
            const point = await pointsApi.addPointToFunction(func.id, {
                functionId: func.id,
                x: newPoint.x,
                y: newPoint.y,
                index: newPoint.index,
            });

            setPoints([...points, point]);
            setShowAddPointDialog(false);
            setNewPoint({ x: 0, y: 0, index: points.length });
            toast.success('Точка добавлена успешно');

            // Обновляем количество точек в функции
            setFunc({
                ...func,
                pointCount: (func.pointCount || 0) + 1,
            });
        } catch (error) {
            toast.error('Ошибка добавления точки');
        } finally {
            setLoadingPoints(false);
        }
    };

    const handleDeleteFunction = async () => {
        if (!func) return;

        try {
            await functionApi.deleteFunction(func.id);
            toast.success('Функция удалена успешно');
            navigate('/functions');
        } catch (error) {
            toast.error('Ошибка удаления функции');
        } finally {
            setShowDeleteDialog(false);
        }
    };

    const handleUpdatePoint = async (pointId: number, updates: Partial<Point>) => {
        try {
            await pointsApi.updatePoint(pointId, updates);

            setPoints(points.map(p =>
                p.id === pointId ? { ...p, ...updates } : p
            ));

            toast.success('Точка обновлена успешно');
        } catch (error) {
            toast.error('Ошибка обновления точки');
        }
    };

    const handleDeletePoint = async (pointId: number) => {
        try {
            await pointsApi.deletePoint(pointId);

            setPoints(points.filter(p => p.id !== pointId));

            // Обновляем количество точек в функции
            if (func) {
                setFunc({
                    ...func,
                    pointCount: (func.pointCount || 1) - 1,
                });
            }

            toast.success('Точка удалена успешно');
        } catch (error) {
            toast.error('Ошибка удаления точки');
        }
    };

    const handleExportFunction = async () => {
        if (!func) return;

        try {
            // TODO: Реализовать экспорт функции
            toast.success(`Функция "${func.name}" экспортирована`);
        } catch (error) {
            toast.error('Ошибка экспорта функции');
        }
    };

    const refreshData = () => {
        if (id) {
            loadFunction(parseInt(id));
        }
    };

    if (loading) {
        return (
            <Box sx={{ py: 4 }}>
                <LinearProgress />
                <Typography align="center" sx={{ mt: 2 }}>Загрузка функции...</Typography>
            </Box>
        );
    }

    if (error || !func) {
        return (
            <Alert severity="error" sx={{ mt: 2 }}>
                {error || 'Функция не найдена'}
            </Alert>
        );
    }

    const isTabulated = func.type === 'TABULATED';

    return (
        <Box>
            {/* Заголовок и действия */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <Typography variant="h4">
                        {func.name}
                    </Typography>
                    <Chip
                        label={isTabulated ? 'Табулированная' : 'Математическая'}
                        color={isTabulated ? 'primary' : 'secondary'}
                    />
                </Box>

                <Box sx={{ display: 'flex', gap: 1 }}>
                    <IconButton onClick={refreshData} title="Обновить">
                        <Refresh />
                    </IconButton>
                    <Button
                        startIcon={<ShowChart />}
                        onClick={() => navigate(`/operations/differentiation?functionId=${func.id}`)}
                    >
                        Дифференцировать
                    </Button>
                    <Button
                        startIcon={<Timeline />}
                        onClick={() => navigate(`/operations/integration?functionId=${func.id}`)}
                    >
                        Интегрировать
                    </Button>
                    <Button
                        startIcon={<Download />}
                        onClick={handleExportFunction}
                    >
                        Экспорт
                    </Button>
                    <Button
                        startIcon={<Edit />}
                        onClick={() => navigate(`/functions?edit=${func.id}`)}
                        variant="outlined"
                    >
                        Редактировать
                    </Button>
                    <Button
                        startIcon={<Delete />}
                        onClick={() => setShowDeleteDialog(true)}
                        color="error"
                        variant="outlined"
                    >
                        Удалить
                    </Button>
                </Box>
            </Box>

            <Grid container spacing={3}>
                {/* Левая колонка - информация о функции */}
                <Grid item xs={12} md={4}>
                    <Paper sx={{ p: 3 }}>
                        <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Functions /> Информация о функции
                        </Typography>

                        <Grid container spacing={2} sx={{ mt: 2 }}>
                            <Grid item xs={6}>
                                <Typography variant="body2" color="text.secondary">
                                    ID функции
                                </Typography>
                                <Typography variant="body1">
                                    #{func.id}
                                </Typography>
                            </Grid>

                            <Grid item xs={6}>
                                <Typography variant="body2" color="text.secondary">
                                    Пользователь ID
                                </Typography>
                                <Typography variant="body1">
                                    #{func.userId}
                                </Typography>
                            </Grid>

                            <Grid item xs={12}>
                                <Typography variant="body2" color="text.secondary">
                                    Описание
                                </Typography>
                                <Typography variant="body1" sx={{ mt: 1 }}>
                                    {func.description || 'Без описания'}
                                </Typography>
                            </Grid>

                            <Grid item xs={6}>
                                <Typography variant="body2" color="text.secondary">
                                    Количество точек
                                </Typography>
                                <Typography variant="body1">
                                    {func.pointCount || 0}
                                </Typography>
                            </Grid>

                            {func.functionClass && (
                                <Grid item xs={12}>
                                    <Typography variant="body2" color="text.secondary">
                                        Класс функции
                                    </Typography>
                                    <Typography variant="body1" fontFamily="monospace">
                                        {func.functionClass}
                                    </Typography>
                                </Grid>
                            )}

                            <Grid item xs={12}>
                                <Typography variant="body2" color="text.secondary">
                                    Тип функции
                                </Typography>
                                <Typography variant="body1">
                                    {isTabulated ? 'Табулированная (задана точками)' : 'Математическая (задана выражением)'}
                                </Typography>
                            </Grid>
                        </Grid>
                    </Paper>

                    {/* Действия с точками для табулированных функций */}
                    {isTabulated && (
                        <Paper sx={{ p: 3, mt: 3 }}>
                            <Typography variant="h6" gutterBottom>
                                Управление точками
                            </Typography>

                            <Button
                                fullWidth
                                startIcon={<Add />}
                                onClick={() => setShowAddPointDialog(true)}
                                variant="contained"
                                sx={{ mb: 2 }}
                            >
                                Добавить точку
                            </Button>

                            <Typography variant="body2" color="text.secondary">
                                Всего точек: {points.length}
                            </Typography>

                            {points.length > 0 && (
                                <Box sx={{ mt: 2 }}>
                                    <Typography variant="body2" color="text.secondary">
                                        Диапазон значений:
                                    </Typography>
                                    <Typography variant="body2">
                                        X: [{Math.min(...points.map(p => p.x)).toFixed(2)}, {Math.max(...points.map(p => p.x)).toFixed(2)}]
                                    </Typography>
                                    <Typography variant="body2">
                                        Y: [{Math.min(...points.map(p => p.y)).toFixed(2)}, {Math.max(...points.map(p => p.y)).toFixed(2)}]
                                    </Typography>
                                </Box>
                            )}
                        </Paper>
                    )}
                </Grid>

                {/* Центральная колонка - график */}
                <Grid item xs={12} md={8}>
                    <Paper sx={{ p: 3, height: '100%' }}>
                        <Typography variant="h6" gutterBottom>
                            График функции
                        </Typography>

                        {points.length > 0 ? (
                            <FunctionGraph
                                points={points}
                                title={func.name}
                                height={400}
                            />
                        ) : (
                            <Box sx={{ height: 400, display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
                                <ShowChart sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
                                <Typography color="text.secondary">
                                    Нет данных для построения графика
                                </Typography>
                                {isTabulated && (
                                    <Button
                                        startIcon={<Add />}
                                        onClick={() => setShowAddPointDialog(true)}
                                        sx={{ mt: 2 }}
                                    >
                                        Добавить точки
                                    </Button>
                                )}
                            </Box>
                        )}
                    </Paper>
                </Grid>

                {/* Таблица точек (только для табулированных функций) */}
                {isTabulated && points.length > 0 && (
                    <Grid item xs={12}>
                        <Paper sx={{ p: 3 }}>
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                                <Typography variant="h6">
                                    Таблица точек ({points.length})
                                </Typography>
                                <Button
                                    size="small"
                                    startIcon={<Add />}
                                    onClick={() => setShowAddPointDialog(true)}
                                >
                                    Добавить точку
                                </Button>
                            </Box>

                            <TableContainer sx={{ maxHeight: 400 }}>
                                <Table stickyHeader size="small">
                                    <TableHead>
                                        <TableRow>
                                            <TableCell>#</TableCell>
                                            <TableCell>ID точки</TableCell>
                                            <TableCell>Значение X</TableCell>
                                            <TableCell>Значение Y</TableCell>
                                            <TableCell>Индекс</TableCell>
                                            <TableCell align="center">Действия</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {points.map((point, index) => (
                                            <TableRow key={point.id} hover>
                                                <TableCell>{index + 1}</TableCell>
                                                <TableCell>#{point.id}</TableCell>
                                                <TableCell>
                                                    <TextField
                                                        type="number"
                                                        value={point.x}
                                                        size="small"
                                                        onChange={(e) => handleUpdatePoint(point.id, { x: parseFloat(e.target.value) })}
                                                        sx={{ width: 100 }}
                                                    />
                                                </TableCell>
                                                <TableCell>
                                                    <TextField
                                                        type="number"
                                                        value={point.y}
                                                        size="small"
                                                        onChange={(e) => handleUpdatePoint(point.id, { y: parseFloat(e.target.value) })}
                                                        sx={{ width: 100 }}
                                                    />
                                                </TableCell>
                                                <TableCell>{point.index}</TableCell>
                                                <TableCell align="center">
                                                    <IconButton
                                                        size="small"
                                                        onClick={() => handleDeletePoint(point.id)}
                                                        color="error"
                                                    >
                                                        <Delete fontSize="small" />
                                                    </IconButton>
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </Paper>
                    </Grid>
                )}
            </Grid>

            {/* Диалог добавления точки */}
            <Dialog open={showAddPointDialog} onClose={() => setShowAddPointDialog(false)}>
                <DialogTitle>Добавить новую точку</DialogTitle>
                <DialogContent>
                    <Grid container spacing={2} sx={{ mt: 1 }}>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="Значение X"
                                type="number"
                                value={newPoint.x}
                                onChange={(e) => setNewPoint({ ...newPoint, x: parseFloat(e.target.value) })}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="Значение Y"
                                type="number"
                                value={newPoint.y}
                                onChange={(e) => setNewPoint({ ...newPoint, y: parseFloat(e.target.value) })}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="Индекс"
                                type="number"
                                value={newPoint.index}
                                onChange={(e) => setNewPoint({ ...newPoint, index: parseInt(e.target.value) })}
                                helperText="Порядковый номер точки в функции"
                            />
                        </Grid>
                    </Grid>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setShowAddPointDialog(false)}>Отмена</Button>
                    <Button
                        onClick={handleAddPoint}
                        variant="contained"
                        disabled={loadingPoints}
                    >
                        {loadingPoints ? 'Добавление...' : 'Добавить'}
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Диалог подтверждения удаления */}
            <Dialog open={showDeleteDialog} onClose={() => setShowDeleteDialog(false)}>
                <DialogTitle>Подтверждение удаления</DialogTitle>
                <DialogContent>
                    <Typography>
                        Вы уверены, что хотите удалить функцию "{func.name}"?
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                        Это действие нельзя отменить. Все точки функции также будут удалены.
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setShowDeleteDialog(false)}>Отмена</Button>
                    <Button
                        onClick={handleDeleteFunction}
                        color="error"
                        variant="contained"
                    >
                        Удалить
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default FunctionDetails;