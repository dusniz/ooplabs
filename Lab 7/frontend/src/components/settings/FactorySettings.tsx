import React, {useEffect, useState} from 'react';
import {
    Alert,
    Box,
    Button,
    Card,
    CardContent,
    Chip,
    Divider,
    FormControl,
    FormControlLabel,
    Grid,
    LinearProgress,
    Paper,
    Radio,
    RadioGroup,
    Typography,
} from '@mui/material';
import {Build, DataArray, Functions, Settings} from '@mui/icons-material';
import {settingsApi} from '../../api/settings';
import {FactoryResponse, FactoryType, FunctionType} from '../../types';
import toast from 'react-hot-toast';

interface FactorySettingsProps {
    onChange?: () => void;
}

const FactorySettings: React.FC<FactorySettingsProps> = ({ onChange }) => {
    const [factoryType, setFactoryType] = useState<FactoryType>(FactoryType.ARRAY);
    const [currentSettings, setCurrentSettings] = useState<FactoryResponse | null>(null);
    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);
    const [isChanged, setIsChanged] = useState(false);

    useEffect(() => {
        loadFactorySettings();
    }, []);

    const loadFactorySettings = async () => {
        try {
            setLoading(true);
            const settings = await settingsApi.getFactoryType();
            setCurrentSettings(settings);
            setFactoryType(settings.factoryType as FactoryType);
        } catch (error) {
            toast.error('Ошибка загрузки настроек фабрики');
        } finally {
            setLoading(false);
        }
    };

    const handleSave = async () => {
        try {
            setSaving(true);
            await settingsApi.setFactoryType(factoryType);

            // Обновляем текущие настройки
            const updatedSettings = await settingsApi.getFactoryType();
            setCurrentSettings(updatedSettings);
            setIsChanged(false);

            toast.success('Настройки фабрики обновлены!');

            // Вызываем callback если передан
            if (onChange) {
                onChange();
            }
        } catch (error) {
            toast.error('Ошибка сохранения настроек');
        } finally {
            setSaving(false);
        }
    };

    const handleFactoryTypeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const newType = event.target.value as FactoryType;
        setFactoryType(newType);
        setIsChanged(newType !== currentSettings?.factoryType);

        // Вызываем callback если передан
        if (onChange) {
            onChange();
        }
    };

    const factoryTypes = [
        {
            value: FactoryType.ARRAY,
            label: 'Табулированные функции на основе массива',
            description: 'Создание функций на основе массивов точек',
            icon: <DataArray />,
            features: [
                'Быстрый доступ по индексу за O(1)',
                'Минимальные накладные расходы памяти'
            ],
        },
        {
            value: FactoryType.LIST,
            label: 'Табулированные функции на основе списка',
            description: 'Создание функций на основе списков точек',
            icon: <Functions />,
            features: [
                'Быстрая вставка/удаление за O(1) при известном месте',
                'Непрерывное выделение памяти не требуется'
            ],
        },
    ];

    const selectedFactory = factoryTypes.find(f => f.value === factoryType);

    return (
        <Box>
            <Typography variant="h5" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Settings /> Настройки фабрики функций
            </Typography>

            {loading && <LinearProgress sx={{ mb: 3 }} />}

            <Grid container spacing={3}>
                <Grid item xs={12} md={8}>
                    <Paper sx={{ p: 3 }}>
                        <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Build /> Выбор типа фабрики
                        </Typography>

                        <FormControl component="fieldset" fullWidth>
                            <RadioGroup
                                value={factoryType}
                                onChange={handleFactoryTypeChange}
                            >
                                <Grid container spacing={2}>
                                    {factoryTypes.map((factory) => (
                                        <Grid item xs={12} key={factory.value}>
                                            <Paper
                                                sx={{
                                                    p: 2,
                                                    border: factoryType === factory.value ? '2px solid' : '1px solid',
                                                    borderColor: factoryType === factory.value ? 'primary.main' : 'divider',
                                                    bgcolor: factoryType === factory.value ? 'primary.dark' : 'background.paper',
                                                    transition: 'all 0.2s',
                                                    cursor: 'pointer',
                                                    '&:hover': {
                                                        borderColor: 'primary.light',
                                                        bgcolor: 'action.hover',
                                                    },
                                                }}
                                                onClick={() => {
                                                    setFactoryType(factory.value);
                                                    setIsChanged(factory.value !== currentSettings?.factoryType);
                                                    if (onChange) onChange();
                                                }}
                                            >
                                                <Box sx={{ display: 'flex', alignItems: 'flex-start', gap: 2 }}>
                                                    <Box sx={{ color: 'primary.main' }}>
                                                        {factory.icon}
                                                    </Box>
                                                    <Box sx={{ flex: 1 }}>
                                                        <FormControlLabel
                                                            value={factory.value}
                                                            control={<Radio />}
                                                            label={
                                                                <Box>
                                                                    <Typography variant="subtitle1">
                                                                        {factory.label}
                                                                    </Typography>
                                                                    <Typography variant="body2" color="text.secondary">
                                                                        {factory.description}
                                                                    </Typography>
                                                                </Box>
                                                            }
                                                            sx={{ margin: 0 }}
                                                        />
                                                    </Box>
                                                </Box>
                                            </Paper>
                                        </Grid>
                                    ))}
                                </Grid>
                            </RadioGroup>
                        </FormControl>

                        {currentSettings && (
                            <Alert severity="info" sx={{ mt: 3 }}>
                                <Typography variant="subtitle2">
                                    Текущие настройки:
                                </Typography>
                                <Typography variant="body2">
                                    {currentSettings.description}
                                </Typography>
                            </Alert>
                        )}

                        <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
                            <Button
                                variant="outlined"
                                onClick={loadFactorySettings}
                                disabled={loading || saving}
                            >
                                Сбросить
                            </Button>
                            <Button
                                variant="contained"
                                onClick={handleSave}
                                disabled={!isChanged || saving}
                                startIcon={<Settings />}
                            >
                                {saving ? 'Сохранение...' : 'Сохранить настройки'}
                            </Button>
                        </Box>
                    </Paper>
                </Grid>

                <Grid item xs={12} md={4}>
                    <Paper sx={{ p: 3, height: '100%' }}>
                        <Typography variant="h6" gutterBottom>
                            Информация о выбранном типе
                        </Typography>

                        {selectedFactory ? (
                            <Box>
                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
                                    <Box sx={{ color: 'primary.main' }}>
                                        {selectedFactory.icon}
                                    </Box>
                                    <Box>
                                        <Typography variant="subtitle1">
                                            {selectedFactory.label}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            {selectedFactory.description}
                                        </Typography>
                                    </Box>
                                </Box>

                                <Divider sx={{ my: 2 }} />

                                <Typography variant="subtitle2" gutterBottom>
                                    Особенности:
                                </Typography>
                                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                                    {selectedFactory.features.map((feature, index) => (
                                        <Box
                                            key={index}
                                            sx={{
                                                display: 'flex',
                                                alignItems: 'center',
                                                gap: 1,
                                                p: 1,
                                                borderRadius: 1,
                                                bgcolor: 'action.hover',
                                            }}
                                        >
                                            <Chip label={index + 1} size="small" />
                                            <Typography variant="body2">{feature}</Typography>
                                        </Box>
                                    ))}
                                </Box>

                                <Box sx={{ mt: 3, p: 2, bgcolor: 'warning.dark', borderRadius: 1 }}>
                                    <Typography variant="body2">
                                        <strong>Внимание:</strong> Изменение типа фабрики повлияет на создание
                                        новых функций. Существующие функции останутся без изменений.
                                    </Typography>
                                </Box>
                            </Box>
                        ) : (
                            <Typography color="text.secondary" align="center" sx={{ py: 4 }}>
                                Выберите тип фабрики
                            </Typography>
                        )}
                    </Paper>
                </Grid>
            </Grid>

            <Paper sx={{ p: 3, mt: 3 }}>
                <Typography variant="h6" gutterBottom>
                    Рекомендации по выбору
                </Typography>
                <Grid container spacing={2}>
                    <Grid item xs={12} md={6}>
                        <Card>
                            <CardContent>
                                <Typography variant="subtitle1" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                    <DataArray /> Когда выбирать табулированные функции:
                                </Typography>
                                <Typography variant="body2" color="text.secondary" component="ul" sx={{ pl: 2 }}>
                                    <li>Работа с экспериментальными данными</li>
                                    <li>Точки заданы вручную или импортированы</li>
                                    <li>Требуется точный контроль значений в узлах</li>
                                    <li>Функция задана только в дискретных точках</li>
                                </Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <Card>
                            <CardContent>
                                <Typography variant="subtitle1" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                    <Functions /> Когда выбирать математические функции:
                                </Typography>
                                <Typography variant="body2" color="text.secondary" component="ul" sx={{ pl: 2 }}>
                                    <li>Работа с аналитическими выражениями</li>
                                    <li>Требуется автоматическая генерация точек</li>
                                    <li>Выполнение сложных математических операций</li>
                                    <li>Функция задана формулой</li>
                                </Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                </Grid>
            </Paper>
        </Box>
    );
};

export default FactorySettings;