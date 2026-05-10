import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:bar-chart-3',
      order: 0,
      title: '数据看板',
    },
    name: 'Dashboard',
    path: '/admin/dashboard',
    component: () => import('#/views/admin/dashboard/index.vue'),
  },
  {
    meta: {
      icon: 'lucide:file-warning',
      order: 1,
      title: '报损审核',
    },
    name: 'DamageReview',
    path: '/admin/damage-review',
    component: () => import('#/views/admin/damage-review/index.vue'),
  },
  {
    meta: {
      icon: 'lucide:users',
      order: 2,
      title: '人员管理',
    },
    name: 'Personnel',
    path: '/admin/personnel',
    component: () => import('#/views/admin/personnel/index.vue'),
  },
  {
    meta: {
      icon: 'lucide:circle-dollar-sign',
      order: 3,
      title: '资金管理',
    },
    name: 'Finance',
    path: '/admin/finance',
    component: () => import('#/views/admin/finance/index.vue'),
  },
  {
    meta: {
      icon: 'lucide:book-open-check',
      order: 4,
      title: '系统收入',
    },
    name: 'Revenue',
    path: '/admin/revenue',
    component: () => import('#/views/admin/revenue/index.vue'),
  },
];

export default routes;
