import React from 'react';
import {
    Box,
    CircularProgress,
    Typography,
    Fade,
} from '@mui/material';

interface LoadingSpinnerProps {
    fullScreen?: boolean;
    message?: string;
    size?: number;
    color?: 'primary' | 'secondary' | 'inherit';
}

const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
                                                           fullScreen = false,
                                                           message = 'Загрузка...',
                                                           size = 40,
                                                           color = 'primary',
                                                       }) => {
    const content = (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
            }}
        >
            <CircularProgress size={size} color={color} />
            {message && (
                <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{ mt: 2 }}
                >
                    {message}
                </Typography>
            )}
        </Box>
    );

    if (fullScreen) {
        return (
            <Fade in timeout={500}>
                <Box
                    sx={{
                        position: 'fixed',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        backgroundColor: 'rgba(0, 0, 0, 0.7)',
                        zIndex: 9999,
                    }}
                >
                    {content}
                </Box>
            </Fade>
        );
    }

    return (
        <Fade in timeout={500}>
            <Box sx={{ py: 4 }}>
                {content}
            </Box>
        </Fade>
    );
};

export default LoadingSpinner;