import { useEffect, useState } from 'react';
import { getUserById, updateSelf } from '../api/users';
import { getCurrentUserId } from '../api/auth-helpers';

function ProfilePage() {
    const userId = getCurrentUserId();

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        if (!userId) {
            return;
        }
        getUserById(userId)
            .then((user) => {
                setFirstName(user.firstName);
                setLastName(user.lastName);
                setEmail(user.email);
            })
            .catch(() => setError('Failed to load profile'));
    }, [userId]);

    const handleSave = async () => {
        setError('');
        setSuccess('');
        if (!firstName.trim() || !lastName.trim() || !email.trim()) {
            setError('Name and email are required');
            return;
        }
        try {
            await updateSelf({
                firstName,
                lastName,
                email,
                password: password.trim() ? password : undefined,
            });
            setSuccess('Profile updated');
            setPassword('');
        } catch {
            setError('Failed to update profile');
        }
    };

    return (
        <div className="form-card">
            <h1>My Profile</h1>
            <div className="form-group">
                <label>First name</label>
                <input type="text" value={firstName} onChange={(e) => setFirstName(e.target.value)} />
            </div>
            <div className="form-group">
                <label>Last name</label>
                <input type="text" value={lastName} onChange={(e) => setLastName(e.target.value)} />
            </div>
            <div className="form-group">
                <label>Email</label>
                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
            </div>
            <div className="form-group">
                <label>Password</label>
                <input
                    type="password"
                    placeholder="Leave empty to keep current password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </div>
            <button className="btn-primary" onClick={handleSave}>Save</button>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {success && <p style={{ color: 'green' }}>{success}</p>}
        </div>
    );
}

export default ProfilePage;