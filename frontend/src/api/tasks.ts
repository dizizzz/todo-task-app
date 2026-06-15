import client from './client';
import type { Task, TaskRequest } from '../types';

interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
}

export async function getTasks(): Promise<Page<Task>> {
    const response = await client.get<Page<Task>>('/tasks');
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