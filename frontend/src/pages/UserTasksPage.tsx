import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getTasksByUser } from '../api/tasks';
import type { Task } from '../types';

function UserTasksPage() {
    const { id } = useParams();
    const userId = Number(id);

    const [tasks, setTasks] = useState<Task[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        getTasksByUser(userId, page, 5, 'id,asc')
            .then((data) => {
                setTasks(data.content);
                setTotalPages(data.totalPages);
            })
            .catch(() => setError('Failed to load tasks'))
            .finally(() => setLoading(false));
    }, [userId, page]);

    if (loading) {
        return <p>Loading...</p>;
    }

    return (
        <div className="page">
            <h1>User's Tasks</h1>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            {tasks.length === 0 ? (
                <p>This user has no tasks</p>
            ) : (
                <>
                    <table>
                        <thead>
                        <tr>
                            <th>No.</th>
                            <th>Id</th>
                            <th>Name</th>
                            <th>Priority</th>
                            <th>State</th>
                            <th>Owner</th>
                        </tr>
                        </thead>
                        <tbody>
                        {tasks.map((task, index) => (
                            <tr key={task.id}>
                                <td>{page * 5 + index + 1}</td>
                                <td>{task.id}</td>
                                <td>{task.name}</td>
                                <td>{task.priority}</td>
                                <td>{task.state}</td>
                                <td>{task.owner.firstName} {task.owner.lastName}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    <div className="pagination">
                        <button
                            className="btn-secondary"
                            onClick={() => setPage(page - 1)}
                            disabled={page === 0}
                        >
                            Previous
                        </button>
                        <span>Page {page + 1} of {totalPages}</span>
                        <button
                            className="btn-secondary"
                            onClick={() => setPage(page + 1)}
                            disabled={page + 1 >= totalPages}
                        >
                            Next
                        </button>
                    </div>
                </>
            )}

            <p>
                <Link to="/users">Go to User List</Link>
            </p>
        </div>
    );
}

export default UserTasksPage;