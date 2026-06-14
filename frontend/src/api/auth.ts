import client from './client';
import type { LoginRequest, LoginResponse, UserRequest, UserInfo } from '../types';

export async function login(data: LoginRequest): Promise<LoginResponse> {
    const response = await client.post<LoginResponse>('/auth/login', data);
    return response.data;
}

export async function register(data: UserRequest): Promise<UserInfo> {
    const response = await client.post<UserInfo>('/users', data);
    return response.data;
}