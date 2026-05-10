<script setup>
import CustomTabBar from '@/components/CustomTabBar.vue'
import { useCatalogStore } from '@/store/catalog/index.js'
import { useOrderStore } from '@/store/order/index.js'
import { useRentalStore } from '@/store/rental/index.js'

const PLACEHOLDER = '/static/images/common/placeholder.png'
const erroredCovers = reactive(new Set())

function shopCoverSrc(book) {
  return erroredCovers.has(book.id) ? PLACEHOLDER : book.cover
}

function onShopCoverError(bookId) {
  erroredCovers.add(bookId)
}

const router = useRouter()
const userStore = useUserStore()
const catalogStore = useCatalogStore()
const orderStore = useOrderStore()
const rentalStore = useRentalStore()
const showTopBackBar = ref(false)
const TOP_BAR_THRESHOLD = 56

const shopBooks = computed(() => catalogStore.mineBooks)

const rentOrders = computed(() => rentalStore.rentalOrders.filter(item => item.ownedByMe))
const tradeOrders = computed(() => orderStore.sellerOrders)

const editingBookId = ref('')
const pricingForm = reactive({
  rentalEnabled: false,
  rentPrice: '',
  deposit: '',
})

const shopStats = computed(() => [
  { label: '在售商品', value: String(shopBooks.value.filter(item => item.status === '在售中').length) },
  { label: '待发货', value: String(tradeOrders.value.filter(item => item.status === '待卖家发货').length) },
  { label: '待鉴定', value: String(rentOrders.value.filter(item => item.damageReport?.status === 'pending').length) },
  { label: '累计订单', value: String(rentOrders.value.length + tradeOrders.value.length) },
])

function goBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack({ delta: 1 })
    return
  }

  uni.reLaunch({
    url: '/pages/mine/index',
  })
}

onPageScroll(({ scrollTop }) => {
  showTopBackBar.value = scrollTop >= TOP_BAR_THRESHOLD
})

function goRegisterShop() {
  router.push('/shop')
}

function goDamageReport(order) {
  if (!order.canReportDamage) {
    uni.showToast({
      title: order.damageReport ? '该订单已报损' : '当前不可报损',
      icon: 'none',
    })
    return
  }

  router.push(`/rentals/report?id=${order.id}`)
}

function beginEditPricing(book) {
  editingBookId.value = book.id
  pricingForm.rentalEnabled = Boolean(book.rentalEnabled)
  pricingForm.rentPrice = String(book.rentPrice || '')
  pricingForm.deposit = String(book.rentalDeposit || book.deposit || '')
}

function cancelEditPricing() {
  editingBookId.value = ''
  pricingForm.rentalEnabled = false
  pricingForm.rentPrice = ''
  pricingForm.deposit = ''
}

function toggleRental(event) {
  pricingForm.rentalEnabled = Boolean(event.detail.value)
}

function savePricing(book) {
  const rentPrice = Number(pricingForm.rentPrice)
  const deposit = Number(pricingForm.deposit)

  if (pricingForm.rentalEnabled && (!rentPrice || rentPrice <= 0 || !deposit || deposit <= 0)) {
    uni.showToast({
      title: '请填写有效的租金和押金',
      icon: 'none',
    })
    return
  }

  catalogStore.updateMineBook(book.id, {
    rentalEnabled: pricingForm.rentalEnabled,
    rentPrice: pricingForm.rentalEnabled ? rentPrice : 0,
    rentalDeposit: pricingForm.rentalEnabled ? deposit : 0,
  }).then(() => {
    cancelEditPricing()
    uni.showToast({
      title: '价格已更新',
      icon: 'success',
    })
  })
}

function toggleBookStatus(book) {
  const nextStatus = book.status === '在售中' ? 'OFF_SALE' : 'ON_SALE'
  catalogStore.updateMineBook(book.id, {
    status: nextStatus,
  }).then(() => {
    uni.showToast({
      title: nextStatus === 'ON_SALE' ? '已重新上架' : '已下架商品',
      icon: 'none',
    })
  })
}

function finishRent(order) {
  rentalStore.finishReturn(order.id)
  uni.showToast({
    title: '已标记归还',
    icon: 'success',
  })
}

function shipOrder(order) {
  if (!orderStore.shipOrder(order.id))
    return

  uni.showToast({
    title: '已标记发货',
    icon: 'success',
  })
}

onShow(() => {
  catalogStore.refreshMineBooks().catch(() => {})
  orderStore.refreshAutoConfirmedOrders()
})
</script>

