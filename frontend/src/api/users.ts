import client from './client';
import type { UserInfo, User, UserAdminUpdateRequest, SelfUpdateRequest } from '../types';

export async function getCollaborators(): Promise<UserInfo[]> {
    const response = await client.get<UserInfo[]>('/users/collaborators');
    return response.data;
}

export async function getAllUsers() {
    const response = await client.get('/users');
    return response.data;
}

export async function deleteUser(id: number) {
    await client.delete(`/users/${id}`);
}

export async function getUserById(id: number): Promise<User> {
    const response = await client.get<User>(`/users/${id}`);
    return response.data;
}

export async function updateUserByAdmin(id: number, data: UserAdminUpdateRequest): Promise<User> {
    const response = await client.put<User>(`/users/${id}`, data);
    return response.data;
}

export async function updateSelf(data: SelfUpdateRequest): Promise<User> {
    const response = await client.put<User>('/users/me', data);
    return response.data;
}