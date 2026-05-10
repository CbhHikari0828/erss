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

import { getRevenueLedgerApi, getRevenueSummaryApi } from '#/api';

defineOptions({ name: 'Revenue' });

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
const ledger = ref<null | Recordable<any>>(null);
const summary = ref<null | Recordable<any>>(null);
const filterCategory = ref<string>('all');

const today = new Date();
const thirtyDaysAgo = new Date(today);
thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
const dateRange = ref<[string, string]>([
  thirtyDaysAgo.toISOString().slice(0, 10),
  today.toISOString().slice(0, 10),
]);

const stats = computed(() => [
  { label: '总收入', value: formatMoney(ledger.value?.totalAmount) },
  {
    label: '驿站配送费',
    value: formatMoney(ledger.value?.categoryTotals?.STATION_DELIVERY_FEE),
  },
  {
    label: '还书驿站费',
    value: formatMoney(ledger.value?.categoryTotals?.RETURN_DELIVERY_FEE),
  },
  {
    label: '报损服务费',
    value: formatMoney(ledger.value?.categoryTotals?.DAMAGE_SERVICE_FEE),
  },
  {
    label: '逾期罚金分成',
    value: formatMoney(ledger.value?.categoryTotals?.OVERDUE_PENALTY_SHARE),
  },
]);

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '类别', dataIndex: 'category', key: 'category', width: 140 },
  { title: '金额', dataIndex: 'amount', key: 'amount', width: 120 },
  { title: '描述', dataIndex: 'description', key: 'description' },
  { title: '参考ID', dataIndex: 'referenceId', key: 'referenceId', width: 160 },
  { title: '时间', dataIndex: 'createdAt', key: 'createdAt', width: 180 },
];

const categoryPieOption = computed(() => {
  if (!summary.value?.categoryTotals) return {};
  const data = Object.entries(
    summary.value.categoryTotals as Record<string, number>,
  ).map(([key, value]) => ({ name: categoryLabel(key), value }));
  return donutOption(data, '{b}: {c} 元 ({d}%)');
});

const monthlyBarOption = computed(() => {
  if (!summary.value?.monthlyTotals) return {};
  const entries = Object.entries(
    summary.value.monthlyTotals as Record<string, number>,
  ).toSorted(([a], [b]) => a.localeCompare(b));
  return {
    tooltip: { trigger: 'axis' as const, formatter: '{b}: {c} 元' },
    grid: { left: '3%', right: '4%', top: 10, bottom: 20, containLabel: true },
    xAxis: { type: 'category' as const, data: entries.map(([m]) => m) },
    yAxis: { type: 'value' as const, axisLabel: { formatter: '{value} 元' } },
    series: [
      {
        type: 'bar' as const,
        data: entries.map(([, v]) => v),
        itemStyle: { borderRadius: [4, 4, 0, 0] },
      },
    ],
  };
});

function categoryLabel(key: string) {
  const map: Record<string, string> = {
    STATION_DELIVERY_FEE: '驿站配送费',
    RETURN_DELIVERY_FEE: '还书驿站费',
    DAMAGE_SERVICE_FEE: '报损服务费',
    OVERDUE_PENALTY_SHARE: '逾期罚金分成',
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

async function refreshData() {
  loading.value = true;
  error.value = '';
  try {
    const [ledgerData, summaryData] = await Promise.all([
      getRevenueLedgerApi({
        category:
          filterCategory.value === 'all' ? undefined : filterCategory.value,
        startDate: dateRange.value[0],
        endDate: dateRange.value[1],
      }),
      getRevenueSummaryApi(),
    ]);
    ledger.value = ledgerData || null;
    summary.value = summaryData || null;
  } catch (error_: any) {
    error.value = error_?.message || '获取收入数据失败';
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  refreshData();
});
</script>

<template>
  <div class="admin-page">
    <header class="admin-header">
      <div>
        <p class="admin-eyebrow">收入管理</p>
        <h1>系统收入账本</h1>
        <p class="admin-lede">
          记录平台所有系统收入（配送费、还书驿站费、报损服务费、逾期罚金分成）。
        </p>
      </div>
      <a-button :disabled="loading" @click="refreshData">
        {{ loading ? '加载中...' : '刷新' }}
      </a-button>
    </header>

    <div v-if="error" class="admin-banner admin-banner--error">
      ⚠ {{ error }}
    </div>

    <section class="admin-stats-grid admin-stats-grid--five">
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
      <a-select
        v-model:value="filterCategory"
        style="width: 160px"
        :options="[
          { value: 'all', label: '全部类别' },
          { value: 'STATION_DELIVERY_FEE', label: '驿站配送费' },
          { value: 'RETURN_DELIVERY_FEE', label: '还书驿站费' },
          { value: 'DAMAGE_SERVICE_FEE', label: '报损服务费' },
          { value: 'OVERDUE_PENALTY_SHARE', label: '逾期罚金分成' },
        ]"
      />
      <a-button type="primary" @click="refreshData">查询</a-button>
    </div>

    <a-card :bordered="false" style="margin-bottom: 16px">
      <a-table
        :data-source="ledger?.records ?? []"
        :columns="columns"
        :loading="loading"
        :pagination="{
          pageSize: 20,
          showSizeChanger: true,
          showTotal: (total: number) => `共 ${total} 条`,
        }"
        :row-key="(record: Recordable) => record.id"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'category'">
            {{ categoryLabel(record.category) }}
          </template>
          <template v-else-if="column.key === 'amount'">
            {{ formatMoney(record.amount) }}
          </template>
          <template v-else-if="column.key === 'createdAt'">
            {{ formatDate(record.createdAt) }}
          </template>
        </template>
      </a-table>
    </a-card>

    <section class="dash-section">
      <h2 class="dash-section-title">收入分析</h2>
      <div class="dash-chart-row dash-chart-row--2">
        <a-card :bordered="false" title="收入类别占比">
          <VChart
            :option="categoryPieOption"
            autoresize
            style="height: 300px"
          />
        </a-card>
        <a-card :bordered="false" title="月度趋势">
          <VChart
            :option="monthlyBarOption"
            autoresize
            style="height: 300px"
          />
        </a-card>
      </div>
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

.admin-stats-grid {
  display: grid;
  gap: 12px;
  margin-bottom: 16px;
}

.admin-stats-grid--five {
  grid-template-columns: repeat(5, minmax(0, 1fr));
}

.admin-stat-card {
  text-align: center;
}

.admin-stat-label {
  margin: 0 0 6px;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.admin-stat-value {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
}

.admin-filter-row {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}

.dash-section {
  margin-top: 8px;
}

.dash-section-title {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
}

.dash-chart-row {
  display: grid;
  gap: 16px;
}

.dash-chart-row--2 {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}
</style>
