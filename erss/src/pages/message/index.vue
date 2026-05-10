<script setup>
import AppIcon from '@/components/AppIcon.vue'
import CustomTabBar from '@/components/CustomTabBar.vue'
import { useMessageStore } from '@/store/message/index.js'
import { formatDateTime } from '@/utils/index.js'

const router = useRouter()
const messageStore = useMessageStore()

const tabs = computed(() => messageStore.categories)
const notifications = computed(() => messageStore.filteredNotifications)
const swipedId = ref(null)
const startX = ref(0)
const startY = ref(0)

const summaryCards = computed(() => [
  {
    label: '未读通知',
    value: String(messageStore.unreadCount),
    hint: '校园助手待查看',
  },
  {
    label: '待处理',
    value: String(messageStore.pendingCount),
    hint: '订单与归还提醒',
  },
])

function selectCategory(key) {
  messageStore.setActiveCategory(key)
}

function toggleAssistant({ detail }) {
  messageStore.toggleAssistant(detail.value)
}

function markAllAsRead() {
  messageStore.markAllAsRead().then(() => {
    uni.showToast({
      title: '已全部设为已读',
      icon: 'success',
    })
  })
}

function openNotification(item) {
  messageStore.markAsRead(item.id)
}

function handleTouchStart(event, id) {
  startX.value = event.touches[0].clientX
  startY.value = event.touches[0].clientY
  if (swipedId.value !== id) {
    swipedId.value = null
  }
}

function handleTouchMove(event, id) {
  const dx = event.touches[0].clientX - startX.value
  const dy = event.touches[0].clientY - startY.value
  if (Math.abs(dx) > 10 && Math.abs(dx) > Math.abs(dy)) {
    if (dx < -40) {
      swipedId.value = id
    }
  }
}

function handleTouchEnd() {
  startX.value = 0
  startY.value = 0
}

function closeSwipe() {
  swipedId.value = null
}

async function handleDelete(item) {
  await messageStore.deleteMessage(item.id)
  swipedId.value = null
}

onShow(() => {
  messageStore.refreshMessages().catch(() => {})
})
</script>

<template>
  <view @click="closeSwipe">
    <view class="app-shell app-shell--with-tabbar">
      <view class="section-title">
        <view>
          <view class="section-title__main">
            消息
          </view>
          <view class="section-title__sub">
            只保留校园助手通知，提醒交易、租赁和系统结果
          </view>
        </view>
      </view>

      <view class="card-panel message-hero">
        <view class="message-hero__head">
          <view>
            <view class="message-hero__title">
              校园助手通知
            </view>
            <view class="message-hero__sub">
              只在关键状态变化、临期提醒和处理结果时触达你
            </view>
          </view>
          <switch :checked="messageStore.assistantEnabled" color="#111111" @change="toggleAssistant" />
        </view>
        <view class="message-summary">
          <view v-for="card in summaryCards" :key="card.label" class="message-summary__item">
            <view class="message-summary__value">
              {{ card.value }}
            </view>
            <view class="message-summary__label">
              {{ card.label }}
            </view>
            <view class="message-summary__hint">
              {{ card.hint }}
            </view>
          </view>
        </view>
      </view>

      <view class="message-toolbar">
        <scroll-view scroll-x class="message-tabs">
          <view class="message-tabs__inner">
            <view
              v-for="tab in tabs"
              :key="tab.key"
              class="message-tabs__item"
              :class="{ 'message-tabs__item--active': tab.key === messageStore.activeCategory }"
              @click="selectCategory(tab.key)"
            >
              <text>
                {{ tab.label }}
              </text>
              <text class="message-tabs__count">
                {{ tab.count }}
              </text>
            </view>
          </view>
        </scroll-view>
        <view class="message-toolbar__action" @click="markAllAsRead">
          全部已读
        </view>
      </view>

      <view v-if="notifications.length" class="message-list">
        <view
          v-for="item in notifications"
          :key="item.id"
          class="swipe-row"
        >
          <view
            class="swipe-row__delete"
            @click.stop="handleDelete(item)"
          >
            <text>删除</text>
          </view>
          <view
            class="card-panel message-item"
            :class="{ 'message-item--swiped': swipedId === item.id }"
            @click="openNotification(item)"
            @touchstart="handleTouchStart($event, item.id)"
            @touchmove="handleTouchMove($event, item.id)"
            @touchend="handleTouchEnd"
          >
            <view class="message-item__body">
              <view class="message-item__head">
                <view class="message-item__title">
                  {{ item.title }}
                </view>
                <view class="message-item__time">
                  {{ formatDateTime(item.time) }}
                </view>
              </view>
              <view class="message-item__meta">
                <text class="message-item__status">
                  {{ item.status }}
                </text>
                <text>
                  {{ item.actionText }}
                </text>
              </view>
              <view class="message-item__preview">
                {{ item.summary }}
              </view>
            </view>
            <view v-if="item.unread" class="message-item__badge"></view>
          </view>
        </view>
      </view>

      <view v-else class="card-panel message-empty">
        <view class="message-empty__title">
          当前分类没有新通知
        </view>
        <view class="message-empty__sub">
          校园助手只会在关键节点提醒，不会发送无效打扰
        </view>
      </view>
    </view>
    <CustomTabBar current="message" />
  </view>
