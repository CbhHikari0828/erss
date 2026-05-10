import alova from '@/api/index.js'

export function listRentals() {
  return alova.Get('/rentals')
}

export function submitDamageReport(id, payload) {
  return alova.Post(`/rentals/${id}/damage-reports`, payload)
}

export function initiateReturn(id, deliveryMethod) {
  return alova.Post(`/rentals/${id}/initiate-return`, { deliveryMethod })
}

export function confirmReturn(id) {
  return alova.Post(`/rentals/${id}/confirm-return`)
}

export function confirmReceipt(id) {
  return alova.Post(`/rentals/${id}/confirm-receipt`)
}

export function finishRentalReturn(id) {
  return alova.Post(`/rentals/${id}/finish-return`)
}
