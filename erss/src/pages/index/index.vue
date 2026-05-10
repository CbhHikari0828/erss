<script setup>
import AppIcon from '@/components/AppIcon.vue'
import BookCard from '@/components/BookCard.vue'
import CustomTabBar from '@/components/CustomTabBar.vue'
import { bannerCards, quickCategories } from '@/constants/catalog.js'
import { useCatalogStore } from '@/store/catalog/index.js'

const router = useRouter()
const catalogStore = useCatalogStore()

const quickCategoryIcons = [
  '/static/images/quick-categories/textbook.svg',
  '/static/images/quick-categories/exam.svg',
  '/static/images/quick-categories/language.svg',
  '/static/images/quick-categories/reading.svg',
]

const featuredBooks = computed(() => catalogStore.books.slice(0, 4))
const freshBooks = computed(() => catalogStore.books.slice(2))

function openSearch() {
  router.push('/search')
}

function openCategory() {
  router.push('/category')
}

onShow(() => {
  catalogStore.refreshBooks().catch(() => {})
})
</script>

<template>
  <view>
    <scroll-view scroll-y class="app-shell app-shell--with-tabbar">
      <view class="hero-search card-panel" @click="openSearch">
        <view class="hero-search__icon i-carbon-search"></view>
        <view class="hero-search__text">
          搜教材、考研书、语言证书和课外读物
        </view>
        <view class="hero-search__hint">
          同校面交
        </view>
      </view>

      <scroll-view scroll-x class="hero-banner">
        <image
          v-for="item in bannerCards"
          :key="item.title"
          class="hero-banner__card"
          :src="item.image"
          mode="aspectFill"
        />
      </scroll-view>

      <view class="section-title section-gap">
        <view>
          <view class="section-title__main">
            快速分区
          </view>
          <view class="section-title__sub">
            教材教辅、考研资料、语言证书、课外阅读都能快速找到
          </view>
        </view>
        <view class="section-link-wrap">
          <AppIcon name="home" size="34rpx" />
          <view class="section-link" @click="openCategory">
            全部分区
          </view>
        </view>
      </view>

      <view class="category-grid">
        <view v-for="(item, index) in quickCategories" :key="item.label" class="category-grid__item card-panel" @click="openCategory">
          <view class="category-grid__icon">
            <image class="category-grid__icon-image" :src="quickCategoryIcons[index]" mode="aspectFit" />
          </view>
          <view class="category-grid__label">
            {{ item.label }}
          </view>
        </view>
      </view>

      <view class="section-title section-gap">
        <view>
          <view class="section-title__main">
            今日推荐
          </view>
          <view class="section-title__sub">
            低价、成色稳定，适合同校交易
          </view>
        </view>
      </view>

      <view class="book-list">
        <BookCard v-for="book in featuredBooks" :key="book.id" :book="book" />
      </view>

      <view class="section-title section-gap">
        <view>
          <view class="section-title__main">
            刚刚上架
          </view>
          <view class="section-title__sub">
            新发布的书，通常更容易约到面交
          </view>
        </view>
      </view>

      <view class="fresh-list">
        <BookCard v-for="book in freshBooks" :key="book.id" :book="book" compact />
      </view>
    </scroll-view>
    <CustomTabBar current="home" />
  </view>
</template>

<style lang="scss" scoped>
.section-gap {
  margin-top: 34rpx;
}

.section-link {
  color: #6b6b65;
  font-size: 24rpx;
}

.section-link-wrap {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.hero-search {
  display: flex;
  align-items: center;
  gap: 18rpx;
  padding: 24rpx 26rpx;

  &__icon,
  &__hint {
    color: #767670;
    font-size: 24rpx;
  }

  &__text {
    flex: 1;
    color: #91918a;
    font-size: 26rpx;
  }

  &__hint {
    padding: 8rpx 14rpx;
    border-radius: 999rpx;
    background: #f1efe8;
  }
}

.hero-banner {
  display: flex;
  gap: 20rpx;
  margin-top: 26rpx;
  white-space: nowrap;

  &__card {
    display: inline-block;
    width: 620rpx;
    height: 300rpx;
    border-radius: 34rpx;
    box-shadow: 0 18rpx 50rpx rgba(17, 17, 17, 0.14);
  }
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;

  &__item {
    padding: 28rpx;
  }

  &__icon {
    width: 72rpx;
    height: 72rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 22rpx;
    background: #f2f0ea;
  }

  &__icon-image {
    width: 42rpx;
    height: 42rpx;
  }

  &__label {
    margin-top: 34rpx;
    font-size: 30rpx;
    font-weight: 600;
    color: #161616;
  }
}

.book-list,
.fresh-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
</style>
