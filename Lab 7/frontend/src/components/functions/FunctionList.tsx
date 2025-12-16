import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Grid,
    Card,
    CardContent,
    CardActions,
    IconButton,
    Chip,
    Tooltip,
    LinearProgress,
    Alert,
} from '@mui/material';
import {
    Edit,
    Delete,
    ShowChart,
    Download,
    MoreVert,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { functionApi } from '../../api';
import { Function } from '../../types';
import toast from 'react-hot-toast';

interface FunctionListProps {
    onEdit?: (func: Function) => void;
    onDelete?: (func: Function) => void;
    onView?: (func: Function) => void;
    filterType?: 'ALL' | 'TABULATED' | 'MATH';
    maxItems?: number;
    showActions?: boolean;
}

const FunctionList: React.FC<FunctionListProps> = ({
                                                       onEdit,
                                                       onDelete,
                                                       onView,
                                                       filterType = 'ALL',
                                                       maxItems,
                                                       showActions = true,
                                                   }) => {
    const navigate = useNavigate();
    const [functions, setFunctions] = useState<Function[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        loadFunctions();
    }, [filterType]);

    const loadFunctions = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await functionApi.getAllFunctions();

            // Фильтрация функций
            let filtered = data;
            if (filterType !== 'ALL') {
                filtered = data.filter(func => func.type === filterType);
            }

            // Ограничение количества
            if (maxItems && maxItems > 0) {
                filtered = filtered.slice(0, maxItems);
            }

            setFunctions(filtered);
        } catch (err: any) {
            setError(err.message || 'Ошибка загрузки функций');
            toast.error('Ошибка загрузки функций');
        } finally {
            setLoading(false);
        }
    };

    const handleEdit = (func: Function) => {
        if (onEdit) {
            onEdit(func);
        } else {
            navigate(`/functions?edit=${func.id}`);
        }
    };

    const handleDelete = (func: Function) => {
        if (onDelete) {
            onDelete(func);
        } else {
            if (window.confirm(`Удалить функцию "${func.name}"?`)) {
                deleteFunction(func.id);
            }
        }
    };

    const handleView = (func: Function) => {
        if (onView) {
            onView(func);
        } else {
            navigate(`/functions?view=${func.id}`);
        }
    };

    const deleteFunction = async (id: number) => {
        try {
            await functionApi.deleteFunction(id);
            toast.success('Функция удалена успешно');
            loadFunctions(); // Перезагружаем список
        } catch (error) {
            toast.error('Ошибка удаления функции');
        }
    };

    const handleExport = async (func: Function) => {
        try {
            // TODO: Реализовать экспорт функции
            toast.success(`Функция "${func.name}" экспортирована`);
        } catch (error) {
            toast.error('Ошибка экспорта функции');
        }
    };

    const getFunctionTypeLabel = (type: string) => {
        switch (type) {
            case 'TABULATED':
                return { label: 'Табулированная', color: 'primary' as const };
            case 'MATH':
                return { label: 'Математическая', color: 'secondary' as const };
            default:
                return { label: type, color: 'default' as const };
        }
    };

    if (loading) {
        return (
            <Box sx={{ py: 4 }}>
                <LinearProgress />
                <Typography align="center" sx={{ mt: 2 }}>Загрузка функций...</Typography>
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

    if (functions.length === 0) {
        return (
            <Box sx={{ py: 6, textAlign: 'center' }}>
                <Typography color="text.secondary">
                    Функции не найдены
                </Typography>
            </Box>
        );
    }

    return (
        <Grid container spacing={3}>
            {functions.map((func) => {
                const typeInfo = getFunctionTypeLabel(func.type);

                return (
                    <Grid item xs={12} sm={6} md={4} lg={3} key={func.id}>
                        <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                            <CardContent sx={{ flexGrow: 1 }}>
                                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                                    <Typography variant="h6" noWrap sx={{ flex: 1, mr: 1 }}>
                                        {func.name}
                                    </Typography>
                                    <Chip
                                        label={typeInfo.label}
                                        size="small"
                                        color={typeInfo.color}
                                    />
                                </Box>

                                {func.description && (
                                    <Typography
                                        variant="body2"
                                        color="text.secondary"
                                        sx={{
                                            mb: 2,
                                            display: '-webkit-box',
                                            WebkitLineClamp: 3,
                                            WebkitBoxOrient: 'vertical',
                                            overflow: 'hidden',
                                        }}
                                    >
                                        {func.description}
                                    </Typography>
                                )}

                                <Grid container spacing={1} sx={{ mt: 'auto' }}>
                                    <Grid item xs={6}>
                                        <Typography variant="caption" color="text.secondary" display="block">
                                            Точек
                                        </Typography>
                                        <Typography variant="body2">
                                            {func.pointCount || 0}
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={6}>
                                        <Typography variant="caption" color="text.secondary" display="block">
                                            ID
                                        </Typography>
                                        <Typography variant="body2">
                                            #{func.id}
                                        </Typography>
                                    </Grid>
                                </Grid>

                                {func.functionClass && (
                                    <Box sx={{ mt: 2 }}>
                                        <Typography variant="caption" color="text.secondary" display="block">
                                            Класс
                                        </Typography>
                                        <Typography variant="body2" fontFamily="monospace" noWrap>
                                            {func.functionClass}
                                        </Typography>
                                    </Box>
                                )}
                            </CardContent>

                            {showActions && (
                                <CardActions sx={{ justifyContent: 'flex-end', pt: 0 }}>
                                    <Tooltip title="Просмотр графика">
                                        <IconButton
                                            size="small"
                                            onClick={() => handleView(func)}
                                        >
                                            <ShowChart />
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title="Редактировать">
                                        <IconButton
                                            size="small"
                                            onClick={() => handleEdit(func)}
                                        >
                                            <Edit />
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title="Экспорт">
                                        <IconButton
                                            size="small"
                                            onClick={() => handleExport(func)}
                                        >
                                            <Download />
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title="Удалить">
                                        <IconButton
                                            size="small"
                                            onClick={() => handleDelete(func)}
                                            sx={{ color: 'error.main' }}
                                        >
                                            <Delete />
                                        </IconButton>
                                    </Tooltip>
                                </CardActions>
                            )}
                        </Card>
                    </Grid>
                );
            })}
        </Grid>
    );
};

export default FunctionList;