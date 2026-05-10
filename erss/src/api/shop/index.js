import alova from '@/api/index.js'

export function registerShop(payload) {
  return alova.Post('/shop', payload)
}
