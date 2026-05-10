import alova from '@/api/index.js'

export function listBooks(params = {}) {
  return alova.Get('/books', {
    params,
  })
}

export function listMineBooks() {
  return alova.Get('/books/mine')
}

export function getBookDetail(id) {
  return alova.Get(`/books/${id}`)
}

export function createBook(payload) {
  return alova.Post('/books', payload)
}

export function updateBook(id, payload) {
  return alova.Post(`/books/${id}`, payload)
}
