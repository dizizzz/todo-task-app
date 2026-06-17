// Дістає userId з JWT-токена в localStorage
export function getCurrentUserId(): number | null {
    const token = localStorage.getItem('token');
    if (!token) {
        return null;
    }
    try {
        const payload = token.split('.')[1]; // середня частина JWT
        const decoded = JSON.parse(atob(payload));
        return decoded.userId ?? null;
    } catch {
        return null;
    }
}