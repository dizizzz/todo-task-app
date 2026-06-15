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
    const [collaboratorIds, setCollaboratorIds] = useState<number[]>([]);
    const [users, setUsers] = useState<UserInfo[]>([]);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        getCollaborators()
            .then((list) => setUsers(list))
            .catch(() => setError('Failed to load users'));
    }, []);

    const toggleCollaborator = (id: number) => {
        setCollaboratorIds((prev) =>
            prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
        );
    };

    const handleClear = () => {
        setName('');
        setPriority('MEDIUM');
        setState('NEW');
        setCollaboratorIds([]);
    };

    const handleCreate = async () => {
        if (!name.trim()) {
            return;
        }
        try {
            await createTask({ name, priority, state, collaboratorIds });
            navigate('/tasks');
        } catch {
            setError('Failed to create task');
        }
    };

    return (
        <div>
            <h1>Create new Task</h1>

            <div>
                <label>Name</label>
                <input type="text" value={name} onChange={(e) => setName(e.target.value)} />
            </div>

            <div>
                <label>Priority</label>
                <select value={priority} onChange={(e) => setPriority(e.target.value as Priority)}>
                    <option value="LOW">LOW</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HIGH">HIGH</option>
                </select>
            </div>

            <div>
                <label>State</label>
                <select value={state} onChange={(e) => setState(e.target.value as State)}>
                    <option value="NEW">NEW</option>
                    <option value="DOING">DOING</option>
                    <option value="DONE">DONE</option>
                </select>
            </div>

            <div>
                <p>Collaborators</p>
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

            <button onClick={handleClear}>Clear</button>
            <button onClick={handleCreate}>Create</button>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            <p>
                <Link to="/tasks">Go to Task List</Link>
            </p>
        </div>
    );
}

export default CreateTaskPage;