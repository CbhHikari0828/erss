<script setup>
import { uploadImages } from '@/api/upload/index.js'
import { useRentalStore } from '@/store/rental/index.js'

const route = useRoute()
const router = useRouter()
const rentalStore = useRentalStore()

const showTopBackBar = ref(false)
const TOP_BAR_THRESHOLD = 56
const submitting = ref(false)

const form = reactive({
  description: '',
  images: [],
})

const order = computed(() => rentalStore.rentalOrders.find(item => item.id === route.query.id))
const pageTitle = '损坏上报'
const pageSubTitle = '卖家提交损坏图片和文字描述，等待管理员鉴定是否影响押金。'

onPageScroll(({ scrollTop }) => {
  showTopBackBar.value = scrollTop >= TOP_BAR_THRESHOLD
})

function redirectToShop() {
  setTimeout(() => {
    router.replace('/my-shop')
  }, 180)
}

function validateReportPermission() {
  if (submitting.value)
    return

  if (!order.value)
    return

  if (order.value.damageReport) {
    uni.showToast({
      title: '该订单已报损，不能重复提交',
      icon: 'none',
    })
    redirectToShop()
    return
  }

  if (!order.value.canReportDamage) {
    uni.showToast({
      title: '只有卖家可以提交报损',
      icon: 'none',
    })
    redirectToShop()
  }
}

watch(order, validateReportPermission, { immediate: true })

onMounted(() => {
  rentalStore.refreshRentals().catch(() => {})
})

function goBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack({ delta: 1 })
    return
  }

  router.replace('/my-shop')
}

function chooseImages() {
  if (!order.value?.canReportDamage)
    return

  uni.chooseImage({
    count: 6,
    success: ({ tempFilePaths }) => {
      form.images = [...form.images, ...tempFilePaths].slice(0, 6)
    },
  })
}

function removeImage(index) {
  form.images.splice(index, 1)
}

async function submit() {
  if (submitting.value)
    return

  if (!order.value) {
    uni.showToast({
      title: '租借订单不存在',
      icon: 'none',
    })
    return
  }

  if (order.value?.damageReport) {
    uni.showToast({
      title: '该订单已报损，不能重复提交',
      icon: 'none',
    })
    return
  }

  if (!order.value.canReportDamage) {
    uni.showToast({
      title: '只有卖家可以提交报损',
      icon: 'none',
    })
    return
  }

  if (!form.description.trim()) {
    uni.showToast({
      title: '请填写损坏描述',
      icon: 'none',
    })
    return
  }

  if (!form.images.length) {
    uni.showToast({
      title: '请上传损坏图片',
      icon: 'none',
    })
    return
  }

  submitting.value = true
  try {
    const uploadedImages = await uploadImages(form.images, 'damage-reports')
    await rentalStore.submitDamageReport(route.query.id, {
      description: form.description.trim(),
      images: uploadedImages,
    })
    uni.showToast({
      title: '已提交管理员鉴定',
      icon: 'success',
    })

    setTimeout(() => {
      router.replace('/my-shop')
    }, 180)
  }
  catch (err) {
    submitting.value = false
    uni.showToast({
      title: err?.message || '提交失败',
      icon: 'none',
    })
  }
}
</script>

