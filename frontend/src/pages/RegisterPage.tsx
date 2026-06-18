import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { register, login } from '../api/auth';

function RegisterPage() {
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async () => {
        setError('');
        if (password !== confirmPassword) {
            setError('Passwords do not match');
            return;
        }
        try {
            await register({ firstName, lastName, email, password });
            const response = await login({ email, password });
            localStorage.setItem('token', response.token);
            navigate('/tasks');
        } catch {
            setError('Registration failed');
        }
    };

    return (
        <div className="form-card">
            <h1>Register</h1>
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
                    type={showPassword ? 'text' : 'password'}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </div>
            <div className="form-group">
                <label>Confirm password</label>
                <input
                    type={showPassword ? 'text' : 'password'}
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                />
            </div>
            <div className="form-group">
                <label style={{ fontWeight: 'normal', cursor: 'pointer' }}>
                    <input
                        type="checkbox"
                        checked={showPassword}
                        onChange={() => setShowPassword(!showPassword)}
                        style={{ width: 'auto', marginRight: '8px' }}
                    />
                    Show password
                </label>
            </div>
            <button onClick={handleSubmit}>Register</button>
            {error && <p style={{ color: 'red' }}>{error}</p>}
        </div>
    );
}

export default RegisterPage;