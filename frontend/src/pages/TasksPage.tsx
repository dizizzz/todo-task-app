import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getTasks, createTask, deleteTask, updateTask } from '../api/tasks';
import type { Task, UserInfo } from '../types';
import { getCollaborators } from '../api/users';

type Priority = 'LOW' | 'MEDIUM' | 'HIGH';
type State = 'NEW' | 'DOING' | 'DONE';

function TasksPage() {
    const [tasks, setTasks] = useState<Task[]>([]);
    const [users, setUsers] = useState<UserInfo[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    // create form
    const [name, setName] = useState('');
    const [priority, setPriority] = useState<Priority>('MEDIUM');
    const [state, setState] = useState<State>('NEW');
    const [collaboratorIds, setCollaboratorIds] = useState<number[]>([]);

    // edit state
    const [editingId, setEditingId] = useState<number | null>(null);
    const [editName, setEditName] = useState('');
    const [editPriority, setEditPriority] = useState<Priority>('MEDIUM');
    const [editState, setEditState] = useState<State>('NEW');
    const [editCollaboratorIds, setEditCollaboratorIds] = useState<number[]>([]);

    const loadTasks = () => {
        getTasks()
            .then((page) => setTasks(page.content))
            .catch(() => setError('Failed to load tasks'))
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        loadTasks();
        getCollaborators()
            .then((list) => setUsers(list))
            .catch(() => setError('Failed to load users'));
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    const toggleCollaborator = (id: number) => {
        setCollaboratorIds((prev) =>
            prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
        );
    };

    const toggleEditCollaborator = (id: number) => {
        setEditCollaboratorIds((prev) =>
            prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
        );
    };

    const handleCreate = async () => {
        if (!name.trim()) {
            return;
        }
        try {
            await createTask({ name, priority, state, collaboratorIds });
            setName('');
            setPriority('MEDIUM');
            setState('NEW');
            setCollaboratorIds([]);
            loadTasks();
        } catch {
            setError('Failed to create task');
        }
    };

    const handleDelete = async (id: number) => {
        try {
            await deleteTask(id);
            loadTasks();
        } catch {
            setError('Failed to delete task');
        }
    };

    const startEdit = (task: Task) => {
        setEditingId(task.id);
        setEditName(task.name);
        setEditPriority(task.priority);
        setEditState(task.state);
        setEditCollaboratorIds(task.collaborators.map((c) => c.id));
    };

    const cancelEdit = () => {
        setEditingId(null);
    };

    const handleUpdate = async (id: number) => {
        if (!editName.trim()) {
            return;
        }
        try {
            await updateTask(id, {
                name: editName,
                priority: editPriority,
                state: editState,
                collaboratorIds: editCollaboratorIds,
            });
            setEditingId(null);
            loadTasks();
        } catch {
            setError('Failed to update task');
        }
    };

    if (loading) {
        return <p>Loading...</p>;
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
                <select value={priority} onChange={(e) => setPriority(e.target.value as Priority)}>
                    <option value="LOW">LOW</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HIGH">HIGH</option>
                </select>
            </div>
            <div>
                <select value={state} onChange={(e) => setState(e.target.value as State)}>
                    <option value="NEW">NEW</option>
                    <option value="DOING">DOING</option>
                    <option value="DONE">DONE</option>
                </select>
            </div>
            <div>
                <p>Collaborators:</p>
                {users.map((user) => (
                    <label key={user.id} style={{ display: 'block' }}>
                        <input
                            type="checkbox"
                            checked={collaboratorIds.includes(user.id)}
                            onChange={() => toggleCollaborator(user.id)}
                        />
                        {user.firstName} {user.lastName} ({user.email})
                    </label>
                ))}
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
                            {editingId === task.id ? (
                                <>
                                    <input
                                        type="text"
                                        value={editName}
                                        onChange={(e) => setEditName(e.target.value)}
                                    />
                                    <select value={editPriority} onChange={(e) => setEditPriority(e.target.value as Priority)}>
                                        <option value="LOW">LOW</option>
                                        <option value="MEDIUM">MEDIUM</option>
                                        <option value="HIGH">HIGH</option>
                                    </select>
                                    <select value={editState} onChange={(e) => setEditState(e.target.value as State)}>
                                        <option value="NEW">NEW</option>
                                        <option value="DOING">DOING</option>
                                        <option value="DONE">DONE</option>
                                    </select>
                                    <div>
                                        <p>Collaborators:</p>
                                        {users.map((user) => (
                                            <label key={user.id} style={{ display: 'block' }}>
                                                <input
                                                    type="checkbox"
                                                    checked={editCollaboratorIds.includes(user.id)}
                                                    onChange={() => toggleEditCollaborator(user.id)}
                                                />
                                                {user.firstName} {user.lastName} ({user.email})
                                            </label>
                                        ))}
                                    </div>
                                    <button onClick={() => handleUpdate(task.id)}>Save</button>
                                    <button onClick={cancelEdit}>Cancel</button>
                                </>
                            ) : (
                                <>
                                    <strong>{task.name}</strong> — {task.priority} / {task.state}
                                    {task.collaborators.length > 0 && (
                                        <span> | shared with: {task.collaborators.map((c) => c.firstName).join(', ')}</span>
                                    )}
                                    {' '}
                                    <button onClick={() => startEdit(task)}>Edit</button>
                                    <button onClick={() => handleDelete(task.id)}>Delete</button>
                                </>
                            )}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default TasksPage;