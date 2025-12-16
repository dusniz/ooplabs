import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import {
    Box,
    Typography,
    Button,
    Grid,
    Card,
    CardContent,
    CardActions,
    Chip,
    IconButton,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    LinearProgress,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Tabs,
    Tab,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Tooltip,
} from '@mui/material';
import {
    Add,
    Edit,
    Delete,
    ShowChart,
    Visibility,
    Download,
    Upload,
    GridView,
    TableRows,
    FilterList,
    Search,
} from '@mui/icons-material';
import { functionApi, pointsApi } from '../api';
import { Function as Func, Point } from '../types';
import FunctionForm from '../components/functions/FunctionForm';
import FunctionGraph from '../components/functions/FunctionGraph';
import toast from 'react-hot-toast';

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

const FunctionsPage: React.FC = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const [functions, setFunctions] = useState<Func[]>([]);
    const [selectedFunction, setSelectedFunction] = useState<Func | null>(null);
    const [functionPoints, setFunctionPoints] = useState<Point[]>([]);
    const [loading, setLoading] = useState(true);
    const [loadingPoints, setLoadingPoints] = useState(false);
    const [viewMode, setViewMode] = useState<'grid' | 'table'>('grid');
    const [tabValue, setTabValue] = useState(0);
    const [filterType, setFilterType] = useState<'ALL' | 'TABULATED' | 'MATH'>('ALL');
    const [searchQuery, setSearchQuery] = useState('');
    const [showCreateDialog, setShowCreateDialog] = useState(false);
    const [showDeleteDialog, setShowDeleteDialog] = useState(false);
    const [showGraphDialog, setShowGraphDialog] = useState(false);
    const [functionToDelete, setFunctionToDelete] = useState<Func | null>(null);

    useEffect(() => {
        loadFunctions();

        // Проверяем параметры URL
        const createParam = searchParams.get('create');
        const viewParam = searchParams.get('view');

        if (createParam === 'true') {
            setShowCreateDialog(true);
        }

        if (viewParam) {
            const funcId = parseInt(viewParam);
            if (funcId) {
                handleViewFunction(funcId);
            }
        }
    }, [searchParams]);

    const loadFunctions = async () => {
        try {
            setLoading(true);
            const data = await functionApi.getAllFunctions();
            setFunctions(data);
        } catch (error) {
            toast.error('Ошибка загрузки функций');
        } finally {
            setLoading(false);
        }
    };

    const loadFunctionPoints = async (funcId: number) => {
        try {
            setLoadingPoints(true);
            const points = await functionApi.getFunctionPoints(funcId);
            setFunctionPoints(points);
        } catch (error) {
            toast.error('Ошибка загрузки точек функции');
        } finally {
            setLoadingPoints(false);
        }
    };

    const handleViewFunction = async (funcId: number) => {
        const func = functions.find(f => f.id === funcId);
        if (func) {
            setSelectedFunction(func);
            await loadFunctionPoints(funcId);
            setShowGraphDialog(true);
        }
    };

    const handleEditFunction = (func: Func) => {
        setSelectedFunction(func);
        setShowCreateDialog(true);
    };

    const handleDeleteFunction = (func: Func) => {
        setFunctionToDelete(func);
        setShowDeleteDialog(true);
    };

    const confirmDeleteFunction = async () => {
        if (!functionToDelete) return;

        try {
            await functionApi.deleteFunction(functionToDelete.id);
            toast.success('Функция удалена успешно');
            loadFunctions();
            setShowDeleteDialog(false);
            setFunctionToDelete(null);
        } catch (error) {
            toast.error('Ошибка удаления функции');
        }
    };

    const handleCreateSuccess = () => {
        loadFunctions();
        setShowCreateDialog(false);
        setSelectedFunction(null);
        // Убираем параметр из URL
        searchParams.delete('create');
        setSearchParams(searchParams);
    };

    const handleExportFunction = async (func: Func) => {
        try {
            // TODO: Реализовать экспорт функции
            toast.success(`Функция "${func.name}" экспортирована`);
        } catch (error) {
            toast.error('Ошибка экспорта функции');
        }
    };

    const handleImportFunction = () => {
        // TODO: Реализовать импорт функции`
    };

    const filteredFunctions = functions.filter(func => {
        // Фильтр по типу
        if (filterType !== 'ALL' && func.type !== filterType) {
            return false;
        }

        // Фильтр по поиску
        if (searchQuery) {
            const query = searchQuery.toLowerCase();
            return (
                func.name.toLowerCase().includes(query) ||
                func.description?.toLowerCase().includes(query) ||
                func.functionClass?.toLowerCase().includes(query)
            );
        }

        return true;
    });

    return (
        <Box>
            {/* Заголовок и кнопки действий */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h4">
                    Функции
                </Typography>
                <Box sx={{ display: 'flex', gap: 2 }}>
                    <Button
                        variant="outlined"
                        startIcon={<Upload />}
                        onClick={handleImportFunction}
                    >
                        Импорт
                    </Button>
                    <Button
                        variant="contained"
                        startIcon={<Add />}
                        onClick={() => setShowCreateDialog(true)}
                    >
                        Новая функция
                    </Button>
                </Box>
            </Box>

            {/* Фильтры и поиск */}
            <Paper sx={{ p: 2, mb: 3 }}>
                <Grid container spacing={2} alignItems="center">
                    <Grid item xs={12} md={4}>
                        <TextField
                            fullWidth
                            placeholder="Поиск функций..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            InputProps={{
                                startAdornment: <Search sx={{ mr: 1, color: 'text.secondary' }} />,
                            }}
                        />
                    </Grid>
                    <Grid item xs={12} md={3}>
                        <FormControl fullWidth>
                            <InputLabel>Тип функции</InputLabel>
                            <Select
                                value={filterType}
                                label="Тип функции"
                                onChange={(e) => setFilterType(e.target.value as any)}
                            >
                                <MenuItem value="ALL">Все типы</MenuItem>
                                <MenuItem value="TABULATED">Табулированные</MenuItem>
                                <MenuItem value="MATH">Математические</MenuItem>
                            </Select>
                        </FormControl>
                    </Grid>
                    <Grid item xs={12} md={3}>
                        <Box sx={{ display: 'flex', gap: 1 }}>
                            <Tooltip title="Плиткой">
                                <IconButton
                                    onClick={() => setViewMode('grid')}
                                    color={viewMode === 'grid' ? 'primary' : 'default'}
                                >
                                    <GridView />
                                </IconButton>
                            </Tooltip>
                            <Tooltip title="Таблицей">
                                <IconButton
                                    onClick={() => setViewMode('table')}
                                    color={viewMode === 'table' ? 'primary' : 'default'}
                                >
                                    <TableRows />
                                </IconButton>
                            </Tooltip>
                        </Box>
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <Typography variant="body2" color="text.secondary" align="right">
                            Найдено: {filteredFunctions.length}
                        </Typography>
                    </Grid>
                </Grid>
            </Paper>

            {loading && <LinearProgress sx={{ mb: 3 }} />}

            {/* Список функций */}
            {viewMode === 'grid' ? (
                <Grid container spacing={3}>
                    {filteredFunctions.map((func) => (
                        <Grid item xs={12} sm={6} md={4} key={func.id}>
                            <Card>
                                <CardContent>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                                        <Typography variant="h6" noWrap sx={{ flex: 1, mr: 1 }}>
                                            {func.name}
                                        </Typography>
                                        <Chip
                                            label={func.type === 'TABULATED' ? 'Таб.' : 'Мат.'}
                                            size="small"
                                            color={func.type === 'TABULATED' ? 'primary' : 'secondary'}
                                        />
                                    </Box>

                                    {func.description && (
                                        <Typography variant="body2" color="text.secondary" paragraph sx={{ mb: 2 }}>
                                            {func.description.length > 100
                                                ? `${func.description.substring(0, 100)}...`
                                                : func.description}
                                        </Typography>
                                    )}

                                    <Grid container spacing={1}>
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
                                            <Typography variant="body2" fontFamily="monospace">
                                                {func.functionClass}
                                            </Typography>
                                        </Box>
                                    )}
                                </CardContent>

                                <CardActions>
                                    <Tooltip title="Просмотр графика">
                                        <IconButton
                                            size="small"
                                            onClick={() => handleViewFunction(func.id)}
                                        >
                                            <ShowChart />
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title="Редактировать">
                                        <IconButton
                                            size="small"
                                            onClick={() => handleEditFunction(func)}
                                        >
                                            <Edit />
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title="Экспорт">
                                        <IconButton
                                            size="small"
                                            onClick={() => handleExportFunction(func)}
                                        >
                                            <Download />
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title="Удалить">
                                        <IconButton
                                            size="small"
                                            onClick={() => handleDeleteFunction(func)}
                                            sx={{ color: 'error.main' }}
                                        >
                                            <Delete />
                                        </IconButton>
                                    </Tooltip>
                                </CardActions>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            ) : (
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>ID</TableCell>
                                <TableCell>Название</TableCell>
                                <TableCell>Тип</TableCell>
                                <TableCell>Описание</TableCell>
                                <TableCell>Точек</TableCell>
                                <TableCell>Класс</TableCell>
                                <TableCell align="center">Действия</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {filteredFunctions.map((func) => (
                                <TableRow key={func.id} hover>
                                    <TableCell>{func.id}</TableCell>
                                    <TableCell>
                                        <Typography variant="body2" fontWeight="medium">
                                            {func.name}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>
                                        <Chip
                                            label={func.type === 'TABULATED' ? 'Табулированная' : 'Математическая'}
                                            size="small"
                                            color={func.type === 'TABULATED' ? 'primary' : 'secondary'}
                                        />
                                    </TableCell>
                                    <TableCell>
                                        <Typography variant="body2" color="text.secondary" noWrap sx={{ maxWidth: 200 }}>
                                            {func.description || '—'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>{func.pointCount || 0}</TableCell>
                                    <TableCell>
                                        <Typography variant="body2" fontFamily="monospace">
                                            {func.functionClass || '—'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell align="center">
                                        <Box sx={{ display: 'flex', justifyContent: 'center', gap: 1 }}>
                                            <Tooltip title="График">
                                                <IconButton
                                                    size="small"
                                                    onClick={() => handleViewFunction(func.id)}
                                                >
                                                    <ShowChart fontSize="small" />
                                                </IconButton>
                                            </Tooltip>
                                            <Tooltip title="Редактировать">
                                                <IconButton
                                                    size="small"
                                                    onClick={() => handleEditFunction(func)}
                                                >
                                                    <Edit fontSize="small" />
                                                </IconButton>
                                            </Tooltip>
                                            <Tooltip title="Удалить">
                                                <IconButton
                                                    size="small"
                                                    onClick={() => handleDeleteFunction(func)}
                                                    sx={{ color: 'error.main' }}
                                                >
                                                    <Delete fontSize="small" />
                                                </IconButton>
                                            </Tooltip>
                                        </Box>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}

            {filteredFunctions.length === 0 && !loading && (
                <Paper sx={{ p: 8, textAlign: 'center' }}>
                    <Typography variant="h6" color="text.secondary" gutterBottom>
                        Функции не найдены
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                        {searchQuery || filterType !== 'ALL'
                            ? 'Попробуйте изменить параметры поиска'
                            : 'Создайте первую функцию, нажав кнопку "Новая функция"'}
                    </Typography>
                </Paper>
            )}

            {/* Диалог создания/редактирования функции */}
            <FunctionForm
                open={showCreateDialog}
                onClose={() => {
                    setShowCreateDialog(false);
                    setSelectedFunction(null);
                    searchParams.delete('create');
                    setSearchParams(searchParams);
                }}
                onSuccess={handleCreateSuccess}
                initialData={selectedFunction || undefined}
            />

            {/* Диалог удаления функции */}
            <Dialog open={showDeleteDialog} onClose={() => setShowDeleteDialog(false)}>
                <DialogTitle>Подтверждение удаления</DialogTitle>
                <DialogContent>
                    <Typography>
                        Вы уверены, что хотите удалить функцию "{functionToDelete?.name}"?
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                        Это действие нельзя отменить. Все точки функции также будут удалены.
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setShowDeleteDialog(false)}>Отмена</Button>
                    <Button
                        onClick={confirmDeleteFunction}
                        color="error"
                        variant="contained"
                    >
                        Удалить
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Диалог просмотра графика функции */}
            <Dialog
                open={showGraphDialog}
                onClose={() => setShowGraphDialog(false)}
                maxWidth="lg"
                fullWidth
            >
                <DialogTitle>
                    {selectedFunction?.name} - График функции
                </DialogTitle>
                <DialogContent>
                    <Tabs value={tabValue} onChange={(_, value) => setTabValue(value)}>
                        <Tab label="График" />
                        <Tab label="Точки" />
                        <Tab label="Информация" />
                    </Tabs>

                    <TabPanel value={tabValue} index={0}>
                        {loadingPoints ? (
                            <LinearProgress sx={{ my: 4 }} />
                        ) : (
                            <FunctionGraph
                                points={functionPoints}
                                title={selectedFunction?.name}
                                height={400}
                            />
                        )}
                    </TabPanel>

                    <TabPanel value={tabValue} index={1}>
                        {loadingPoints ? (
                            <LinearProgress sx={{ my: 4 }} />
                        ) : functionPoints.length > 0 ? (
                            <TableContainer component={Paper} sx={{ maxHeight: 400 }}>
                                <Table stickyHeader size="small">
                                    <TableHead>
                                        <TableRow>
                                            <TableCell>#</TableCell>
                                            <TableCell>X</TableCell>
                                            <TableCell>Y</TableCell>
                                            <TableCell>Индекс</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {functionPoints.map((point, index) => (
                                            <TableRow key={point.id}>
                                                <TableCell>{index + 1}</TableCell>
                                                <TableCell>{point.x.toFixed(4)}</TableCell>
                                                <TableCell>{Number.isFinite(point.y) ? point.y.toFixed(4) : 0}</TableCell>
                                                <TableCell>{point.index}</TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        ) : (
                            <Typography color="text.secondary" align="center" sx={{ py: 4 }}>
                                У функции нет точек
                            </Typography>
                        )}
                    </TabPanel>

                    <TabPanel value={tabValue} index={2}>
                        {selectedFunction && (
                            <Grid container spacing={2}>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Название:
                                    </Typography>
                                    <Typography variant="body1">{selectedFunction.name}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Тип:
                                    </Typography>
                                    <Typography variant="body1">
                                        {selectedFunction.type === 'TABULATED' ? 'Табулированная' : 'Математическая'}
                                    </Typography>
                                </Grid>
                                <Grid item xs={12}>
                                    <Typography variant="body2" color="text.secondary">
                                        Описание:
                                    </Typography>
                                    <Typography variant="body1">
                                        {selectedFunction.description || '—'}
                                    </Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        ID функции:
                                    </Typography>
                                    <Typography variant="body1">#{selectedFunction.id}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Количество точек:
                                    </Typography>
                                    <Typography variant="body1">{selectedFunction.pointCount || 0}</Typography>
                                </Grid>
                                {selectedFunction.functionClass && (
                                    <Grid item xs={12}>
                                        <Typography variant="body2" color="text.secondary">
                                            Класс функции:
                                        </Typography>
                                        <Typography variant="body1" fontFamily="monospace">
                                            {selectedFunction.functionClass}
                                        </Typography>
                                    </Grid>
                                )}
                            </Grid>
                        )}
                    </TabPanel>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setShowGraphDialog(false)}>Закрыть</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default FunctionsPage;