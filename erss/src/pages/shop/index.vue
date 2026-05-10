<script setup>
import { campusOptions } from '@/constants/catalog.js'

const router = useRouter()
const userStore = useUserStore()
const showTopBackBar = ref(false)
const TOP_BAR_THRESHOLD = 56

const copy = {
  title: '注册商铺',
  subTitle: '完成商铺信息后，才能发布书籍',
  name: '商铺名称',
  namePlaceholder: '例如：主校区二手教材小店',
  campus: '所在校区',
  intro: '商铺简介',
  introPlaceholder: '简单说明你主要出售的书籍类型或交易方式',
  submit: '完成注册',
  back: '返回',
  success: '商铺注册成功',
  invalidName: '请输入商铺名称',
  invalidIntro: '请填写商铺简介',
}

const form = reactive({
  shopName: '',
  shopCampus: campusOptions[0],
  shopIntro: '',
})

function syncShopForm() {
  form.shopName = userStore.userInfo.shopName || ''
  form.shopCampus = userStore.userInfo.shopCampus || userStore.userInfo.campus || campusOptions[0]
  form.shopIntro = userStore.userInfo.shopIntro || ''
}

onShow(() => {
  syncShopForm()
})

function goBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack({
      delta: 1,
    })
    return
  }

  router.replace('/mine')
}

onPageScroll(({ scrollTop }) => {
  showTopBackBar.value = scrollTop >= TOP_BAR_THRESHOLD
})

function submitShop() {
  if (!form.shopName.trim()) {
    uni.showToast({
      title: copy.invalidName,
      icon: 'none',
    })
    return
  }

  if (!form.shopIntro.trim()) {
    uni.showToast({
      title: copy.invalidIntro,
      icon: 'none',
    })
    return
  }

  userStore.registerShop({
    shopName: form.shopName.trim(),
    shopCampus: form.shopCampus,
    shopIntro: form.shopIntro.trim(),
  }).then(() => {
    uni.showToast({
      title: copy.success,
      icon: 'success',
    })

    setTimeout(() => {
      router.replace('/publish')
    }, 180)
  })
}

function updateShopName(event) {
  form.shopName = event?.detail?.value || ''
}
</script>

<template>
  <view class="app-shell shop-page">
    <view v-if="showTopBackBar" class="shop-topbar">
      <button class="shop-topbar__back" @click="goBack">
        <image class="shop-topbar__icon" src="/static/images/common/back.svg" mode="aspectFit" />
      </button>
      <view class="shop-topbar__title">
        {{ copy.title }}
      </view>
    </view>
    <button v-if="!showTopBackBar" class="shop-back shop-back--floating" @click="goBack">
      <image class="shop-back__icon" src="/static/images/common/back.svg" mode="aspectFit" />
    </button>
    <view class="shop-header">
      <view class="shop-back shop-back--placeholder"></view>
      <view class="shop-header__body">
        <view class="shop-header__title">
          {{ copy.title }}
        </view>
        <view class="shop-header__sub">
          {{ copy.subTitle }}
        </view>
      </view>
    </view>

    <view class="card-panel shop-panel">
      <view class="shop-field">
        <view class="shop-field__label">
          {{ copy.name }}
        </view>
        <input
          :value="form.shopName"
          class="shop-field__input"
          type="text"
          maxlength="40"
          :placeholder="copy.namePlaceholder"
          placeholder-class="shop-field__placeholder"
          @input="updateShopName"
        />
      </view>

      <view class="shop-field">
        <view class="shop-field__label">
          {{ copy.campus }}
        </view>
        <view class="shop-field__chips">
          <text
            v-for="item in campusOptions"
            :key="item"
            class="chip"
            :class="{ 'chip--active': form.shopCampus === item }"
            @click="form.shopCampus = item"
          >
            {{ item }}
          </text>
        </view>
      </view>

      <view class="shop-field shop-field--last">
        <view class="shop-field__label">
          {{ copy.intro }}
        </view>
        <textarea
          v-model="form.shopIntro"
          class="shop-field__textarea"
          :placeholder="copy.introPlaceholder"
        />
      </view>
    </view>

    <button class="primary-button shop-submit" @click="submitShop">
      {{ copy.submit }}
    </button>
  </view>
</template>

<style lang="scss" scoped>
.shop-page {
  min-height: 100vh;
  padding-top: calc(var(--safe-top) + 20rpx);
}

.shop-header {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;

  &__body {
    min-width: 0;
    flex: 1;
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

.shop-back {
  width: 72rpx;
  height: 72rpx;
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

.shop-topbar {
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
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.shop-panel {
  margin-top: 24rpx;
  padding: 28rpx;
}

.shop-field {
  padding: 20rpx 0;
  border-bottom: 1rpx solid rgba(17, 17, 17, 0.07);

  &--last {
    padding-bottom: 0;
    border-bottom: none;
  }

  &__label {
    margin-bottom: 14rpx;
    color: #23231f;
    font-size: 24rpx;
    font-weight: 600;
  }

  &__input,
  &__textarea {
    width: 100%;
    min-height: 82rpx;
    padding: 24rpx;
    border-radius: 22rpx;
    background: #f4f2ec;
    color: #111111;
    font-size: 26rpx;
  }

  &__placeholder {
    color: #9a9a92;
  }

  &__textarea {
    min-height: 220rpx;
    line-height: 1.6;
  }

  &__chips {
    display: flex;
    flex-wrap: wrap;
    gap: 14rpx;
  }
}

.shop-submit {
  margin-top: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 0;
  border: none;
  text-align: center;
  line-height: 88rpx;

  &::after {
    border: none;
  }
}
</style>
