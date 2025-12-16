import React, { useState, useEffect } from 'react';
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    IconButton,
    TextField,
    Tooltip,
    Box,
    Typography,
    LinearProgress,
    Alert,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Grid,
} from '@mui/material';
import {
    Edit,
    Delete,
    Save,
    Cancel,
    Add,
    ShowChart,
} from '@mui/icons-material';
import { pointsApi } from '../../api';
import { Point } from '../../types';
import toast from 'react-hot-toast';

interface PointTableProps {
    functionId?: number;
    points?: Point[];
    onPointsChange?: (points: Point[]) => void;
    readOnly?: boolean;
    showActions?: boolean;
    height?: number;
}

const PointTable: React.FC<PointTableProps> = ({
                                                   functionId,
                                                   points: externalPoints,
                                                   onPointsChange,
                                                   readOnly = false,
                                                   showActions = true,
                                                   height = 400,
                                               }) => {
    const [points, setPoints] = useState<Point[]>(externalPoints || []);
    const [loading, setLoading] = useState(!externalPoints && !!functionId);
    const [error, setError] = useState<string | null>(null);
    const [editingId, setEditingId] = useState<number | null>(null);
    const [editValues, setEditValues] = useState<{ x: number; y: number }>({ x: 0, y: 0 });
    const [showAddDialog, setShowAddDialog] = useState(false);
    const [showDeleteDialog, setShowDeleteDialog] = useState(false);
    const [pointToDelete, setPointToDelete] = useState<Point | null>(null);
    const [newPoint, setNewPoint] = useState({ x: 0, y: 0, index: 0 });

    useEffect(() => {
        if (externalPoints) {
            setPoints(externalPoints);
        } else if (functionId) {
            loadPoints();
        }
    }, [functionId, externalPoints]);

    const loadPoints = async () => {
        if (!functionId) return;

        try {
            setLoading(true);
            setError(null);
            const data = await pointsApi.getFunctionPoints(functionId);
            setPoints(data);
            if (onPointsChange) {
                onPointsChange(data);
            }
        } catch (err: any) {
            setError(err.message || 'Ошибка загрузки точек');
            toast.error('Ошибка загрузки точек');
        } finally {
            setLoading(false);
        }
    };

    const handleEdit = (point: Point) => {
        setEditingId(point.id);
        setEditValues({ x: point.x, y: point.y });
    };

    const handleSave = async (pointId: number) => {
        try {
            await pointsApi.updatePoint(pointId, editValues);

            const updatedPoints = points.map(p =>
                p.id === pointId ? { ...p, ...editValues } : p
            );

            setPoints(updatedPoints);
            setEditingId(null);

            if (onPointsChange) {
                onPointsChange(updatedPoints);
            }

            toast.success('Точка обновлена успешно');
        } catch (error) {
            toast.error('Ошибка обновления точки');
        }
    };

    const handleCancel = () => {
        setEditingId(null);
    };

    const handleDelete = (point: Point) => {
        setPointToDelete(point);
        setShowDeleteDialog(true);
    };

    const confirmDelete = async () => {
        if (!pointToDelete) return;

        try {
            await pointsApi.deletePoint(pointToDelete.id);

            const updatedPoints = points.filter(p => p.id !== pointToDelete.id);
            setPoints(updatedPoints);
            setShowDeleteDialog(false);
            setPointToDelete(null);

            if (onPointsChange) {
                onPointsChange(updatedPoints);
            }

            toast.success('Точка удалена успешно');
        } catch (error) {
            toast.error('Ошибка удаления точки');
        }
    };

    const handleAddPoint = async () => {
        if (!functionId) {
            // Локальное добавление
            const newPointObj: Point = {
                id: Date.now(), // Временный ID
                functionId: 0,
                x: newPoint.x,
                y: newPoint.y,
                index: newPoint.index,
            };

            const updatedPoints = [...points, newPointObj];
            setPoints(updatedPoints);
            setShowAddDialog(false);
            setNewPoint({ x: 0, y: 0, index: points.length });

            if (onPointsChange) {
                onPointsChange(updatedPoints);
            }

            return;
        }

        try {
            const point = await pointsApi.addPointToFunction(functionId, {
                functionId,
                x: newPoint.x,
                y: newPoint.y,
                index: newPoint.index,
            });

            const updatedPoints = [...points, point];
            setPoints(updatedPoints);
            setShowAddDialog(false);
            setNewPoint({ x: 0, y: 0, index: points.length });

            if (onPointsChange) {
                onPointsChange(updatedPoints);
            }

            toast.success('Точка добавлена успешно');
        } catch (error) {
            toast.error('Ошибка добавления точки');
        }
    };

    const handleCellChange = (pointId: number, field: 'x' | 'y', value: number) => {
        const updatedPoints = points.map(p =>
            p.id === pointId ? { ...p, [field]: value } : p
        );

        setPoints(updatedPoints);

        if (onPointsChange) {
            onPointsChange(updatedPoints);
        }
    };

    const calculateStats = () => {
        if (points.length === 0) return null;

        const xs = points.map(p => p.x);
        const ys = points.map(p => p.y);

        return {
            count: points.length,
            xMin: Math.min(...xs),
            xMax: Math.max(...xs),
            yMin: Math.min(...ys),
            yMax: Math.max(...ys),
            xAvg: xs.reduce((a, b) => a + b) / xs.length,
            yAvg: ys.reduce((a, b) => a + b) / ys.length,
        };
    };

    const stats = calculateStats();

    if (loading) {
        return (
            <Box sx={{ py: 4 }}>
                <LinearProgress />
                <Typography align="center" sx={{ mt: 2 }}>Загрузка точек...</Typography>
            </Box>
        );
    }

    if (error) {
        return (
            <Alert severity="error" sx={{ mt: 2 }}>
                {error}
            </Alert>
        );
    }

    return (
        <Box>
            {/* Заголовок и кнопки */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6">
                    Точки функции ({points.length})
                </Typography>

                {!readOnly && showActions && (
                    <Box sx={{ display: 'flex', gap: 1 }}>
                        <Button
                            startIcon={<Add />}
                            onClick={() => setShowAddDialog(true)}
                            size="small"
                        >
                            Добавить точку
                        </Button>
                    </Box>
                )}
            </Box>

            {/* Статистика */}
            {stats && (
                <Paper sx={{ p: 2, mb: 2, bgcolor: 'action.hover' }}>
                    <Grid container spacing={1}>
                        <Grid item xs={6} sm={3}>
                            <Typography variant="caption" color="text.secondary">X min</Typography>
                            <Typography variant="body2">{stats.xMin.toFixed(4)}</Typography>
                        </Grid>
                        <Grid item xs={6} sm={3}>
                            <Typography variant="caption" color="text.secondary">X max</Typography>
                            <Typography variant="body2">{stats.xMax.toFixed(4)}</Typography>
                        </Grid>
                        <Grid item xs={6} sm={3}>
                            <Typography variant="caption" color="text.secondary">Y min</Typography>
                            <Typography variant="body2">{stats.yMin.toFixed(4)}</Typography>
                        </Grid>
                        <Grid item xs={6} sm={3}>
                            <Typography variant="caption" color="text.secondary">Y max</Typography>
                            <Typography variant="body2">{stats.yMax.toFixed(4)}</Typography>
                        </Grid>
                    </Grid>
                </Paper>
            )}

            {/* Таблица */}
            <TableContainer component={Paper} sx={{ maxHeight: height }}>
                <Table stickyHeader size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell>#</TableCell>
                            <TableCell>ID</TableCell>
                            <TableCell>Значение X</TableCell>
                            <TableCell>Значение Y</TableCell>
                            <TableCell>Индекс</TableCell>
                            {showActions && !readOnly && <TableCell align="center">Действия</TableCell>}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {points.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={showActions && !readOnly ? 6 : 5} align="center">
                                    <Typography color="text.secondary" sx={{ py: 4 }}>
                                        Точки не найдены
                                    </Typography>
                                </TableCell>
                            </TableRow>
                        ) : (
                            points.map((point, index) => (
                                <TableRow key={point.id} hover>
                                    <TableCell>{index + 1}</TableCell>
                                    <TableCell>#{point.id}</TableCell>
                                    <TableCell>
                                        {editingId === point.id ? (
                                            <TextField
                                                type="number"
                                                value={editValues.x}
                                                onChange={(e) => setEditValues({ ...editValues, x: parseFloat(e.target.value) })}
                                                size="small"
                                                sx={{ width: 120 }}
                                            />
                                        ) : readOnly ? (
                                            point.x.toFixed(4)
                                        ) : (
                                            <TextField
                                                type="number"
                                                value={point.x}
                                                onChange={(e) => handleCellChange(point.id, 'x', parseFloat(e.target.value))}
                                                size="small"
                                                sx={{ width: 120 }}
                                            />
                                        )}
                                    </TableCell>
                                    <TableCell>
                                        {editingId === point.id ? (
                                            <TextField
                                                type="number"
                                                value={editValues.y}
                                                onChange={(e) => setEditValues({ ...editValues, y: parseFloat(e.target.value) })}
                                                size="small"
                                                sx={{ width: 120 }}
                                            />
                                        ) : readOnly ? (
                                            Number.isFinite(point.y) ? point.y.toFixed(4) : 0
                                        ) : (
                                            <TextField
                                                type="number"
                                                value={point.y}
                                                onChange={(e) => handleCellChange(point.id, 'y', parseFloat(e.target.value))}
                                                size="small"
                                                sx={{ width: 120 }}
                                            />
                                        )}
                                    </TableCell>
                                    <TableCell>{point.index}</TableCell>

                                    {showActions && !readOnly && (
                                        <TableCell align="center">
                                            {editingId === point.id ? (
                                                <>
                                                    <Tooltip title="Сохранить">
                                                        <IconButton
                                                            size="small"
                                                            onClick={() => handleSave(point.id)}
                                                            color="primary"
                                                        >
                                                            <Save fontSize="small" />
                                                        </IconButton>
                                                    </Tooltip>
                                                    <Tooltip title="Отменить">
                                                        <IconButton
                                                            size="small"
                                                            onClick={handleCancel}
                                                            color="error"
                                                        >
                                                            <Cancel fontSize="small" />
                                                        </IconButton>
                                                    </Tooltip>
                                                </>
                                            ) : (
                                                <>
                                                    <Tooltip title="Редактировать">
                                                        <IconButton
                                                            size="small"
                                                            onClick={() => handleEdit(point)}
                                                        >
                                                            <Edit fontSize="small" />
                                                        </IconButton>
                                                    </Tooltip>
                                                    <Tooltip title="Удалить">
                                                        <IconButton
                                                            size="small"
                                                            onClick={() => handleDelete(point)}
                                                            color="error"
                                                        >
                                                            <Delete fontSize="small" />
                                                        </IconButton>
                                                    </Tooltip>
                                                </>
                                            )}
                                        </TableCell>
                                    )}
                                </TableRow>
                            ))
                        )}
                    </TableBody>
                </Table>
            </TableContainer>

            {/* Диалог добавления точки */}
            <Dialog open={showAddDialog} onClose={() => setShowAddDialog(false)}>
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
                    <Button onClick={() => setShowAddDialog(false)}>Отмена</Button>
                    <Button onClick={handleAddPoint} variant="contained">
                        Добавить
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Диалог подтверждения удаления */}
            <Dialog open={showDeleteDialog} onClose={() => setShowDeleteDialog(false)}>
                <DialogTitle>Подтверждение удаления</DialogTitle>
                <DialogContent>
                    <Typography>
                        Вы уверены, что хотите удалить точку (X: {pointToDelete?.x}, Y: {pointToDelete?.y})?
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setShowDeleteDialog(false)}>Отмена</Button>
                    <Button onClick={confirmDelete} color="error" variant="contained">
                        Удалить
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default PointTable;