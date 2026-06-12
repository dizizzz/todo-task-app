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
