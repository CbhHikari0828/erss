<script setup>
import CustomTabBar from '@/components/CustomTabBar.vue'

const userStore = useUserStore()
const router = useRouter()

const showPwdForm = ref(false)
const pwdForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
})
const submitting = ref(false)

function goBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack({ delta: 1 })
    return
  }
  uni.reLaunch({ url: '/pages/mine/index' })
}

function togglePwdForm() {
  showPwdForm.value = !showPwdForm.value
  if (!showPwdForm.value) {
    pwdForm.currentPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  }
}

function changePassword() {
  if (!pwdForm.currentPassword) {
    uni.showToast({ title: '请输入当前密码', icon: 'none' })
    return
  }
  if (pwdForm.newPassword.length < 6 || pwdForm.newPassword.length > 20) {
    uni.showToast({ title: '新密码长度需为 6-20 位', icon: 'none' })
    return
  }
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    uni.showToast({ title: '两次输入的新密码不一致', icon: 'none' })
    return
  }
  const info = userStore.userInfo || {}
  submitting.value = true
  userStore.updateProfile({
    username: info.username || '',
    department: info.department || '',
    campus: info.campus || '',
    bio: info.bio || '',
    avatar: info.avatar || '',
    currentPassword: pwdForm.currentPassword,
    newPassword: pwdForm.newPassword,
  }).then(() => {
    uni.showToast({ title: '密码修改成功', icon: 'success' })
    togglePwdForm()
  }).finally(() => {
    submitting.value = false
  })
}

function logout() {
  uni.showModal({
    title: '退出登录',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.showToast({ title: '已退出登录', icon: 'success' })
        setTimeout(() => {
          uni.reLaunch({ url: '/pages/mine/index' })
        }, 300)
      }
    },
  })
}
</script>

<template>
  <view>
    <view class="app-shell app-shell--with-tabbar menu-page">
      <view class="menu-head">
        <button class="menu-back" @click="goBack">
          <image class="menu-back__icon" src="/static/images/common/back.svg" mode="aspectFit" />
        </button>
        <view class="menu-head__body">
          <view class="menu-head__title">
            设置
          </view>
        </view>
      </view>

      <view class="menu-list">
        <view class="menu-item card-panel" @click="togglePwdForm">
          <view class="menu-item__body">
            <view class="menu-item__title">修改密码</view>
            <view class="menu-item__hint">更改账户登录密码</view>
          </view>
          <view class="menu-item__arrow" :class="{ 'menu-item__arrow--open': showPwdForm }">
            >
          </view>
        </view>

        <view v-if="showPwdForm" class="pwd-form card-panel">
          <view class="form-row">
            <text class="form-label">当前密码</text>
            <input
              v-model="pwdForm.currentPassword"
              class="form-input"
              type="text"
              password
              placeholder="请输入当前密码"
            />
          </view>
          <view class="form-row">
            <text class="form-label">新密码</text>
            <input
              v-model="pwdForm.newPassword"
              class="form-input"
              type="text"
              password
              placeholder="请输入新密码（6-20位）"
            />
          </view>
          <view class="form-row">
            <text class="form-label">确认新密码</text>
            <input
              v-model="pwdForm.confirmPassword"
              class="form-input"
              type="text"
              password
              placeholder="请再次输入新密码"
            />
          </view>
          <button class="primary-button pwd-form__btn" :disabled="submitting" @click="changePassword">
            {{ submitting ? '提交中...' : '确认修改' }}
          </button>
        </view>

        <view class="menu-item card-panel" @click="logout">
          <view class="menu-item__body">
            <view class="menu-item__title menu-item__title--danger">退出登录</view>
            <view class="menu-item__hint">退出当前账户</view>
          </view>
          <view class="menu-item__arrow">
            >
          </view>
        </view>
      </view>

      <CustomTabBar current="mine" />
    </view>
  </view>
</template>

<style lang="scss" scoped>
.menu-page {
  min-height: 100vh;
  padding-top: calc(var(--safe-top) + 20rpx);
}

.menu-head {
  display: flex;
  align-items: center;
  gap: 18rpx;
  margin-bottom: 34rpx;
  padding: 0 24rpx;

  &__body {
    flex: 1;
    min-width: 0;
  }

  &__title {
    font-size: 52rpx;
    line-height: 1.14;
    font-weight: 700;
    color: #111111;
    letter-spacing: 1rpx;
  }
}

.menu-back {
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
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
  padding: 0 24rpx;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  padding: 26rpx 28rpx;
  transition: transform 0.15s ease;

  &:active {
    transform: scale(0.985);
  }

  &__body {
    min-width: 0;
    flex: 1;
  }

  &__title {
    font-size: 30rpx;
    font-weight: 600;
    color: #111111;

    &--danger {
      color: #d14343;
    }
  }

  &__hint {
    margin-top: 8rpx;
    color: #90908a;
    font-size: 24rpx;
  }

  &__arrow {
    color: #a09d95;
    font-size: 28rpx;
    font-weight: 600;
    transition: transform 0.2s ease;

    &--open {
      transform: rotate(90deg);
    }
  }
}

.pwd-form {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
  padding: 28rpx;
  border: 1rpx solid rgba(17, 17, 17, 0.06);
  border-radius: 30rpx;
  background: rgba(255, 255, 255, 0.94);
  box-shadow:
    0 4rpx 12rpx rgba(17, 17, 17, 0.02),
    0 14rpx 34rpx rgba(17, 17, 17, 0.04);

  &__btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    margin-top: 6rpx;
    padding: 0;
    border: none;
    text-align: center;
    line-height: 80rpx;
    font-size: 26rpx;

    &::after {
      border: none;
    }
  }
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.form-label {
  color: #5d5d57;
  font-size: 24rpx;
  font-weight: 600;
}

.form-input {
  width: 100%;
  min-height: 84rpx;
  padding: 20rpx 24rpx;
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
</style>
