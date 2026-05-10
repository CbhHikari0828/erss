import alova from '@/api/index.js'

export function postUserLogin(payload = {}) {
  return alova.Post('/login', payload)
}

export function postUserLogout(token = '') {
  const headers = token
    ? { [process.env.VITE_API_TOKEN_KEY]: `Bearer ${token}` }
    : {}

  return alova.Post('/logout', {}, { headers })
}

export function getUserInfo() {
  return alova.Get('/getInfo')
}

export function updateUserProfile(payload) {
  return alova.Put('/user/profile', payload)
}
