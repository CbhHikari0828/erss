import { requestClient } from '#/api/request';

/** 报损报告列表 */
export async function getDamageReportsApi(params?: { status?: string }) {
  return requestClient.get('/admin/damage-reports', { params });
}

/** 审核报损报告 */
export async function reviewDamageReportApi(
  id: number | string,
  data: { compensationAmount?: number; reviewNote?: string },
) {
  return requestClient.post(`/rentals/damage-reports/${id}/review`, data);
}

/** 用户列表 */
export async function getUsersApi(params?: {
  keyword?: string;
  scope?: string;
}) {
  return requestClient.get('/admin/users', { params });
}

/** 封禁用户 */
export async function banUserApi(
  id: number | string,
  data: { banReason?: string },
) {
  return requestClient.post(`/admin/users/${id}/ban`, data);
}

/** 解封用户 */
export async function unbanUserApi(id: number | string) {
  return requestClient.post(`/admin/users/${id}/unban`);
}

/** 财务汇总 */
export async function getFinanceSummaryApi() {
  return requestClient.get('/admin/finance/summary');
}

/** 发放列表 */
export async function getPayoutsApi(params?: { status?: string }) {
  return requestClient.get('/admin/finance/payouts', { params });
}

/** 发放 */
export async function releasePayoutApi(
  id: number | string,
  data: { releaseNote?: string },
) {
  return requestClient.post(`/admin/finance/payouts/${id}/release`, data);
}

/** 系统收入账本 */
export async function getRevenueLedgerApi(params?: {
  category?: string;
  startDate?: string;
  endDate?: string;
}) {
  return requestClient.get('/admin/revenue/ledger', { params });
}

/** 系统收入汇总 */
export async function getRevenueSummaryApi() {
  return requestClient.get('/admin/revenue/summary');
}
