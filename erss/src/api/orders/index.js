import alova from '@/api/index.js'

export function createOrder(payload) {
  return alova.Post('/orders', payload)
}

export function listBuyerOrders() {
  return alova.Get('/orders/buyer')
}

export function listSellerOrders() {
  return alova.Get('/orders/seller')
}

export function shipOrder(id) {
  return alova.Post(`/orders/${id}/ship`)
}

export function confirmOrder(id) {
  return alova.Post(`/orders/${id}/confirm`)
}
