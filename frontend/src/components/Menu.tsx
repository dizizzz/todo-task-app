import { Link, useNavigate } from 'react-router-dom';

function Menu() {
    const navigate = useNavigate();
    const token = localStorage.getItem('token');

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    return (
        <nav className="menu">
            <Link to="/" className="logo">ToDos Tasks</Link>
            <Link to="/">Home</Link>
            {token && <Link to="/tasks">My To-Dos</Link>}
            {token && <Link to="/profile">My Profile</Link>}
            {token && <Link to="/users">List of Users</Link>}
            {token && <button onClick={handleLogout}>Logout</button>}
            {!token && <Link to="/login">Login</Link>}
        </nav>
    );
}

export default Menu;