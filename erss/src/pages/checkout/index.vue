<script setup>
import { ref, watch } from 'vue'
import { useCatalogStore } from '@/store/catalog/index.js'
import { createCheckoutDraft, useOrderStore } from '@/store/order/index.js'

const route = useRoute()
const router = useRouter()
const catalogStore = useCatalogStore()
const orderStore = useOrderStore()

const deliveryOptions = computed(() => Object.entries(orderStore.deliveryOptions).map(([key, value]) => ({
  key,
  ...value,
})))

const PLACEHOLDER = '/static/images/common/placeholder.png'
const draft = computed(() => orderStore.checkoutDraft)
const isRental = computed(() => draft.value?.orderType === 'rental')
const coverSrc = ref('')

watch(draft, (val) => {
  coverSrc.value = val?.cover || ''
})

function onCoverError() {
  coverSrc.value = PLACEHOLDER
}

function goBack() {
  uni.navigateBack({
    delta: 1,
  })
}

function initDraft() {
  if (draft.value) {
    ensureBookAvailable(draft.value.bookId)
    return
  }

  const bookId = route.query.id
  if (!bookId)
    return

  const book = catalogStore.getBookById(bookId)
  if (!book) {
    catalogStore.refreshBooks().then(() => {
      const nextBook = catalogStore.getBookById(bookId)
      if (nextBook && ensureBookAvailable(bookId)) {
        orderStore.setCheckoutDraft(createCheckoutDraft(nextBook, route.query.type || 'purchase', {
          rentalPeriod: route.query.period,
        }))
      }
    })
    return
  }

  if (!ensureBookAvailable(bookId))
    return

  orderStore.setCheckoutDraft(createCheckoutDraft(book, route.query.type || 'purchase', {
    rentalPeriod: route.query.period,
  }))
}

