<script lang="ts" setup>
import type { Recordable } from '@vben/types';

import { computed, onMounted, ref, watch } from 'vue';

import { message } from 'ant-design-vue';

import { getDamageReportsApi, reviewDamageReportApi } from '#/api';

defineOptions({ name: 'DamageReview' });
const reports = ref<Recordable<any>[]>([]);
const selectedId = ref<null | number>(null);
const filter = ref('all');
const loading = ref(false);
const busy = ref(false);
const error = ref('');

const reviewNote = ref('');
const compensationAmount = ref('');

const selectedReport = computed(
  () => reports.value.find((item) => item.id === selectedId.value) || null,
);

const stats = computed(() => {
  const pending = reports.value.filter((i) => i.status === 'pending').length;
  const reviewed = reports.value.filter((i) => i.status === 'reviewed').length;
  const compensation = reports.value.filter(
    (i) => i.compensationAmount !== null,
  ).length;
  return [
    { label: '总报损数', value: reports.value.length },
    { label: '待审核', value: pending },
    { label: '已审核', value: reviewed },
    { label: '需赔付', value: compensation },
  ];
});

const filters = [
  { key: 'all', label: '全部' },
  { key: 'PENDING', label: '待审核' },
  { key: 'REVIEWED', label: '已审核' },
];

async function refreshReports() {
  loading.value = true;
  error.value = '';
  try {
    const params: Recordable<any> = {};
    if (filter.value !== 'all') params.status = filter.value;
    const data = await getDamageReportsApi(params);
    const nextReports = Array.isArray(data) ? data : [];
    reports.value = nextReports;
    if (
      selectedId.value &&
      !nextReports.some((i) => i.id === selectedId.value)
    ) {
      selectedId.value = nextReports[0]?.id || null;
    } else if (!selectedId.value) {
      selectedId.value = nextReports[0]?.id || null;
    }
    if (selectedReport.value) {
      reviewNote.value = selectedReport.value.reviewNote || '';
      compensationAmount.value = selectedReport.value.compensationAmount ?? '';
    }
  } catch (error_: any) {
    const msg = error_?.message || '获取报损列表失败';
    error.value = msg;
  } finally {
    loading.value = false;
  }
}

async function handleReview() {
  if (!selectedReport.value || selectedReport.value.status !== 'pending')
    return;

  busy.value = true;
  error.value = '';
  try {
    await reviewDamageReportApi(selectedReport.value.id, {
      compensationAmount: compensationAmount.value
        ? Number(compensationAmount.value)
        : undefined,
      reviewNote: reviewNote.value.trim(),
    });
    message.success('审核提交成功');
    await refreshReports();
  } catch (error_: any) {
    const msg = error_?.message || '提交审核失败';
    error.value = msg;
  } finally {
    busy.value = false;
  }
}

function selectReport(id: number) {
  selectedId.value = id;
  const report = reports.value.find((i) => i.id === id);
  if (report) {
    reviewNote.value = report.reviewNote || '';
    compensationAmount.value = report.compensationAmount ?? '';
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
  refreshReports();
});

watch(selectedReport, (report) => {
  if (report) {
    reviewNote.value = report.reviewNote || '';
    compensationAmount.value = report.compensationAmount ?? '';
  }
});

onMounted(() => {
  refreshReports();
});
</script>

