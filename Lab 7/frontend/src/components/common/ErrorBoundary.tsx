import React, { Component, ErrorInfo, ReactNode } from 'react';
import { Box, Typography, Button, Paper } from '@mui/material';
import { Error as ErrorIcon } from '@mui/icons-material';

interface Props {
    children: ReactNode;
}

interface State {
    hasError: boolean;
    error: Error | null;
}

class ErrorBoundary extends Component<Props, State> {
    public state: State = {
        hasError: false,
        error: null,
    };

    public static getDerivedStateFromError(error: Error): State {
        return { hasError: true, error };
    }

    public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
        console.error('Uncaught error:', error, errorInfo);
    }

    public render() {
        if (this.state.hasError) {
            return (
                <Box
                    sx={{
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                        minHeight: '100vh',
                        p: 3,
                    }}
                >
                    <Paper
                        elevation={3}
                        sx={{
                            p: 4,
                            maxWidth: 500,
                            textAlign: 'center',
                        }}
                    >
                        <ErrorIcon sx={{ fontSize: 60, color: 'error.main', mb: 2 }} />
                        <Typography variant="h5" gutterBottom>
                            Что-то пошло не так
                        </Typography>
                        <Typography color="text.secondary" paragraph>
                            Приложение столкнулось с ошибкой. Пожалуйста, обновите страницу или
                            попробуйте позже.
                        </Typography>
                        {this.state.error && (
                            <Paper
                                sx={{
                                    p: 2,
                                    bgcolor: 'background.default',
                                    textAlign: 'left',
                                    fontFamily: 'monospace',
                                    fontSize: 12,
                                    maxHeight: 200,
                                    overflow: 'auto',
                                    mb: 2,
                                }}
                            >
                                {this.state.error.toString()}
                            </Paper>
                        )}
                        <Button
                            variant="contained"
                            onClick={() => window.location.reload()}
                        >
                            Обновить страницу
                        </Button>
                    </Paper>
                </Box>
            );
        }

        return this.props.children;
    }
}

export default ErrorBoundary;