<template>
  <view>
    <view class="app-shell app-shell--with-tabbar shop-dashboard">
      <view v-if="showTopBackBar" class="shop-dashboard__topbar">
        <button class="shop-dashboard__topbar-back" @click="goBack">
          <image class="shop-dashboard__topbar-icon" src="/static/images/common/back.svg" mode="aspectFit" />
        </button>
        <view class="shop-dashboard__topbar-title">
          我的店铺
        </view>
      </view>
      <button v-if="!showTopBackBar" class="shop-dashboard__back shop-dashboard__back--floating" @click="goBack">
        <image class="shop-dashboard__back-icon" src="/static/images/common/back.svg" mode="aspectFit" />
      </button>
      <view class="shop-dashboard__header">
        <view class="shop-dashboard__back shop-dashboard__back--placeholder"></view>
        <view class="shop-dashboard__header-body">
          <view class="shop-dashboard__title">
            我的店铺
          </view>
          <view class="shop-dashboard__sub">
            {{ userStore.hasShop ? (userStore.userInfo.shopName || '已开通店铺') : '先注册店铺后再管理商品、购买订单和租借订单' }}
          </view>
        </view>
      </view>

      <view v-if="!userStore.hasShop" class="card-panel shop-empty">
        <view class="shop-empty__title">
          你还没有店铺
        </view>
        <view class="shop-empty__text">
          注册店铺后，你可以在这里查看在售商品、租借状态、待发货订单和托管结算情况。
        </view>
        <button class="primary-button shop-empty__button" @click="goRegisterShop">
          去注册店铺
        </button>
      </view>

      <template v-else>
        <view class="card-panel shop-info">
          <view class="shop-info__name">
            {{ userStore.userInfo.shopName }}
          </view>
          <view class="shop-info__meta">
            {{ userStore.userInfo.shopCampus || userStore.userInfo.campus }}
          </view>
          <view class="shop-info__intro">
            {{ userStore.userInfo.shopIntro || '主营教材、考研资料和校内面交书籍。' }}
          </view>
        </view>

        <view class="shop-stats">
          <view v-for="item in shopStats" :key="item.label" class="card-panel shop-stats__item">
            <view class="shop-stats__value">
              {{ item.value }}
            </view>
            <view class="shop-stats__label">
              {{ item.label }}
            </view>
          </view>
        </view>

        <view class="section-title section-gap">
          <view>
            <view class="section-title__main">
              店铺商品
            </view>
            <view class="section-title__sub">
              可查看当前租金、押金和上架状态
            </view>
          </view>
        </view>

        <view class="card-panel shop-books">
          <view v-for="book in shopBooks" :key="book.id" class="shop-books__item">
            <image class="shop-books__cover" :src="shopCoverSrc(book)" mode="aspectFill" @error="onShopCoverError(book.id)" />
            <view class="shop-books__body">
              <view class="shop-books__title">
                {{ book.title }}
              </view>
              <view class="shop-books__meta">
                {{ book.condition }} · {{ book.campus }} · {{ book.status }}
              </view>
              <view class="shop-books__pricing">
                <text>售价 ￥{{ book.price }}</text>
                <text>
                  {{ book.rentalEnabled ? `日租 ￥${book.rentPrice}` : '未开启租赁' }}
                </text>
                <text v-if="book.rentalEnabled">
                  押金 ￥{{ book.rentalDeposit }}
                </text>
              </view>

              <view v-if="editingBookId === book.id" class="pricing-editor">
                <view class="pricing-editor__switch">
                  <view>
                    <view class="pricing-editor__label">
                      开启租赁
                    </view>
                    <view class="pricing-editor__hint">
                      关闭后买家只能直接购买
                    </view>
                  </view>
                  <switch :checked="pricingForm.rentalEnabled" color="#111111" @change="toggleRental" />
                </view>
                <view class="pricing-editor__row">
                  <view class="pricing-editor__field">
                    <view class="pricing-editor__label">
                      日租金
                    </view>
                    <input v-model="pricingForm.rentPrice" class="pricing-editor__input" type="digit" placeholder="请输入日租金" :disabled="!pricingForm.rentalEnabled" />
                  </view>
                  <view class="pricing-editor__field">
                    <view class="pricing-editor__label">
                      押金
                    </view>
                    <input v-model="pricingForm.deposit" class="pricing-editor__input" type="digit" placeholder="请输入押金" :disabled="!pricingForm.rentalEnabled" />
                  </view>
                </view>
                <view class="pricing-editor__actions">
                  <button class="ghost-button pricing-editor__button" @click="cancelEditPricing">
                    取消
                  </button>
                  <button class="primary-button pricing-editor__button pricing-editor__button--primary" @click="savePricing(book)">
                    保存
                  </button>
                </view>
              </view>

              <view v-else class="shop-books__actions">
                <button class="ghost-button shop-books__button" @click="beginEditPricing(book)">
                  租赁设置
                </button>
                <button class="ghost-button shop-books__button" @click="toggleBookStatus(book)">
                  {{ book.status === '已下架' ? '重新上架' : '下架商品' }}
                </button>
              </view>
            </view>
          </view>
        </view>

        <view class="section-title section-gap">
          <view>
            <view class="section-title__main">
              购买与租赁订单
            </view>
            <view class="section-title__sub">
              买家付款后先由系统托管，发货后等待买家确认收货或 15 天自动确认
            </view>
          </view>
        </view>

        <view class="card-panel rent-orders">
          <view v-for="item in tradeOrders" :key="item.id" class="rent-orders__item">
            <view class="rent-orders__head">
              <view class="rent-orders__title">
                {{ item.title }}
              </view>
              <view class="rent-orders__status">
                {{ item.status }}
              </view>
            </view>
            <view class="rent-orders__meta">
              {{ item.orderType === 'rental' ? `租期 ${item.rentalPeriod}` : '直接购买' }} · {{ item.deliveryMethod === 'pickup' ? '上门自提' : '快递驿站取件' }}
            </view>
            <view class="rent-orders__meta">
              订单金额 ￥{{ orderStore.formatPrice(item.totalAmount) }} · {{ item.escrowStatus }}
            </view>
            <view class="rent-orders__meta">
              {{ item.statusText }}
            </view>
            <view v-if="item.status === '待卖家发货'" class="rent-orders__actions">
              <button class="ghost-button rent-orders__button" @click="shipOrder(item)">
                标记已发货
              </button>
            </view>
          </view>
        </view>

        <view class="section-title section-gap">
          <view>
            <view class="section-title__main">
              租借情况
            </view>
            <view class="section-title__sub">
              查看当前租借订单，并在需要时提交损坏鉴定
            </view>
          </view>
        </view>

        <view class="card-panel rent-orders">
          <view v-for="item in rentOrders" :key="item.id" class="rent-orders__item">
            <view class="rent-orders__head">
              <view class="rent-orders__title">
                {{ item.title }}
              </view>
              <view class="rent-orders__status">
                {{ item.status }}
              </view>
            </view>
            <view class="rent-orders__meta">
              租期 {{ item.period }} · 押金 ￥{{ item.deposit }}
            </view>
            <view class="rent-orders__meta">
              {{ item.dueText }}
            </view>
            <view class="rent-orders__meta">
              租客 {{ item.borrowerName }}
            </view>
            <view v-if="item.status !== '已归还'" class="rent-orders__actions">
              <button v-if="item.canReportDamage" class="ghost-button rent-orders__button" @click="goDamageReport(item)">
                报损
              </button>
              <button class="ghost-button rent-orders__button" @click="finishRent(item)">
                标记已归还
              </button>
            </view>
          </view>
        </view>
      </template>
    </view>
    <CustomTabBar current="mine" />
  </view>
