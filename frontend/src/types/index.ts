// Запит на логін
export interface LoginRequest {
    email: string;
    password: string;
}

// Відповідь логіну (токен)
export interface LoginResponse {
    token: string;
}

// Користувач (коротка інфо, як UserInfo на backend)
export interface UserInfo {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
}

// Задача (як приходить з backend)
export interface Task {
    id: number;
    name: string;
    priority: 'LOW' | 'MEDIUM' | 'HIGH';
    state: 'NEW' | 'DOING' | 'DONE';
    owner: UserInfo;
    collaborators: UserInfo[];
}

// Запит на створення задачі
export interface TaskRequest {
    name: string;
    priority: 'LOW' | 'MEDIUM' | 'HIGH';
    state: 'NEW' | 'DOING' | 'DONE';
    collaboratorIds: number[];
}

// Запит на реєстрацію
export interface UserRequest {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
}