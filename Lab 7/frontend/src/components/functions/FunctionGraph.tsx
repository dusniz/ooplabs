import React, { useMemo } from 'react';
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
    ScatterChart,
    Scatter,
    AreaChart,
    Area,
    Brush,
    ReferenceLine,
} from 'recharts';
import { Paper, Typography, Box, FormControl, InputLabel, Select, MenuItem } from '@mui/material';
import { Point } from '../../types';

interface FunctionGraphProps {
    points: Point[];
    title?: string;
    height?: number;
    showDerivative?: boolean;
    derivativePoints?: Point[];
    showIntegral?: boolean;
    integralArea?: { x: number; y: number }[];
}

const FunctionGraph: React.FC<FunctionGraphProps> = ({
                                                         points,
                                                         title = 'График функции',
                                                         height = 400,
                                                         showDerivative = false,
                                                         derivativePoints = [],
                                                         showIntegral = false,
                                                         integralArea = [],
                                                     }) => {
    const [chartType, setChartType] = React.useState<'line' | 'scatter' | 'area'>('line');

    const data = useMemo(() => {
        if (!points || points.length === 0) return [];

        return points
            .map(point => ({
                x: point.x,
                y: point.y,
                derivative: derivativePoints.find(p => Math.abs(p.x - point.x) < 0.001)?.y,
            }))
            .sort((a, b) => a.x - b.x);
    }, [points, derivativePoints]);

    const getDomain = () => {
        if (data.length === 0) return [0, 10];

        const xs = data.map(d => d.x);
        const ys = data.map(d => d.y);

        const xMin = Math.min(...xs);
        const xMax = Math.max(...xs);
        const yMin = Math.min(...ys);
        const yMax = Math.max(...ys);

        const xPadding = (xMax - xMin) * 0.1;
        const yPadding = (yMax - yMin) * 0.1;

        return [
            Math.floor(xMin - xPadding),
            Math.ceil(xMax + xPadding),
            Math.floor(yMin - yPadding),
            Math.ceil(yMax + yPadding),
        ];
    };

    const [xMin, xMax, yMin, yMax] = getDomain();

    if (data.length === 0) {
        return (
            <Paper sx={{ p: 3, height }}>
                <Typography variant="h6" gutterBottom>
                    {title}
                </Typography>
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 'calc(100% - 60px)' }}>
                    <Typography color="text.secondary">
                        Нет данных для построения графика
                    </Typography>
                </Box>
            </Paper>
        );
    }

    const renderChart = () => {
        const commonProps = {
            data,
            margin: { top: 10, right: 90, left: 60, bottom: 10 },
        };

        switch (chartType) {
            case 'scatter':
                return (
                    <ScatterChart {...commonProps}>
                        <CartesianGrid strokeDasharray="3 3" stroke="#444" />
                        <XAxis
                            type="number"
                            dataKey="x"
                            domain={[xMin, xMax]}
                            label={{ value: 'X', position: 'insideBottom', offset: -10 }}
                        />
                        <YAxis
                            type="number"
                            dataKey="y"
                            domain={[yMin, yMax]}
                            label={{ value: 'Y', angle: -90, position: 'insideLeft' }}
                        />
                        <Tooltip
                            formatter={(value: number, name: string) => [
                                Number.isFinite(value) ? value.toFixed(4) : 0,
                                name === 'y' ? 'f(x)' : name,
                            ]}
                            labelFormatter={(label) => `x = ${parseFloat(label).toFixed(4)}`}
                            contentStyle={{ backgroundColor: '#1e1e1e', borderColor: '#333' }}
                        />
                        <Legend />
                        <Scatter name="Функция" dataKey="y" fill="#8884d8" />
                        {showDerivative && (
                            <Scatter name="Производная" dataKey="derivative" fill="#82ca9d" />
                        )}
                    </ScatterChart>
                );

            case 'area':
                return (
                    <AreaChart {...commonProps}>
                        <CartesianGrid strokeDasharray="3 3" stroke="#444" />
                        <XAxis
                            type="number"
                            dataKey="x"
                            domain={[xMin, xMax]}
                            label={{ value: 'X', position: 'insideBottom', offset: -10 }}
                        />
                        <YAxis
                            type="number"
                            dataKey="y"
                            domain={[yMin, yMax]}
                            label={{ value: 'Y', angle: -90, position: 'insideLeft' }}
                        />
                        <Tooltip
                            formatter={(value: number, name: string) => [
                                Number.isFinite(value) ? value.toFixed(4) : 0,
                                name === 'y' ? 'f(x)' : name,
                            ]}
                            labelFormatter={(label) => `x = ${parseFloat(label).toFixed(4)}`}
                            contentStyle={{ backgroundColor: '#1e1e1e', borderColor: '#333' }}
                        />
                        <Legend />
                        <Area
                            type="monotone"
                            dataKey="y"
                            stroke="#8884d8"
                            fill="#8884d8"
                            fillOpacity={0.3}
                            name="Функция"
                        />
                        {showIntegral && integralArea.length > 0 && (
                            <Area
                                type="monotone"
                                data={integralArea}
                                dataKey="y"
                                stroke="#82ca9d"
                                fill="#82ca9d"
                                fillOpacity={0.6}
                                name="Интеграл"
                            />
                        )}
                        <Brush dataKey="x" height={30} stroke="#8884d8" />
                    </AreaChart>
                );

            default: // line
                return (
                    <LineChart {...commonProps}>
                        <CartesianGrid strokeDasharray="3 3" stroke="#444" />
                        <XAxis
                            type="number"
                            dataKey="x"
                            domain={[xMin, xMax]}
                            label={{ value: 'X', position: 'insideBottom', offset: -10 }}
                        />
                        <YAxis
                            type="number"
                            dataKey="y"
                            domain={[yMin, yMax]}
                            label={{ value: 'Y', angle: -90, position: 'insideLeft' }}
                        />
                        <Tooltip
                            formatter={(value: number, name: string) => [
                                Number.isFinite(value) ? value.toFixed(4) : 0,
                                name === 'y' ? 'f(x)' : name === 'derivative' ? "f'(x)" : name,
                            ]}
                            labelFormatter={(label) => `x = ${parseFloat(label).toFixed(4)}`}
                            contentStyle={{ backgroundColor: '#1e1e1e', borderColor: '#333' }}
                        />
                        <Legend />
                        <Line
                            type="monotone"
                            dataKey="y"
                            stroke="#8884d8"
                            strokeWidth={2}
                            dot={{ r: 4 }}
                            activeDot={{ r: 6 }}
                            name="Функция"
                        />
                        {showDerivative && (
                            <Line
                                type="monotone"
                                dataKey="derivative"
                                stroke="#82ca9d"
                                strokeWidth={2}
                                strokeDasharray="5 5"
                                name="Производная"
                            />
                        )}
                        {showIntegral && (
                            <ReferenceLine y={0} stroke="#666" />
                        )}
                        <Brush dataKey="x" height={30} stroke="#8884d8" />
                    </LineChart>
                );
        }
    };

    return (
        <Paper sx={{ p: 3, height }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6">
                    {title}
                </Typography>
                <FormControl size="small" sx={{ minWidth: 120 }}>
                    <InputLabel>Тип графика</InputLabel>
                    <Select
                        value={chartType}
                        label="Тип графика"
                        onChange={(e) => setChartType(e.target.value as any)}
                    >
                        <MenuItem value="line">Линейный</MenuItem>
                        <MenuItem value="scatter">Точечный</MenuItem>
                        <MenuItem value="area">Зонный</MenuItem>
                    </Select>
                </FormControl>
            </Box>

            <Box sx={{ height: height - 100 }}>
                <ResponsiveContainer width="100%" height="100%">
                    {renderChart()}
                </ResponsiveContainer>
            </Box>

            <Box sx={{ mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="body2" color="text.secondary">
                    Диапазон: x ∈ [{xMin.toFixed(2)}, {xMax.toFixed(2)}], y ∈ [{yMin.toFixed(2)}, {yMax.toFixed(2)}]
                </Typography>
                <Typography variant="body2" color="text.secondary">
                    Точек: {data.length}
                </Typography>
            </Box>
        </Paper>
    );
};

export default FunctionGraph;