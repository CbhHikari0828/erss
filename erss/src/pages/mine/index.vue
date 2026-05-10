<script setup>
import AppIcon from '@/components/AppIcon.vue'
import CustomTabBar from '@/components/CustomTabBar.vue'
import { useCatalogStore } from '@/store/catalog/index.js'
import { useOrderStore } from '@/store/order/index.js'
import { useRentalStore } from '@/store/rental/index.js'
import { ensureLogin } from '@/utils/auth.js'

const userStore = useUserStore()
const catalogStore = useCatalogStore()
const orderStore = useOrderStore()
const rentalStore = useRentalStore()
const router = useRouter()

const PLACEHOLDER = '/static/images/common/placeholder.png'
const avatarError = ref(false)
const avatarSrc = computed(() => avatarError.value ? PLACEHOLDER : userStore.userInfo.avatar)
const erroredCovers = reactive(new Set())

function onAvatarError() {
  avatarError.value = true
}

function mineCoverSrc(book) {
  return erroredCovers.has(book.id) ? PLACEHOLDER : book.cover
}

function onMineCoverError(bookId) {
  erroredCovers.add(bookId)
}

const publishedBooks = computed(() => catalogStore.mineBooks.filter(b => b.rawStatus === 'ON_SALE').slice(0, 2))
const walletBalanceText = computed(() => Number(userStore.userInfo.walletBalance || 0).toFixed(2))
const dashboardMenus = computed(() => [
  { label: '我的店铺', value: userStore.hasShop ? '已开通' : '去开通', icon: 'i-carbon-store' },
  { label: '我的订单', value: String(orderStore.buyerOrders.length), icon: 'i-carbon-receipt' },
  { label: '我的租借', value: String(rentalStore.rentalOrders.filter(item => !item.ownedByMe).length), icon: 'i-carbon-document-add' },
  { label: '设置', value: '>', icon: 'i-carbon-settings-adjust' },
])

function handleProfileClick() {
  ensureLogin(userStore, () => {
    router.push('/settings')
  }, {
    onConfirm: () => router.push('/login'),
  })
}

function handleMenuClick(index) {
  if (index === 0) {
    ensureLogin(userStore, () => {
      router.push('/my-shop')
    }, {
      onConfirm: () => router.push('/login'),
    })
    return
  }

  if (index === 1) {
    ensureLogin(userStore, () => {
      router.push('/orders')
    }, {
      onConfirm: () => router.push('/login'),
    })
    return
  }

  if (index === 2) {
    ensureLogin(userStore, () => {
      router.push('/rentals')
    }, {
      onConfirm: () => router.push('/login'),
    })
    return
  }

  if (index === 3) {
    ensureLogin(userStore, () => {
      router.push('/settings/menu')
    }, {
      onConfirm: () => router.push('/login'),
    })
  }
}

function openWallet() {
  ensureLogin(userStore, () => {
    router.push('/wallet')
  }, {
    onConfirm: () => router.push('/login'),
  })
}

onShow(async () => {
  if (userStore.isLogin)
    await userStore.getUserData().catch(() => {})

  catalogStore.refreshMineBooks().catch(() => {})
  orderStore.refreshOrders().catch(() => {})
  rentalStore.refreshRentals().catch(() => {})
})
</script>

<template>
  <view>
    <scroll-view scroll-y class="app-shell app-shell--with-tabbar">
      <view class="profile card-panel" @click="handleProfileClick">
        <image class="profile__bg" src="/static/images/mine/profile-bg.png" mode="aspectFill" />
        <view class="profile__content">
          <image
            v-if="userStore.userInfo.avatar"
            class="profile__avatar"
            :src="avatarSrc"
            mode="aspectFill"
            @error="onAvatarError"
          />
          <view v-else class="profile__avatar profile__avatar--text">
            校
          </view>
          <view class="profile__body">
            <view class="profile__name">
              {{ userStore.userInfo.username }}
            </view>
            <view class="profile__meta">
              {{ userStore.userInfo.department || '点击登录并完善校园身份信息' }}
            </view>
            <view class="profile__meta">
              {{ userStore.userInfo.campus }}
            </view>
          </view>
        </view>
      </view>

      <view class="card-panel shop-strip" @click="handleMenuClick(0)">
        <view class="shop-strip__body">
          <view class="shop-strip__title">
            我的店铺
          </view>
          <view class="shop-strip__text">
            {{ userStore.hasShop ? `${userStore.userInfo.shopName} · 查看商品、订单和租借情况` : '注册店铺后可统一管理商品、购买订单和租借订单' }}
          </view>
        </view>
        <view class="shop-strip__arrow">
          >
        </view>
      </view>

      <view class="card-panel wallet-strip" @click="openWallet">
        <view class="wallet-strip__body">
          <view class="wallet-strip__title">
            我的钱包
          </view>
          <view class="wallet-strip__text">
            {{ userStore.isLogin ? `余额 ￥${walletBalanceText}` : '登录后可查看余额并发起提现' }}
          </view>
        </view>
        <view class="wallet-strip__action">
          查看
        </view>
      </view>

      <view class="stats-grid">
        <view v-for="(item, index) in dashboardMenus" :key="item.label" class="stats-grid__item card-panel" @click="handleMenuClick(index)">
          <view class="stats-grid__icon" :class="item.icon"></view>
          <view class="stats-grid__value">
            {{ item.value }}
          </view>
          <view class="stats-grid__label">
            {{ item.label }}
          </view>
        </view>
      </view>

      <view class="section-title section-gap">
        <view>
          <view class="section-title__main">
            我的在售
          </view>
          <view class="section-title__sub">
            最近挂出的书籍，建议定期更新成色和自提说明
          </view>
        </view>
        <AppIcon name="mine" size="38rpx" />
      </view>

      <view class="card-panel mine-books">
        <view v-for="book in publishedBooks" :key="book.id" class="mine-books__item">
          <image class="mine-books__cover" :src="mineCoverSrc(book)" mode="aspectFill" @error="onMineCoverError(book.id)" />
          <view class="mine-books__body">
            <view class="mine-books__title">
              {{ book.title }}
            </view>
            <view class="mine-books__meta">
              {{ book.condition }} · {{ book.campus }}
            </view>
            <view class="mine-books__price">
              ￥{{ book.price }}
            </view>
          </view>
        </view>
      </view>

    </scroll-view>
    <CustomTabBar current="mine" />
  </view>
