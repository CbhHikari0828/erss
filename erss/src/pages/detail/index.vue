<script setup>
import { onPageScroll, onShow } from '@dcloudio/uni-app'
import { computed, reactive, ref } from 'vue'
import { useCatalogStore } from '@/store/catalog/index.js'
import { createCheckoutDraft, useOrderStore } from '@/store/order/index.js'
import { useUserStore } from '@/store/user/index.js'
import { ensureLogin } from '@/utils/auth.js'

const route = useRoute()
const router = useRouter()
const catalogStore = useCatalogStore()
const orderStore = useOrderStore()
const userStore = useUserStore()

const activeIndex = ref(0)
const showTopBackBar = ref(false)
const loadingBook = ref(false)
const rentForm = reactive({ days: '7' })

const rentDays = computed(() => {
  const n = Number.parseInt(rentForm.days, 10)
  return Number.isNaN(n) || n < 1 ? 1 : n > 365 ? 365 : n
})

function onRentDaysBlur() {
  const raw = rentForm.days.trim()
  if (!raw || !/^\d+$/.test(raw)) {
    rentForm.days = '7'
    return
  }
  const n = Number.parseInt(raw, 10)
  if (n < 1) rentForm.days = '1'
  else if (n > 365) rentForm.days = '365'
  else rentForm.days = String(n)
}

const TOP_BAR_THRESHOLD = 300

const book = ref(null)
const topBarTitle = computed(() => book.value?.title || '')
const coverImages = computed(() => book.value?.images || [])
const displayImages = computed(() => coverImages.value.length ? coverImages.value : [book.value?.cover].filter(Boolean))

const PLACEHOLDER = '/static/images/common/placeholder.png'
const erroredImages = reactive(new Set())

function gallerySrc(image) {
  return erroredImages.has(image) ? PLACEHOLDER : image
}

function onGalleryError(image) {
  erroredImages.add(image)
}
const currentOrder = computed(() => orderStore.getLatestOrderByBookId(book.value?.id))
const isSold = computed(() => orderStore.isBookUnavailable(book.value?.id))
const isMyBook = computed(() => {
  if (!book.value || !userStore.userInfo) return false
  const userId = userStore.userInfo.id ?? userStore.userInfo.userId
  return book.value.sellerId != null && String(book.value.sellerId) === String(userId)
})
const isRentalEnabled = computed(() => Boolean(book.value?.rentalEnabled))

const rentInfo = computed(() => {
  return {
    dailyRent: Number(book.value?.rentPrice || 0),
    deposit: Number(book.value?.rentalDeposit || book.value?.deposit || 0),
    returnRule: '付款后资金先由平台暂存，卖家发货后进入履约流程；买家确认收货后再结算给卖家，超时 15 天自动确认。',
  }
})

function goBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack({ delta: 1 })
    return
  }

  uni.reLaunch({
    url: '/pages/index/index',
  })
}

function requireAction(onAuthed) {
  ensureLogin(userStore, () => {
    onAuthed?.()
  }, {
    content: '该操作需要登录后才能使用，是否现在前往登录？',
    onConfirm: () => router.push('/login'),
  })
}

function collectBook() {
  requireAction(() => {
    uni.showToast({
      title: '已加入收藏',
      icon: 'success',
    })
  })
}

function rentBook() {
  if (isSold.value || !book.value || !isRentalEnabled.value)
    return

  const raw = rentForm.days.trim()
  if (!raw) {
    uni.showToast({ title: '请输入租赁天数', icon: 'none' })
    return
  }
  if (!/^\d+$/.test(raw)) {
    uni.showToast({ title: '请输入正整数', icon: 'none' })
    return
  }
  const days = Number.parseInt(raw, 10)
  if (days < 1 || days > 365) {
    uni.showToast({ title: '租赁天数需在1～365之间', icon: 'none' })
    return
  }

  requireAction(() => {
    orderStore.setCheckoutDraft(createCheckoutDraft(book.value, 'rental', {
      rentalPeriod: `${rentDays.value}天`,
    }))
    router.push('/checkout')
  })
}

function buyBook() {
  if (isSold.value || !book.value)
    return

  requireAction(() => {
    orderStore.setCheckoutDraft(createCheckoutDraft(book.value, 'purchase'))
    router.push('/checkout')
  })
}

function viewOrder() {
  router.push('/orders')
}

async function loadBook() {
  if (!route.query.id) {
    book.value = null
    return
  }

  loadingBook.value = true
  try {
    book.value = await catalogStore.getBook(route.query.id)
  }
  finally {
    loadingBook.value = false
  }
}

onPageScroll(({ scrollTop }) => {
  showTopBackBar.value = scrollTop >= TOP_BAR_THRESHOLD
})

onShow(() => {
  catalogStore.refreshBooks().catch(() => {})
  orderStore.refreshAutoConfirmedOrders()
  loadBook().catch(() => {})
})
</script>

