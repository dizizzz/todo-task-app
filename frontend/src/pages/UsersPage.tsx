import { useEffect, useState } from 'react';
import { getAllUsers, deleteUser } from '../api/users';
import { useNavigate } from 'react-router-dom';
import type { UserInfo } from '../types';
import { getCurrentUserRole } from '../api/auth-helpers';

function UsersPage() {
    const [users, setUsers] = useState<UserInfo[]>([]);
    const isAdmin = getCurrentUserRole() === 'ADMIN';
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const loadUsers = () => {
        getAllUsers()
            .then((page) => setUsers(page.content))
            .catch(() => setError('Failed to load users'));
    };

    useEffect(() => {
        loadUsers();
    }, []);

    const handleRemove = async (id: number) => {
        try {
            await deleteUser(id);
            loadUsers();
        } catch {
            setError('Failed to delete user');
        }
    };

    return (
        <div className="page">
            <h1>List of Users</h1>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <table>
                <thead>
                <tr>
                    <th>No.</th>
                    <th>Full name</th>
                    <th>E-mail</th>
                    {isAdmin && <th>Operations</th>}
                </tr>
                </thead>
                <tbody>
                {users.map((user, index) => (
                    <tr key={user.id}>
                        <td>{index + 1}</td>
                        <td>{user.firstName} {user.lastName}</td>
                        <td>{user.email}</td>
                        {isAdmin && (
                            <td>
                                <button onClick={() => navigate(`/users/${user.id}/tasks`)}>View tasks</button>
                                <button onClick={() => navigate(`/users/${user.id}/edit`)}>Edit</button>
                                <button onClick={() => handleRemove(user.id)}>Remove</button>
                            </td>
                        )}
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default UsersPage;