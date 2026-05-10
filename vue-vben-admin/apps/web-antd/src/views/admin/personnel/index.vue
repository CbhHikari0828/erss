<script lang="ts" setup>
import type { Recordable } from '@vben/types';

import { computed, onMounted, ref, watch } from 'vue';

import { message } from 'ant-design-vue';

import { banUserApi, getUsersApi, unbanUserApi } from '#/api';

defineOptions({ name: 'Personnel' });
const users = ref<Recordable<any>[]>([]);
const selectedId = ref<null | number>(null);
const scope = ref('all');
const keyword = ref('');
const loading = ref(false);
const busy = ref(false);
const error = ref('');
const banReason = ref('');

const selectedUser = computed(
  () => users.value.find((item) => item.id === selectedId.value) || null,
);

const stats = computed(() => {
  const merchants = users.value.filter((i) => i.merchant).length;
  const banned = users.value.filter((i) => i.banned).length;
  const admins = users.value.filter((i) => i.role === 'admin').length;
  return [
    { label: '总人数', value: users.value.length },
    { label: '商家数', value: merchants },
    { label: '已封号', value: banned },
    { label: '管理员', value: admins },
  ];
});

const filters = [
  { key: 'all', label: '全部' },
  { key: 'merchant', label: '商家' },
  { key: 'student', label: '学生' },
  { key: 'banned', label: '已封号' },
];

async function refreshUsers(searchKeyword?: string) {
  loading.value = true;
  error.value = '';
  try {
    const kw = searchKeyword ?? keyword.value;
    const params: Recordable<any> = {};
    if (scope.value !== 'all') params.scope = scope.value;
    if (kw?.trim()) params.keyword = kw.trim();
    const data = await getUsersApi(params);
    const nextUsers = Array.isArray(data) ? data : [];
    users.value = nextUsers;
    if (selectedId.value && !nextUsers.some((i) => i.id === selectedId.value)) {
      selectedId.value = nextUsers[0]?.id || null;
    } else if (!selectedId.value) {
      selectedId.value = nextUsers[0]?.id || null;
    }
  } catch (error_: any) {
    const msg = error_?.message || '获取人员列表失败';
    error.value = msg;
  } finally {
    loading.value = false;
  }
}

async function handleSearch() {
  await refreshUsers(keyword.value);
}

async function handleBan() {
  if (!selectedUser.value) return;

  busy.value = true;
  error.value = '';
  try {
    await banUserApi(selectedUser.value.id, {
      banReason: banReason.value.trim(),
    });
    message.success('封禁成功');
    await refreshUsers();
  } catch (error_: any) {
    error.value = error_?.message || '封号失败';
  } finally {
    busy.value = false;
  }
}

async function handleUnban() {
  if (!selectedUser.value) return;

  busy.value = true;
  error.value = '';
  try {
    await unbanUserApi(selectedUser.value.id);
    message.success('解封成功');
    await refreshUsers();
  } catch (error_: any) {
    error.value = error_?.message || '解封失败';
  } finally {
    busy.value = false;
  }
}

function selectUser(id: number) {
  selectedId.value = id;
  banReason.value = '';
}

