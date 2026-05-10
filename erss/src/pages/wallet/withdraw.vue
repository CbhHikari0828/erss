<script setup>
import { getWalletSummary, withdrawWallet } from '@/api/wallet/index.js'
import { ensureLogin } from '@/utils/auth.js'

const router = useRouter()
const userStore = useUserStore()
const amount = ref('')
const channel = ref('alipay')
const destinationAccount = ref('')
const note = ref('')
const loading = ref(false)
const submitting = ref(false)
const balance = ref(0)
const error = ref('')

const withdrawAmount = computed(() => Number(amount.value || 0))
const isAmountOverBalance = computed(() => withdrawAmount.value > balance.value)
const canSubmit = computed(() => {
  return withdrawAmount.value > 0 && withdrawAmount.value <= balance.value && !submitting.value && !loading.value
})

const channelOptions = [
  { key: 'alipay', label: '支付宝' },
  { key: 'wechat', label: '微信' },
]

function goBack() {
  uni.navigateBack({ delta: 1 })
}

function amountText(value) {
  return Number(value || 0).toFixed(2)
}

function onAmountInput(event) {
  amount.value = event?.detail?.value ?? event?.target?.value ?? ''
}

function onAccountInput(event) {
  destinationAccount.value = event?.detail?.value ?? event?.target?.value ?? ''
}

function onNoteInput(event) {
  note.value = event?.detail?.value ?? event?.target?.value ?? ''
}

async function loadBalance() {
  if (!userStore.isLogin) return
  loading.value = true
  try {
    const res = await getWalletSummary()
    balance.value = Number(res.data?.walletBalance || userStore.userInfo.walletBalance || 0)
  }
  catch { /* ignore */ }
  finally {
    loading.value = false
  }
}

async function submitWithdraw() {
  if (!withdrawAmount.value || withdrawAmount.value <= 0) {
    uni.showToast({ title: '请输入有效金额', icon: 'none' })
    return
  }
  if (withdrawAmount.value > balance.value) {
    uni.showToast({ title: '提现金额不能大于当前余额', icon: 'none' })
    return
  }

  submitting.value = true
  error.value = ''
  try {
    await withdrawWallet({
      amount: withdrawAmount.value,
      channel: channel.value,
      destinationAccount: destinationAccount.value,
      note: note.value,
    })
    uni.showToast({ title: '提现成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack({ delta: 1 })
    }, 1200)
  }
  catch (err) {
    uni.showToast({ title: err.message || '提现失败', icon: 'none' })
  }
  finally {
    submitting.value = false
  }
}

onShow(() => {
  if (!userStore.isLogin) {
    balance.value = 0
    return
  }
  ensureLogin(userStore, () => {
    void loadBalance()
  }, {
    onConfirm: () => router.replace('/login'),
  })
})
</script>

<template>
  <view class="app-shell withdraw-page">
    <view class="withdraw-head">
      <button class="withdraw-back" @click="goBack">
        <image class="withdraw-back__icon" src="/static/images/common/back.svg" mode="aspectFit" />
      </button>
      <view class="withdraw-head__body">
        <view class="withdraw-head__title">
          提现
        </view>
        <view class="withdraw-head__sub">
          可提现余额 ￥{{ amountText(balance) }}
        </view>
      </view>
    </view>

    <view v-if="error" class="withdraw-banner">
      {{ error }}
    </view>

    <view class="card-panel withdraw-form">
      <view class="withdraw-form__field">
        <view class="withdraw-form__label">
          提现金额
        </view>
        <input
          class="withdraw-form__input"
          type="number"
          inputmode="decimal"
          :value="amount"
          placeholder="请输入提现金额"
          @input="onAmountInput"
        />
        <view v-if="withdrawAmount > 0" class="withdraw-form__hint" :class="{ 'withdraw-form__hint--error': isAmountOverBalance }">
          {{ isAmountOverBalance ? '提现金额不能大于当前余额' : `可提现余额 ￥${amountText(balance)}` }}
        </view>
      </view>
      <view class="withdraw-form__field">
        <view class="withdraw-form__label">
          提现方式
        </view>
        <view class="withdraw-form__chips">
          <view
            v-for="item in channelOptions"
            :key="item.key"
            class="withdraw-form__chip"
            :class="{ 'withdraw-form__chip--active': channel === item.key }"
            @click="channel = item.key"
          >
            {{ item.label }}
          </view>
        </view>
      </view>
      <view class="withdraw-form__field">
        <view class="withdraw-form__label">
          收款账号
        </view>
        <input
          class="withdraw-form__input"
          type="text"
          :value="destinationAccount"
          placeholder="可填微信号或支付宝账号"
          @input="onAccountInput"
        />
      </view>
      <view class="withdraw-form__field">
        <view class="withdraw-form__label">
          备注
        </view>
        <textarea
          class="withdraw-form__textarea"
          :value="note"
          placeholder="可选，填写提现说明"
          @input="onNoteInput"
        ></textarea>
      </view>
      <button class="primary-button withdraw-form__submit" :disabled="!canSubmit" @click="submitWithdraw">
        {{ submitting ? '提交中...' : '确认提现' }}
      </button>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.withdraw-head {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;
}

.withdraw-head__body {
  flex: 1;
}

.withdraw-head__title {
  font-size: 40rpx;
  font-weight: 700;
  color: #111111;
}

.withdraw-head__sub {
  margin-top: 10rpx;
  color: #6f6f69;
  font-size: 24rpx;
  line-height: 1.6;
}

.withdraw-back {
  width: 72rpx;
  height: 72rpx;
  flex: none;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  border: none;
  border-radius: 22rpx;
  background: #f1eee6;
}

.withdraw-back::after {
  border: none;
}

.withdraw-back__icon {
  width: 30rpx;
  height: 30rpx;
}

.withdraw-banner {
  margin-top: 20rpx;
  padding: 20rpx 24rpx;
  border-radius: 22rpx;
  background: #f8efe7;
  color: #9d4f12;
  font-size: 23rpx;
}

.withdraw-form {
  margin-top: 24rpx;
  padding: 28rpx;
}

.withdraw-form__field {
  margin-top: 20rpx;
}

.withdraw-form__label {
  margin-bottom: 12rpx;
  color: #6f6f69;
  font-size: 23rpx;
}

.withdraw-form__hint {
  margin-top: 10rpx;
  color: #6f6f69;
  font-size: 22rpx;
  line-height: 1.5;
}

.withdraw-form__hint--error {
  color: #b03a0f;
}

.withdraw-form__input,
.withdraw-form__textarea {
  width: 100%;
  display: block;
  box-sizing: border-box;
  min-height: 92rpx;
  padding: 22rpx 20rpx;
  border: 1rpx solid rgba(17, 17, 17, 0.08);
  border-radius: 18rpx;
  background: #f7f4ed;
  color: #111111;
  font-size: 26rpx;
}

.withdraw-form__textarea {
  min-height: 140rpx;
}

.withdraw-form__chips {
  display: flex;
  gap: 16rpx;
}

.withdraw-form__chip {
  padding: 16rpx 24rpx;
  border-radius: 999rpx;
  background: #ece8df;
  color: #5d5d58;
  font-size: 24rpx;
}

.withdraw-form__chip--active {
  background: #111111;
  color: #ffffff;
}

.withdraw-form__submit {
  margin-top: 24rpx;
}
</style>
