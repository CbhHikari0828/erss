<script setup>
import { useRentalStore } from '@/store/rental/index.js'

const route = useRoute()
const router = useRouter()
const rentalStore = useRentalStore()

const selectedMethod = ref('pickup')
const submitting = ref(false)

const order = computed(() => rentalStore.rentalOrders.find(item => item.id === route.query.id))

const deliveryOptions = computed(() => [
  { key: 'pickup', label: '上门自提', desc: '当面归还给卖家，无需额外费用。', fee: '免费' },
  { key: 'station', label: '快递驿站还书', desc: '归还至快递驿站由卖家取件，需3元服务费。', fee: '3元' },
])

function goBack() {
  uni.navigateBack({ delta: 1 })
}

function validateReturnPermission() {
  if (submitting.value) return

  if (!order.value) return

  if (!order.value.canInitiateReturn) {
    uni.showToast({
      title: order.value.ownedByMe ? '只有买家可以发起还书' : '当前状态不能发起还书',
      icon: 'none',
    })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/rentals/index' })
    }, 180)
  }
}

watch(order, validateReturnPermission, { immediate: true })

onShow(() => {
  rentalStore.refreshRentals().catch(() => {})
})

async function submit() {
  if (submitting.value) return

  if (!order.value?.canInitiateReturn) {
    uni.showToast({ title: '当前状态不能还书', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    await rentalStore.initiateReturn(order.value.id, selectedMethod.value)
    uni.showToast({ title: '还书请求已发出', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/rentals/index' })
    }, 220)
  } catch (err) {
    uni.showToast({ title: err?.message || '还书失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <view class="app-shell return-page">
    <view class="return-head">
      <button class="return-back" @click="goBack">
        <image class="return-back__icon" src="/static/images/common/back.svg" mode="aspectFit" />
      </button>
      <view class="return-head__body">
        <view class="return-head__title">
          还书方式
        </view>
        <view class="return-head__sub">
          选择归还方式，卖家确认后即可结算
        </view>
      </view>
    </view>

    <view v-if="order" class="card-panel return-order">
      <view class="return-order__title">
        {{ order.title }}
      </view>
      <view class="return-order__meta">
        <text>租期 {{ order.period }}</text>
        <text>押金 ￥{{ order.deposit }}</text>
      </view>
      <view class="return-order__meta">
        {{ order.dueText }}
      </view>
    </view>

    <view class="return-list">
      <view
        v-for="item in deliveryOptions"
        :key="item.key"
        class="card-panel return-item"
        :class="{ 'return-item--active': selectedMethod === item.key }"
        @click="selectedMethod = item.key"
      >
        <view class="return-item__body">
          <view class="return-item__title">
            {{ item.label }}
          </view>
          <view class="return-item__sub">
            {{ item.desc }}
          </view>
        </view>
        <view class="return-item__fee">
          {{ item.fee }}
        </view>
      </view>
    </view>

    <button
      class="primary-button return-submit"
      :disabled="submitting"
      @click="submit"
    >
      {{ submitting ? '提交中...' : '确认还书' }}
    </button>
  </view>
</template>

<style lang="scss" scoped>
.return-page {
  min-height: 100vh;
  padding-top: calc(var(--safe-top) + 20rpx);
  padding-bottom: calc(var(--safe-bottom) + 120rpx);
}

.return-head {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;
}

.return-head__body {
  flex: 1;
}

.return-head__title {
  font-size: 40rpx;
  font-weight: 700;
  color: #111111;
}

.return-head__sub {
  margin-top: 10rpx;
  color: #6f6f69;
  font-size: 24rpx;
  line-height: 1.6;
}

.return-back {
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

.return-back::after {
  border: none;
}

.return-back__icon {
  width: 30rpx;
  height: 30rpx;
}

.return-order {
  margin-top: 24rpx;
  padding: 28rpx;
}

.return-order__title {
  color: #111111;
  font-size: 32rpx;
  font-weight: 700;
}

.return-order__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx 18rpx;
  margin-top: 12rpx;
  color: #6d6d67;
  font-size: 22rpx;
}

.return-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
  margin-top: 24rpx;
}

.return-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx;
}

.return-item--active {
  border-color: rgba(17, 17, 17, 0.22);
  box-shadow: 0 18rpx 40rpx rgba(17, 17, 17, 0.08);
}

.return-item__body {
  flex: 1;
}

.return-item__title {
  color: #111111;
  font-size: 28rpx;
  font-weight: 700;
}

.return-item__sub {
  margin-top: 8rpx;
  color: #6f6f69;
  font-size: 22rpx;
  line-height: 1.5;
}

.return-item__fee {
  color: #6f6f69;
  font-size: 24rpx;
  flex: none;
  margin-left: 16rpx;
}

.return-submit {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: calc(var(--safe-bottom) + 20rpx);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  border: none;
  line-height: 88rpx;
  box-shadow: 0 16rpx 34rpx rgba(17, 17, 17, 0.12);

  &::after {
    border: none;
  }
}

.return-submit:disabled {
  opacity: 0.56;
}
</style>
