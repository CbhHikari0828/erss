<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  book: {
    type: Object,
    required: true,
  },
  compact: {
    type: Boolean,
    default: false,
  },
})

const PLACEHOLDER = '/static/images/common/placeholder.png'

const router = useRouter()

const coverSrc = ref(props.book.cover)

watch(() => props.book.cover, (val) => {
  coverSrc.value = val
})

function onCoverError() {
  coverSrc.value = PLACEHOLDER
}

function openDetail() {
  router.push({
    path: '/detail',
    query: {
      id: String(props.book.id),
    },
  })
}
</script>

<template>
  <view class="book-card" :class="{ 'book-card--compact': compact }" @click="openDetail">
    <image class="book-card__cover" :src="coverSrc" mode="aspectFill" @error="onCoverError" />
    <view class="book-card__body">
      <view class="book-card__meta">
        <text class="book-card__campus">
          {{ book.campus }}
        </text>
        <text class="book-card__time">
          {{ book.publishedAt }}
        </text>
      </view>
      <view class="book-card__title">
        {{ book.title }}
      </view>
      <view class="book-card__summary">
        {{ book.summary }}
      </view>
      <view class="book-card__tags">
        <text v-for="tag in book.tags.slice(0, compact ? 1 : 2)" :key="tag" class="book-card__tag">
          {{ tag }}
        </text>
      </view>
      <view class="book-card__footer">
        <view class="book-card__price-wrap">
          <text class="book-card__price">
            ￥{{ book.price }}
          </text>
          <text class="book-card__origin">
            ￥{{ book.originalPrice }}
          </text>
        </view>
        <view class="book-card__seller">
          {{ book.seller }}
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.book-card {
  display: flex;
  gap: 22rpx;
  padding: 24rpx;
  border: 1rpx solid rgba(17, 17, 17, 0.08);
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 10rpx 30rpx rgba(17, 17, 17, 0.05);

  &--compact {
    padding: 20rpx;
  }

  &__cover {
    width: 176rpx;
    height: 220rpx;
    flex: none;
    border-radius: 22rpx;
    background: #eceae4;
  }

  &__body {
    min-width: 0;
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  &__meta,
  &__footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16rpx;
  }

  &__campus,
  &__time,
  &__origin,
  &__seller {
    font-size: 22rpx;
    color: #8a8a84;
  }

  &__title {
    margin-top: 10rpx;
    color: #111111;
    font-size: 30rpx;
    line-height: 1.4;
    font-weight: 600;
  }

  &__summary {
    margin-top: 10rpx;
    color: #575751;
    font-size: 24rpx;
    line-height: 1.5;
  }

  &__tags {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
    margin-top: 14rpx;
  }

  &__tag {
    padding: 8rpx 14rpx;
    border-radius: 999rpx;
    background: #f0eee8;
    color: #3d3d37;
    font-size: 20rpx;
  }

  &__footer {
    margin-top: auto;
    padding-top: 18rpx;
  }

  &__price-wrap {
    display: flex;
    align-items: baseline;
    gap: 10rpx;
  }

  &__price {
    color: #111111;
    font-size: 34rpx;
    font-weight: 700;
  }
}
</style>
