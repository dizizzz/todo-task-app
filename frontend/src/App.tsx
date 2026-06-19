import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Menu from './components/Menu';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import TasksPage from './pages/TasksPage';
import ProtectedRoute from './components/ProtectedRoute';
import RegisterPage from './pages/RegisterPage';
import UsersPage from './pages/UsersPage';
import CreateTaskPage from './pages/CreateTaskPage';
import UpdateTaskPage from './pages/UpdateTaskPage';
import UpdateUserPage from './pages/UpdateUserPage';
import ProfilePage from './pages/ProfilePage';
import UserTasksPage from './pages/UserTasksPage';

function App() {
    return (
        <BrowserRouter>
            <Menu />
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route
                    path="/tasks"
                    element={
                        <ProtectedRoute>
                            <TasksPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/tasks/new"
                    element={
                        <ProtectedRoute>
                            <CreateTaskPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/tasks/:id/edit"
                    element={
                        <ProtectedRoute>
                            <UpdateTaskPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/users"
                    element={
                        <ProtectedRoute>
                            <UsersPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/users/:id/edit"
                    element={
                        <ProtectedRoute>
                            <UpdateUserPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/users/:id/tasks"
                    element={
                        <ProtectedRoute>
                            <UserTasksPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/profile"
                    element={
                        <ProtectedRoute>
                            <ProfilePage />
                        </ProtectedRoute>
                    }
                />
            </Routes>
        </BrowserRouter>
    );
}

export default App;