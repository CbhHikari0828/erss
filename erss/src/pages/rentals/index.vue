<script setup>
import CustomTabBar from '@/components/CustomTabBar.vue'
import { useRentalStore } from '@/store/rental/index.js'

const router = useRouter()
const rentalStore = useRentalStore()

const showTopBackBar = ref(false)
const TOP_BAR_THRESHOLD = 56
const tab = ref('borrowed')

const borrowedOrders = computed(() => rentalStore.rentalOrders.filter(item => !item.ownedByMe))
const ownedOrders = computed(() => rentalStore.rentalOrders.filter(item => item.ownedByMe))

function goBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack({ delta: 1 })
    return
  }
  uni.reLaunch({ url: '/pages/mine/index' })
}

function goReport(orderId) {
  router.push({ path: '/pages/rentals/report', query: { id: orderId } })
}

function goReturn(orderId) {
  router.push({ path: '/pages/rentals/return', query: { id: orderId } })
}

async function handleConfirmReceipt(orderId) {
  try {
    await rentalStore.confirmReceipt(orderId)
    uni.showToast({ title: '已确认收货，租赁计时开始', icon: 'success' })
  } catch (err) {
    uni.showToast({ title: err?.message || '操作失败', icon: 'none' })
  }
}

async function handleConfirmReturn(orderId) {
  uni.showModal({
    title: '确认还书',
    content: '确认买家已归还书籍？确认后资金将进入待发放状态。',
    success: async ({ confirm }) => {
      if (!confirm) return
      try {
        await rentalStore.confirmReturn(orderId)
        uni.showToast({ title: '还书已确认', icon: 'success' })
      } catch (err) {
        uni.showToast({ title: err?.message || '操作失败', icon: 'none' })
      }
    },
  })
}

function deliveryLabel(method) {
  if (method === 'station') return '驿站'
  if (method === 'pickup') return '自提'
  return ''
}

onPageScroll(({ scrollTop }) => {
  showTopBackBar.value = scrollTop >= TOP_BAR_THRESHOLD
})

onShow(() => {
  rentalStore.refreshRentals().catch(() => {})
})
</script>

<template>
  <view>
    <view class="app-shell app-shell--with-tabbar rentals-page">
      <view v-if="showTopBackBar" class="rentals-topbar">
        <button class="rentals-topbar__back" @click="goBack">
          <image class="rentals-topbar__icon" src="/static/images/common/back.svg" mode="aspectFit" />
        </button>
        <view class="rentals-topbar__title">
          我的租借
        </view>
      </view>
      <button v-if="!showTopBackBar" class="rentals-back rentals-back--floating" @click="goBack">
        <image class="rentals-back__icon" src="/static/images/common/back.svg" mode="aspectFit" />
      </button>

      <view class="rentals-header">
        <view class="rentals-back rentals-back--placeholder"></view>
        <view class="rentals-header__body">
          <view class="rentals-header__title">
            我的租借
          </view>
          <view class="rentals-header__sub">
            管理租借和归还流程；如有损坏争议，卖家可提交鉴定。
          </view>
        </view>
      </view>

      <!-- tabs -->
      <view class="rentals-tabs">
        <view
          class="rentals-tab"
          :class="{ 'rentals-tab--active': tab === 'borrowed' }"
          @click="tab = 'borrowed'"
        >
          我租的
        </view>
        <view
          class="rentals-tab"
          :class="{ 'rentals-tab--active': tab === 'owned' }"
          @click="tab = 'owned'"
        >
          我租出的
        </view>
      </view>

      <!-- borrowed -->
      <view v-if="tab === 'borrowed'" class="card-panel rentals-list">
        <view v-if="borrowedOrders.length === 0" class="rentals-empty">
          暂无租借记录
        </view>
        <view v-for="order in borrowedOrders" :key="order.id" class="rentals-item">
          <view class="rentals-item__head">
            <view class="rentals-item__title">
              {{ order.title }}
            </view>
            <view class="rentals-item__status" :class="{ 'rentals-item__status--warning': order.damageReport?.status === 'pending' }">
              {{ order.status }}
            </view>
          </view>
          <view class="rentals-item__meta">
            租期 {{ order.period }} · 押金 ￥{{ order.deposit }}
          </view>
          <view class="rentals-item__meta">
            {{ order.dueText }}
          </view>

          <view v-if="order.returnDeliveryMethod" class="rentals-item__meta">
            还书方式：{{ deliveryLabel(order.returnDeliveryMethod) }}
          </view>

          <view v-if="order.damageReport" class="rentals-report-status">
            {{ order.damageReport.status === 'pending' ? '已提交报损，等待管理员鉴定' : '已完成报损鉴定' }}
          </view>

          <button
            v-if="order.status === '待确认收货'"
            class="rentals-item__button primary-button"
            @click="handleConfirmReceipt(order.id)"
          >
            确认收货
          </button>

          <button
            v-if="order.canInitiateReturn"
            class="rentals-item__button primary-button"
            @click="goReturn(order.id)"
          >
            发起还书
          </button>

          <view v-if="order.status === '等待发货'" class="rentals-item__hint">
            等待卖家发货
          </view>
          <view v-else-if="!order.canInitiateReturn && !order.damageReport && order.status !== '已归还' && order.status !== '待确认收货'" class="rentals-item__hint">
            如需报损，请联系卖家提交鉴定。
          </view>
        </view>
      </view>

      <!-- owned -->
      <view v-if="tab === 'owned'" class="card-panel rentals-list">
        <view v-if="ownedOrders.length === 0" class="rentals-empty">
          暂无租出记录
        </view>
        <view v-for="order in ownedOrders" :key="order.id" class="rentals-item">
          <view class="rentals-item__head">
            <view class="rentals-item__title">
              {{ order.title }}
            </view>
            <view class="rentals-item__status" :class="{ 'rentals-item__status--warning': order.damageReport?.status === 'pending' }">
              {{ order.status }}
            </view>
          </view>
          <view class="rentals-item__meta">
            租期 {{ order.period }} · 押金 ￥{{ order.deposit }}
          </view>
          <view class="rentals-item__meta">
            租客：{{ order.borrowerName }} · {{ order.dueText }}
          </view>

          <view v-if="order.returnDeliveryMethod" class="rentals-item__meta">
            还书方式：{{ deliveryLabel(order.returnDeliveryMethod) }}
          </view>

          <view v-if="order.damageReport" class="rentals-report-status">
            {{ order.damageReport.status === 'pending' ? '已提交报损，等待管理员鉴定' : '已完成报损鉴定' }}
          </view>

          <view class="rentals-item__actions">
            <button
              v-if="order.canConfirmReturn"
              class="rentals-item__button primary-button"
              @click="handleConfirmReturn(order.id)"
            >
              确认还书
            </button>
            <button
              v-if="order.canReportDamage"
              class="rentals-item__button rentals-item__button--outline"
              @click="goReport(order.id)"
            >
              提交报损
            </button>
          </view>
        </view>
      </view>
      <CustomTabBar current="mine" />
    </view>
  </view>