function formatDate(value: any) {
  if (!value) return '-';
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return '-';
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

function formatMoney(value: any) {
  if (value === null || value === undefined || value === '') return '-';
  const n = Number(value);
  if (!Number.isFinite(n)) return '-';
  return `${n.toFixed(2)} 元`;
}

watch(scope, () => {
  refreshUsers();
});

onMounted(() => {
  refreshUsers();
});
</script>

<template>
  <div class="admin-page">
    <header class="admin-header">
      <div>
        <p class="admin-eyebrow">管理员功能</p>
        <h1>人员管理</h1>
      </div>
      <a-button :disabled="loading || busy" @click="refreshUsers()">
        {{ loading ? '加载中...' : '刷新' }}
      </a-button>
    </header>

    <div v-if="error" class="admin-banner admin-banner--error">
      ⚠ {{ error }}
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

    <div class="admin-filter-row">
      <a-button
        v-for="item in filters"
        :key="item.key"
        :type="item.key === scope ? 'primary' : 'default'"
        size="small"
        @click="scope = item.key"
      >
        {{ item.label }}
      </a-button>
      <div class="admin-search">
        <a-input
          v-model:value="keyword"
          placeholder="学号、姓名、学院"
          @press-enter="handleSearch"
        />
        <a-button type="primary" @click="handleSearch">搜索</a-button>
      </div>
    </div>

    <section class="admin-content-grid">
      <a-card :bordered="false" class="admin-list-panel" title="人员列表">
        <template #extra>
          <span class="admin-count">{{ users.length }} 人</span>
        </template>
        <div v-if="users.length > 0" class="report-list">
          <button
            v-for="item in users"
            :key="item.id"
            type="button"
            class="report-card"
            :class="{ active: item.id === selectedId }"
            @click="selectUser(item.id)"
          >
            <div class="row">
              <strong>{{ item.username }}</strong>
              <a-tag :color="item.banned ? 'orange' : 'green'">
                {{ item.banned ? '已封号' : '正常' }}
              </a-tag>
            </div>
            <p>
              {{ item.studentId }} · {{ item.campus || '-' }} ·
              {{ item.department || '-' }}
            </p>
            <div class="row">
              <a-tag>{{ item.merchant ? '商家' : '普通用户' }}</a-tag>
              <a-tag>{{ item.role }}</a-tag>
            </div>
          </button>
        </div>
        <a-empty v-else description="暂无人员数据" />
      </a-card>

      <a-card :bordered="false" class="admin-detail-panel" title="账号详情">
        <template #extra>
          <span>{{
            selectedUser ? selectedUser.studentId : '未选择人员'
          }}</span>
        </template>

        <a-empty
          v-if="!selectedUser"
          description="请选择一个账号查看和处理"
          style="margin-top: 60px"
        />
        <div v-else class="detail-stack">
          <section>
            <p class="admin-eyebrow">用户 #{{ selectedUser.id }}</p>
            <h4>{{ selectedUser.username }}</h4>
            <p class="admin-lede">
              {{ selectedUser.department || '暂无学院信息' }}
            </p>
          </section>

          <section class="admin-meta-grid">
            <div class="admin-meta-card">
              <span>学号</span><strong>{{ selectedUser.studentId || '-' }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>校区</span><strong>{{ selectedUser.campus || '-' }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>身份</span><strong>{{
                selectedUser.merchant ? '商家' : '普通用户'
              }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>角色</span><strong>{{ selectedUser.role || '-' }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>是否商家</span><strong>{{ selectedUser.shopRegistered ? '是' : '否' }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>封号状态</span><strong>{{ selectedUser.banned ? '已封号' : '正常' }}</strong>
            </div>
          </section>

          <section class="admin-meta-grid">
            <div class="admin-meta-card">
              <span>商铺名称</span><strong>{{ selectedUser.shopName || '-' }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>封号时间</span><strong>{{ formatDate(selectedUser.bannedAt) }}</strong>
            </div>
            <div class="admin-meta-card">
              <span>钱包余额</span><strong>{{ formatMoney(selectedUser.walletBalance) }}</strong>
            </div>
          </section>

          <section>
            <h3 class="admin-section-head">备注信息</h3>
            <p class="admin-lede">
              {{ selectedUser.banReason || '暂无封号备注' }}
            </p>
          </section>

          <!-- Admin: read only -->
          <div v-if="selectedUser.role === 'admin'" class="admin-banner">
            管理员账号仅展示，不支持封号。
          </div>

          <!-- Banned user: unban -->
          <div v-else-if="selectedUser.banned" class="admin-actions">
            <a-button type="primary" :loading="busy" @click="handleUnban">
              解封账号
            </a-button>
          </div>

          <!-- Normal user: ban -->
          <form v-else class="admin-review-form" @submit.prevent="handleBan">
            <label>
              封号原因
              <a-textarea
                v-model:value="banReason"
                placeholder="例如：恶意刷单、违规发言、扰乱交易秩序"
                :rows="3"
              />
            </label>
            <div class="admin-actions">
              <a-button
                type="primary"
                danger
                html-type="submit"
                :loading="busy"
              >
                封禁账号
              </a-button>
            </div>
          </form>
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
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  margin-bottom: 16px;
}

.admin-search {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

.admin-search :deep(.ant-input-affix-wrapper) {
  width: 200px;
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

.admin-section-head {
  display: flex;
  gap: 6px;
  align-items: center;
  margin-top: 16px;
  font-size: 14px;
  font-weight: 600;
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
