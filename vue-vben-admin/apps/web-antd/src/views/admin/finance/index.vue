<script lang="ts" setup>
import type { Recordable } from '@vben/types';

import { computed, onMounted, ref, watch } from 'vue';

import { message } from 'ant-design-vue';

import { getFinanceSummaryApi, getPayoutsApi, releasePayoutApi } from '#/api';

defineOptions({ name: 'Finance' });
const summary = ref<null | Recordable<any>>(null);
const payouts = ref<Recordable<any>[]>([]);
const selectedId = ref<null | number>(null);
const filter = ref('all');
const loading = ref(false);
const busy = ref(false);
const error = ref('');
const releaseNote = ref('');

const selectedPayout = computed(
  () => payouts.value.find((item) => item.id === selectedId.value) || null,
);

const stats = computed(() => [
  {
    label: '系统托管余额',
    value: formatMoney(summary.value?.systemEscrowBalance),
  },
  {
    label: '托管中笔数',
    value: String(summary.value?.holdingPayoutCount ?? 0),
  },
  {
    label: '托管中金额',
    value: formatMoney(summary.value?.holdingPayoutAmount),
  },
  {
    label: '待发放笔数',
    value: String(summary.value?.pendingPayoutCount ?? 0),
  },
  {
    label: '待发放总额',
    value: formatMoney(summary.value?.pendingPayoutAmount),
  },
  {
    label: '已发放总额',
    value: formatMoney(summary.value?.releasedPayoutAmount),
  },
]);

const filters = [
  { key: 'all', label: '全部' },
  { key: 'ESCROW_HOLDING', label: '托管中' },
  { key: 'PENDING', label: '待发放' },
  { key: 'PAID', label: '已发放' },
];

function statusLabel(status: string) {
  if (status === 'ESCROW_HOLDING') return '托管中';
  if (status === 'PENDING') return '待发放';
  if (status === 'PAID') return '已发放';
  return status || '-';
}

function statusColor(status: string) {
  if (status === 'ESCROW_HOLDING') return 'blue';
  if (status === 'PENDING') return 'orange';
  if (status === 'PAID') return 'green';
  return 'default';
}

function payLabel(key: string) {
  const map: Record<string, string> = {
    wechat: '微信支付',
    alipay: '支付宝支付',
    campus: '校园卡支付',
    wallet: '系统钱包',
  };
  return map[key] || key;
}

async function refreshData() {
  loading.value = true;
  error.value = '';
  try {
    const [summaryData, payoutsData] = await Promise.all([
      getFinanceSummaryApi(),
      getPayoutsApi(
        filter.value === 'all' ? undefined : { status: filter.value },
      ),
    ]);
    summary.value = summaryData || null;
    const nextPayouts = Array.isArray(payoutsData) ? payoutsData : [];
    payouts.value = nextPayouts;
    if (
      selectedId.value &&
      !nextPayouts.some((i) => i.id === selectedId.value)
    ) {
      selectedId.value = nextPayouts[0]?.id || null;
    } else if (!selectedId.value) {
      selectedId.value = nextPayouts[0]?.id || null;
    }
    if (selectedPayout.value) {
      releaseNote.value = selectedPayout.value.releaseNote || '';
    }
  } catch (error_: any) {
    const msg = error_?.message || '获取资金数据失败';
    error.value = msg;
  } finally {
    loading.value = false;
  }
}

async function handleRelease() {
  if (!selectedPayout.value) return;

  busy.value = true;
  error.value = '';
  try {
    await releasePayoutApi(selectedPayout.value.id, {
      releaseNote: releaseNote.value.trim() || '平台已结清，确认发放到卖家钱包',
    });
    message.success('发放成功');
    await refreshData();
  } catch (error_: any) {
    error.value = error_?.message || '发放失败';
  } finally {
    busy.value = false;
  }
}

function selectPayout(id: number) {
  selectedId.value = id;
  const p = payouts.value.find((i) => i.id === id);
  if (p) {
    releaseNote.value = p.releaseNote || '平台已结清，确认发放到卖家钱包';
  }
}

