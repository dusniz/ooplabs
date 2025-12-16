import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useNavigate, Link } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { register, clearError } from '../store/authSlice';
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
import { PersonAdd } from '@mui/icons-material';

const schema = yup.object().shape({
    username: yup
        .string()
        .min(3, 'Минимум 3 символа')
        .max(20, 'Максимум 20 символов')
        .matches(/^[a-zA-Z0-9_]+$/, 'Только буквы, цифры и подчеркивание')
        .required('Имя пользователя обязательно'),
    password: yup
        .string()
        .min(6, 'Минимум 6 символов')
        .max(50, 'Максимум 50 символов')
        .matches(/[A-Z]/, 'Должна быть хотя бы одна заглавная буква')
        .matches(/[0-9]/, 'Должна быть хотя бы одна цифра')
        .required('Пароль обязателен'),
    confirmPassword: yup
        .string()
        .oneOf([yup.ref('password')], 'Пароли должны совпадать')
        .required('Подтверждение пароля обязательно'),
});

interface RegisterForm {
    username: string;
    password: string;
    confirmPassword: string;
}

const Register: React.FC = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch<AppDispatch>();
    const { loading, error } = useSelector((state: RootState) => state.auth);
    const { register: registerField, handleSubmit, formState: { errors } } = useForm<RegisterForm>({
        resolver: yupResolver(schema),
    });

    React.useEffect(() => {
        dispatch(clearError());
    }, [dispatch]);

    const onSubmit = async (data: RegisterForm) => {
        const result = await dispatch(register({
            username: data.username,
            password: data.password,
        }));

        if (register.fulfilled.match(result)) {
            navigate('/login');
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
                        <PersonAdd sx={{ fontSize: 40, color: 'primary.main', mb: 1 }} />
                        <Typography component="h1" variant="h5">
                            Регистрация
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
                            {...registerField('username')}
                            error={!!errors.username}
                            helperText={errors.username?.message}
                            disabled={loading}
                        />
                        <TextField
                            margin="normal"
                            fullWidth
                            label="Пароль"
                            type="password"
                            autoComplete="new-password"
                            {...registerField('password')}
                            error={!!errors.password}
                            helperText={errors.password?.message}
                            disabled={loading}
                        />
                        <TextField
                            margin="normal"
                            fullWidth
                            label="Подтверждение пароля"
                            type="password"
                            autoComplete="new-password"
                            {...registerField('confirmPassword')}
                            error={!!errors.confirmPassword}
                            helperText={errors.confirmPassword?.message}
                            disabled={loading}
                        />

                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                            disabled={loading}
                        >
                            {loading ? <CircularProgress size={24} /> : 'Зарегистрироваться'}
                        </Button>

                        <Grid container justifyContent="flex-end">
                            <Grid item>
                                <Link to="/login" style={{ textDecoration: 'none' }}>
                                    <Typography variant="body2" color="primary" sx={{ '&:hover': { textDecoration: 'underline' } }}>
                                        Уже есть аккаунт? Войти
                                    </Typography>
                                </Link>
                            </Grid>
                        </Grid>
                    </form>
                </Paper>

                {/* Информация о требованиях */}
                <Paper sx={{ p: 3, mt: 3, width: '100%' }}>
                    <Typography variant="subtitle2" gutterBottom>
                        Требования к учетной записи:
                    </Typography>
                    <ul style={{ margin: 0, paddingLeft: 20, color: 'text.secondary' }}>
                        <li>Имя пользователя: 3-20 символов, только буквы, цифры и подчеркивание</li>
                        <li>Пароль: минимум 6 символов</li>
                        <li>Пароль должен содержать хотя бы одну заглавную букву и одну цифру</li>
                    </ul>
                </Paper>
            </Box>
        </Container>
    );
};

export default Register;