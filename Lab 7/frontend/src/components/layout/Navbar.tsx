import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import {
    AppBar,
    Box,
    Toolbar,
    IconButton,
    Typography,
    Menu,
    MenuItem,
    Avatar,
    Tooltip,
    Badge,
    Button,
} from '@mui/material';
import {
    Notifications as NotificationsIcon,
    AccountCircle,
    ExitToApp,
    Dashboard,
    Settings,
} from '@mui/icons-material';
import { logout } from '../../store/authSlice';
import {AppDispatch, RootState} from '../../store/store';

const Navbar: React.FC = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch<AppDispatch>();
    const { user } = useSelector((state: RootState) => state.auth);
    const [anchorElUser, setAnchorElUser] = React.useState<null | HTMLElement>(null);
    const [anchorElNotifications, setAnchorElNotifications] = React.useState<null | HTMLElement>(null);

    const handleOpenUserMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorElUser(event.currentTarget);
    };

    const handleCloseUserMenu = () => {
        setAnchorElUser(null);
    };

    const handleOpenNotifications = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorElNotifications(event.currentTarget);
    };

    const handleCloseNotifications = () => {
        setAnchorElNotifications(null);
    };

    const handleLogout = async () => {
        try {
            await dispatch(logout()).unwrap();
            navigate('/login');
            handleCloseUserMenu();
        } catch (error) {
            console.error('Logout error:', error);
        }
    };

    const handleProfile = () => {
        navigate('/profile');
        handleCloseUserMenu();
    };

    const handleSettings = () => {
        navigate('/settings');
        handleCloseUserMenu();
    };

    const handleDashboard = () => {
        navigate('/dashboard');
    };

    const userInitial = user?.username?.charAt(0).toUpperCase() || 'U';

    return (
        <>
            <Box sx={{ flexGrow: 1 }} />

            {/* Профиль пользователя */}
            <Tooltip title="Настройки профиля">
                <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                    <Avatar sx={{ bgcolor: 'primary.main', width: 36, height: 36 }}>
                        {userInitial}
                    </Avatar>
                </IconButton>
            </Tooltip>
            <Menu
                anchorEl={anchorElUser}
                open={Boolean(anchorElUser)}
                onClose={handleCloseUserMenu}
                PaperProps={{
                    sx: {
                        mt: 1.5,
                        minWidth: 200,
                    },
                }}
            >
                <MenuItem disabled>
                    <Typography variant="body2" color="text.secondary">
                        {user?.username}
                    </Typography>
                </MenuItem>
                <MenuItem onClick={handleDashboard}>
                    <Dashboard sx={{ mr: 1, fontSize: 20 }} />
                    Дашборд
                </MenuItem>
                <MenuItem onClick={handleSettings}>
                    <Settings sx={{ mr: 1, fontSize: 20 }} />
                    Настройки
                </MenuItem>
                <MenuItem onClick={handleProfile}>
                    <AccountCircle sx={{ mr: 1, fontSize: 20 }} />
                    Профиль
                </MenuItem>
                <MenuItem onClick={handleLogout} sx={{ color: 'error.main' }}>
                    <ExitToApp sx={{ mr: 1, fontSize: 20 }} />
                    Выйти
                </MenuItem>
            </Menu>
        </>
    );
};

export default Navbar;