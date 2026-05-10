<script setup>
import { uploadImages } from '@/api/upload/index.js'
import AppIcon from '@/components/AppIcon.vue'
import CustomTabBar from '@/components/CustomTabBar.vue'
import { campusOptions, categoryTree, conditionOptions } from '@/constants/catalog.js'
import { useCatalogStore } from '@/store/catalog/index.js'
import { ensureLogin } from '@/utils/auth.js'

const copy = {
  title: '发布闲置书',
  subTitle: '用简洁清晰的方式展示书况和交易信息',
  image: '图片',
  cover: '封面图',
  extra: '补充图',
  name: '书名',
  namePlaceholder: '例如：高等数学 同济第七版 上册',
  sellPrice: '售价',
  originalPrice: '原价',
  rental: '租赁',
  enableRental: '开启租赁',
  rentPrice: '日租金',
  rentalDeposit: '押金',
  category: '分类',
  condition: '成色',
  campus: '校区',
  description: '描述',
  descriptionPlaceholder: '写明是否有笔记、划线、是否支持小刀、约见时间等。',
  tipsTitle: '发布建议',
  tipsText: '封面图尽量拍清楚书脊和成色，描述里说明课程名称、版本和交易地点，成交率会更高。',
  submit: '发布图书',
  draftCreated: '已生成发布草稿',
  loginToPublish: '发布图书需要先登录，登录后才能继续发布。',
  shopGateTitle: '先注册商铺',
  shopGateText: '发布书籍之前，需要先完成商铺注册，用来展示你的商铺名称、校区和简介。',
  shopGateButton: '去注册商铺',
  shopReadyTitle: '已开通商铺',
  shopReadyTextPrefix: '当前商铺：',
}

const userStore = useUserStore()
const catalogStore = useCatalogStore()
const router = useRouter()

const form = reactive({
  title: '',
  price: '',
  originalPrice: '',
  rentalEnabled: false,
  rentPrice: '',
  rentalDeposit: '',
  category: categoryTree[0].label,
  condition: conditionOptions[0],
  campus: campusOptions[0],
  description: '',
})

const images = ref([])
const publishing = ref(false)

const isReadyToPublish = computed(() => userStore.isLogin && userStore.hasShop)

function goRegisterShop() {
  ensureLogin(userStore, () => {
    router.push('/shop')
  }, {
    content: copy.loginToPublish,
    onConfirm: () => router.push('/login'),
  })
}

function chooseImage() {
  uni.chooseImage({
    count: 3,
    success: ({ tempFilePaths }) => {
      images.value = [...images.value, ...tempFilePaths].slice(0, 3)
    },
  })
}

function toggleRental(event) {
  form.rentalEnabled = Boolean(event.detail.value)
}

function updateField(key, event) {
  form[key] = event.detail?.value ?? event.target?.value ?? ''
}

function submitPublish() {
  if (publishing.value)
    return

  ensureLogin(userStore, async () => {
    if (!userStore.hasShop) {
      uni.showModal({
        title: copy.shopGateTitle,
        content: copy.shopGateText,
        confirmText: '去注册',
        cancelText: '先看看',
        success: ({ confirm }) => {
          if (confirm)
            router.push('/shop')
        },
      })
      return
    }

    const price = Number(form.price || 0)
    const rentPrice = Number(form.rentPrice || 0)
    const rentalDeposit = Number(form.rentalDeposit || 0)
    if (!price || price <= 0) {
      uni.showToast({
        title: '请填写有效的售价',
        icon: 'none',
      })
      return
    }
    if (form.rentalEnabled && (!rentPrice || rentPrice <= 0 || !rentalDeposit || rentalDeposit <= 0)) {
      uni.showToast({
        title: '请填写有效的租金和押金',
        icon: 'none',
      })
      return
    }

    publishing.value = true
    try {
      const uploadedImages = await uploadImages(images.value, 'books')
      const book = await catalogStore.createMineBook({
        title: form.title.trim(),
        category: form.category,
        subcategory: '',
        price,
        originalPrice: Number(form.originalPrice || 0),
        rentalEnabled: form.rentalEnabled,
        rentPrice: form.rentalEnabled ? rentPrice : 0,
        rentalDeposit: form.rentalEnabled ? rentalDeposit : 0,
        condition: form.condition,
        campus: form.campus,
        cover: uploadedImages[0] || '',
        summary: form.description.trim().slice(0, 60),
        description: form.description.trim(),
        tags: [],
        images: uploadedImages,
      })
      uni.showToast({
        title: '已发布成功',
        icon: 'success',
      })
      form.title = ''
      form.price = ''
      form.originalPrice = ''
      form.rentalEnabled = false
      form.rentPrice = ''
      form.rentalDeposit = ''
      form.description = ''
      images.value = []
      setTimeout(() => {
        router.push({ path: '/detail', query: { id: book.id } })
      }, 800)
    }
    catch (err) {
      uni.showToast({
        title: err?.message || '发布失败',
        icon: 'none',
      })
    }
    finally {
      publishing.value = false
    }
  }, {
    content: copy.loginToPublish,
    onConfirm: () => router.push('/login'),
  })
}
</script>

