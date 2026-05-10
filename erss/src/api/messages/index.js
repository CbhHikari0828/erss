import alova from '@/api/index.js'

export function listMessages(params = {}) {
  return alova.Get('/messages', {
    params,
  })
}

export function markMessageRead(id) {
  return alova.Post(`/messages/${id}/read`)
}

export function markAllMessagesRead() {
  return alova.Post('/messages/read-all')
}

export function deleteMessage(id) {
  return alova.Delete(`/messages/${id}`)
}
