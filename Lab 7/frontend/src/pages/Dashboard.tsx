import React, { useEffect } from 'react';
import {
    Grid,
    Paper,
    Typography,
    Box,
    Card,
    CardContent,
    CardActions,
    Button,
    LinearProgress,
} from '@mui/material';
import {
    Functions,
    Calculate,
    Settings,
    Timeline,
    Add,
    ShowChart,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../store/store';
import { functionApi } from '../api/functions';
import { Function } from '../types';
import toast from 'react-hot-toast';

const Dashboard: React.FC = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch<AppDispatch>();
    const { user } = useSelector((state: RootState) => state.auth);
    const [functions, setFunctions] = React.useState<Function[]>([]);
    const [loading, setLoading] = React.useState(true);

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
            setLoading(false);
        }
    };

    const stats = {
        totalFunctions: functions.length,
        tabulatedFunctions: functions.filter(f => f.type === 'TABULATED').length,
        mathFunctions: functions.filter(f => f.type === 'MATH').length,
        totalPoints: functions.reduce((sum, f) => sum + (f.pointCount || 0), 0),
    };

    return (
        <Box>
            <Typography variant="h4" gutterBottom sx={{ mb: 4 }}>
                Добро пожаловать, {user?.username}!
            </Typography>

            {loading && <LinearProgress sx={{ mb: 3 }} />}

            {/* Quick Actions */}
            <Grid container spacing={3} sx={{ mb: 4 }}>
                <Grid item xs={12} sm={6} md={3}>
                    <Card
                        sx={{
                            height: '100%',
                            cursor: 'pointer',
                            transition: 'transform 0.2s',
                            '&:hover': { transform: 'translateY(-4px)' }
                        }}
                        onClick={() => navigate('/functions')}
                    >
                        <CardContent sx={{ textAlign: 'center' }}>
                            <Functions sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
                            <Typography variant="h6">Функции</Typography>
                            <Typography variant="body2" color="text.secondary">
                                Управление функциями
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} sm={6} md={3}>
                    <Card
                        sx={{
                            height: '100%',
                            cursor: 'pointer',
                            transition: 'transform 0.2s',
                            '&:hover': { transform: 'translateY(-4px)' }
                        }}
                        onClick={() => navigate('/operations')}
                    >
                        <CardContent sx={{ textAlign: 'center' }}>
                            <Calculate sx={{ fontSize: 48, color: 'secondary.main', mb: 2 }} />
                            <Typography variant="h6">Операции</Typography>
                            <Typography variant="body2" color="text.secondary">
                                Дифференцирование и интегрирование
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} sm={6} md={3}>
                    <Card
                        sx={{
                            height: '100%',
                            cursor: 'pointer',
                            transition: 'transform 0.2s',
                            '&:hover': { transform: 'translateY(-4px)' }
                        }}
                        onClick={() => navigate('/functions?create=true')}
                    >
                        <CardContent sx={{ textAlign: 'center' }}>
                            <Add sx={{ fontSize: 48, color: 'success.main', mb: 2 }} />
                            <Typography variant="h6">Создать функцию</Typography>
                            <Typography variant="body2" color="text.secondary">
                                Новая функция из точек или выражения
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} sm={6} md={3}>
                    <Card
                        sx={{
                            height: '100%',
                            cursor: 'pointer',
                            transition: 'transform 0.2s',
                            '&:hover': { transform: 'translateY(-4px)' }
                        }}
                        onClick={() => navigate('/settings')}
                    >
                        <CardContent sx={{ textAlign: 'center' }}>
                            <Settings sx={{ fontSize: 48, color: 'warning.main', mb: 2 }} />
                            <Typography variant="h6">Настройки</Typography>
                            <Typography variant="body2" color="text.secondary">
                                Настройки фабрики и системы
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            {/* Statistics */}
            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <Paper sx={{ p: 3 }}>
                        <Typography variant="h6" gutterBottom>
                            Статистика
                        </Typography>
                        <Grid container spacing={2}>
                            <Grid item xs={6}>
                                <Paper sx={{ p: 2, bgcolor: 'primary.dark' }}>
                                    <Typography variant="h4" align="center">
                                        {stats.totalFunctions}
                                    </Typography>
                                    <Typography align="center">Всего функций</Typography>
                                </Paper>
                            </Grid>
                            <Grid item xs={6}>
                                <Paper sx={{ p: 2, bgcolor: 'secondary.dark' }}>
                                    <Typography variant="h4" align="center">
                                        {stats.totalPoints}
                                    </Typography>
                                    <Typography align="center">Всего точек</Typography>
                                </Paper>
                            </Grid>
                            <Grid item xs={6}>
                                <Paper sx={{ p: 2, bgcolor: 'info.dark' }}>
                                    <Typography variant="h4" align="center">
                                        {stats.tabulatedFunctions}
                                    </Typography>
                                    <Typography align="center">Табулированные</Typography>
                                </Paper>
                            </Grid>
                            <Grid item xs={6}>
                                <Paper sx={{ p: 2, bgcolor: 'success.dark' }}>
                                    <Typography variant="h4" align="center">
                                        {stats.mathFunctions}
                                    </Typography>
                                    <Typography align="center">Математические</Typography>
                                </Paper>
                            </Grid>
                        </Grid>
                    </Paper>
                </Grid>

                <Grid item xs={12} md={6}>
                    <Paper sx={{ p: 3, height: '100%' }}>
                        <Typography variant="h6" gutterBottom>
                            Последние функции
                        </Typography>
                        {functions.slice(0, 5).map((func) => (
                            <Box
                                key={func.id}
                                sx={{
                                    p: 2,
                                    mb: 1,
                                    bgcolor: 'action.hover',
                                    borderRadius: 1,
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'space-between',
                                    cursor: 'pointer',
                                    '&:hover': { bgcolor: 'action.selected' }
                                }}
                                onClick={() => navigate(`/functions?view=${func.id}`)}
                            >
                                <Box>
                                    <Typography variant="subtitle1">{func.name}</Typography>
                                    <Typography variant="body2" color="text.secondary">
                                        {func.type === 'TABULATED' ? 'Табулированная' : 'Математическая'} • {func.pointCount} точек
                                    </Typography>
                                </Box>
                                <ShowChart />
                            </Box>
                        ))}
                        {functions.length === 0 && (
                            <Typography color="text.secondary" align="center" sx={{ py: 4 }}>
                                Функции еще не созданы
                            </Typography>
                        )}
                        {functions.length > 5 && (
                            <Button
                                fullWidth
                                onClick={() => navigate('/functions')}
                                sx={{ mt: 2 }}
                            >
                                Показать все функции
                            </Button>
                        )}
                    </Paper>
                </Grid>
            </Grid>
        </Box>
    );
};

export default Dashboard;