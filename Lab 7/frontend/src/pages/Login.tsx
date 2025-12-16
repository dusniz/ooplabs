import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useNavigate, Link } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { login, clearError } from '../store/authSlice';
import { RootState, AppDispatch } from '../store/store';
import {
    Container,
    Paper,
    TextField,
    Button,
    Typography,
    Box,
    CircularProgress,
    Alert,
    Grid,
} from '@mui/material';
import { Login as LoginIcon } from '@mui/icons-material';

const schema = yup.object().shape({
    username: yup.string().required('Имя пользователя обязательно'),
    password: yup.string().min(6, 'Минимум 6 символов').required('Пароль обязателен'),
});

interface LoginForm {
    username: string;
    password: string;
}

const Login: React.FC = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch<AppDispatch>();
    const { loading, error } = useSelector((state: RootState) => state.auth);
    const { register, handleSubmit, formState: { errors } } = useForm<LoginForm>({
        resolver: yupResolver(schema),
    });

    React.useEffect(() => {
        dispatch(clearError());
    }, [dispatch]);

    const onSubmit = async (data: LoginForm) => {
        const result = await dispatch(login(data));
        if (login.fulfilled.match(result)) {
            navigate('/dashboard');
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Paper
                    elevation={3}
                    sx={{
                        padding: 4,
                        width: '100%',
                        backgroundColor: 'background.paper',
                    }}
                >
                    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 3 }}>
                        <LoginIcon sx={{ fontSize: 40, color: 'primary.main', mb: 1 }} />
                        <Typography component="h1" variant="h5">
                            Вход в систему
                        </Typography>
                    </Box>

                    {error && (
                        <Alert severity="error" sx={{ mb: 2 }}>
                            {error}
                        </Alert>
                    )}

                    <form onSubmit={handleSubmit(onSubmit)}>
                        <TextField
                            margin="normal"
                            fullWidth
                            label="Имя пользователя"
                            autoComplete="username"
                            autoFocus
                            {...register('username')}
                            error={!!errors.username}
                            helperText={errors.username?.message}
                            disabled={loading}
                        />
                        <TextField
                            margin="normal"
                            fullWidth
                            label="Пароль"
                            type="password"
                            autoComplete="current-password"
                            {...register('password')}
                            error={!!errors.password}
                            helperText={errors.password?.message}
                            disabled={loading}
                        />

                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                            disabled={loading}
                        >
                            {loading ? <CircularProgress size={24} /> : 'Войти'}
                        </Button>

                        <Grid container justifyContent="flex-end">
                            <Grid item>
                                <Link to="/register" style={{ textDecoration: 'none' }}>
                                    <Typography variant="body2" color="primary" sx={{ '&:hover': { textDecoration: 'underline' } }}>
                                        Нет аккаунта? Зарегистрироваться
                                    </Typography>
                                </Link>
                            </Grid>
                        </Grid>
                    </form>
                </Paper>
            </Box>
        </Container>
    );
};

export default Login;