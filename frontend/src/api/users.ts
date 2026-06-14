import client from './client';
import type { UserInfo } from '../types';

export async function getCollaborators(): Promise<UserInfo[]> {
    const response = await client.get<UserInfo[]>('/users/collaborators');
    return response.data;
}