<template>
  <view>
    <scroll-view scroll-y class="app-shell app-shell--with-tabbar">
      <view class="section-title">
        <view>
          <view class="section-title__main">
            {{ copy.title }}
          </view>
          <view class="section-title__sub">
            {{ copy.subTitle }}
          </view>
        </view>
        <AppIcon name="publish" size="38rpx" />
      </view>

      <view v-if="!isReadyToPublish" class="card-panel publish-gate">
        <view class="publish-gate__title">
          {{ userStore.hasShop ? copy.shopReadyTitle : copy.shopGateTitle }}
        </view>
        <view class="publish-gate__text">
          {{ userStore.hasShop ? `${copy.shopReadyTextPrefix}${userStore.userInfo.shopName}` : copy.shopGateText }}
        </view>
        <button class="primary-button publish-gate__button" @click="goRegisterShop">
          {{ copy.shopGateButton }}
        </button>
      </view>

      <template v-else>
        <view class="card-panel publish-panel">
          <view class="publish-panel__label">
            {{ copy.image }}
          </view>
          <view class="upload-grid">
            <view v-for="index in 3" :key="index" class="upload-grid__item" @click="chooseImage">
              <image v-if="images[index - 1]" class="upload-grid__preview" :src="images[index - 1]" mode="aspectFill" />
              <template v-else>
                <view class="i-carbon-add upload-grid__icon"></view>
                <view class="upload-grid__text">
                  {{ index === 1 ? copy.cover : copy.extra }}
                </view>
              </template>
            </view>
          </view>
        </view>

        <view class="card-panel publish-panel">
          <view class="field">
            <view class="field__label">
              {{ copy.name }}
            </view>
            <input
              :value="form.title"
              class="field__input"
              type="text"
              maxlength="80"
              :placeholder="copy.namePlaceholder"
              placeholder-class="field__placeholder"
              @input="updateField('title', $event)"
            />
          </view>

          <view class="field field--double">
            <view class="field__col">
              <view class="field__label">
                {{ copy.sellPrice }}
              </view>
              <input v-model="form.price" class="field__input" type="number" placeholder="18" />
            </view>
            <view class="field__col">
              <view class="field__label">
                {{ copy.originalPrice }}
              </view>
              <input v-model="form.originalPrice" class="field__input" type="number" placeholder="49.8" />
            </view>
          </view>

          <view class="field">
            <view class="field__switch-row">
              <view>
                <view class="field__label">
                  {{ copy.rental }}
                </view>
                <view class="field__hint">
                  购买价格必须发布，租赁可单独开启或关闭
                </view>
              </view>
              <switch :checked="form.rentalEnabled" color="#111111" @change="toggleRental" />
            </view>
          </view>

          <view v-if="form.rentalEnabled" class="field field--double">
            <view class="field__col">
              <view class="field__label">
                {{ copy.rentPrice }}
              </view>
              <input v-model="form.rentPrice" class="field__input" type="number" placeholder="4" />
            </view>
            <view class="field__col">
              <view class="field__label">
                {{ copy.rentalDeposit }}
              </view>
              <input v-model="form.rentalDeposit" class="field__input" type="number" placeholder="30" />
            </view>
          </view>

          <view class="field">
            <view class="field__label">
              {{ copy.category }}
            </view>
            <view class="field__chips">
              <text
                v-for="item in categoryTree"
                :key="item.key"
                class="chip"
                :class="{ 'chip--active': form.category === item.label }"
                @click="form.category = item.label"
              >
                {{ item.label }}
              </text>
            </view>
          </view>

          <view class="field">
            <view class="field__label">
              {{ copy.condition }}
            </view>
            <view class="field__chips">
              <text
                v-for="item in conditionOptions"
                :key="item"
                class="chip"
                :class="{ 'chip--active': form.condition === item }"
                @click="form.condition = item"
              >
                {{ item }}
              </text>
            </view>
          </view>

          <view class="field">
            <view class="field__label">
              {{ copy.campus }}
            </view>
            <view class="field__chips">
              <text
                v-for="item in campusOptions"
                :key="item"
                class="chip"
                :class="{ 'chip--active': form.campus === item }"
                @click="form.campus = item"
              >
                {{ item }}
              </text>
            </view>
          </view>

          <view class="field">
            <view class="field__label">
              {{ copy.description }}
            </view>
            <textarea
              v-model="form.description"
              class="field__textarea"
              :placeholder="copy.descriptionPlaceholder"
            />
          </view>
        </view>

        <view class="card-panel publish-tip">
          <view class="publish-tip__title">
            {{ copy.tipsTitle }}
          </view>
          <view class="publish-tip__text">
            {{ copy.tipsText }}
          </view>
        </view>
      </template>

      <button v-if="isReadyToPublish" class="primary-button publish-submit" :disabled="publishing" @click="submitPublish">
        {{ publishing ? '发布中...' : copy.submit }}
      </button>
    </scroll-view>
    <CustomTabBar current="publish" />
  </view>
