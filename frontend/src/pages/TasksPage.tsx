import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getTasks, deleteTask} from '../api/tasks';
import type { Task } from '../types';
import { getCurrentUserId } from '../api/auth-helpers';

function TasksPage() {
    const [tasks, setTasks] = useState<Task[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const currentUserId = getCurrentUserId();

    const loadTasks = () => {
        getTasks()
            .then((page) => setTasks(page.content))
            .catch(() => setError('Failed to load tasks'))
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        loadTasks();
    }, []);

    const handleDelete = async (id: number) => {
        try {
            await deleteTask(id);
            loadTasks();
        } catch {
            setError('Failed to delete task');
        }
    };

    if (loading) {
        return <p>Loading...</p>;
    }

    return (
        <div className="page">
            <h1>All Tasks</h1>
            <button onClick={() => navigate('/tasks/new')}>Create New Task</button>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            {tasks.length === 0 ? (
                <p>No tasks yet</p>
            ) : (
                <table>
                    <thead>
                    <tr>
                        <th>No.</th>
                        <th>Id</th>
                        <th>Name</th>
                        <th>Priority</th>
                        <th>State</th>
                        <th>Operations</th>
                    </tr>
                    </thead>
                    <tbody>
                    {tasks.map((task, index) => (
                        <tr key={task.id}>
                            <td>{index + 1}</td>
                            <td>{task.id}</td>
                            <td>{task.name}</td>
                            <td>{task.priority}</td>
                            <td>{task.state}</td>
                            <td>
                                <button onClick={() => navigate(`/tasks/${task.id}/edit`)}>Edit</button>
                                {task.owner.id === currentUserId && (
                                    <button onClick={() => handleDelete(task.id)}>Remove</button>
                                )}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default TasksPage;