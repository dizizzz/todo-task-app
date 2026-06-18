import { useEffect, useState } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { getTaskById, updateTask } from '../api/tasks';
import { getCollaborators } from '../api/users';
import type { UserInfo } from '../types';

type Priority = 'LOW' | 'MEDIUM' | 'HIGH';
type State = 'NEW' | 'DOING' | 'DONE';

function UpdateTaskPage() {
    const { id } = useParams();
    const taskId = Number(id);
    const navigate = useNavigate();

    const [name, setName] = useState('');
    const [priority, setPriority] = useState<Priority>('MEDIUM');
    const [state, setState] = useState<State>('NEW');
    const [collaboratorIds, setCollaboratorIds] = useState<number[]>([]);
    const [users, setUsers] = useState<UserInfo[]>([]);
    const [error, setError] = useState('');
    const [owner, setOwner] = useState<UserInfo | null>(null);

    // завантажуємо задачу і список користувачів
    useEffect(() => {
        getTaskById(taskId)
            .then((task) => {
                setName(task.name);
                setPriority(task.priority);
                setState(task.state);
                setCollaboratorIds(task.collaborators.map((c) => c.id));
                setOwner(task.owner);
            })
            .catch(() => setError('Failed to load task'));

        getCollaborators()
            .then((list) => setUsers(list))
            .catch(() => setError('Failed to load users'));
    }, [taskId]);

    const toggleCollaborator = (userId: number) => {
        setCollaboratorIds((prev) =>
            prev.includes(userId) ? prev.filter((x) => x !== userId) : [...prev, userId]
        );
    };

    const handleClear = () => {
        setName('');
        setPriority('MEDIUM');
        setState('NEW');
        setCollaboratorIds([]);
    };

    const handleUpdate = async () => {
        if (!name.trim()) {
            return;
        }
        try {
            await updateTask(taskId, { name, priority, state, collaboratorIds });
            navigate('/tasks');
        } catch {
            setError('Failed to update task');
        }
    };

    return (
        <div className="form-card">
            <h1>Update existing Task</h1>
            <div className="form-group">
                <label>Id</label>
                <input type="text" value={taskId} disabled />
            </div>
            <div className="form-group">
                <label>Name</label>
                <input type="text" value={name} onChange={(e) => setName(e.target.value)} />
            </div>
            <div className="form-group">
                <label>Priority</label>
                <select value={priority} onChange={(e) => setPriority(e.target.value as Priority)}>
                    <option value="LOW">LOW</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HIGH">HIGH</option>
                </select>
            </div>
            <div className="form-group">
                <label>State</label>
                <select value={state} onChange={(e) => setState(e.target.value as State)}>
                    <option value="NEW">NEW</option>
                    <option value="DOING">DOING</option>
                    <option value="DONE">DONE</option>
                </select>
            </div>
            <div className="form-group">
                <label>Owner</label>
                {owner && (
                    <p style={{ margin: '0 0 12px' }}>
                        {owner.firstName} {owner.lastName} ({owner.email})
                    </p>
                )}
                <label>Collaborators</label>
                {users.filter((user) => user.id !== owner?.id).map((user) => (
                    <label key={user.id} style={{ display: 'block', fontWeight: 'normal' }}>
                        <input
                            type="checkbox"
                            checked={collaboratorIds.includes(user.id)}
                            onChange={() => toggleCollaborator(user.id)}
                            style={{ width: 'auto', marginRight: '8px' }}
                        />
                        {user.firstName} {user.lastName} ({user.email})
                    </label>
                ))}
            </div>
            <button className="btn-secondary" onClick={handleClear}>Clear</button>
            <button className="btn-primary" onClick={handleUpdate}>Update</button>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <p>
                <Link to="/tasks">Go to Task List</Link>
            </p>
        </div>
    );
}

export default UpdateTaskPage;