<template>
  <view class="detail-page">
    <view class="detail-nav" :class="{ 'detail-nav--active': showTopBackBar }">
      <button class="detail-nav__back" @click="goBack">
        <image class="detail-nav__icon" src="/static/images/common/back.svg" mode="aspectFit" />
      </button>
      <view class="detail-nav__title">
        {{ topBarTitle }}
      </view>
    </view>

    <view v-if="loadingBook" class="detail-scroll">
      <view class="detail-panel card-panel">
        正在加载书籍详情...
      </view>
    </view>

    <view v-else-if="!book" class="detail-scroll">
      <view class="detail-panel card-panel">
        书籍不存在或已下架。
      </view>
    </view>

    <view v-else class="detail-scroll">
      <view class="detail-gallery">
        <swiper
          v-if="displayImages.length"
          class="detail-gallery__swiper"
          circular
          @change="activeIndex = $event.detail.current"
        >
          <swiper-item v-for="image in displayImages" :key="image">
            <image class="detail-gallery__image" :src="gallerySrc(image)" mode="aspectFill" @error="onGalleryError(image)" />
          </swiper-item>
        </swiper>
        <view v-else class="detail-gallery__empty"></view>
        <view v-if="displayImages.length" class="detail-gallery__indicator">
          {{ activeIndex + 1 }}/{{ displayImages.length }}
        </view>
      </view>

      <view class="detail-panel card-panel">
        <view class="detail-panel__price">
          ￥{{ book.price }}
          <text class="detail-panel__origin">
            ￥{{ book.originalPrice }}
          </text>
        </view>
        <view class="detail-panel__title">
          {{ book.title }}
        </view>
        <view class="detail-panel__chips">
          <text class="chip chip--active">
            {{ book.condition }}
          </text>
          <text v-if="isSold" class="chip detail-panel__sold">
            已售
          </text>
          <text class="chip">
            {{ book.campus }}
          </text>
          <text v-for="tag in book.tags" :key="tag" class="chip">
            {{ tag }}
          </text>
        </view>
      </view>

      <view class="detail-panel card-panel">
        <view class="seller-row">
          <view class="seller-row__avatar">
            {{ book.seller.slice(0, 1) }}
          </view>
          <view class="seller-row__body">
            <view class="seller-row__name">
              {{ book.seller }}
            </view>
            <view class="seller-row__meta">
              {{ book.sellerTag }} · 信用 {{ book.sellerRating }}
            </view>
          </view>
        </view>
      </view>

      <view v-if="isRentalEnabled" class="detail-panel card-panel">
        <view class="detail-block__title">
          租赁信息
        </view>
        <view class="rent-summary">
          <view class="rent-summary__item">
            <view class="rent-summary__label">
              日租金
            </view>
            <view class="rent-summary__value">
              ￥{{ rentInfo.dailyRent }}/天
            </view>
          </view>
          <view class="rent-summary__item">
            <view class="rent-summary__label">
              押金
            </view>
            <view class="rent-summary__value">
              ￥{{ rentInfo.deposit }}
            </view>
          </view>
        </view>
        <view class="detail-block__sub">
          租赁时长（天）
        </view>
        <view class="rent-days-row">
          <input
            v-model="rentForm.days"
            type="text"
            inputmode="numeric"
            placeholder="输入租赁天数"
          />
          <text class="rent-days-suffix">天</text>
        </view>
        <view class="detail-block__sub detail-block__sub--rule">
          结算说明
        </view>
        <view class="detail-block__text">
          {{ rentInfo.returnRule }}
        </view>
      </view>

      <view class="detail-panel card-panel">
        <view class="detail-block__title">
          书况描述
        </view>
        <view class="detail-block__text">
          {{ book.description }}
        </view>
      </view>

      <view v-if="isMyBook" class="detail-panel card-panel">
        <view class="detail-block__title">
          提示
        </view>
        <view class="detail-block__text">
          这是你自己发布的书籍，不能购买或租赁。
        </view>
      </view>

      <view v-if="isSold && currentOrder" class="detail-panel card-panel">
        <view class="detail-block__title">
          当前状态
        </view>
        <view class="detail-block__text">
          该商品已进入订单流程，当前状态为“{{ currentOrder.status }}”。此时不允许再次购买或租赁。
        </view>
      </view>
    </view>

    <view class="detail-action">
      <button class="ghost-button detail-action__minor" @click="collectBook">
        收藏
      </button>
      <button
        class="ghost-button detail-action__minor"
        :disabled="isMyBook || isSold || !isRentalEnabled"
        :class="{ 'detail-action__minor--disabled': isMyBook || isSold || !isRentalEnabled }"
        @click="rentBook"
      >
        {{ isRentalEnabled ? '租赁' : '暂不租赁' }}
      </button>
      <button
        class="primary-button detail-action__major"
        :disabled="isMyBook"
        :class="{ 'detail-action__major--disabled': isMyBook || isSold }"
        @click="isSold ? viewOrder() : buyBook()"
      >
        {{ isMyBook ? '我的发布' : (isSold ? '查看订单' : '立即购买') }}
      </button>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  padding-bottom: calc(var(--safe-bottom) + 148rpx);
  background: #f5f5f1;
}

.detail-scroll {
  min-height: 100vh;
}