</template>

<style lang="scss" scoped>
.message-hero {
  padding: 28rpx;
  background: linear-gradient(135deg, #f7f4ed 0%, #efe8dc 100%);

  &__head {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 20rpx;
  }

  &__title {
    color: #111111;
    font-size: 32rpx;
    font-weight: 700;
  }

  &__sub {
    margin-top: 10rpx;
    color: #66665f;
    font-size: 23rpx;
    line-height: 1.6;
  }
}

.message-summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 24rpx;

  &__item {
    padding: 20rpx;
    border-radius: 24rpx;
    background: rgba(255, 255, 255, 0.7);
  }

  &__value {
    color: #111111;
    font-size: 40rpx;
    font-weight: 700;
  }

  &__label {
    margin-top: 8rpx;
    color: #111111;
    font-size: 24rpx;
    font-weight: 600;
  }

  &__hint {
    margin-top: 6rpx;
    color: #70706a;
    font-size: 22rpx;
  }
}

.message-toolbar {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-top: 24rpx;

  &__action {
    flex: none;
    padding: 0 24rpx;
    height: 64rpx;
    display: flex;
    align-items: center;
    border-radius: 999rpx;
    background: #111111;
    color: #ffffff;
    font-size: 22rpx;
  }
}

.message-tabs {
  flex: 1;
  min-width: 0;
  white-space: nowrap;

  &__inner {
    display: inline-flex;
    gap: 14rpx;
  }

  &__item {
    display: inline-flex;
    align-items: center;
    gap: 8rpx;
    height: 64rpx;
    padding: 0 22rpx;
    border-radius: 999rpx;
    background: #f1eee6;
    color: #676761;
    font-size: 23rpx;

    &--active {
      background: #111111;
      color: #ffffff;
    }
  }

  &__count {
    font-size: 20rpx;
    opacity: 0.78;
  }
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
  margin-top: 18rpx;
}

/* 左滑删除 */
.swipe-row {
  position: relative;
  border-radius: 28rpx;
  overflow: hidden;
}

.swipe-row__delete {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  width: 160rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4rpx;
  background: #d43d3d;
  color: #ffffff;
  font-size: 26rpx;
  font-weight: 600;
  letter-spacing: 2rpx;
}

.message-item {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 18rpx;
  padding: 26rpx;
  background: #ffffff;
  border-radius: 28rpx;
  transition: transform 0.2s ease;
  z-index: 1;

  &--swiped {
    border-radius: 28rpx 0 0 28rpx;
    transform: translateX(-160rpx);
  }

  &__body {
    min-width: 0;
    flex: 1;
  }

  &__head {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 16rpx;
  }

  &__title {
    flex: 1;
    min-width: 0;
    font-size: 30rpx;
    font-weight: 700;
    color: #111111;
    line-height: 1.5;
  }

  &__time,
  &__meta {
    color: #7e7e77;
    font-size: 22rpx;
  }

  &__meta {
    display: flex;
    align-items: center;
    gap: 12rpx;
    margin-top: 8rpx;
  }

  &__status {
    padding: 4rpx 12rpx;
    border-radius: 999rpx;
    background: #f1eee6;
    color: #111111;
    font-size: 20rpx;
  }

  &__preview {
    margin-top: 10rpx;
    color: #494943;
    font-size: 24rpx;
    line-height: 1.5;
  }

  &__badge {
    width: 18rpx;
    height: 18rpx;
    margin-top: 14rpx;
    border-radius: 999rpx;
    background: #d95c38;
    flex: none;
  }
}

.message-empty {
  margin-top: 18rpx;
  padding: 40rpx 28rpx;
  text-align: center;

  &__title {
    color: #111111;
    font-size: 28rpx;
    font-weight: 700;
  }

  &__sub {
    margin-top: 10rpx;
    color: #787872;
    font-size: 23rpx;
    line-height: 1.6;
  }
}
</style>
