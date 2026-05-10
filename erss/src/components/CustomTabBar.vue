<script setup>
import { useMessageStore } from '@/store/message/index.js'
import AppIcon from './AppIcon.vue'

const props = defineProps({
  current: {
    type: String,
    required: true,
  },
})

const messageStore = useMessageStore()

const tabs = [
  {
    key: 'home',
    label: '首页',
    url: '/pages/index/index',
  },
  {
    key: 'category',
    label: '分类',
    url: '/pages/category/index',
  },
  {
    key: 'publish',
    label: '发布',
    url: '/pages/publish/index',
  },
  {
    key: 'message',
    label: '消息',
    url: '/pages/message/index',
  },
  {
    key: 'mine',
    label: '我的',
    url: '/pages/mine/index',
  },
]

function switchPage(tab) {
  if (tab.key === props.current)
    return

  uni.reLaunch({
    url: tab.url,
  })
}
</script>

<template>
  <view class="custom-tabbar">
    <view class="custom-tabbar__inner">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        class="custom-tabbar__item"
        :class="{ 'custom-tabbar__item--active': current === tab.key }"
        @click="switchPage(tab)"
      >
        <view class="custom-tabbar__icon-wrap">
          <AppIcon :name="tab.key" :active="current === tab.key" size="34rpx" />
          <view v-if="tab.key === 'message' && messageStore.unreadCount" class="custom-tabbar__badge">
            {{ messageStore.unreadCount > 99 ? '99+' : messageStore.unreadCount }}
          </view>
        </view>
        <text class="custom-tabbar__label">
          {{ tab.label }}
        </text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.custom-tabbar {
  position: fixed;
  left: 20rpx;
  right: 20rpx;
  bottom: 20rpx;
  z-index: 50;
  padding-bottom: var(--safe-bottom);

  &__inner {
    display: flex;
    align-items: center;
    gap: 10rpx;
    padding: 12rpx;
    border: 1rpx solid rgba(17, 17, 17, 0.08);
    border-radius: 34rpx;
    background: rgba(252, 252, 249, 0.92);
    box-shadow: 0 18rpx 48rpx rgba(17, 17, 17, 0.1);
    backdrop-filter: blur(24rpx);
  }

  &__item {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    padding: 12rpx 6rpx;
    border-radius: 24rpx;
    color: #83837d;
    transition: all 0.2s ease;

    &--active {
      background: #111111;
      color: #ffffff;
    }
  }

  &__icon-wrap {
    position: relative;
    width: 56rpx;
    height: 56rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__icon {
    width: 34rpx;
    height: 34rpx;
    opacity: 0.72;
  }

  &__badge {
    position: absolute;
    top: 0;
    right: -2rpx;
    min-width: 32rpx;
    height: 32rpx;
    padding: 0 8rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 4rpx solid rgba(252, 252, 249, 0.92);
    border-radius: 999rpx;
    background: #d95c38;
    color: #fff;
    font-size: 18rpx;
    line-height: 1;
    font-weight: 700;
  }

  &__label {
    font-size: 20rpx;
    line-height: 1;
  }
}
</style>
