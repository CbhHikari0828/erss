<script setup>
import AppIcon from '@/components/AppIcon.vue'
import BookCard from '@/components/BookCard.vue'
import CustomTabBar from '@/components/CustomTabBar.vue'
import { categoryTree } from '@/constants/catalog.js'
import { useCatalogStore } from '@/store/catalog/index.js'

const catalogStore = useCatalogStore()
const activeCategory = ref(categoryTree[0].key)
const allLabel = '全部'
const activeChild = ref(allLabel)

const currentCategory = computed(() => categoryTree.find(item => item.key === activeCategory.value) || categoryTree[0])
const childOptions = computed(() => [allLabel, ...currentCategory.value.children])
const filteredBooks = computed(() => {
  return catalogStore.books.filter((book) => {
    if (book.category !== currentCategory.value.label)
      return false

    if (activeChild.value === allLabel)
      return true

    return book.subcategory === activeChild.value
  })
})

function onCategoryChange(item) {
  activeCategory.value = item.key
  activeChild.value = allLabel
}

onShow(() => {
  catalogStore.refreshBooks().catch(() => {})
})
</script>

<template>
  <view class="app-shell app-shell--with-tabbar category-page">
    <view class="section-title">
      <view>
        <view class="section-title__main">
          分类书库
        </view>
        <view class="section-title__sub">
          按课程、考试和阅读方向筛选
        </view>
      </view>
      <AppIcon name="category" size="38rpx" />
    </view>

    <view class="category-layout">
      <scroll-view scroll-y class="category-sidebar card-panel">
        <view
          v-for="item in categoryTree"
          :key="item.key"
          class="category-sidebar__item"
          :class="{ 'category-sidebar__item--active': activeCategory === item.key }"
          @click="onCategoryChange(item)"
        >
          {{ item.label }}
        </view>
      </scroll-view>

      <view class="category-main">
        <view class="card-panel category-main__head">
          <view class="category-main__title">
            {{ currentCategory.label }}
          </view>
          <view class="category-main__chips">
            <text
              v-for="item in childOptions"
              :key="item"
              class="chip"
              :class="{ 'chip--active': activeChild === item }"
              @click="activeChild = item"
            >
              {{ item }}
            </text>
          </view>
        </view>

        <scroll-view scroll-y class="category-main__list">
          <BookCard v-for="book in filteredBooks" :key="book.id" :book="book" compact />
        </scroll-view>
      </view>
    </view>
  </view>
  <CustomTabBar current="category" />
</template>

<style lang="scss" scoped>
.category-page {
  height: 100vh;
  overflow: hidden;
}

.category-layout {
  display: flex;
  gap: 18rpx;
  height: calc(100vh - var(--safe-top) - var(--safe-bottom) - 320rpx);
}

.category-sidebar {
  width: 188rpx;
  padding: 14rpx;

  &__item {
    padding: 22rpx 14rpx;
    border-radius: 22rpx;
    color: #6f6f69;
    font-size: 26rpx;
    line-height: 1.4;

    &--active {
      background: #111111;
      color: #ffffff;
      font-weight: 600;
    }
  }
}

.category-main {
  min-width: 0;
  flex: 1;

  &__head {
    padding: 24rpx;
  }

  &__title {
    font-size: 34rpx;
    font-weight: 700;
    color: #111111;
  }

  &__chips {
    display: flex;
    flex-wrap: wrap;
    gap: 14rpx;
    margin-top: 18rpx;
  }

  &__list {
    margin-top: 18rpx;
    height: calc(100% - 166rpx);
    display: flex;
    flex-direction: column;
    gap: 18rpx;
  }
}
</style>
