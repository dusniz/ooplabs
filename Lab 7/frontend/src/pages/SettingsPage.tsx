import React, { useState } from 'react';
import {
    Box,
    Typography,
    Tabs,
    Tab,
    Paper,
    Alert,
    Button,
} from '@mui/material';
import {
    Settings,
    Build,
    Person,
    Security,
    Notifications,
    DataArray,
} from '@mui/icons-material';
import FactorySettings from '../components/settings/FactorySettings';
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

const SettingsPage: React.FC = () => {
    const [tabValue, setTabValue] = useState(0);
    const [hasUnsavedChanges, setHasUnsavedChanges] = useState(false);

    const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
        if (hasUnsavedChanges) {
            if (!window.confirm('Есть несохраненные изменения. Перейти без сохранения?')) {
                return;
            }
            setHasUnsavedChanges(false);
        }
        setTabValue(newValue);
    };

    const handleSaveAll = () => {
        // TODO: Реализовать сохранение всех настроек
        setHasUnsavedChanges(false);
        toast.success('Все настройки сохранены');
    };

    const handleFactoryChange = () => {
        setHasUnsavedChanges(true);
    };

    const tabs = [
        {
            label: 'Фабрика функций',
            icon: <Build />,
            component: <FactorySettings onChange={handleFactoryChange} />,
        },
        {
            label: 'Профиль',
            icon: <Person />,
            component: (
                <Paper sx={{ p: 3 }}>
                    <Typography variant="h6" gutterBottom>
                        Настройки профиля
                    </Typography>
                    <Typography color="text.secondary">
                        Настройки профиля пользователя (в разработке)
                    </Typography>
                </Paper>
            ),
        },
        {
            label: 'Безопасность',
            icon: <Security />,
            component: (
                <Paper sx={{ p: 3 }}>
                    <Typography variant="h6" gutterBottom>
                        Настройки безопасности
                    </Typography>
                    <Typography color="text.secondary">
                        Настройки безопасности и доступа (в разработке)
                    </Typography>
                </Paper>
            ),
        },
        {
            label: 'Уведомления',
            icon: <Notifications />,
            component: (
                <Paper sx={{ p: 3 }}>
                    <Typography variant="h6" gutterBottom>
                        Настройки уведомлений
                    </Typography>
                    <Typography color="text.secondary">
                        Настройки уведомлений и оповещений (в разработке)
                    </Typography>
                </Paper>
            ),
        },
    ];

    return (
        <Box>
            {/* Заголовок и кнопки */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 3 }}>
                <Box>
                    <Typography variant="h4" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Settings /> Настройки системы
                    </Typography>
                    <Typography variant="body1" color="text.secondary">
                        Управление настройками приложения, фабрики функций и профиля пользователя
                    </Typography>
                </Box>

                {hasUnsavedChanges && (
                    <Button
                        variant="contained"
                        onClick={handleSaveAll}
                        startIcon={<Settings />}
                    >
                        Сохранить все
                    </Button>
                )}
            </Box>

            {hasUnsavedChanges && (
                <Alert severity="warning" sx={{ mb: 3 }}>
                    Есть несохраненные изменения. Не забудьте сохранить настройки.
                </Alert>
            )}

            {/* Вкладки */}
            <Paper sx={{ width: '100%' }}>
                <Tabs
                    value={tabValue}
                    onChange={handleTabChange}
                    variant="scrollable"
                    scrollButtons="auto"
                    sx={{
                        borderBottom: 1,
                        borderColor: 'divider',
                    }}
                >
                    {tabs.map((tab, index) => (
                        <Tab
                            key={index}
                            icon={tab.icon}
                            iconPosition="start"
                            label={tab.label}
                            sx={{
                                textTransform: 'none',
                                fontWeight: 'medium',
                                minHeight: 60,
                            }}
                        />
                    ))}
                </Tabs>
            </Paper>

            {/* Контент вкладок */}
            {tabs.map((tab, index) => (
                <TabPanel key={index} value={tabValue} index={index}>
                    {tab.component}
                </TabPanel>
            ))}

            {/* Информационная панель */}
            <Paper sx={{ p: 3, mt: 3 }}>
                <Typography variant="h6" gutterBottom>
                    Справка по настройкам
                </Typography>
                <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: 'repeat(2, 1fr)' }, gap: 3 }}>
                    <Box>
                        <Typography variant="subtitle1" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Build fontSize="small" /> Фабрика функций
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            Выбор типа фабрики определяет способ создания новых функций.
                            Табулированные функции создаются из массивов точек, математические - из выражений.
                        </Typography>
                    </Box>
                    <Box>
                        <Typography variant="subtitle1" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <DataArray fontSize="small" /> Системные настройки
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            Настройки системы, включая параметры вычислений, форматирование данных
                            и другие системные параметры приложения.
                        </Typography>
                    </Box>
                </Box>

                <Alert severity="info" sx={{ mt: 2 }}>
                    <Typography variant="body2">
                        <strong>Важно:</strong> Изменение типа фабрики повлияет на создание новых функций,
                        но не затронет уже существующие функции. Рекомендуется выбирать тип фабрики
                        в соответствии с типом данных, с которыми вы работаете.
                    </Typography>
                </Alert>
            </Paper>
        </Box>
    );
};

export default SettingsPage;