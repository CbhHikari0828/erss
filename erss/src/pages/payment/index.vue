<script setup>
import { useOrderStore } from '@/store/order/index.js'
import { useUserStore } from '@/store/user/index.js'

const router = useRouter()
const orderStore = useOrderStore()
const userStore = useUserStore()
const selectedPayment = ref('wechat')
const submitting = ref(false)

const paymentOptions = computed(() => [
  { key: 'wechat', label: '微信支付', desc: '模拟调起微信收银台，确认后直接进入系统托管。' },
  { key: 'alipay', label: '支付宝支付', desc: '模拟调起支付宝收银台，确认后直接进入系统托管。' },
  { key: 'campus', label: '校园卡支付', desc: '模拟校园一卡通扣费，确认后直接进入系统托管。' },
  { key: 'wallet', label: '系统钱包', desc: '直接扣除你的系统余额，支付成功后进入托管。' },
])
const draft = computed(() => orderStore.checkoutDraft)
const walletBalance = computed(() => Number(userStore.userInfo.walletBalance || 0))

function goBack() {
  uni.navigateBack({ delta: 1 })
}

function ensureBookAvailable() {
  const bookId = draft.value?.bookId
  if (!bookId || !orderStore.isBookUnavailable(bookId))
    return true

  orderStore.clearCheckoutDraft()
  uni.showToast({
    title: '该书籍已售，无法继续购买',
    icon: 'none',
  })
  setTimeout(() => {
    uni.reLaunch({ url: `/detail?id=${bookId}` })
  }, 180)
  return false
}

async function submitPayment() {
  if (submitting.value)
    return

  if (!ensureBookAvailable())
    return

  submitting.value = true
  const bookId = draft.value?.bookId || ''
  try {
    const orderId = await orderStore.submitOrder(selectedPayment.value)
    if (!orderId)
      return

    const channelText = selectedPayment.value === 'wechat'
      ? '微信支付'
      : selectedPayment.value === 'alipay'
        ? '支付宝支付'
        : selectedPayment.value === 'campus'
          ? '校园卡支付'
          : '系统钱包支付'

    uni.showToast({
      title: `${channelText}成功`,
      icon: 'success',
    })

    setTimeout(() => {
      uni.reLaunch({ url: `/detail?id=${bookId}` })
    }, 220)
  }
  catch (error) {
    uni.showToast({
      title: error?.message || '支付失败',
      icon: 'none',
    })
  }
  finally {
    submitting.value = false
  }
}

onShow(() => {
  ensureBookAvailable()
})
</script>

<template>
  <view class="app-shell payment-page">
    <view class="payment-head">
      <button class="payment-back" @click="goBack">
        <image class="payment-back__icon" src="/static/images/common/back.svg" mode="aspectFit" />
      </button>
      <view class="payment-head__body">
        <view class="payment-head__title">
          选择付款方式
        </view>
        <view class="payment-head__sub">
          支付后订单资金先进入系统托管，买家确认收货后再由管理员发放到卖家钱包
        </view>
      </view>
    </view>

    <view v-if="draft" class="card-panel payment-amount">
      <view class="payment-amount__label">
        应付金额
      </view>
      <view class="payment-amount__value">
        ￥{{ orderStore.formatPrice(draft.totalAmount) }}
      </view>
      <view class="payment-amount__sub">
        你的系统钱包余额 ￥{{ orderStore.formatPrice(walletBalance) }}
      </view>
    </view>

    <view class="payment-list">
      <view
        v-for="item in paymentOptions"
        :key="item.key"
        class="card-panel payment-item"
        :class="{ 'payment-item--active': selectedPayment === item.key }"
        @click="selectedPayment = item.key"
      >
        <view class="payment-item__body">
          <view class="payment-item__title">
            {{ item.label }}
          </view>
          <view class="payment-item__sub">
            {{ item.desc }}
          </view>
        </view>
        <view class="payment-item__status">
          {{ selectedPayment === item.key ? '已选择' : '点击选择' }}
        </view>
      </view>
    </view>

    <button
      class="primary-button payment-submit"
      :disabled="submitting || (selectedPayment === 'wallet' && walletBalance < Number(draft?.totalAmount || 0))"
      @click="submitPayment"
    >
      {{ submitting ? '处理中...' : (selectedPayment === 'wallet' && walletBalance < Number(draft?.totalAmount || 0) ? '余额不足' : '确认支付') }}
    </button>
  </view>
</template>

<style lang="scss" scoped>
.payment-head {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;
}

.payment-head__body {
  flex: 1;
}

.payment-head__title {
  font-size: 40rpx;
  font-weight: 700;
  color: #111111;
}

.payment-head__sub {
  margin-top: 10rpx;
  color: #6f6f69;
  font-size: 24rpx;
  line-height: 1.6;
}

.payment-back {
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

.payment-back::after {
  border: none;
}

.payment-back__icon {
  width: 30rpx;
  height: 30rpx;
}

.payment-amount {
  margin-top: 24rpx;
  padding: 34rpx 28rpx;
  text-align: center;
}

.payment-amount__label {
  color: #6f6f69;
  font-size: 24rpx;
}

.payment-amount__value {
  margin-top: 14rpx;
  color: #111111;
  font-size: 52rpx;
  font-weight: 700;
}

.payment-amount__sub {
  margin-top: 12rpx;
  color: #6f6f69;
  font-size: 23rpx;
}

.payment-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
  margin-top: 24rpx;
}

.payment-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx;
}

.payment-item--active {
  border-color: rgba(17, 17, 17, 0.22);
  box-shadow: 0 18rpx 40rpx rgba(17, 17, 17, 0.08);
}

.payment-item__body {
  flex: 1;
}

.payment-item__title {
  color: #111111;
  font-size: 28rpx;
  font-weight: 700;
}

.payment-item__sub {
  margin-top: 8rpx;
  color: #6f6f69;
  font-size: 22rpx;
  line-height: 1.5;
}

.payment-item__status {
  color: #6f6f69;
  font-size: 23rpx;
  flex: none;
}

.payment-submit {
  margin-top: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 0;
  border: none;
  line-height: 88rpx;
}

.payment-submit::after {
  border: none;
}

.payment-submit:disabled {
  opacity: 0.56;
}
</style>
