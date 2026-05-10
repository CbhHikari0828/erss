<script setup>
import BookCard from '@/components/BookCard.vue'
import { hotKeywords, searchHistory } from '@/constants/catalog.js'
import { useCatalogStore } from '@/store/catalog/index.js'

const router = useRouter()
const catalogStore = useCatalogStore()
const keyword = ref('')
const showTopBackBar = ref(false)
const TOP_BAR_THRESHOLD = 56

const results = computed(() => {
  const availableBooks = catalogStore.books
  const value = keyword.value.trim().toLowerCase()
  if (!value)
    return availableBooks
  return availableBooks.filter((book) => {
    return [book.title, book.category, book.subcategory, book.summary].some(item => item.toLowerCase().includes(value))
  })
})

function fillKeyword(value) {
  keyword.value = value
}

function goBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack({
      delta: 1,
    })
    return
  }

  uni.reLaunch({
    url: '/pages/index/index',
  })
}

function handleScroll(event) {
  showTopBackBar.value = event.detail.scrollTop >= TOP_BAR_THRESHOLD
}

onShow(() => {
  catalogStore.refreshBooks().catch(() => {})
})
</script>

<template>
  <view class="search-page">
    <view v-if="showTopBackBar" class="search-topbar">
      <button class="search-topbar__back" @click="goBack">
        <image class="search-topbar__icon" src="/static/images/common/back.svg" mode="aspectFit" />
      </button>
      <view class="search-topbar__title">
        搜索
      </view>
    </view>
    <button v-if="!showTopBackBar" class="search-back search-back--floating" @click="goBack">
      <image class="search-back__icon" src="/static/images/common/back.svg" mode="aspectFit" />
    </button>

    <scroll-view scroll-y class="app-shell search-scroll" @scroll="handleScroll">
      <view class="search-toolbar">
        <view class="search-back search-back--placeholder"></view>
        <view class="search-bar card-panel">
          <input v-model="keyword" class="search-bar__input" placeholder="搜索书名、课程、考试关键词" />
          <view class="search-bar__icon i-carbon-search"></view>
        </view>
      </view>

      <view class="search-block">
        <view class="search-block__title">
          搜索历史
        </view>
        <view class="search-block__chips">
          <text v-for="item in searchHistory" :key="item" class="chip" @click="fillKeyword(item)">
            {{ item }}
          </text>
        </view>
      </view>

      <view class="search-block">
        <view class="search-block__title">
          热门搜索
        </view>
        <view class="search-block__chips">
          <text v-for="item in hotKeywords" :key="item" class="chip" @click="fillKeyword(item)">
            {{ item }}
          </text>
        </view>
      </view>

      <view class="section-title">
        <view>
          <view class="section-title__main">
            搜索结果
          </view>
          <view class="section-title__sub">
            共 {{ results.length }} 本书
          </view>
        </view>
      </view>

      <view class="search-results">
        <BookCard v-for="book in results" :key="book.id" :book="book" compact />
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
.search-page {
  position: relative;
  height: 100vh;
}

.search-scroll {
  height: 100%;
  padding-top: calc(var(--safe-top) + 20rpx);
}

.search-toolbar {
  display: flex;
  align-items: center;
  gap: 18rpx;
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 24rpx;
  flex: 1;

  &__icon {
    color: #75756f;
  }

  &__input {
    flex: 1;
    font-size: 28rpx;
    color: #111111;
  }
}

.search-back {
  width: 68rpx;
  height: 68rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: none;
  padding: 0;
  border: none;
  border-radius: 20rpx;
  background: #f3f0e8;
  box-shadow: 0 10rpx 24rpx rgba(17, 17, 17, 0.08);

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

.search-topbar {
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

.search-block {
  margin-top: 28rpx;

  &__title {
    margin-bottom: 16rpx;
    color: #61615b;
    font-size: 24rpx;
    font-weight: 600;
  }

  &__chips {
    display: flex;
    flex-wrap: wrap;
    gap: 14rpx;
  }
}

.search-results {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
}
</style>
