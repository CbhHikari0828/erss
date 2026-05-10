<script lang="ts" setup>
import type { Recordable } from '@vben/types';

import { computed, onMounted, ref } from 'vue';
import VChart from 'vue-echarts';

import { BarChart, PieChart } from 'echarts/charts';
import {
  GridComponent,
  LegendComponent,
  TooltipComponent,
} from 'echarts/components';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';

import {
  getDamageReportsApi,
  getFinanceSummaryApi,
  getPayoutsApi,
  getUsersApi,
} from '#/api';

defineOptions({ name: 'Dashboard' });

use([
  CanvasRenderer,
  BarChart,
  PieChart,
  GridComponent,
  LegendComponent,
  TooltipComponent,
]);

const loading = ref(false);
const error = ref('');
const summary = ref<null | Recordable<any>>(null);
const payouts = ref<Recordable<any>[]>([]);
const users = ref<Recordable<any>[]>([]);
const reports = ref<Recordable<any>[]>([]);

// ---- Finance charts ----

const amountDonutOption = computed(() => {
  const data = [
    { name: '托管中', value: Number(summary.value?.holdingPayoutAmount ?? 0) },
    { name: '待发放', value: Number(summary.value?.pendingPayoutAmount ?? 0) },
    { name: '已发放', value: Number(summary.value?.releasedPayoutAmount ?? 0) },
  ];
  return donutOption(data, '{b}: {c} 元 ({d}%)');
});

const paymentMethodOption = computed(() => {
  const count: Record<string, number> = {};
  for (const p of payouts.value) {
    const m = p.paymentMethod || 'unknown';
    count[m] = (count[m] || 0) + 1;
  }
  const data = Object.entries(count).map(([key, value]) => ({
    name: payLabel(key),
    value,
  }));
  return donutOption(data, '{b}: {c} 笔 ({d}%)');
});

const topSellersOption = computed(() => {
  const map: Record<string, number> = {};
  for (const p of payouts.value) {
    const name = p.sellerName || 'unknown';
    map[name] = (map[name] || 0) + Number(p.amount ?? 0);
  }
  const sorted = Object.entries(map)
    .toSorted((a, b) => b[1] - a[1])
    .slice(0, 10);
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: '{b}: {c} 元',
    },
    grid: { left: '3%', right: '10%', top: 10, bottom: 0, containLabel: true },
    xAxis: { type: 'value' },
    yAxis: {
      type: 'category',
      data: sorted.map(([name]) => name),
      inverse: true,
    },
    series: [
      {
        type: 'bar',
        data: sorted.map(([, value]) => value),
        itemStyle: { borderRadius: [0, 4, 4, 0] },
      },
    ],
  };
});

// ---- Personnel charts ----

const roleOption = computed(() => {
  const count: Record<string, number> = {};
  for (const u of users.value) {
    const r = u.role || 'unknown';
    count[r] = (count[r] || 0) + 1;
  }
  const data = Object.entries(count).map(([name, value]) => ({ name, value }));
  return donutOption(data, '{b}: {c} 人 ({d}%)');
});

const bannedOption = computed(() => {
  const banned = users.value.filter((u) => u.banned).length;
  const data = [
    { name: '正常', value: users.value.length - banned },
    { name: '已封号', value: banned },
  ];
  return donutOption(data, '{b}: {c} 人 ({d}%)');
});

const campusOption = computed(() => {
  const count: Record<string, number> = {};
  for (const u of users.value) {
    const c = u.campus || '未知';
    count[c] = (count[c] || 0) + 1;
  }
  const data = Object.entries(count)
    .toSorted((a, b) => b[1] - a[1])
    .map(([name, value]) => ({ name, value }));
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: '{b}: {c} 人',
    },
    grid: { left: '3%', right: '10%', top: 10, bottom: 0, containLabel: true },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: data.map((d) => d.name), inverse: true },
    series: [
      {
        type: 'bar',
        data: data.map((d) => d.value),
        itemStyle: { borderRadius: [0, 4, 4, 0] },
      },
    ],
  };
});

// ---- Damage charts ----

const reviewStatusOption = computed(() => ({
  ...donutOption(
    [
      {
        name: '待审核',
        value: reports.value.filter((i) => i.status === 'pending').length,
      },
      {
        name: '已审核',
        value: reports.value.filter((i) => i.status === 'reviewed').length,
      },
    ],
    '{b}: {c} 条 ({d}%)',
  ),
}));

