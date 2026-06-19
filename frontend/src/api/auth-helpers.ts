export function getCurrentUserId(): number | null {
    const token = localStorage.getItem('token');
    if (!token) {
        return null;
    }
    try {
        const payload = token.split('.')[1];
        const decoded = JSON.parse(atob(payload));
        return decoded.userId ?? null;
    } catch {
        return null;
    }
}

export function getCurrentUserRole(): string | null {
    const token = localStorage.getItem('token');
    if (!token) {
        return null;
    }
    try {
        const payload = token.split('.')[1];
        const decoded = JSON.parse(atob(payload));
        return decoded.role ?? null;
    } catch {
        return null;
    }
}