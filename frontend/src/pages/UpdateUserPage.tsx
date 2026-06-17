import { useEffect, useState } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { getUserById, updateUserByAdmin } from '../api/users';

type Role = 'USER' | 'ADMIN';

function UpdateUserPage() {
    const { id } = useParams();
    const userId = Number(id);
    const navigate = useNavigate();

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [role, setRole] = useState<Role>('USER');
    const [error, setError] = useState('');

    useEffect(() => {
        getUserById(userId)
            .then((user) => {
                setFirstName(user.firstName);
                setLastName(user.lastName);
                setEmail(user.email);
                setRole(user.role);
            })
            .catch(() => setError('Failed to load user'));
    }, [userId]);

    const handleUpdate = async () => {
        try {
            await updateUserByAdmin(userId, { role });
            navigate('/users');
        } catch {
            setError('Failed to update user');
        }
    };

    return (
        <div className="form-card">
            <h1>Update existing User</h1>
            <div className="form-group">
                <label>Id</label>
                <input type="text" value={userId} disabled />
            </div>
            <div className="form-group">
                <label>First name</label>
                <input type="text" value={firstName} disabled />
            </div>
            <div className="form-group">
                <label>Last name</label>
                <input type="text" value={lastName} disabled />
            </div>
            <div className="form-group">
                <label>Email</label>
                <input type="email" value={email} disabled />
            </div>
            <div className="form-group">
                <label>Role</label>
                <select value={role} onChange={(e) => setRole(e.target.value as Role)}>
                    <option value="USER">User</option>
                    <option value="ADMIN">Admin</option>
                </select>
            </div>
            <button className="btn-primary" onClick={handleUpdate}>Update</button>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <p>
                <Link to="/users">Go to User List</Link>
            </p>
        </div>
    );
}

export default UpdateUserPage;