import { Link, useNavigate } from 'react-router-dom';

function Menu() {
    const navigate = useNavigate();
    const token = localStorage.getItem('token');

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    return (
        <nav style={{ padding: '10px', borderBottom: '1px solid #ccc', marginBottom: '20px' }}>
            <Link to="/" style={{ marginRight: '15px', fontWeight: 'bold' }}>ToDos Tasks</Link>
            <Link to="/" style={{ marginRight: '15px' }}>Home</Link>
            {token && <Link to="/tasks" style={{ marginRight: '15px' }}>My To-Dos</Link>}
            {token && <Link to="/users" style={{ marginRight: '15px' }}>List of Users</Link>}
            {token && <button onClick={handleLogout}>Logout</button>}
            {!token && <Link to="/login">Login</Link>}
        </nav>
    );
}

export default Menu;