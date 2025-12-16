import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';

// Pages
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import FunctionsPage from './pages/FunctionsPage';
import OperationsPage from './pages/OperationsPage';
import SettingsPage from './pages/SettingsPage';

// Components
import Layout from './components/layout/Layout';
import PrivateRoute from './components/common/PrivateRoute';
import ErrorBoundary from './components/common/ErrorBoundary';

// Nested routes components
import FunctionDetails from './components/functions/FunctionDetails';

const AppRoutes: React.FC = () => {
    return (
        <ErrorBoundary>
            <Routes>
                {/* Public routes */}
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />

                {/* Protected routes */}
                <Route element={<PrivateRoute><Layout /></PrivateRoute>}>
                    <Route path="/" element={<Navigate to="/dashboard" replace />} />
                    <Route path="/dashboard" element={<Dashboard />} />

                    {/* Functions routes */}
                    <Route path="/functions" element={<FunctionsPage />} />
                    <Route path="/functions/:id" element={<FunctionDetails />} />

                    {/* Operations routes */}
                    <Route path="/operations" element={<OperationsPage />} />
                    <Route path="/operations/differentiation" element={<OperationsPage />} />
                    <Route path="/operations/integration" element={<OperationsPage />} />
                    <Route path="/operations/binary" element={<OperationsPage />} />

                    {/* Settings routes */}
                    <Route path="/settings" element={<SettingsPage />} />

                    {/* Fallback route */}
                    <Route path="*" element={<Navigate to="/dashboard" replace />} />
                </Route>
            </Routes>
        </ErrorBoundary>
    );
};

export default AppRoutes;