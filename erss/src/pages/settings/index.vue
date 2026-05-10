<script setup>
import { uploadImage } from '@/api/upload/index.js'
import CustomTabBar from '@/components/CustomTabBar.vue'
import { campusOptions } from '@/constants/catalog.js'

const userStore = useUserStore()
const router = useRouter()
const showTopBackBar = ref(false)
const TOP_BAR_THRESHOLD = 56

const isAuthed = computed(() => userStore.isLogin)

const form = reactive({
  username: '',
  avatar: '',
  department: '',
  campus: campusOptions[0],
})

const PLACEHOLDER = '/static/images/common/placeholder.png'
const avatarError = ref(false)
const avatarSrc = computed(() => avatarError.value ? PLACEHOLDER : form.avatar)

function onAvatarError() {
  avatarError.value = true
}

const submitting = ref(false)
const uploadingAvatar = ref(false)

function syncFromUser() {
  const info = userStore.userInfo || {}
  form.username = info.username || ''
  form.avatar = info.avatar || ''
  form.department = info.department || ''
  form.campus = info.campus || campusOptions[0]
}

function chooseAvatar() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      uploadingAvatar.value = true
      try {
        const data = await uploadImage(res.tempFilePaths[0], 'avatars')
        form.avatar = data.url
      } catch (err) {
        uni.showToast({ title: err.message || '头像上传失败', icon: 'none' })
      } finally {
        uploadingAvatar.value = false
      }
    },
  })
}

function goBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack({ delta: 1 })
    return
  }
  uni.reLaunch({ url: '/pages/mine/index' })
}

function pickCampus() {
  uni.showActionSheet({
    itemList: campusOptions,
    success: ({ tapIndex }) => {
      form.campus = campusOptions[tapIndex]
    },
  })
}

onPageScroll(({ scrollTop }) => {
  showTopBackBar.value = scrollTop >= TOP_BAR_THRESHOLD
})

function saveSettings() {
  if (!isAuthed.value) {
    router.push('/login')
    return
  }
  if (!form.username.trim()) {
    uni.showToast({ title: '请输入昵称', icon: 'none' })
    return
  }
  if (!form.department.trim()) {
    uni.showToast({ title: '请输入学院', icon: 'none' })
    return
  }
  submitting.value = true
  userStore.updateProfile({
    username: form.username.trim(),
    avatar: form.avatar,
    department: form.department.trim(),
    campus: form.campus,
    bio: userStore.userInfo.bio || '',
  }).then(() => {
    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => goBack(), 180)
  }).finally(() => {
    submitting.value = false
  })
}

onShow(() => {
  if (isAuthed.value)
    syncFromUser()
})
</script>

<template>
  <view>
    <view class="app-shell app-shell--with-tabbar settings-page">
      <view v-if="showTopBackBar" class="settings-topbar">
        <button class="settings-topbar__back" @click="goBack">
          <image class="settings-topbar__icon" src="/static/images/common/back.svg" mode="aspectFit" />
        </button>
        <view class="settings-topbar__title">
          编辑资料
        </view>
      </view>
      <button v-if="!showTopBackBar" class="settings-back settings-back--floating" @click="goBack">
        <image class="settings-back__icon" src="/static/images/common/back.svg" mode="aspectFit" />
      </button>
      <view class="settings-head">
        <view class="settings-back settings-back--placeholder"></view>
        <view class="settings-head__body">
          <view class="settings-head__title">
            编辑资料
          </view>
          <view class="settings-head__sub">
            修改昵称、头像、学院和校区
          </view>
        </view>
      </view>

      <view v-if="!isAuthed" class="card-panel login-card">
        <view class="login-card__title">
          登录后再编辑
        </view>
        <view class="login-card__text">
          未登录时可以预览该页，但修改信息需要先登录。
        </view>
        <button class="primary-button settings-submit" @click="router.push('/login')">
          去登录
        </button>
      </view>

      <view v-else class="settings-form">
        <view class="avatar-card card-panel" @click="chooseAvatar">
          <image
            v-if="form.avatar"
            class="avatar-card__img"
            :src="avatarSrc"
            mode="aspectFill"
            @error="onAvatarError"
          />
          <view v-else class="avatar-card__img avatar-card__img--placeholder">
            <view class="i-carbon-camera" />
          </view>
          <view class="avatar-card__body">
            <view class="avatar-card__title">
              头像
            </view>
            <view class="avatar-card__hint">
              {{ uploadingAvatar ? '上传中...' : '点击更换头像' }}
            </view>
          </view>
        </view>

        <view class="form-row">
          <text class="form-label">
            昵称
          </text>
          <input
            v-model="form.username"
            class="form-input"
            type="text"
            placeholder="请输入昵称"
          />
        </view>

        <view class="form-row">
          <text class="form-label">
            学院
          </text>
          <input
            v-model="form.department"
            class="form-input"
            type="text"
            placeholder="请输入学院"
          />
        </view>

        <view class="form-row" @click="pickCampus">
          <text class="form-label">
            校区
          </text>
          <view class="picker-input">
            <text>{{ form.campus }}</text>
            <view class="i-carbon-chevron-right"></view>
          </view>
        </view>

        <button class="primary-button settings-submit" :disabled="submitting" @click="saveSettings">
          {{ submitting ? '保存中...' : '保存修改' }}
        </button>
      </view>
      <CustomTabBar current="mine" />
    </view>
  </view>
