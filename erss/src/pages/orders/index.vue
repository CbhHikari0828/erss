<script setup>
import { useOrderStore } from '@/store/order/index.js'

const orderStore = useOrderStore()
const activeType = ref('all')

const typeTabs = [
  { key: 'all', label: '全部' },
  { key: 'purchase', label: '购买' },
  { key: 'rental', label: '租赁' },
]

const orders = computed(() => {
  if (activeType.value === 'all')
    return orderStore.buyerOrders

  return orderStore.buyerOrders.filter(item => item.orderType === activeType.value)
})

function goBack() {
  uni.navigateBack({ delta: 1 })
}

async function confirmReceipt(order) {
  await orderStore.confirmReceipt(order.id)

  uni.showToast({
    title: '已确认收货',
    icon: 'success',
  })
}

onShow(() => {
  orderStore.refreshOrders().catch(() => {})
  orderStore.refreshAutoConfirmedOrders()
})
</script>

<template>
  <view class="app-shell orders-page">
    <view class="orders-head">
      <button class="orders-back" @click="goBack">
        <image class="orders-back__icon" src="/static/images/common/back.svg" mode="aspectFit" />
      </button>
      <view class="orders-head__body">
        <view class="orders-head__title">
          我的订单
        </view>
        <view class="orders-head__sub">
          买家付款后先由系统托管，确认收货或超时 15 天后才会结算给卖家
        </view>
      </view>
    </view>

    <view class="orders-tabs">
      <view
        v-for="tab in typeTabs"
        :key="tab.key"
        class="orders-tabs__item"
        :class="{ 'orders-tabs__item--active': activeType === tab.key }"
        @click="activeType = tab.key"
      >
        {{ tab.label }}
      </view>
    </view>

    <view v-if="orders.length" class="orders-list">
      <view v-for="order in orders" :key="order.id" class="card-panel orders-item">
        <view class="orders-item__head">
          <view class="orders-item__title">
            {{ order.title }}
          </view>
          <view class="orders-item__status">
            {{ order.status }}
          </view>
        </view>
        <view class="orders-item__meta">
          {{ order.orderType === 'rental' ? `租期 ${order.rentalPeriod}` : '直接购买' }} · {{ order.deliveryMethod === 'pickup' ? '上门自提' : '快递驿站取件' }}
        </view>
        <view class="orders-item__meta">
          支付方式 {{ orderStore.paymentOptions[order.paymentMethod] }} · 合计 ￥{{ orderStore.formatPrice(order.totalAmount) }}
        </view>
        <view class="orders-item__meta">
          {{ order.statusText }}
        </view>
        <view class="orders-item__meta">
          {{ order.escrowStatus }}
        </view>
        <button v-if="order.status === '待确认收货' && order.orderType !== 'rental'" class="primary-button orders-item__button" @click="confirmReceipt(order)">
          确认收货
        </button>
      </view>
    </view>

    <view v-else class="card-panel orders-empty">
      暂无订单，去商品详情页发起购买或租赁吧。
    </view>
  </view>
</template>

<style lang="scss" scoped>
.orders-head {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;
}
.orders-head__body {
  flex: 1;
}
.orders-head__title {
  font-size: 40rpx;
  font-weight: 700;
  color: #111111;
}
.orders-head__sub {
  margin-top: 10rpx;
  color: #6f6f69;
  font-size: 24rpx;
  line-height: 1.6;
}
.orders-back {
  width: 72rpx;
  height: 72rpx;
  flex: none;
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
}
.orders-back::after {
  border: none;
}
.orders-back__icon {
  width: 30rpx;
  height: 30rpx;
}
.orders-tabs {
  display: flex;
  gap: 14rpx;
  margin-top: 24rpx;
}
.orders-tabs__item {
  padding: 14rpx 24rpx;
  border-radius: 999rpx;
  background: #eceae4;
  color: #5f5f59;
  font-size: 23rpx;
  transition: all 0.2s ease;
}
.orders-tabs__item--active {
  background: linear-gradient(135deg, #1a1a1a, #2d2d2d);
  color: #ffffff;
  box-shadow: 0 4rpx 12rpx rgba(17, 17, 17, 0.12);
}
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
  margin-top: 18rpx;
}
.orders-item {
  padding: 28rpx;
}
.orders-item__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}
.orders-item__title {
  flex: 1;
  color: #111111;
  font-size: 30rpx;
  line-height: 1.5;
  font-weight: 700;
}
.orders-item__status {
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #1a1a1a, #272727);
  color: #ffffff;
  font-size: 20rpx;
  font-weight: 500;
}
.orders-item__meta {
  margin-top: 12rpx;
  color: #6f6f69;
  font-size: 23rpx;
  line-height: 1.6;
}
.orders-item__button {
  margin-top: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 0;
  border: none;
  line-height: 82rpx;
}
.orders-item__button::after {
  border: none;
}
.orders-empty {
  margin-top: 20rpx;
  padding: 40rpx 28rpx;
  color: #6f6f69;
  font-size: 24rpx;
  line-height: 1.7;
  text-align: center;
}
</style>