</template>

<style lang="scss" scoped>
.rentals-page {
  min-height: 100vh;
  padding-top: calc(var(--safe-top) + 20rpx);
}

.rentals-header {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;

  &__body {
    flex: 1;
    min-width: 0;
  }

  &__title {
    color: #111111;
    font-size: 38rpx;
    font-weight: 700;
  }

  &__sub {
    margin-top: 10rpx;
    color: #6d6d67;
    font-size: 24rpx;
    line-height: 1.6;
  }
}

.rentals-back {
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  border: none;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.94);
  box-shadow:
    0 4rpx 12rpx rgba(17, 17, 17, 0.04),
    0 14rpx 30rpx rgba(17, 17, 17, 0.08);

  &::after {
    border: none;
  }

  &__icon {
    width: 30rpx;
    height: 30rpx;
  }

  &--floating {
    position: fixed;
    top: calc(var(--safe-top) + 20rpx);
    left: 24rpx;
    z-index: 20;
  }

  &--placeholder {
    visibility: hidden;
  }
}

.rentals-topbar {
  position: fixed;
  top: calc(var(--safe-top) + 20rpx);
  left: 24rpx;
  right: 24rpx;
  z-index: 20;
  height: 72rpx;
  display: flex;
  align-items: center;
  border: 1rpx solid rgba(255, 255, 255, 0.32);
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.78);
  box-shadow:
    0 4rpx 12rpx rgba(17, 17, 17, 0.04),
    0 12rpx 28rpx rgba(17, 17, 17, 0.06);
  backdrop-filter: blur(18rpx);

  &__back {
    width: 72rpx;
    height: 72rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0;
    padding: 0;
    border: none;
    border-radius: 999rpx;
    background: transparent;

    &::after {
      border: none;
    }
  }

  &__icon {
    width: 30rpx;
    height: 30rpx;
  }

  &__title {
    flex: 1;
    padding-right: 28rpx;
    color: #111111;
    font-size: 26rpx;
    font-weight: 600;
    text-align: center;
  }
}

.rentals-tabs {
  display: flex;
  gap: 0;
  margin-top: 24rpx;
  background: #edece5;
  border-radius: 24rpx;
  padding: 6rpx;
}

.rentals-tab {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  border-radius: 20rpx;
  font-size: 24rpx;
  color: #6d6d67;
  transition: all 0.2s ease;

  &--active {
    background: rgba(255, 255, 255, 0.94);
    color: #111111;
    font-weight: 600;
    box-shadow:
      0 2rpx 8rpx rgba(17, 17, 17, 0.04),
      0 6rpx 16rpx rgba(17, 17, 17, 0.06);
  }
}

.rentals-list {
  margin-top: 24rpx;
  padding: 28rpx;
}

.rentals-empty {
  text-align: center;
  color: #8a8a84;
  font-size: 24rpx;
  padding: 40rpx 0;
}

.rentals-item {
  padding: 24rpx 0;
  border-bottom: 1rpx solid rgba(17, 17, 17, 0.06);
  transition: opacity 0.15s ease;

  &:last-child {
    border-bottom: none;
  }

  &__head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16rpx;
  }

  &__title {
    color: #111111;
    font-size: 30rpx;
    font-weight: 700;
  }

  &__status {
    padding: 8rpx 16rpx;
    border-radius: 999rpx;
    background: linear-gradient(135deg, #1a1a1a, #272727);
    color: #fff;
    font-size: 20rpx;
    font-weight: 500;
    white-space: nowrap;

    &--warning {
      background: linear-gradient(135deg, #9b6b22, #b87a29);
    }
  }

  &__meta {
    margin-top: 10rpx;
    color: #71716a;
    font-size: 22rpx;
  }

  &__actions {
    display: flex;
    gap: 16rpx;
    margin-top: 16rpx;
  }

  &__button {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 60rpx;
    min-height: 60rpx;
    padding: 0 28rpx;
    line-height: 1;
    font-size: 22rpx;
    flex: 1;

    &::after {
      border: none;
    }

    &--outline {
      background: #fff;
      color: #111111;
      border: 2rpx solid #111111;
    }
  }

  &__hint {
    margin-top: 16rpx;
    color: #7a6541;
    font-size: 22rpx;
    line-height: 1.5;
  }
}

.rentals-report-status {
  margin-top: 16rpx;
  color: #7a6541;
  font-size: 22rpx;
}
</style>
