import { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { createTask } from '../api/tasks';
import { getCollaborators } from '../api/users';
import type { UserInfo } from '../types';

type Priority = 'LOW' | 'MEDIUM' | 'HIGH';
type State = 'NEW' | 'DOING' | 'DONE';

function CreateTaskPage() {
    const [name, setName] = useState('');
    const [priority, setPriority] = useState<Priority>('MEDIUM');
    const [state, setState] = useState<State>('NEW');
    const [collaborators, setCollaborators] = useState<UserInfo[]>([]);
    const [allUsers, setAllUsers] = useState<UserInfo[]>([]);
    const [selectedToAdd, setSelectedToAdd] = useState<number | ''>('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        getCollaborators()
            .then((list) => setAllUsers(list))
            .catch(() => setError('Failed to load users'));
    }, []);

    const availableUsers = allUsers.filter(
        (user) => !collaborators.some((c) => c.id === user.id)
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

    const handleCreate = async () => {
        if (!name.trim()) {
            return;
        }
        try {
            await createTask({
                name,
                priority,
                state,
                collaboratorIds: collaborators.map((c) => c.id),
            });
            navigate('/tasks');
        } catch {
            setError('Failed to create task');
        }
    };

    return (
        <div className="form-card">
            <h1>Create new Task</h1>
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
                                    <button onClick={() => handleRemoveCollaborator(c.id)}>Remove</button>
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
            <button className="btn-primary" onClick={handleCreate}>Create</button>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <p>
                <Link to="/tasks">Go to Task List</Link>
            </p>
        </div>
    );
}

export default CreateTaskPage;