</template>

<style lang="scss" scoped>
.publish-gate,
.publish-panel,
.publish-tip {
  margin-top: 22rpx;
  padding: 28rpx;
}

.publish-gate {
  &__title {
    color: #111111;
    font-size: 30rpx;
    font-weight: 700;
  }

  &__text {
    margin-top: 14rpx;
    color: #676761;
    font-size: 24rpx;
    line-height: 1.7;
  }

  &__button {
    margin-top: 24rpx;
  }
}

.upload-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 18rpx;

  &__item {
    height: 182rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 12rpx;
    border: 1rpx dashed rgba(17, 17, 17, 0.18);
    border-radius: 24rpx;
    background: #f5f3ed;
  }

  &__icon {
    font-size: 34rpx;
    color: #55554f;
  }

  &__text {
    color: #777771;
    font-size: 22rpx;
  }
}

.field {
  padding: 22rpx 0;
  border-bottom: 1rpx solid rgba(17, 17, 17, 0.07);

  &:last-child {
    padding-bottom: 0;
    border-bottom: none;
  }

  &--double {
    display: flex;
    gap: 18rpx;
  }

  &__col {
    flex: 1;
  }

  &__label {
    margin-bottom: 14rpx;
    color: #272723;
    font-size: 24rpx;
    font-weight: 600;
  }

  &__switch-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 24rpx;
  }

  &__hint {
    color: #777771;
    font-size: 22rpx;
    line-height: 1.5;
  }

  &__input,
  &__textarea {
    width: 100%;
    min-height: 82rpx;
    border-radius: 22rpx;
    background: #f4f2ec;
    padding: 24rpx;
    font-size: 26rpx;
    color: #111111;
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

.publish-tip {
  &__title {
    font-size: 28rpx;
    font-weight: 700;
    color: #111111;
  }

  &__text {
    margin-top: 12rpx;
    color: #696963;
    font-size: 24rpx;
    line-height: 1.7;
  }
}

.publish-submit {
  margin-top: 26rpx;
}

.publish-gate__button,
.publish-submit {
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