<template>
  <div class="admin-page">
    <header class="admin-header">
      <div>
        <p class="admin-eyebrow">管理员功能</p>
        <h1>报损审核</h1>
      </div>
      <a-button :disabled="loading || busy" @click="refreshReports">
        {{ loading ? '加载中...' : '刷新' }}
      </a-button>
    </header>

    <div v-if="error" class="admin-banner admin-banner--error">
      ⚠ {{ error }}
    </div>

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

    <section class="admin-stats-grid">
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

    <section class="admin-content-grid">
      <a-card :bordered="false" class="admin-list-panel" title="记录列表">
        <template #extra>
          <span class="admin-count">{{ reports.length }} 条</span>
        </template>
        <div v-if="reports.length > 0" class="report-list">
          <button
            v-for="report in reports"
            :key="report.id"
            type="button"
            class="report-card"
            :class="{ active: report.id === selectedId }"
            @click="selectReport(report.id)"
          >
            <div class="row">
              <a-tag :color="report.status === 'pending' ? 'orange' : 'green'">
                {{ report.status === 'pending' ? '待审核' : '已审核' }}
              </a-tag>
              <a-tag>{{ report.bookTitle || '未知书籍' }}</a-tag>
            </div>
            <h4>{{ report.description }}</h4>
            <p>
              {{ report.reporterName || report.ownerName || '-' }} · 租客
              {{ report.borrowerName || '-' }}
            </p>
          </button>
        </div>
        <a-empty v-else description="暂无报损记录" />
      </a-card>

      <a-card :bordered="false" class="admin-detail-panel" title="审核详情">
        <template #extra>
          <span>{{
            selectedReport
              ? selectedReport.status === 'pending'
                ? '待审核'
                : '已审核'
              : '未选择记录'
          }}</span>
        </template>

        <a-empty
          v-if="!selectedReport"
          description="请选择一条报损记录查看和审核"
          style="margin-top: 60px"
        />
        <div v-else class="detail-stack">
          <!-- 基本信息 -->
          <section>
            <p class="admin-eyebrow">报损单 #{{ selectedReport.id }}</p>
            <h4>{{ selectedReport.bookTitle || '未知书籍' }}</h4>
            <p class="admin-lede">{{ selectedReport.description }}</p>
          </section>

          <!-- 信息 Grid -->
          <section class="admin-meta-grid">
            <div class="admin-meta-card">
              <span>报损卖家</span>
              <strong>{{
                selectedReport.reporterName || selectedReport.ownerName || '-'
              }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>租客</span>
              <strong>{{ selectedReport.borrowerName || '-' }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>租借状态</span>
              <strong>{{ selectedReport.rentalStatus || '-' }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>到期时间</span>
              <strong>{{ formatDate(selectedReport.dueAt) }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>租期</span>
              <strong>{{ selectedReport.periodText || '-' }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>当前赔偿金额</span>
              <strong>{{
                selectedReport.compensationAmount != null
                  ? `${Number(selectedReport.compensationAmount).toFixed(2)} 元`
                  : '待定'
              }}</strong>
            </div>
          </section>

          <!-- 金额参考 -->
          <section>
            <h3 class="admin-section-head">金额参考</h3>
            <div class="admin-meta-grid">
              <div class="admin-meta-card">
                <span>书籍挂牌价</span><strong>{{ formatMoney(selectedReport.bookPrice) }}</strong>
              </div>
              <div class="admin-meta-card">
                <span>书籍原价</span><strong>{{
                  formatMoney(selectedReport.bookOriginalPrice)
                }}</strong>
              </div>
              <div class="admin-meta-card">
                <span>租借单价</span><strong>{{
                  formatMoney(selectedReport.rentalUnitPrice)
                }}</strong>
              </div>
              <div class="admin-meta-card">
                <span>租借押金</span><strong>{{
                  formatMoney(selectedReport.rentalDepositAmount)
                }}</strong>
              </div>
              <div class="admin-meta-card">
                <span>运费/配送费</span><strong>{{
                  formatMoney(selectedReport.rentalDeliveryFee)
                }}</strong>
              </div>
              <div class="admin-meta-card">
                <span>订单总额</span><strong>{{
                  formatMoney(selectedReport.rentalTotalAmount)
                }}</strong>
              </div>
            </div>
            <p class="admin-lede" style="margin-top: 10px">
              订单号：{{ selectedReport.orderNo || '-' }}
            </p>
          </section>

          <!-- 证据图片 -->
          <section>
            <div class="admin-section-head">
              <h3>证据图片</h3>
              <span>{{ selectedReport.images?.length || 0 }} 张图片</span>
            </div>
            <div class="admin-image-grid">
              <img
                v-for="src in selectedReport.images || []"
                :key="src"
                :src="src"
                alt="evidence"
              />
            </div>
          </section>

          <!-- 审核表单 (PENDING) -->
          <form
            v-if="selectedReport.status === 'pending'"
            class="admin-review-form"
            @submit.prevent="handleReview"
          >
            <label>
              审核备注
              <a-textarea
                v-model:value="reviewNote"
                placeholder="填写审核结果、处理说明或赔付理由"
                :rows="3"
              />
            </label>
            <label>
              赔偿金额（元）
              <a-input
                v-model:value="compensationAmount"
                placeholder="无赔付可留空，填写如 12.00"
              />
            </label>
            <div class="admin-actions">
              <a-button type="primary" html-type="submit" :loading="busy">
                提交审核
              </a-button>
            </div>
          </form>

          <!-- 审核结果 (REVIEWED) -->
          <div
            v-else-if="selectedReport.status === 'reviewed'"
            class="admin-review-result"
          >
            <h3 class="admin-section-head">审核结果</h3>
            <div class="admin-meta-grid">
              <div class="admin-meta-card">
                <span>赔偿金额</span>
                <strong>{{
                  selectedReport.compensationAmount != null
                    ? `${Number(selectedReport.compensationAmount).toFixed(2)} 元`
                    : '无赔付'
                }}</strong>
              </div>
              <div class="admin-meta-card">
                <span>审核备注</span>
                <strong>{{ selectedReport.reviewNote || '无备注' }}</strong>
              </div>
            </div>
          </div>
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

.admin-filter-row {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.admin-stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.admin-stat-label {
  margin: 0;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.admin-stat-value {
  margin: 8px 0 0;
  font-size: 28px;
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

.admin-section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
}

.admin-section-head h3 {
  margin: 0;
  font-size: 14px;
}

.admin-image-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
  margin-top: 8px;
}

.admin-image-grid img {
  width: 100%;
  background: hsl(var(--muted));
  border-radius: 8px;
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

.admin-review-result {
  margin-top: 16px;
}

.detail-stack section {
  margin-bottom: 16px;
}
</style>
