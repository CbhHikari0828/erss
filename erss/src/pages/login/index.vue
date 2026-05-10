<script setup>
const userStore = useUserStore()

const copy = {
  welcomeBack: '欢迎回来',
  createAccount: '创建校园账号',
  loginDesc: '使用学生号和密码登录，查看校内二手教材与交易信息。',
  registerDesc: '使用学生号完成注册，后续可直接登录发布和购买教材。',
  login: '登录',
  register: '注册',
  studentLogin: '学生账号登录',
  studentRegister: '学生账号注册',
  formSub: '仅限本校学生使用，请填写学生号和密码。',
  studentId: '学生号',
  password: '密码',
  confirmPassword: '确认密码',
  studentIdPlaceholder: '请输入学生号',
  passwordPlaceholder: '请输入密码',
  confirmPasswordPlaceholder: '请再次输入密码',
  studentIdRule: '学生号需为 6-20 位数字',
  passwordRule: '密码长度 6-20 位',
  agreement: '我已阅读并同意《校园二手书交易须知》',
  registerAndEnter: '注册并进入',
  noAccount: '还没有账号？',
  hasAccount: '已有账号？',
  goRegister: '去注册',
  goLogin: '去登录',
  enterStudentId: '请输入学生号',
  invalidStudentId: '学生号格式不正确',
  enterPassword: '请输入密码',
  invalidPasswordLength: '密码长度需为 6-20 位',
  reenterPassword: '请再次输入密码',
  passwordMismatch: '两次输入的密码不一致',
  agreeFirst: '请先勾选同意平台须知',
  loginSuccess: '登录成功',
  registerSuccess: '注册成功',
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

const mode = ref('login')
const agreed = ref(true)
const loading = ref(false)
const form = reactive({
  studentId: '',
  password: '',
  confirmPassword: '',
})

const isLogin = computed(() => mode.value === 'login')
const pageTitle = computed(() => (isLogin.value ? copy.welcomeBack : copy.createAccount))
const pageDesc = computed(() => (isLogin.value ? copy.loginDesc : copy.registerDesc))
const submitText = computed(() => (isLogin.value ? copy.login : copy.registerAndEnter))
const switchHint = computed(() => (isLogin.value ? copy.noAccount : copy.hasAccount))
const switchActionText = computed(() => (isLogin.value ? copy.goRegister : copy.goLogin))
const authPanelTitle = computed(() => (isLogin.value ? copy.studentLogin : copy.studentRegister))

function switchMode(nextMode) {
  if (mode.value === nextMode)
    return

  mode.value = nextMode
  form.password = ''
  form.confirmPassword = ''
}

function showToast(title) {
  uni.showToast({
    title,
    icon: 'none',
  })
}

function validateForm() {
  const studentId = form.studentId.trim()

  if (!studentId) {
    showToast(copy.enterStudentId)
    return false
  }

  if (!/^\d{6,20}$/.test(studentId)) {
    showToast(copy.invalidStudentId)
    return false
  }

  if (!form.password) {
    showToast(copy.enterPassword)
    return false
  }

  if (form.password.length < 6 || form.password.length > 20) {
    showToast(copy.invalidPasswordLength)
    return false
  }

  if (!isLogin.value) {
    if (!form.confirmPassword) {
      showToast(copy.reenterPassword)
      return false
    }

    if (form.password !== form.confirmPassword) {
      showToast(copy.passwordMismatch)
      return false
    }
  }

  if (!agreed.value) {
    showToast(copy.agreeFirst)
    return false
  }

  return true
}

async function handleSubmit() {
  if (!validateForm())
    return

  loading.value = true

  try {
    await userStore.login({
      studentId: form.studentId.trim(),
      password: form.password,
    })

    uni.showToast({
      title: isLogin.value ? copy.loginSuccess : copy.registerSuccess,
      icon: 'success',
    })

    setTimeout(() => {
      uni.reLaunch({
        url: '/pages/mine/index',
      })
    }, 160)
  }
  catch (error) {
    showToast(error?.message || '登录失败')
  }
  finally {
    loading.value = false
  }
}
</script>

<template>
  <view class="app-shell login-page">
    <button class="login-back" @click="goBack">
      <image class="login-back__icon" src="/static/images/common/back.svg" mode="aspectFit" />
    </button>

    <view class="login-hero">
      <view class="login-hero__eyebrow">
        CAMPUS ACCOUNT
      </view>
      <view class="login-hero__title">
        {{ pageTitle }}
      </view>
      <view class="login-hero__desc">
        {{ pageDesc }}
      </view>
    </view>

    <view class="card-panel auth-panel">
      <view class="mode-switch">
        <view
          class="mode-switch__item"
          :class="{ 'mode-switch__item--active': mode === 'login' }"
          @click="switchMode('login')"
        >
          {{ copy.login }}
        </view>
        <view
          class="mode-switch__item"
          :class="{ 'mode-switch__item--active': mode === 'register' }"
          @click="switchMode('register')"
        >
          {{ copy.register }}
        </view>
      </view>

      <view class="auth-panel__intro">
        <view class="auth-panel__title">
          {{ authPanelTitle }}
        </view>
        <view class="auth-panel__sub">
          {{ copy.formSub }}
        </view>
      </view>

      <view class="form-field">
        <view class="form-field__label">
          {{ copy.studentId }}
        </view>
        <input
          v-model.trim="form.studentId"
          class="form-field__input"
          type="number"
          maxlength="20"
          :placeholder="copy.studentIdPlaceholder"
          placeholder-class="form-field__placeholder"
        />
      </view>

      <view class="form-field">
        <view class="form-field__label">
          {{ copy.password }}
        </view>
        <input
          v-model="form.password"
          class="form-field__input"
          password
          maxlength="20"
          :placeholder="copy.passwordPlaceholder"
          placeholder-class="form-field__placeholder"
        />
      </view>

      <view v-if="!isLogin" class="form-field">
        <view class="form-field__label">
          {{ copy.confirmPassword }}
        </view>
        <input
          v-model="form.confirmPassword"
          class="form-field__input"
          password
          maxlength="20"
          :placeholder="copy.confirmPasswordPlaceholder"
          placeholder-class="form-field__placeholder"
        />
      </view>

      <view class="tips-row">
        <view class="tips-row__item">
          {{ copy.studentIdRule }}
        </view>
        <view class="tips-row__item">
          {{ copy.passwordRule }}
        </view>
      </view>
    </view>

    <view class="agreement" @click="agreed = !agreed">
      <view class="agreement__box" :class="{ 'agreement__box--active': agreed }">
        <view v-if="agreed" class="i-carbon-checkmark"></view>
      </view>
      <view class="agreement__text">
        {{ copy.agreement }}
      </view>
    </view>

    <button class="primary-button login-submit" :loading="loading" @click="handleSubmit">
      {{ submitText }}
    </button>

    <view class="switch-row">
      <text class="switch-row__hint">
        {{ switchHint }}
      </text>
      <text class="switch-row__action" @click="switchMode(isLogin ? 'register' : 'login')">
        {{ switchActionText }}
      </text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.login-page {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding-top: 104rpx;
  position: relative;
}

.login-back {
  position: absolute;
  top: 32rpx;
  left: 32rpx;
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
}

.login-back__icon {
  width: 30rpx;
  height: 30rpx;
}

.login-hero {
  margin-bottom: 28rpx;

  &__eyebrow {
    color: #777771;
    font-size: 20rpx;
    letter-spacing: 3rpx;
  }

  &__title {
    margin-top: 18rpx;
    font-size: 56rpx;
    line-height: 1.16;
    font-weight: 700;
    color: #111111;
  }

  &__desc {
    margin-top: 16rpx;
    color: #696963;
    font-size: 26rpx;
    line-height: 1.7;
  }
}

.auth-panel {
  padding: 20rpx;

  &__intro {
    padding: 18rpx 8rpx 10rpx;
  }

  &__title {
    font-size: 34rpx;
    font-weight: 700;
    color: #161616;
  }

  &__sub {
    margin-top: 10rpx;
    color: #7a7a73;
    font-size: 24rpx;
    line-height: 1.6;
  }
}

.mode-switch {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10rpx;
  padding: 10rpx;
  border-radius: 24rpx;
  background: #f3f1eb;

  &__item {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 72rpx;
    border-radius: 18rpx;
    color: #66665f;
    font-size: 28rpx;
    font-weight: 600;
    transition: all 0.2s ease;

    &--active {
      background: #111111;
      color: #ffffff;
      box-shadow: 0 10rpx 24rpx rgba(17, 17, 17, 0.12);
    }
  }
}

.form-field {
  margin-top: 18rpx;
  padding: 24rpx;
  border: 1rpx solid rgba(17, 17, 17, 0.08);
  border-radius: 24rpx;
  background: rgba(247, 246, 242, 0.9);

  &__label {
    color: #66665f;
    font-size: 22rpx;
  }

  &__input {
    width: 100%;
    margin-top: 12rpx;
    color: #111111;
    font-size: 30rpx;
    line-height: 1.4;
  }
}

.form-field__placeholder {
  color: #a0a099;
}

.tips-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 20rpx;

  &__item {
    display: inline-flex;
    align-items: center;
    min-height: 48rpx;
    padding: 0 18rpx;
    border-radius: 999rpx;
    background: #f1efe8;
    color: #65655f;
    font-size: 22rpx;
  }
}

.agreement {
  display: flex;
  align-items: center;
  gap: 14rpx;
  margin: 24rpx 0 28rpx;

  &__box {
    width: 36rpx;
    height: 36rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1rpx solid rgba(17, 17, 17, 0.18);
    border-radius: 10rpx;

    &--active {
      background: #111111;
      color: #ffffff;
    }
  }

  &__text {
    color: #64645d;
    font-size: 24rpx;
    line-height: 1.6;
  }
}

.switch-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  margin-top: 24rpx;
  color: #6e6e67;
  font-size: 24rpx;

  &__action {
    color: #111111;
    font-weight: 600;
  }
}

.login-submit {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  margin: 0;
  padding: 0;
  border: none;
  text-align: center;
  line-height: 88rpx;
  box-shadow: 0 14rpx 30rpx rgba(17, 17, 17, 0.12);

  &::after {
    border: none;
  }
}
</style>
