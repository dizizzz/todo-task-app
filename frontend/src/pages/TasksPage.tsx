import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getTasks, createTask } from '../api/tasks';
import type { Task } from '../types';

function TasksPage() {
    const [tasks, setTasks] = useState<Task[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const [name, setName] = useState('');
    const [priority, setPriority] = useState<'LOW' | 'MEDIUM' | 'HIGH'>('MEDIUM');
    const [state, setState] = useState<'NEW' | 'DOING' | 'DONE'>('NEW');

    const loadTasks = () => {
        getTasks()
            .then((page) => setTasks(page.content))
            .catch(() => setError('Failed to load tasks'))
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        loadTasks();
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    const handleCreate = async () => {
        if (!name.trim()) {
            return;
        }
        try {
            await createTask({ name, priority, state, collaboratorIds: [] });
            setName('');
            setPriority('MEDIUM');
            setState('NEW');
            loadTasks();
        } catch {
            setError('Failed to create task');
        }
    };

    if (loading) {
        return <p>loading...</p>;
    }

    return (
        <div>
            <h1>My tasks</h1>
            <button onClick={handleLogout}>Logout</button>

            <h2>New task</h2>
            <div>
                <input
                    type="text"
                    placeholder="Name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />
            </div>
            <div>
                <select value={priority} onChange={(e) => setPriority(e.target.value as 'LOW' | 'MEDIUM' | 'HIGH')}>
                    <option value="LOW">LOW</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HIGH">HIGH</option>
                </select>
            </div>
            <div>
                <select value={state} onChange={(e) => setState(e.target.value as 'NEW' | 'DOING' | 'DONE')}>
                    <option value="NEW">NEW</option>
                    <option value="DOING">DOING</option>
                    <option value="DONE">DONE</option>
                </select>
            </div>
            <button onClick={handleCreate}>Create</button>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            <h2>List</h2>
            {tasks.length === 0 ? (
                <p>No tasks yet</p>
            ) : (
                <ul>
                    {tasks.map((task) => (
                        <li key={task.id}>
                            <strong>{task.name}</strong> — {task.priority} / {task.state}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default TasksPage;