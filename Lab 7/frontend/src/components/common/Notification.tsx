import React from 'react';
import { Alert, Snackbar } from '@mui/material';

interface NotificationProps {
    open: boolean;
    message: string;
    severity: 'success' | 'error' | 'warning' | 'info';
    onClose: () => void;
    autoHideDuration?: number;
}

const Notification: React.FC<NotificationProps> = ({
                                                       open,
                                                       message,
                                                       severity,
                                                       onClose,
                                                       autoHideDuration = 6000,
                                                   }) => {
    return (
        <Snackbar
            open={open}
            autoHideDuration={autoHideDuration}
            onClose={onClose}
            anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        >
            <Alert
                onClose={onClose}
                severity={severity}
                variant="filled"
                sx={{ width: '100%' }}
            >
                {message}
            </Alert>
        </Snackbar>
    );
};

export default Notification;