</template>

<style lang="scss" scoped>
.shop-dashboard {
  min-height: 100vh;
  padding-top: calc(var(--safe-top) + 20rpx);
}
.shop-dashboard__header {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;
}
.shop-dashboard__header-body {
  min-width: 0;
  flex: 1;
}
.shop-dashboard__title {
  color: #111111;
  font-size: 38rpx;
  font-weight: 700;
}
.shop-dashboard__sub {
  margin-top: 10rpx;
  color: #6d6d67;
  font-size: 24rpx;
  line-height: 1.6;
}
.shop-dashboard__back {
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  border: none;
  border-radius: 22rpx;
  background: #f1eee6;
}
.shop-dashboard__back::after {
  border: none;
}
.shop-dashboard__back--floating {
  position: fixed;
  top: calc(var(--safe-top) + 20rpx);
  left: 24rpx;
  z-index: 20;
}
.shop-dashboard__back--placeholder {
  visibility: hidden;
}
.shop-dashboard__back-icon {
  width: 30rpx;
  height: 30rpx;
}
.shop-dashboard__topbar {
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
  box-shadow: 0 12rpx 28rpx rgba(17, 17, 17, 0.08);
  backdrop-filter: blur(18rpx);
}
.shop-dashboard__topbar-back {
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
}
.shop-dashboard__topbar-back::after {
  border: none;
}
.shop-dashboard__topbar-icon {
  width: 30rpx;
  height: 30rpx;
}
.shop-dashboard__topbar-title {
  flex: 1;
  padding-right: 28rpx;
  color: #111111;
  font-size: 26rpx;
  font-weight: 600;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.section-gap {
  margin-top: 28rpx;
}
.shop-empty,
.shop-info,
.shop-books,
.rent-orders {
  margin-top: 24rpx;
  padding: 28rpx;
}
.shop-empty__title {
  color: #111111;
  font-size: 30rpx;
  font-weight: 700;
}
.shop-empty__text {
  margin-top: 12rpx;
  color: #666660;
  font-size: 24rpx;
  line-height: 1.7;
}
.shop-empty__button {
  margin-top: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 76rpx;
  min-height: 76rpx;
  padding: 0;
  line-height: 1;
  font-size: 24rpx;
}
.shop-empty__button::after {
  border: none;
}
.shop-info__name {
  color: #111111;
  font-size: 34rpx;
  font-weight: 700;
}
.shop-info__meta {
  margin-top: 10rpx;
  color: #7a7a74;
  font-size: 24rpx;
}
.shop-info__intro {
  margin-top: 14rpx;
  color: #575751;
  font-size: 24rpx;
  line-height: 1.7;
}
.shop-stats {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
  margin-top: 20rpx;
}
.shop-stats__item {
  padding: 26rpx;
}
.shop-stats__value {
  color: #111111;
  font-size: 38rpx;
  font-weight: 700;
}
.shop-stats__label {
  margin-top: 8rpx;
  color: #71716a;
  font-size: 24rpx;
}
.shop-books__item {
  display: flex;
  gap: 18rpx;
  padding: 20rpx 0;
  border-bottom: 1rpx solid rgba(17, 17, 17, 0.07);
}
.shop-books__item:last-child {
  border-bottom: none;
}
.shop-books__cover {
  width: 120rpx;
  height: 150rpx;
  border-radius: 18rpx;
  background: #ece8df;
  flex: none;
}
.shop-books__body {
  flex: 1;
  min-width: 0;
}
.shop-books__title {
  color: #161616;
  font-size: 28rpx;
  font-weight: 600;
  line-height: 1.5;
}
.shop-books__meta {
  margin-top: 8rpx;
  color: #787872;
  font-size: 22rpx;
}
.shop-books__pricing {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx 18rpx;
  margin-top: 12rpx;
  color: #4c4c47;
  font-size: 22rpx;
}
.shop-books__actions {
  display: flex;
  gap: 12rpx;
  margin-top: 16rpx;
}
.shop-books__button,
.rent-orders__button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60rpx;
  min-height: 60rpx;
  padding: 0 14rpx;
  border: 2rpx solid rgba(17, 17, 17, 0.14);
  border-radius: 999rpx;
  background: #fbfaf7;
  line-height: 1;
  font-size: 22rpx;
}
.shop-books__button::after,
.rent-orders__button::after {
  border: 2rpx solid rgba(17, 17, 17, 0.14);
  border-radius: 999rpx;
}
.pricing-editor {
  margin-top: 16rpx;
  padding: 18rpx;
  border: 1rpx solid rgba(17, 17, 17, 0.08);
  border-radius: 22rpx;
  background: #f7f5ef;
}
.pricing-editor__switch {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 14rpx;
}
.pricing-editor__row {
  display: flex;
  gap: 12rpx;
}
.pricing-editor__field {
  flex: 1;
}
.pricing-editor__label {
  margin-bottom: 10rpx;
  color: #53534d;
  font-size: 22rpx;
}
.pricing-editor__hint {
  color: #777771;
  font-size: 20rpx;
}
.pricing-editor__input {
  width: 100%;
  height: 68rpx;
  padding: 0 20rpx;
  border-radius: 18rpx;
  background: #ffffff;
  border: 1rpx solid rgba(17, 17, 17, 0.08);
  color: #111111;
  font-size: 24rpx;
}
.pricing-editor__actions {
  display: flex;
  gap: 12rpx;
  margin-top: 14rpx;
}
.pricing-editor__button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 56rpx;
  min-height: 56rpx;
  padding: 0 12rpx;
  border-radius: 999rpx;
  border: 2rpx solid rgba(17, 17, 17, 0.14);
  line-height: 1;
  font-size: 22rpx;
}
.pricing-editor__button::after {
  border: 2rpx solid rgba(17, 17, 17, 0.14);
  border-radius: 999rpx;
}
.rent-orders__item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid rgba(17, 17, 17, 0.07);
}
.rent-orders__item:last-child {
  border-bottom: none;
}
.rent-orders__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}
.rent-orders__title {
  color: #161616;
  font-size: 28rpx;
  font-weight: 600;
  line-height: 1.5;
}
.rent-orders__status {
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  background: #111111;
  color: #ffffff;
  font-size: 20rpx;
  border: 2rpx solid rgba(17, 17, 17, 0.18);
}
.rent-orders__meta {
  margin-top: 10rpx;
  color: #777771;
  font-size: 22rpx;
}
.rent-orders__actions {
  display: flex;
  gap: 12rpx;
  margin-top: 16rpx;
}
</style>
