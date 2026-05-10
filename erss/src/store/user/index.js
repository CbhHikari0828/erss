import { defineStore, getActivePinia } from 'pinia'
import { registerShop } from '@/api/shop/index.js'
import { getUserInfo, postUserLogin, postUserLogout, updateUserProfile } from '@/api/user/index.js'

function createDefaultUserInfo() {
  return {
    id: '',
    username: '游客同学',
    avatar: '',
    campus: '主校区',
    department: '',
    bio: '',
    shopName: '',
    shopCampus: '',
    shopIntro: '',
    shopRegistered: false,
    walletBalance: 0,
  }
}

function normalizeUserInfo(data = {}) {
  const next = data || {}

  return {
    ...createDefaultUserInfo(),
    ...next,
    shopName: next.shopName || '',
    shopCampus: next.shopCampus || next.campus || '',
    shopIntro: next.shopIntro || '',
    shopRegistered: Boolean(next.shopRegistered),
    walletBalance: next.walletBalance ?? 0,
  }
}

function clearAccountStores() {
  const stores = getActivePinia()?._s
  stores?.get('catalog')?.clearMineBooks?.()
  stores?.get('order')?.clearOrders?.()
  stores?.get('rental')?.clearRentals?.()
  stores?.get('message')?.clearMessages?.()
  uni.removeStorageSync('order')
}

function clearAuthCookie() {
  if (typeof document === 'undefined')
    return

  const tokenName = process.env.VITE_API_TOKEN_KEY || 'Authorization'
  document.cookie = `${tokenName}=; Max-Age=0; path=/`
  document.cookie = `${tokenName}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/`
}

export const useUserStore = defineStore(
  'user',
  () => {
    const userInfo = ref(createDefaultUserInfo())
    const token = ref('')
    const syncedToken = ref('')
    let userDataRequestId = 0

    const isLogin = computed(() => Boolean(token.value))
    const userId = computed(() => userInfo.value.id || '')
    const hasShop = computed(() => Boolean(userInfo.value.shopRegistered))
    const isUserInfoSynced = computed(() => Boolean(token.value) && syncedToken.value === token.value && Boolean(userId.value))

    async function login(payload = {}) {
      clearLocalSession()
      const res = await postUserLogin(payload)
      userDataRequestId += 1
      token.value = res.token
      syncedToken.value = ''
      userInfo.value = createDefaultUserInfo()
      clearAccountStores()

      try {
        await getUserData()
      }
      catch (error) {
        logout()
        throw error
      }
    }

    function clearLocalSession() {
      userDataRequestId += 1
      token.value = ''
      syncedToken.value = ''
      userInfo.value = createDefaultUserInfo()
      clearAccountStores()
      clearAuthCookie()
    }

    function logout() {
      const currentToken = token.value
      clearLocalSession()

      if (currentToken)
        postUserLogout(currentToken).catch(() => {})
    }

    async function getUserData() {
      if (!token.value)
        return

      const requestToken = token.value
      const requestId = ++userDataRequestId
      const res = await getUserInfo()

      if (requestId !== userDataRequestId || requestToken !== token.value)
        return

      userInfo.value = normalizeUserInfo(res.data)
      syncedToken.value = requestToken
    }

    async function registerUserShop(payload = {}) {
      await registerShop(payload)
      await getUserData()
    }

    async function updateProfile(payload = {}) {
      const res = await updateUserProfile(payload)
      userInfo.value = normalizeUserInfo(res.data)
    }

    return {
      token,
      userInfo,
      isLogin,
      userId,
      hasShop,
      isUserInfoSynced,
      login,
      logout,
      getUserData,
      registerShop: registerUserShop,
      updateProfile,
    }
  },
  {
    persist: {
      paths: ['token', 'userInfo'],
    },
  },
)