function formatMoney(value: any) {
  if (value === null || value === undefined || value === '') return '-';
  const n = Number(value);
  if (!Number.isFinite(n)) return '-';
  return `${n.toFixed(2)} 元`;
}

function formatDate(value: any) {
  if (!value) return '-';
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return '-';
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

watch(filter, () => {
  refreshData();
});

watch(selectedPayout, (p) => {
  if (p) {
    releaseNote.value = p.releaseNote || '平台已结清，确认发放到卖家钱包';
  }
});

onMounted(() => {
  refreshData();
});
</script>

<template>
  <div class="admin-page">
    <header class="admin-header">
      <div>
        <p class="admin-eyebrow">管理员功能</p>
        <h1>资金管理</h1>
      </div>
      <a-button :disabled="loading || busy" @click="refreshData">
        {{ loading ? '加载中...' : '刷新' }}
      </a-button>
    </header>

    <div v-if="error" class="admin-banner admin-banner--error">
      ⚠ {{ error }}
    </div>

    <section class="admin-stats-grid admin-stats-grid--six">
      <a-card
        v-for="s in stats"
        :key="s.label"
        size="small"
        class="admin-stat-card"
      >
        <p class="admin-stat-label">{{ s.label }}</p>
        <p class="admin-stat-value">{{ s.value }}</p>
      </a-card>
    </section>

    <div class="admin-filter-row">
      <a-button
        v-for="item in filters"
        :key="item.key"
        :type="item.key === filter ? 'primary' : 'default'"
        size="small"
        @click="filter = item.key"
      >
        {{ item.label }}
      </a-button>
    </div>

    <section class="admin-content-grid">
      <a-card :bordered="false" class="admin-list-panel" title="资金流水">
        <template #extra>
          <span class="admin-count">{{ payouts.length }} 条</span>
        </template>
        <div v-if="payouts.length > 0" class="report-list">
          <button
            v-for="item in payouts"
            :key="item.id"
            type="button"
            class="report-card"
            :class="{ active: item.id === selectedId }"
            @click="selectPayout(item.id)"
          >
            <div class="row">
              <a-tag :color="statusColor(item.status)">
                {{ statusLabel(item.status) }}
              </a-tag>
              <a-tag>{{ payLabel(item.paymentMethod) }}</a-tag>
            </div>
            <h4>{{ item.bookTitle || '未知书籍' }}</h4>
            <p>
              {{ item.orderNo || '-' }} · 卖家: {{ item.sellerName || '-' }} 丨
              买家: {{ item.buyerName || '-' }}
            </p>
          </button>
        </div>
        <a-empty v-else description="暂无资金记录" />
      </a-card>

      <a-card :bordered="false" class="admin-detail-panel" title="发放详情">
        <template #extra>
          <span>{{
            selectedPayout ? selectedPayout.orderNo : '未选择记录'
          }}</span>
        </template>

        <div
          v-if="!selectedPayout"
          style=" padding: 12px 0;color: hsl(var(--muted-foreground))"
        >
          请从左侧列表选择一条记录
        </div>

        <div v-if="selectedPayout" class="detail-stack">
          <section>
            <p class="admin-eyebrow">发放单 #{{ selectedPayout.id }}</p>
            <h4>{{ selectedPayout.bookTitle || '未知书籍' }}</h4>
          </section>

          <div
            v-if="selectedPayout.status === 'ESCROW_HOLDING'"
            class="admin-banner"
            style="margin-top: 0; margin-bottom: 12px"
          >
            当前资金还在托管中，买家尚未确认收货。提前发放请确保已核实情况。
          </div>
          <div
            v-if="selectedPayout.status === 'PAID'"
            class="admin-banner"
            style="margin-top: 0; margin-bottom: 12px"
          >
            这笔资金已经完成发放。
          </div>

          <div class="admin-meta-grid" style="margin-top: 0">
            <div class="admin-meta-card">
              <span>发放金额</span><strong>{{ formatMoney(selectedPayout.amount) }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>当前状态</span><strong>{{ statusLabel(selectedPayout.status) }}</strong>
            </div>
          </div>

          <div style="display: flex; flex-direction: column; gap: 8px">
            <span style="font-size: 13px; font-weight: 600">发放备注</span>
            <a-textarea
              v-model:value="releaseNote"
              :disabled="selectedPayout.status !== 'PENDING'"
              placeholder="例如：确认收货后发放，平台托管金额已结清"
              :rows="2"
            />
            <a-button
              type="primary"
              :loading="busy"
              :disabled="selectedPayout.status !== 'PENDING'"
              @click="handleRelease"
            >
              发放到卖家钱包
            </a-button>
          </div>

          <section class="admin-meta-grid">
            <div class="admin-meta-card">
              <span>订单号</span><strong>{{ selectedPayout.orderNo || '-' }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>订单类型</span><strong>{{ selectedPayout.orderType || '-' }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>支付方式</span><strong>{{ payLabel(selectedPayout.paymentMethod) }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>卖家</span><strong>{{ selectedPayout.sellerName || '-' }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>买家</span><strong>{{ selectedPayout.buyerName || '-' }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>申请时间</span><strong>{{ formatDate(selectedPayout.requestedAt) }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>发放时间</span><strong>{{ formatDate(selectedPayout.releasedAt) }}</strong>
            </div>
          </section>
        </div>
      </a-card>
    </section>
  </div>
</template>

<style scoped>
.admin-page {
  min-height: 100vh;
  padding: 24px;
  background: hsl(var(--background));
}

.admin-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 16px;
}

.admin-header h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
}

.admin-eyebrow {
  margin: 0 0 4px;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
  text-transform: uppercase;
}

.admin-lede {
  margin: 8px 0 0;
  font-size: 13px;
  color: hsl(var(--muted-foreground));
}

.admin-banner {
  display: flex;
  gap: 8px;
  align-items: center;
  padding: 10px 16px;
  margin-top: 16px;
  font-size: 13px;
  color: hsl(var(--muted-foreground));
  background: hsl(var(--muted));
  border-radius: 8px;
}

.admin-banner--error {
  margin-top: 0;
  margin-bottom: 16px;
  color: hsl(var(--destructive));
  background: hsl(var(--destructive) / 10%);
}

.admin-filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.admin-stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.admin-stats-grid--six {
  grid-template-columns: repeat(6, minmax(0, 1fr));
}

.admin-stat-label {
  margin: 0;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.admin-stat-value {
  margin: 8px 0 0;
  font-size: 22px;
  font-weight: 700;
}

.admin-content-grid {
  display: grid;
  grid-template-columns: 360px minmax(0, 1fr);
  gap: 16px;
  align-items: start;
}

.admin-list-panel {
  max-height: calc(100vh - 300px);
  overflow-y: auto;
}

.admin-detail-panel {
  min-height: 400px;
}

.admin-count {
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.report-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.report-card {
  display: block;
  width: 100%;
  padding: 12px;
  text-align: left;
  cursor: pointer;
  background: hsl(var(--card));
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
  transition: border-color 0.2s;
}

.report-card:hover {
  border-color: hsl(var(--primary));
}

.report-card.active {
  background: hsl(var(--primary) / 8%);
  border-color: hsl(var(--primary));
}

.report-card h4 {
  margin: 6px 0 4px;
  font-size: 14px;
}

.report-card p {
  margin: 0;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.report-card .row {
  display: flex;
  gap: 6px;
  align-items: center;
}

.admin-meta-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  margin-top: 12px;
}

.admin-meta-card {
  padding: 10px 12px;
  background: hsl(var(--muted));
  border-radius: 8px;
}

.admin-meta-card span {
  display: block;
  font-size: 11px;
  color: hsl(var(--muted-foreground));
}

.admin-meta-card strong {
  display: block;
  margin-top: 4px;
  font-size: 14px;
}

.admin-review-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
}

.admin-review-form label {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
}

.admin-actions {
  display: flex;
  gap: 8px;
}

.detail-stack section {
  margin-bottom: 16px;
}
</style>
