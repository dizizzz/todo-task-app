import client from './client';
import type { Task, TaskRequest } from '../types';

interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
}

export async function getTasks(
    page = 0,
    size = 5,
    sort = 'id,asc'
): Promise<Page<Task>> {
    const response = await client.get<Page<Task>>('/tasks', {
        params: { page, size, sort },
    });
    return response.data;
}

export async function createTask(data: TaskRequest): Promise<Task> {
    const response = await client.post<Task>('/tasks', data);
    return response.data;
}

export async function deleteTask(id: number): Promise<void> {
    await client.delete(`/tasks/${id}`);
}

export async function updateTask(id: number, data: TaskRequest): Promise<Task> {
    const response = await client.put<Task>(`/tasks/${id}`, data);
    return response.data;
}

export async function getTaskById(id: number): Promise<Task> {
    const response = await client.get<Task>(`/tasks/${id}`);
    return response.data;
}

export async function getTasksByUser(
    userId: number,
    page = 0,
    size = 5,
    sort = 'id,asc'
): Promise<Page<Task>> {
    const response = await client.get<Page<Task>>(`/tasks/user/${userId}`, {
        params: { page, size, sort },
    });
    return response.data;
}