</template>

<style lang="scss" scoped>
.settings-page {
  min-height: 100vh;
  padding-top: calc(var(--safe-top) + 20rpx);
}

.settings-head {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;
  margin-bottom: 34rpx;

  &__body {
    flex: 1;
    min-width: 0;
    padding-top: 4rpx;
  }

  &__title {
    font-size: 52rpx;
    line-height: 1.14;
    font-weight: 700;
    color: #111111;
    letter-spacing: 1rpx;
  }

  &__sub {
    margin-top: 12rpx;
    color: #6f6f68;
    font-size: 24rpx;
    line-height: 1.7;
    max-width: 560rpx;
  }
}

.settings-back {
  width: 72rpx;
  height: 72rpx;
  flex: none;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0;
  padding: 0;
  border: none;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.96);
  box-shadow:
    0 4rpx 12rpx rgba(17, 17, 17, 0.04),
    0 14rpx 30rpx rgba(17, 17, 17, 0.08);

  &::after {
    border: none;
  }

  &__icon {
    width: 32rpx;
    height: 32rpx;
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

.settings-topbar {
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
  box-shadow:
    0 4rpx 12rpx rgba(17, 17, 17, 0.04),
    0 12rpx 28rpx rgba(17, 17, 17, 0.06);
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
    width: 32rpx;
    height: 32rpx;
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

.login-card {
  padding: 34rpx 30rpx;
  border: 1rpx solid rgba(17, 17, 17, 0.06);
  border-radius: 32rpx;
  background: rgba(255, 255, 255, 0.94);
  box-shadow:
    0 4rpx 12rpx rgba(17, 17, 17, 0.03),
    0 18rpx 42rpx rgba(17, 17, 17, 0.05);

  &__title {
    font-size: 34rpx;
    font-weight: 700;
    color: #111111;
  }

  &__text {
    margin-top: 14rpx;
    color: #70706a;
    font-size: 24rpx;
    line-height: 1.7;
  }
}

.settings-form {
  display: flex;
  flex-direction: column;
  gap: 22rpx;
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  padding: 24rpx;
  border: 1rpx solid rgba(17, 17, 17, 0.06);
  border-radius: 30rpx;
  background: rgba(255, 255, 255, 0.94);
  box-shadow:
    0 4rpx 12rpx rgba(17, 17, 17, 0.02),
    0 14rpx 34rpx rgba(17, 17, 17, 0.04);
  transition: border-color 0.2s ease;

  &:focus-within {
    border-color: rgba(17, 17, 17, 0.16);
  }
}

.form-label {
  color: #5d5d57;
  font-size: 24rpx;
  font-weight: 600;
}

.form-input {
  width: 100%;
  min-height: 96rpx;
  padding: 24rpx;
  border: 1rpx solid rgba(17, 17, 17, 0.08);
  border-radius: 22rpx;
  background: #f6f4ee;
  color: #111111;
  font-size: 28rpx;
  transition: border-color 0.2s ease;

  &:focus {
    border-color: rgba(17, 17, 17, 0.22);
  }
}

.avatar-card {
  display: flex;
  align-items: center;
  gap: 22rpx;
  padding: 24rpx 28rpx;
  transition: transform 0.15s ease;

  &:active {
    transform: scale(0.985);
  }

  &__img {
    width: 100rpx;
    height: 100rpx;
    border-radius: 28rpx;
    background: #ece8df;
    box-shadow: 0 6rpx 18rpx rgba(17, 17, 17, 0.08);

    &--placeholder {
      display: flex;
      align-items: center;
      justify-content: center;
      color: #a09d95;
      font-size: 36rpx;
    }
  }

  &__body {
    min-width: 0;
    flex: 1;
  }

  &__title {
    font-size: 30rpx;
    font-weight: 600;
    color: #111111;
  }

  &__hint {
    margin-top: 8rpx;
    color: #90908a;
    font-size: 24rpx;
  }
}

.picker-input {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  min-height: 96rpx;
  padding: 24rpx;
  border: 1rpx solid rgba(17, 17, 17, 0.08);
  border-radius: 22rpx;
  background: #f6f4ee;
  color: #111111;
  font-size: 28rpx;
}

.settings-submit {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  margin-top: 12rpx;
  padding: 0;
  border: none;
  text-align: center;
  line-height: 88rpx;

  &::after {
    border: none;
  }
}
</style>