.detail-gallery {
  position: relative;

  &__swiper,
  &__image {
    width: 100%;
    height: 620rpx;
  }

  &__indicator {
    position: absolute;
    right: 24rpx;
    bottom: 24rpx;
    padding: 10rpx 18rpx;
    border-radius: 999rpx;
    background: rgba(17, 17, 17, 0.55);
    color: #ffffff;
    font-size: 22rpx;
  }

  &__empty {
    width: 100%;
    height: 620rpx;
    background: #eceae4;
  }
}

.detail-nav {
  position: fixed;
  top: calc(var(--safe-top) + 20rpx);
  left: 24rpx;
  z-index: 60;
  width: 76rpx;
  height: 76rpx;
  display: flex;
  align-items: center;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 12rpx 28rpx rgba(17, 17, 17, 0.12);
  backdrop-filter: blur(16rpx);
  overflow: hidden;
  transition:
    width 180ms ease,
    background-color 180ms ease,
    border-color 180ms ease,
    box-shadow 180ms ease;

  &--active {
    width: calc(100vw - 48rpx);
    border: 1rpx solid rgba(255, 255, 255, 0.5);
    background: rgba(255, 255, 255, 0.38);
    box-shadow:
      0 12rpx 28rpx rgba(17, 17, 17, 0.08),
      inset 0 1rpx 0 rgba(255, 255, 255, 0.55);
    backdrop-filter: blur(24rpx) saturate(140%);
  }

  &__back {
    width: 76rpx;
    height: 76rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0;
    padding: 0;
    border: none;
    border-radius: 999rpx;
    background: transparent;
    flex: none;

    &::after {
      border: none;
    }
  }

  &__icon {
    width: 34rpx;
    height: 34rpx;
  }

  &__title {
    flex: 1;
    padding-right: 28rpx;
    color: #111111;
    font-size: 26rpx;
    font-weight: 600;
    text-align: center;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    pointer-events: none;
    opacity: 0;
    transition: opacity 140ms ease;
  }

  &--active &__title {
    opacity: 1;
  }
}

.detail-panel {
  margin: 22rpx 24rpx 0;
  padding: 28rpx;

  &__price {
    color: #111111;
    font-size: 46rpx;
    font-weight: 700;
  }

  &__origin {
    margin-left: 12rpx;
    color: #8a8a84;
    font-size: 24rpx;
    text-decoration: line-through;
  }

  &__title {
    margin-top: 16rpx;
    color: #111111;
    font-size: 36rpx;
    line-height: 1.4;
    font-weight: 700;
  }

  &__chips {
    display: flex;
    flex-wrap: wrap;
    gap: 14rpx;
    margin-top: 22rpx;
  }

  &__sold {
    background: #8f6a2a;
    color: #ffffff;
  }
}

.seller-row {
  display: flex;
  align-items: center;
  gap: 18rpx;

  &__avatar {
    width: 80rpx;
    height: 80rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 24rpx;
    background: #111111;
    color: #ffffff;
    font-size: 28rpx;
    font-weight: 700;
  }

  &__name {
    color: #111111;
    font-size: 30rpx;
    font-weight: 700;
  }

  &__meta {
    margin-top: 8rpx;
    color: #71716a;
    font-size: 24rpx;
  }
}

.detail-block {
  &__title {
    color: #111111;
    font-size: 28rpx;
    font-weight: 700;
  }

  &__sub {
    margin-top: 22rpx;
    color: #454540;
    font-size: 24rpx;
    font-weight: 600;

    &--rule {
      margin-top: 24rpx;
    }
  }

  &__text {
    margin-top: 14rpx;
    color: #55554f;
    font-size: 26rpx;
    line-height: 1.8;
  }
}

.rent-summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 18rpx;

  &__item {
    padding: 22rpx;
    border-radius: 24rpx;
    background: #f3f0e8;
  }

  &__label {
    color: #71716a;
    font-size: 22rpx;
  }

  &__value {
    margin-top: 10rpx;
    color: #111111;
    font-size: 30rpx;
    font-weight: 700;
  }
}

.detail-action {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  gap: 16rpx;
  padding: 20rpx 24rpx calc(var(--safe-bottom) + 20rpx);
  background: rgba(245, 245, 241, 0.96);
  backdrop-filter: blur(18rpx);

  &__minor {
    flex: 1;
  }

  &__major {
    flex: 1.4;
  }

  &__minor,
  &__major {
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0;
    padding: 0;
    border: none;
    text-align: center;
    line-height: 88rpx;

    &::after {
      border: none;
    }
  }

  &__minor--disabled {
    opacity: 0.45;
  }

  &__major--disabled {
    background: #7b7b75;
  }
}

.rent-days-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-top: 12rpx;
}

.rent-days-row input {
  flex: 1;
  height: 72rpx;
  padding: 0 22rpx;
  border: 1rpx solid rgba(17, 17, 17, 0.12);
  border-radius: 16rpx;
  background: #f7f5ef;
  font-size: 28rpx;
}

.rent-days-suffix {
  color: #6f6f69;
  font-size: 26rpx;
  flex: none;
}
</style>