function ensureBookAvailable(bookId) {
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

function selectDelivery(key) {
  orderStore.updateCheckoutDelivery(key)
}

function goPay() {
  if (!draft.value || !ensureBookAvailable(draft.value.bookId))
    return

  router.push('/payment')
}

onShow(() => {
  initDraft()
})
</script>

<template>
  <view class="app-shell checkout-page">
    <view class="checkout-head">
      <button class="checkout-back" @click="goBack">
        <image class="checkout-back__icon" src="/static/images/common/back.svg" mode="aspectFit" />
      </button>
      <view class="checkout-head__body">
        <view class="checkout-head__title">
          {{ isRental ? '租赁结算' : '确认订单' }}
        </view>
        <view class="checkout-head__sub">
          先选择收货方式，再进入支付页面选择付款方式
        </view>
      </view>
    </view>

    <view v-if="draft" class="card-panel checkout-book">
      <image class="checkout-book__cover" :src="coverSrc" mode="aspectFill" @error="onCoverError" />
      <view class="checkout-book__body">
        <view class="checkout-book__title">
          {{ draft.title }}
        </view>
        <view class="checkout-book__meta">
          卖家 {{ draft.sellerName }}
        </view>
        <view class="checkout-book__meta">
          {{ isRental ? `租期 ${draft.rentalPeriod}` : '直接购买' }}
        </view>
      </view>
    </view>

    <view class="section-title section-gap">
      <view>
        <view class="section-title__main">
          收货方式
        </view>
        <view class="section-title__sub">
          上门自提不加价，快递驿站取件加 ￥3，该费用归系统所有
        </view>
      </view>
    </view>

    <view class="delivery-list">
      <view
        v-for="item in deliveryOptions"
        :key="item.key"
        class="card-panel delivery-item"
        :class="{ 'delivery-item--active': draft?.deliveryMethod === item.key }"
        @click="selectDelivery(item.key)"
      >
        <view class="delivery-item__body">
          <view class="delivery-item__title">
            {{ item.label }}
          </view>
          <view class="delivery-item__sub">
            {{ item.fee ? `配送加价 ￥${item.fee}` : '无需额外费用' }}
          </view>
        </view>
        <view class="delivery-item__side">
          <view class="delivery-item__price">
            {{ item.fee ? `+￥${item.fee}` : '免费' }}
          </view>
          <view class="delivery-item__check" :class="{ 'delivery-item__check--active': draft?.deliveryMethod === item.key }">
            <view v-if="draft?.deliveryMethod === item.key" class="delivery-item__check-dot"></view>
          </view>
        </view>
      </view>
    </view>

    <view v-if="draft" class="card-panel checkout-price">
      <view class="checkout-price__row">
        <text>{{ isRental ? '租赁小计' : '商品小计' }}</text>
        <text>￥{{ orderStore.formatPrice(draft.subtotal) }}</text>
      </view>
      <view v-if="isRental" class="checkout-price__row">
        <text>其中押金</text>
        <text>￥{{ orderStore.formatPrice(draft.depositAmount) }}</text>
      </view>
      <view class="checkout-price__row">
        <text>收货加价</text>
        <text>￥{{ orderStore.formatPrice(draft.deliveryFee) }}</text>
      </view>
      <view class="checkout-price__row checkout-price__row--total">
        <text>应付合计</text>
        <text>￥{{ orderStore.formatPrice(draft.totalAmount) }}</text>
      </view>
    </view>

    <view class="card-panel checkout-tip">
      付款后资金不会直接打给商家，而是先由系统托管。买家确认收货后再结算给卖家；超过 15 天未确认收货将自动确认。
    </view>

    <button class="primary-button checkout-submit" @click="goPay">
      去选择付款方式
    </button>
  </view>
</template>

<style lang="scss" scoped>
.checkout-page {
  min-height: 100vh;
}
.checkout-head {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;
}
.checkout-head__body {
  flex: 1;
}
.checkout-head__title {
  font-size: 40rpx;
  font-weight: 700;
  color: #111111;
}
.checkout-head__sub {
  margin-top: 10rpx;
  color: #6f6f69;
  font-size: 24rpx;
  line-height: 1.6;
}
.checkout-back {
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
.checkout-back::after {
  border: none;
}
.checkout-back__icon {
  width: 30rpx;
  height: 30rpx;
}
.section-gap {
  margin-top: 28rpx;
}
.checkout-book,
.checkout-price,
.checkout-tip {
  margin-top: 24rpx;
  padding: 28rpx;
}
.checkout-book {
  display: flex;
  gap: 18rpx;
}
.checkout-book__cover {
  width: 128rpx;
  height: 164rpx;
  border-radius: 20rpx;
  background: #ece8df;
  flex: none;
}
.checkout-book__body {
  flex: 1;
  min-width: 0;
}
.checkout-book__title {
  font-size: 30rpx;
  line-height: 1.5;
  font-weight: 700;
  color: #111111;
}
.checkout-book__meta {
  margin-top: 10rpx;
  color: #71716a;
  font-size: 23rpx;
}
.delivery-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
  margin-top: 18rpx;
}
.delivery-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 26rpx 28rpx;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;
}
.delivery-item--active {
  border-color: #111111;
  background: linear-gradient(135deg, #fffdf8 0%, #f3eee4 100%);
  box-shadow: 0 18rpx 40rpx rgba(17, 17, 17, 0.12);
  transform: translateY(-2rpx);
}
.delivery-item__body {
  flex: 1;
  min-width: 0;
}
.delivery-item__title {
  color: #111111;
  font-size: 28rpx;
  font-weight: 700;
}
.delivery-item__sub,
.delivery-item__price {
  margin-top: 8rpx;
  color: #6f6f69;
  font-size: 23rpx;
}
.delivery-item--active .delivery-item__sub {
  color: #3f3f39;
}
.delivery-item__side {
  display: flex;
  align-items: center;
  gap: 18rpx;
  margin-left: 18rpx;
}
.delivery-item__price {
  margin-top: 0;
  color: #111111;
  font-weight: 600;
}
.delivery-item__check {
  width: 36rpx;
  height: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid rgba(17, 17, 17, 0.18);
  border-radius: 999rpx;
  background: #ffffff;
}
.delivery-item__check--active {
  border-color: #111111;
  background: #111111;
}
.delivery-item__check-dot {
  width: 14rpx;
  height: 14rpx;
  border-radius: 999rpx;
  background: #ffffff;
}
.checkout-price__row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14rpx 0;
  color: #575751;
  font-size: 24rpx;
}
.checkout-price__row--total {
  margin-top: 8rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid rgba(17, 17, 17, 0.08);
  color: #111111;
  font-size: 28rpx;
  font-weight: 700;
}
.checkout-tip {
  color: #696963;
  font-size: 23rpx;
  line-height: 1.7;
}
.checkout-submit {
  margin-top: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 0;
  border: none;
  line-height: 88rpx;
}
.checkout-submit::after {
  border: none;
}
</style>
