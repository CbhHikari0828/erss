import type { UserInfo } from '@vben/types';

import { baseRequestClient, requestClient } from '#/api/request';

export namespace AuthApi {
  export interface LoginParams {
    password?: string;
    studentId?: string;
  }

  export interface LoginResult {
    token: string;
  }
}

/**
 * 登录 - 使用 baseRequestClient 获取原始响应（token 在顶层）
 */
export async function loginApi(data: AuthApi.LoginParams) {
  return baseRequestClient.post<AuthApi.LoginResult>('/login', data);
}

/**
 * 退出登录
 */
export async function logoutApi() {
  return baseRequestClient.post('/logout');
}

/**
 * 获取当前用户信息
 */
export async function getUserInfoApi() {
  return requestClient.get<UserInfo>('/getInfo');
}
