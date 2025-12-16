import React from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    IconButton,
    Box,
    Typography,
    Slide,
} from '@mui/material';
import { Close } from '@mui/icons-material';
import { TransitionProps } from '@mui/material/transitions';

const Transition = React.forwardRef(function Transition(
    props: TransitionProps & {
        children: React.ReactElement;
    },
    ref: React.Ref<unknown>,
) {
    return <Slide direction="up" ref={ref} {...props} />;
});

interface ModalProps {
    open: boolean;
    onClose: () => void;
    title?: string;
    children: React.ReactNode;
    actions?: React.ReactNode;
    maxWidth?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
    fullWidth?: boolean;
    fullScreen?: boolean;
    disableBackdropClick?: boolean;
    showCloseButton?: boolean;
}

const Modal: React.FC<ModalProps> = ({
                                         open,
                                         onClose,
                                         title,
                                         children,
                                         actions,
                                         maxWidth = 'md',
                                         fullWidth = true,
                                         fullScreen = false,
                                         disableBackdropClick = false,
                                         showCloseButton = true,
                                     }) => {
    const handleClose = (event: {}, reason: 'backdropClick' | 'escapeKeyDown') => {
        if (disableBackdropClick && reason === 'backdropClick') {
            return;
        }
        onClose();
    };

    return (
        <Dialog
            open={open}
            onClose={handleClose}
            maxWidth={maxWidth}
            fullWidth={fullWidth}
            fullScreen={fullScreen}
            TransitionComponent={fullScreen ? Transition : undefined}
            PaperProps={{
                sx: {
                    borderRadius: fullScreen ? 0 : 2,
                    backgroundImage: 'none',
                },
            }}
        >
            {(title || showCloseButton) && (
                <DialogTitle>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        {title && (
                            <Typography variant="h6" component="div">
                                {title}
                            </Typography>
                        )}
                        {showCloseButton && (
                            <IconButton
                                aria-label="close"
                                onClick={onClose}
                                sx={{
                                    color: (theme) => theme.palette.grey[500],
                                }}
                            >
                                <Close />
                            </IconButton>
                        )}
                    </Box>
                </DialogTitle>
            )}

            <DialogContent dividers>
                {children}
            </DialogContent>

            {actions && (
                <DialogActions sx={{ px: 3, py: 2 }}>
                    {actions}
                </DialogActions>
            )}
        </Dialog>
    );
};

export default Modal;