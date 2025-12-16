import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
    Box,
    List,
    ListItem,
    ListItemButton,
    ListItemIcon,
    ListItemText,
    Divider,
    Typography,
    Toolbar,
} from '@mui/material';
import {
    Dashboard,
    Functions,
    Calculate,
    Settings,
    Timeline,
    Add,
    IntegrationInstructions,
    Difference,
    AddCircle,
} from '@mui/icons-material';

const menuItems = [
    {
        text: 'Дашборд',
        icon: <Dashboard />,
        path: '/dashboard',
    },
    {
        text: 'Функции',
        icon: <Functions />,
        path: '/functions',
        subItems: [
            { text: 'Все функции', path: '/functions' },
            { text: 'Создать функцию', path: '/functions?create=true' },
        ],
    },
    {
        text: 'Операции',
        icon: <Calculate />,
        path: '/operations',
    },
    {
        text: 'Настройки',
        icon: <Settings />,
        path: '/settings',
    },
];

const Sidebar: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [expandedMenu, setExpandedMenu] = React.useState<string | null>(null);

    const handleMenuClick = (path: string, hasSubItems?: boolean) => {
        if (hasSubItems) {
            setExpandedMenu(expandedMenu === path ? null : path);
        } else {
            navigate(path);
        }
    };

    const isActive = (path: string) => {
        return location.pathname === path || location.pathname.startsWith(path + '/');
    };

    return (
        <Box>
            <Toolbar>
                <Typography variant="h6" noWrap sx={{ fontWeight: 'bold' }}>
                    FunLab
                </Typography>
            </Toolbar>
            <Divider />

            {/* Основное меню */}
            <List>
                {menuItems.map((item) => (
                    <React.Fragment key={item.text}>
                        <ListItem disablePadding>
                            <ListItemButton
                                selected={isActive(item.path)}
                                onClick={() => handleMenuClick(item.path, !!item.subItems)}
                                sx={{
                                    '&.Mui-selected': {
                                        backgroundColor: 'primary.dark',
                                        '&:hover': {
                                            backgroundColor: 'primary.dark',
                                        },
                                    },
                                }}
                            >
                                <ListItemIcon sx={{ color: isActive(item.path) ? 'primary.light' : 'inherit' }}>
                                    {item.icon}
                                </ListItemIcon>
                                <ListItemText primary={item.text} />
                            </ListItemButton>
                        </ListItem>
                        {item.subItems && expandedMenu === item.path && (
                            <List component="div" disablePadding>
                                {item.subItems.map((subItem) => (
                                    <ListItem key={subItem.text} disablePadding>
                                        <ListItemButton
                                            selected={isActive(subItem.path)}
                                            onClick={() => navigate(subItem.path)}
                                            sx={{ pl: 4 }}
                                        >
                                            <ListItemText primary={subItem.text} />
                                        </ListItemButton>
                                    </ListItem>
                                ))}
                            </List>
                        )}
                    </React.Fragment>
                ))}
            </List>

            <Divider sx={{ my: 2 }} />

            {/* Информация о системе */}
            <Box sx={{ p: 2 }}>
                <Typography variant="caption" color="text.secondary" display="block">
                    Версия 0.1.0
                </Typography>
                <Typography variant="caption" color="text.secondary" display="block">
                    © 2025 FunLab
                </Typography>
            </Box>
        </Box>
    );
};

export default Sidebar;