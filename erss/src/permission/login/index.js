export default function login(router) {
  const homePath = '/pages/index/index'
  const loginPath = '/pages/login/index'
  const loginPaths = [loginPath, '/login']

  router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    const isLoginPath = loginPaths.includes(to.path)

    // 可以在 .env.dev.local 定义用于接口测试的 Token 环境变量。
    // 只有显式开启自动登录时才注入，避免退出后又被固定测试账号登录。
    if (process.env.VITE_API_TOKEN_AUTO_LOGIN === '1' && process.env.VITE_API_TOKEN && !userStore.token && !isLoginPath) {
      userStore.token = process.env.VITE_API_TOKEN
    }

    const token = userStore.token
    const userId = userStore.userId

    if (!token) {
      next()
      return
    }

    if (isLoginPath) {
      next(homePath)
      return
    }

    if (userId && userStore.isUserInfoSynced) {
      next()
      return
    }

    userStore
      .getUserData()
      .then(() => {
        next()
      })
      .catch((error) => {
        console.warn(error)
        userStore.logout()
        next()
      })
  })

  router.afterEach(() => {})
}
