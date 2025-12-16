import React, { useState } from 'react';
import {
    Box,
    Typography,
    Tabs,
    Tab,
    Paper,
} from '@mui/material';
import {
    Calculate,
    IntegrationInstructions,
    Difference,
    Add,
} from '@mui/icons-material';
import Differentiation from '../components/operations/Differentiation';
import Integration from '../components/operations/Integration';
import BinaryOperations from '../components/operations/BinaryOperations';

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

const OperationsPage: React.FC = () => {
    const [tabValue, setTabValue] = useState(0);

    const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
        setTabValue(newValue);
    };

    const tabs = [
        {
            label: 'Дифференцирование',
            icon: <Difference />,
            component: <Differentiation />,
        },
        {
            label: 'Интегрирование',
            icon: <IntegrationInstructions />,
            component: <Integration />,
        },
        {
            label: 'Бинарные операции',
            icon: <Add />,
            component: <BinaryOperations />,
        },
    ];

    return (
        <Box>
            {/* Заголовок */}
            <Box sx={{ mb: 3 }}>
                <Typography variant="h4" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Calculate /> Математические операции
                </Typography>
                <Typography variant="body1" color="text.secondary">
                    Выполнение операций над функциями: дифференцирование, интегрирование и бинарные операции
                </Typography>
            </Box>

            {/* Вкладки */}
            <Paper sx={{ width: '100%' }}>
                <Tabs
                    value={tabValue}
                    onChange={handleTabChange}
                    variant="fullWidth"
                    sx={{
                        borderBottom: 1,
                        borderColor: 'divider',
                        '& .MuiTab-root': {
                            py: 2,
                        },
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
                    Информация об операциях
                </Typography>
                <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: 'repeat(3, 1fr)' }, gap: 3 }}>
                    <Box>
                        <Typography variant="subtitle1" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Difference fontSize="small" /> Дифференцирование
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            Вычисление производной функции. Поддерживаются табулированные и математические функции.
                            Для табулированных функций используется численное дифференцирование.
                        </Typography>
                    </Box>
                    <Box>
                        <Typography variant="subtitle1" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <IntegrationInstructions fontSize="small" /> Интегрирование
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            Вычисление определенного интеграла функции. Поддерживается многопоточное вычисление
                            для ускорения расчетов на больших интервалах.
                        </Typography>
                    </Box>
                    <Box>
                        <Typography variant="subtitle1" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Add fontSize="small" /> Бинарные операции
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            Выполнение операций над двумя функциями: сложение, вычитание, умножение, деление
                            и композиция. Результат можно сохранить как новую функцию.
                        </Typography>
                    </Box>
                </Box>
            </Paper>
        </Box>
    );
};

export default OperationsPage;