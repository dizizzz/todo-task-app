import { useEffect, useState } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { getTaskById, updateTask } from '../api/tasks';
import { getCollaborators } from '../api/users';
import type { UserInfo } from '../types';
import { getCurrentUserId } from '../api/auth-helpers';

type Priority = 'LOW' | 'MEDIUM' | 'HIGH';
type State = 'NEW' | 'DOING' | 'DONE';

function UpdateTaskPage() {
    const { id } = useParams();
    const taskId = Number(id);
    const navigate = useNavigate();
    const currentUserId = getCurrentUserId();

    const [name, setName] = useState('');
    const [priority, setPriority] = useState<Priority>('MEDIUM');
    const [state, setState] = useState<State>('NEW');
    const [collaborators, setCollaborators] = useState<UserInfo[]>([]);
    const [owner, setOwner] = useState<UserInfo | null>(null);
    const [allUsers, setAllUsers] = useState<UserInfo[]>([]);
    const [selectedToAdd, setSelectedToAdd] = useState<number | ''>('');
    const [error, setError] = useState('');

    useEffect(() => {
        getTaskById(taskId)
            .then((task) => {
                setName(task.name);
                setPriority(task.priority);
                setState(task.state);
                setOwner(task.owner);
                setCollaborators(task.collaborators);
            })
            .catch(() => setError('Failed to load task'));

        getCollaborators()
            .then((list) => setAllUsers(list))
            .catch(() => setError('Failed to load users'));
    }, [taskId]);

    const availableUsers = allUsers.filter(
        (user) => user.id !== owner?.id && !collaborators.some((c) => c.id === user.id)
    );

    const handleAddCollaborator = () => {
        if (selectedToAdd === '') {
            return;
        }
        const user = allUsers.find((u) => u.id === selectedToAdd);
        if (user) {
            setCollaborators((prev) => [...prev, user]);
        }
        setSelectedToAdd('');
    };

    const handleRemoveCollaborator = (userId: number) => {
        setCollaborators((prev) => prev.filter((c) => c.id !== userId));
    };

    const handleClear = () => {
        setName('');
        setPriority('MEDIUM');
        setState('NEW');
        setCollaborators([]);
    };

    const handleUpdate = async () => {
        if (!name.trim()) {
            return;
        }
        try {
            await updateTask(taskId, {
                name,
                priority,
                state,
                collaboratorIds: collaborators.map((c) => c.id),
            });
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
                    <p className="owner-info">
                        {owner.firstName} {owner.lastName} ({owner.email})
                    </p>
                )}

                <label>Collaborators</label>
                {collaborators.length === 0 ? (
                    <p>No collaborators yet</p>
                ) : (
                    <table>
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Name</th>
                            <th>Operations</th>
                        </tr>
                        </thead>
                        <tbody>
                        {collaborators.map((c, index) => (
                            <tr key={c.id}>
                                <td>{index + 1}</td>
                                <td>{c.firstName} {c.lastName}</td>
                                <td>
                                    {c.id !== currentUserId && (
                                        <button onClick={() => handleRemoveCollaborator(c.id)}>Remove</button>
                                    )}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}

                <label>Add new collaborator</label>
                <div className="add-collaborator">
                    <select
                        value={selectedToAdd}
                        onChange={(e) => setSelectedToAdd(e.target.value === '' ? '' : Number(e.target.value))}
                    >
                        <option value="">Select collaborator...</option>
                        {availableUsers.map((user) => (
                            <option key={user.id} value={user.id}>
                                {user.firstName} {user.lastName} ({user.email})
                            </option>
                        ))}
                    </select>
                    <button className="btn-primary" onClick={handleAddCollaborator}>Add</button>
                </div>
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