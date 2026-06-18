import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { login } from '../api/auth';

function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async () => {
        setError('');
        try {
            const response = await login({ email, password });
            localStorage.setItem('token', response.token);
            navigate('/tasks');
        } catch {
            setError('Incorrect email or password');
        }
    };

    return (
        <div className="form-card">
            <h1>Login</h1>
            <div className="form-group">
                <label>Email</label>
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
            </div>
            <div className="form-group">
                <label>Password</label>
                <input
                    type={showPassword ? 'text' : 'password'}
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
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
            <button onClick={handleSubmit}>Login</button>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <p>
                No account? <Link to="/register">Register</Link>
            </p>
        </div>
    );
}

export default LoginPage;