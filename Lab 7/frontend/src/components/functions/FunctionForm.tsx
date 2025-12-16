import React, { useState } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Button,
    Box,
    Tabs,
    Tab,
    Typography,
    Grid,
    IconButton,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
} from '@mui/material';
import { Add, Delete } from '@mui/icons-material';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import {Function as Func, Point, FunctionType, FunctionClass} from '../../types';
import { functionApi } from '../../api/functions';
import toast from 'react-hot-toast';

interface FunctionFormProps {
    open: boolean;
    onClose: () => void;
    onSuccess: () => void;
    initialData?: Func;
}

interface FunctionFormData {
    name: string;
    description?: string;
    type: FunctionType;
    functionClass?: string;
}

const schema = yup.object().shape({
    name: yup.string().required('Название обязательно'),
    description: yup.string(),
    type: yup.mixed<FunctionType>().oneOf([FunctionType.TABULATED, FunctionType.MATH]).required('Тип обязателен'),
    functionClass: yup.string(),
});

const FunctionForm: React.FC<FunctionFormProps> = ({
                                                       open,
                                                       onClose,
                                                       onSuccess,
                                                       initialData,
                                                   }) => {
    const [tab, setTab] = useState(0);
    const [points, setPoints] = useState<Omit<Point, 'id' | 'functionId'>[]>([
        { x: 0, y: 0, index: 0 },
        { x: 1, y: 1, index: 1 },
    ]);
    const [loading, setLoading] = useState(false);

    const {
        register,
        handleSubmit,
        watch,
        formState: { errors },
        reset,
    } = useForm<FunctionFormData>({
        resolver: yupResolver(schema),
        defaultValues: initialData ? {
            name: initialData.name,
            description: initialData.description || '',
            type: initialData.type,
            functionClass: initialData.functionClass || '',
        } : {
            name: '',
            description: '',
            type: FunctionType.TABULATED,
            functionClass: '',
        },
    });

    const selectedType = watch('type');

    const handleAddPoint = () => {
        const lastPoint = points[points.length - 1];
        const newIndex = lastPoint ? lastPoint.index + 1 : 0;
        setPoints([...points, { x: 0, y: 0, index: newIndex }]);
    };

    const handleRemovePoint = (index: number) => {
        setPoints(points.filter((_, i) => i !== index));
    };

    const handlePointChange = (index: number, field: 'x' | 'y', value: number) => {
        const newPoints = [...points];
        newPoints[index] = { ...newPoints[index], [field]: value };
        setPoints(newPoints);
    };

    const onSubmit = async (data: FunctionFormData) => {
        try {
            setLoading(true);

            if (selectedType === FunctionType.TABULATED) {
                // Создание табулированной функции из точек
                const response = await functionApi.createFunctionFromPoints({
                    userId: 1, // TODO: Получить из авторизации
                    name: data.name,
                    description: data.description,
                    type: data.type,
                    points: points.map((p, idx) => ({
                        functionId: 0,
                        x: p.x,
                        y: p.y,
                        index: idx,
                    })),
                });
                toast.success('Функция создана успешно!');
            } else {
                // Создание математической функции
                const response = await functionApi.createFunction({
                    userId: 1,
                    name: data.name,
                    description: data.description,
                    type: data.type,
                    functionClass: data.functionClass,
                    pointCount: 0,
                });
                toast.success('Функция создана успешно!');
            }

            onSuccess();
            onClose();
            reset();
            setPoints([{ x: 0, y: 0, index: 0 }, { x: 1, y: 1, index: 1 }]);
        } catch (error: any) {
            toast.error(error.message || 'Ошибка создания функции');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
            <DialogTitle>
                {initialData ? 'Редактировать функцию' : 'Создать новую функцию'}
            </DialogTitle>
            <form onSubmit={handleSubmit(onSubmit)}>
                <DialogContent>
                    <Tabs value={tab} onChange={(_, value) => setTab(value)} sx={{ mb: 3 }}>
                        <Tab label="Основная информация" />
                        <Tab label="Данные функции" />
                    </Tabs>

                    {tab === 0 && (
                        <Box>
                            <TextField
                                fullWidth
                                label="Название функции"
                                margin="normal"
                                {...register('name')}
                                error={!!errors.name}
                                helperText={errors.name?.message}
                            />

                            <TextField
                                fullWidth
                                label="Описание"
                                margin="normal"
                                multiline
                                rows={3}
                                {...register('description')}
                            />

                            <FormControl fullWidth margin="normal">
                                <InputLabel>Тип функции</InputLabel>
                                <Select
                                    label="Тип функции"
                                    {...register('type')}
                                    defaultValue={FunctionType.TABULATED}
                                >
                                    <MenuItem value={FunctionType.TABULATED}>Табулированная</MenuItem>
                                    <MenuItem value={FunctionType.MATH}>Математическая</MenuItem>
                                </Select>
                            </FormControl>

                            {selectedType === FunctionType.MATH && (
                                <FormControl fullWidth margin="normal">
                                    <InputLabel>Класс функции</InputLabel>
                                    <Select
                                        label="Класс функции"
                                        {...register('functionClass')}
                                        defaultValue={FunctionClass.LINEAR}
                                    >
                                        <MenuItem value={FunctionClass.LINEAR}>Линейная: y = x</MenuItem>
                                        <MenuItem value={FunctionClass.SQR}>Квадратичная: y = x^2</MenuItem>
                                        <MenuItem value={FunctionClass.ZERO}>Нулевая: y = 0</MenuItem>
                                        <MenuItem value={FunctionClass.UNIT}>Единичная: y = 1</MenuItem>
                                        <MenuItem value={FunctionClass.NATURAL_LOG}>Натуральный логарифм: y = ln(x)</MenuItem>
                                        <MenuItem value={FunctionClass.SIN}>Синусоида: y = sin(x)</MenuItem>
                                        <MenuItem value={FunctionClass.COS}>Косинусоида: y = cos(x)</MenuItem>
                                        <MenuItem value={FunctionClass.TAN}>Тангенс: y = tg(x)</MenuItem>
                                        <MenuItem value={FunctionClass.COT}>Котангенс: y = ctg(x)</MenuItem>
                                    </Select>
                                </FormControl>
                            )}
                        </Box>
                    )}

                    {tab === 1 && (
                        <Box>
                            {selectedType === FunctionType.TABULATED ? (
                                <>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                                        <Typography variant="h6">Точки функции</Typography>
                                        <Button
                                            startIcon={<Add />}
                                            onClick={handleAddPoint}
                                            variant="outlined"
                                        >
                                            Добавить точку
                                        </Button>
                                    </Box>

                                    <TableContainer component={Paper} sx={{ maxHeight: 400 }}>
                                        <Table stickyHeader size="small">
                                            <TableHead>
                                                <TableRow>
                                                    <TableCell>#</TableCell>
                                                    <TableCell>X</TableCell>
                                                    <TableCell>Y</TableCell>
                                                    <TableCell align="center">Действия</TableCell>
                                                </TableRow>
                                            </TableHead>
                                            <TableBody>
                                                {points.map((point, index) => (
                                                    <TableRow key={index}>
                                                        <TableCell>{index + 1}</TableCell>
                                                        <TableCell>
                                                            <TextField
                                                                type="number"
                                                                value={point.x}
                                                                onChange={(e) =>
                                                                    handlePointChange(index, 'x', parseFloat(e.target.value))
                                                                }
                                                                size="small"
                                                                fullWidth
                                                            />
                                                        </TableCell>
                                                        <TableCell>
                                                            <TextField
                                                                type="number"
                                                                value={point.y}
                                                                onChange={(e) =>
                                                                    handlePointChange(index, 'y', parseFloat(e.target.value))
                                                                }
                                                                size="small"
                                                                fullWidth
                                                            />
                                                        </TableCell>
                                                        <TableCell align="center">
                                                            <IconButton
                                                                size="small"
                                                                onClick={() => handleRemovePoint(index)}
                                                                disabled={points.length <= 2}
                                                            >
                                                                <Delete />
                                                            </IconButton>
                                                        </TableCell>
                                                    </TableRow>
                                                ))}
                                            </TableBody>
                                        </Table>
                                    </TableContainer>

                                    <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                                        Минимум 2 точки. Порядок точек определяется индексом.
                                    </Typography>
                                </>
                            ) : (
                                <Typography color="text.secondary">
                                    Для математических функций точки будут рассчитаны автоматически
                                    при использовании функции.
                                </Typography>
                            )}
                        </Box>
                    )}
                </DialogContent>

                <DialogActions>
                    <Button onClick={onClose} disabled={loading}>
                        Отмена
                    </Button>
                    <Button
                        type="submit"
                        variant="contained"
                        disabled={loading}
                    >
                        {loading ? 'Сохранение...' : initialData ? 'Сохранить' : 'Создать'}
                    </Button>
                </DialogActions>
            </form>
        </Dialog>
    );
};

export default FunctionForm;