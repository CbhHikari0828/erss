<script setup>
import { getWalletSummary, listTransactions } from '@/api/wallet/index.js'
import { ensureLogin } from '@/utils/auth.js'
import { formatDateTime } from '@/utils/index.js'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const summary = ref(null)
const transactions = ref([])
const error = ref('')

const balance = computed(() => Number(userStore.userInfo.walletBalance || summary.value?.walletBalance || 0))
const withdrawCount = computed(() => summary.value?.withdrawCount ?? 0)
const withdrawAmount = computed(() => Number(summary.value?.withdrawAmount || 0))

function goBack() {
  uni.navigateBack({ delta: 1 })
}

function goWithdraw() {
  ensureLogin(userStore, () => {
    router.push('/wallet/withdraw')
  }, {
    onConfirm: () => router.replace('/login'),
  })
}

function amountText(value) {
  return Number(value || 0).toFixed(2)
}

async function refreshWallet() {
  if (!userStore.isLogin) {
    clearWallet()
    return
  }

  const requestToken = userStore.token
  loading.value = true
  error.value = ''
  try {
    const [summaryRes, txRes] = await Promise.all([
      getWalletSummary(),
      listTransactions(),
    ])
    if (requestToken !== userStore.token) return
    summary.value = summaryRes.data || null
    transactions.value = txRes.data || []
    await userStore.getUserData().catch(() => {})
  }
  catch (err) {
    error.value = err.message || '获取钱包信息失败'
  }
  finally {
    loading.value = false
  }
}

function clearWallet() {
  summary.value = null
  transactions.value = []
  error.value = ''
}

onShow(() => {
  if (!userStore.isLogin) {
    clearWallet()
  }
  ensureLogin(userStore, () => {
    void refreshWallet()
  }, {
    onConfirm: () => router.replace('/login'),
  })
})
</script>

<template>
  <view class="app-shell wallet-page">
    <view class="wallet-head">
      <button class="wallet-back" @click="goBack">
        <image class="wallet-back__icon" src="/static/images/common/back.svg" mode="aspectFit" />
      </button>
      <view class="wallet-head__body">
        <view class="wallet-head__title">
          我的钱包
        </view>
        <view class="wallet-head__sub">
          管理余额与提现
        </view>
      </view>
    </view>

    <!-- 余额卡片 -->
    <view class="card-panel wallet-balance-card">
      <view class="wallet-balance-card__label">
        当前余额
      </view>
      <view class="wallet-balance-card__value">
        ￥{{ amountText(balance) }}
      </view>
      <view class="wallet-balance-card__stats">
        <view class="wallet-balance-card__stat">
          <text class="wallet-balance-card__stat-value">{{ withdrawCount }}</text>
          <text class="wallet-balance-card__stat-label">提现笔数</text>
        </view>
        <view class="wallet-balance-card__stat">
          <text class="wallet-balance-card__stat-value">￥{{ amountText(withdrawAmount) }}</text>
          <text class="wallet-balance-card__stat-label">累计提现</text>
        </view>
      </view>
      <button class="primary-button wallet-withdraw-btn" @click="goWithdraw">
        提现
      </button>
    </view>

    <view v-if="error" class="wallet-banner">
      {{ error }}
    </view>

    <!-- 资金流水 -->
    <view class="section-title section-gap">
      <view>
        <view class="section-title__main">
          资金流水
        </view>
        <view class="section-title__sub">
          余额变动记录
        </view>
      </view>
    </view>

    <view v-if="transactions.length" class="flow-list">
      <view v-for="item in transactions" :key="item.id" class="card-panel flow-item">
        <view class="flow-item__head">
          <view class="flow-item__left">
            <view class="flow-item__title">
              {{ item.title }}
            </view>
            <view class="flow-item__time">
              {{ formatDateTime(item.createdAt) }}
            </view>
          </view>
          <view class="flow-item__amount" :class="item.type === 'INCOME' ? 'flow-item__amount--in' : 'flow-item__amount--out'">
            {{ item.type === 'INCOME' ? '+' : '-' }}￥{{ amountText(item.amount) }}
          </view>
        </view>
        <view class="flow-item__balance">
          余额 ￥{{ amountText(item.balanceAfter) }}
        </view>
      </view>
    </view>

    <view v-else class="card-panel wallet-empty">
      暂无资金流水记录。
    </view>
  </view>
</template>

<style lang="scss" scoped>
.wallet-head {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;
}

.wallet-head__body {
  flex: 1;
}

.wallet-head__title {
  font-size: 40rpx;
  font-weight: 700;
  color: #111111;
}

.wallet-head__sub {
  margin-top: 10rpx;
  color: #6f6f69;
  font-size: 24rpx;
  line-height: 1.6;
}

.wallet-back {
  width: 72rpx;
  height: 72rpx;
  flex: none;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  border: none;
  border-radius: 22rpx;
  background: #f1eee6;
}

.wallet-back::after {
  border: none;
}

.wallet-back__icon {
  width: 30rpx;
  height: 30rpx;
}

/* 余额卡片 */
.wallet-balance-card {
  margin-top: 24rpx;
  padding: 34rpx 28rpx;
  text-align: center;
}

.wallet-balance-card__label {
  color: #6f6f69;
  font-size: 24rpx;
}

.wallet-balance-card__value {
  margin-top: 12rpx;
  color: #111111;
  font-size: 52rpx;
  font-weight: 700;
}

.wallet-balance-card__stats {
  display: flex;
  justify-content: center;
  gap: 48rpx;
  margin-top: 24rpx;
}

.wallet-balance-card__stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
}

.wallet-balance-card__stat-value {
  color: #111111;
  font-size: 28rpx;
  font-weight: 600;
}

.wallet-balance-card__stat-label {
  color: #6f6f69;
  font-size: 22rpx;
}

.wallet-withdraw-btn {
  margin-top: 28rpx;
  width: 100%;
}

.wallet-banner {
  margin-top: 20rpx;
  padding: 20rpx 24rpx;
  border-radius: 22rpx;
  background: #f8efe7;
  color: #9d4f12;
  font-size: 23rpx;
}

/* 资金流水 */
.flow-list {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  margin-top: 18rpx;
}

.flow-item {
  padding: 26rpx;
}

.flow-item__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.flow-item__left {
  min-width: 0;
  flex: 1;
}

.flow-item__title {
  color: #111111;
  font-size: 28rpx;
  font-weight: 600;
}

.flow-item__time {
  margin-top: 6rpx;
  color: #91918b;
  font-size: 22rpx;
}

.flow-item__amount {
  flex: none;
  font-size: 28rpx;
  font-weight: 700;
}

.flow-item__amount--in {
  color: #3d7a2b;
}

.flow-item__amount--out {
  color: #b03a0f;
}

.flow-item__balance {
  margin-top: 10rpx;
  color: #91918b;
  font-size: 22rpx;
}

.wallet-empty {
  margin-top: 20rpx;
  padding: 40rpx 28rpx;
  color: #6f6f69;
  font-size: 24rpx;
  line-height: 1.7;
  text-align: center;
}
</style>
