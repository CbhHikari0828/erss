import { defineStore } from 'pinia'
import { createBook, getBookDetail, listBooks, listMineBooks, updateBook } from '@/api/books/index.js'
import { categoryTree } from '@/constants/catalog.js'
import { formatRelativeTime } from '@/utils/index.js'

function mapBook(book) {
  if (!book)
    return null

  return {
    id: String(book.id),
    title: book.title || '',
    category: book.category || '',
    subcategory: book.subcategory || '',
    price: Number(book.price || 0),
    originalPrice: Number(book.originalPrice || 0),
    rentalEnabled: Boolean(book.rentalEnabled),
    rentPrice: Number(book.rentPrice || 0),
    rentalDeposit: Number(book.rentalDeposit || 0),
    deposit: Number(book.rentalDeposit || 0),
    condition: book.condition || '',
    campus: book.campus || '',
    seller: book.seller || '',
    sellerTag: book.sellerTag || '',
    sellerRating: Number(book.sellerRating || 4.9),
    images: Array.isArray(book.images) ? book.images : [],
    cover: book.cover || (Array.isArray(book.images) ? book.images[0] : '') || '',
    summary: book.summary || '',
    tags: Array.isArray(book.tags) ? book.tags : [],
    description: book.description || '',
    status: book.status === 'ON_SALE' ? '在售中' : book.status === 'RENTED' ? '租借中' : '已下架',
    rawStatus: book.status || '',
    publishedAt: formatRelativeTime(book.createdAt),
    createdAt: book.createdAt,
    sellerId: book.sellerId ?? null,
  }
}

export const useCatalogStore = defineStore(
  'catalog',
  () => {
    const books = ref([])
    const mineBooks = ref([])
    const loading = ref(false)

    async function refreshBooks(params = {}) {
      loading.value = true
      try {
        const res = await listBooks(params)
        books.value = (res.data || []).map(mapBook).filter(Boolean)
      }
      finally {
        loading.value = false
      }
    }

    async function refreshMineBooks() {
      const userStore = useUserStore()
      if (!userStore.isLogin) {
        clearMineBooks()
        return
      }

      const requestToken = userStore.token
      const res = await listMineBooks()
      if (requestToken !== userStore.token)
        return

      mineBooks.value = (res.data || []).map(mapBook).filter(Boolean)
    }

    function clearMineBooks() {
      mineBooks.value = []
    }

    async function createMineBook(payload) {
      const res = await createBook(payload)
      await refreshBooks()
      await refreshMineBooks()
      return mapBook(res.data)
    }

    async function updateMineBook(id, payload) {
      const res = await updateBook(id, payload)
      await refreshBooks()
      await refreshMineBooks()
      return mapBook(res.data)
    }

    async function getBook(id) {
      if (!id)
        return null

      const cached = books.value.find(item => item.id === String(id)) || mineBooks.value.find(item => item.id === String(id))
      if (cached)
        return cached

      const res = await getBookDetail(id)
      return mapBook(res.data)
    }

    function getBookById(id) {
      return books.value.find(item => item.id === String(id)) || mineBooks.value.find(item => item.id === String(id)) || null
    }

    onBeforeMount(() => {
      refreshBooks().catch(() => {})
      refreshMineBooks().catch(() => {})
    })

    return {
      books,
      mineBooks,
      loading,
      categoryTree,
      refreshBooks,
      refreshMineBooks,
      clearMineBooks,
      createMineBook,
      updateMineBook,
      getBook,
      getBookById,
    }
  },
)