const rentalStatusOption = computed(() => {
  const count: Record<string, number> = {};
  for (const r of reports.value) {
    const s = r.rentalStatus || 'unknown';
    count[s] = (count[s] || 0) + 1;
  }
  const data = Object.entries(count).map(([name, value]) => ({ name, value }));
  return donutOption(data, '{b}: {c} 条 ({d}%)');
});

// ---- Shared donut helper ----

function payLabel(key: string) {
  const map: Record<string, string> = {
    wechat: '微信支付',
    alipay: '支付宝支付',
    campus: '校园卡支付',
    wallet: '系统钱包',
  };
  return map[key] || key;
}

function donutOption(
  data: { name: string; value: number }[],
  formatter: string,
) {
  return {
    tooltip: { trigger: 'item' as const, formatter },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie' as const,
        radius: ['55%', '75%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 4,
          borderColor: 'transparent',
          borderWidth: 2,
        },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 16, fontWeight: 'bold' as const },
        },
        data,
      },
    ],
  };
}

// ---- Data fetching ----

async function refreshAll() {
  loading.value = true;
  error.value = '';
  try {
    const [summaryData, payoutsData, usersData, reportsData] =
      await Promise.all([
        getFinanceSummaryApi(),
        getPayoutsApi(),
        getUsersApi(),
        getDamageReportsApi(),
      ]);
    summary.value = summaryData || null;
    payouts.value = Array.isArray(payoutsData) ? payoutsData : [];
    users.value = Array.isArray(usersData) ? usersData : [];
    reports.value = Array.isArray(reportsData) ? reportsData : [];
  } catch (error_: any) {
    error.value = error_?.message || '加载数据失败';
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  refreshAll();
});
</script>

<template>
  <div class="admin-page">
    <header class="admin-header">
      <div>
        <p class="admin-eyebrow">数据看板</p>
        <h1>运营概览</h1>
      </div>
      <a-button :disabled="loading" @click="refreshAll">
        {{ loading ? '加载中...' : '刷新' }}
      </a-button>
    </header>

    <div v-if="error" class="admin-banner admin-banner--error">
      ⚠ {{ error }}
    </div>

    <template v-if="!loading">
      <!-- 资金 -->
      <section class="dash-section">
        <h2 class="dash-section-title">资金概览</h2>
        <div class="dash-chart-row dash-chart-row--3">
          <a-card :bordered="false" title="金额占比">
            <VChart
              :option="amountDonutOption"
              autoresize
              style="height: 260px"
            />
          </a-card>
          <a-card :bordered="false" title="支付方式分布">
            <VChart
              :option="paymentMethodOption"
              autoresize
              style="height: 260px"
            />
          </a-card>
          <a-card :bordered="false" title="卖家金额 Top 10">
            <VChart
              :option="topSellersOption"
              autoresize
              style="height: 260px"
            />
          </a-card>
        </div>
      </section>

      <!-- 人员 -->
      <section class="dash-section">
        <h2 class="dash-section-title">人员概览</h2>
        <div class="dash-chart-row dash-chart-row--3">
          <a-card :bordered="false" title="角色分布">
            <VChart :option="roleOption" autoresize style="height: 260px" />
          </a-card>
          <a-card :bordered="false" title="封号状态">
            <VChart :option="bannedOption" autoresize style="height: 260px" />
          </a-card>
          <a-card :bordered="false" title="各校区人数">
            <VChart :option="campusOption" autoresize style="height: 260px" />
          </a-card>
        </div>
      </section>

      <!-- 报损 -->
      <section class="dash-section">
        <h2 class="dash-section-title">报损概览</h2>
        <div class="dash-chart-row dash-chart-row--2">
          <a-card :bordered="false" title="审核状态占比">
            <VChart
              :option="reviewStatusOption"
              autoresize
              style="height: 260px"
            />
          </a-card>
          <a-card :bordered="false" title="租借状态分布">
            <VChart
              :option="rentalStatusOption"
              autoresize
              style="height: 260px"
            />
          </a-card>
        </div>
      </section>
    </template>
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

.admin-banner {
  display: flex;
  gap: 8px;
  align-items: center;
  padding: 10px 16px;
  margin-bottom: 16px;
  font-size: 13px;
  color: hsl(var(--muted-foreground));
  background: hsl(var(--muted));
  border-radius: 8px;
}

.admin-banner--error {
  color: hsl(var(--destructive));
  background: hsl(var(--destructive) / 10%);
}

.dash-section {
  margin-bottom: 24px;
}

.dash-section-title {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
}

.dash-chart-row {
  display: grid;
  gap: 12px;
}

.dash-chart-row--2 {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.dash-chart-row--3 {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}
</style>