</template>

<style lang="scss" scoped>
.section-gap {
  margin-top: 32rpx;
}

.profile {
  position: relative;
  display: flex;
  align-items: center;
  gap: 22rpx;
  padding: 30rpx;
  min-height: 220rpx;
  overflow: hidden;
  transition: transform 0.15s ease;
  &:active {
    transform: scale(0.985);
  }
}

.profile__bg {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.profile__content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 22rpx;
  width: 100%;
}

.profile__avatar {
  width: 108rpx;
  height: 108rpx;
  border-radius: 32rpx;
  background: #ece8df;
  box-shadow: 0 8rpx 24rpx rgba(17, 17, 17, 0.08);
}

.profile__avatar--text {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #111111;
  font-size: 36rpx;
  font-weight: 700;
}

.profile__body {
  min-width: 0;
  flex: 1;
}

.profile__name {
  font-size: 38rpx;
  font-weight: 700;
  color: #111111;
}

.profile__meta {
  margin-top: 8rpx;
  color: #70706a;
  font-size: 24rpx;
}

.shop-strip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-top: 22rpx;
  padding: 26rpx 28rpx;
  transition: transform 0.15s ease;
  &:active {
    transform: scale(0.985);
  }
}

.shop-strip__body {
  min-width: 0;
  flex: 1;
}

.shop-strip__title {
  color: #111111;
  font-size: 30rpx;
  font-weight: 700;
}

.shop-strip__text {
  margin-top: 10rpx;
  color: #6f6f69;
  font-size: 24rpx;
  line-height: 1.6;
}

.shop-strip__arrow {
  color: #a09d95;
  font-size: 28rpx;
  font-weight: 700;
}

.wallet-strip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-top: 22rpx;
  padding: 26rpx 28rpx;
  transition: transform 0.15s ease;
  &:active {
    transform: scale(0.985);
  }
}

.wallet-strip__body {
  min-width: 0;
  flex: 1;
}

.wallet-strip__title {
  color: #111111;
  font-size: 30rpx;
  font-weight: 700;
}

.wallet-strip__text {
  margin-top: 10rpx;
  color: #6f6f69;
  font-size: 24rpx;
  line-height: 1.6;
}

.wallet-strip__action {
  flex: none;
  padding: 14rpx 22rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #1a1a1a, #2d2d2d);
  color: #ffffff;
  font-size: 22rpx;
  font-weight: 500;
  box-shadow: 0 4rpx 12rpx rgba(17, 17, 17, 0.15);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
  margin-top: 22rpx;
}

.stats-grid__item {
  padding: 26rpx;
  transition: transform 0.15s ease;
  &:active {
    transform: scale(0.96);
  }
}

.stats-grid__icon {
  font-size: 30rpx;
  color: #111111;
}

.stats-grid__value {
  margin-top: 28rpx;
  font-size: 38rpx;
  font-weight: 700;
  color: #111111;
}

.stats-grid__label {
  margin-top: 8rpx;
  color: #71716a;
  font-size: 24rpx;
}

.mine-books {
  padding: 10rpx 24rpx;
}

.mine-books__item {
  display: flex;
  gap: 18rpx;
  padding: 20rpx 0;
  border-bottom: 1rpx solid rgba(17, 17, 17, 0.06);
}

.mine-books__item:last-child {
  border-bottom: none;
}

.mine-books__cover {
  width: 120rpx;
  height: 150rpx;
  border-radius: 18rpx;
  background: #ece8df;
  box-shadow: 0 6rpx 18rpx rgba(17, 17, 17, 0.08);
}

.mine-books__body {
  flex: 1;
  min-width: 0;
}

.mine-books__title {
  color: #161616;
  font-size: 28rpx;
  font-weight: 600;
  line-height: 1.5;
}

.mine-books__meta {
  margin-top: 8rpx;
  color: #787872;
  font-size: 22rpx;
}

.mine-books__price {
  margin-top: 18rpx;
  color: #111111;
  font-size: 30rpx;
  font-weight: 700;
}
</style>