<template>
  <view class="app-shell report-page">
    <view v-if="showTopBackBar" class="report-topbar">
      <button class="report-topbar__back" @click="goBack">
        <view class="report-topbar__icon i-carbon-arrow-left"></view>
      </button>
      <view class="report-topbar__title">
        {{ pageTitle }}
      </view>
    </view>
    <button v-if="!showTopBackBar" class="report-back report-back--floating" @click="goBack">
      <view class="report-back__icon i-carbon-arrow-left"></view>
    </button>

    <view class="report-header">
      <view class="report-back report-back--placeholder"></view>
      <view class="report-header__body">
        <view class="report-header__title">
          {{ pageTitle }}
        </view>
        <view class="report-header__sub">
          {{ pageSubTitle }}
        </view>
      </view>
    </view>

    <view class="card-panel report-order">
      <view class="report-order__title">
        {{ order?.title || '当前租借订单' }}
      </view>
      <view class="report-order__meta">
        <text>租期 {{ order?.period }}</text>
        <text>押金 ￥{{ order?.deposit }}</text>
      </view>
      <view class="report-order__meta">
        {{ order?.dueText }}
      </view>
    </view>

    <view class="card-panel report-panel">
      <view class="report-field">
        <view class="report-field__label">
          损坏描述
        </view>
        <textarea
          v-model="form.description"
          class="report-field__textarea"
          maxlength="300"
          placeholder="例如：书脊开胶、封面划伤、内页破损、受潮痕迹等，请尽量写清楚位置和程度"
        />
      </view>

      <view class="report-field report-field--last">
        <view class="report-field__head">
          <view class="report-field__label">
            损坏图片
          </view>
          <view class="report-field__count">
            {{ form.images.length }}/6
          </view>
        </view>
        <view class="report-images">
          <view class="report-images__add" @click="chooseImages">
            <view class="report-images__plus">
              +
            </view>
            <view class="report-images__text">
              上传图片
            </view>
          </view>
          <view v-for="(image, index) in form.images" :key="`${image}-${index}`" class="report-images__item">
            <image class="report-images__preview" :src="image" mode="aspectFill" />
            <view class="report-images__remove" @click="removeImage(index)">
              ×
            </view>
          </view>
        </view>
      </view>
    </view>

    <button class="primary-button report-submit" :disabled="submitting" @click="submit">
      {{ submitting ? '提交中...' : '提交管理员鉴定' }}
    </button>
  </view>
</template>

<style lang="scss" scoped>
.report-page {
  min-height: 100vh;
  padding-top: calc(var(--safe-top) + 20rpx);
  padding-bottom: calc(var(--safe-bottom) + 120rpx);
}

.report-header {
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

.report-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  border: none;
  border-radius: 22rpx;
  background: #f1eee6;

  &::after {
    border: none;
  }

  &__icon {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24rpx;
    color: #111111;
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

.report-topbar {
  position: fixed;
  top: calc(var(--safe-top) + 20rpx);
  left: 24rpx;
  right: 24rpx;
  z-index: 20;
  height: 60rpx;
  display: flex;
  align-items: center;
  border: 1rpx solid rgba(255, 255, 255, 0.32);
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 12rpx 28rpx rgba(17, 17, 17, 0.08);
  backdrop-filter: blur(18rpx);

  &__back {
    width: 60rpx;
    height: 60rpx;
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
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24rpx;
    color: #111111;
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
  }
}

.report-order,
.report-panel {
  margin-top: 24rpx;
  padding: 28rpx;
}

.report-order {
  &__title {
    color: #111111;
    font-size: 32rpx;
    font-weight: 700;
  }

  &__meta {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx 18rpx;
    margin-top: 12rpx;
    color: #6d6d67;
    font-size: 22rpx;
  }
}

.report-field {
  padding-bottom: 24rpx;
  border-bottom: 1rpx solid rgba(17, 17, 17, 0.07);

  &--last {
    padding-bottom: 0;
    border-bottom: none;
  }

  &__head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12rpx;
  }

  &__label {
    color: #111111;
    font-size: 26rpx;
    font-weight: 600;
  }

  &__count {
    color: #8a8a84;
    font-size: 22rpx;
  }

  &__textarea {
    width: 100%;
    min-height: 220rpx;
    margin-top: 16rpx;
    padding: 20rpx;
    border-radius: 22rpx;
    background: #f7f5ef;
    color: #111111;
    font-size: 24rpx;
    line-height: 1.7;
  }
}

.report-images {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 16rpx;

  &__add,
  &__item {
    position: relative;
    height: 180rpx;
    border-radius: 22rpx;
    overflow: hidden;
  }

  &__add {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    border: 2rpx dashed rgba(17, 17, 17, 0.18);
    background: #fbfaf7;
  }

  &__plus {
    color: #5f5f59;
    font-size: 42rpx;
    line-height: 1;
  }

  &__text {
    color: #5f5f59;
    font-size: 22rpx;
  }

  &__preview {
    width: 100%;
    height: 100%;
    background: #ece8df;
  }

  &__remove {
    position: absolute;
    top: 10rpx;
    right: 10rpx;
    width: 40rpx;
    height: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 999rpx;
    background: rgba(17, 17, 17, 0.65);
    color: #fff;
    font-size: 28rpx;
    line-height: 1;
  }
}

.report-submit {
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
</style>
