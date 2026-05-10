import { defineStore } from 'pinia'
import { confirmOrder, createOrder, listBuyerOrders, listSellerOrders, shipOrder } from '@/api/orders/index.js'
import { useCatalogStore } from '@/store/catalog/index.js'
import { useRentalStore } from '@/store/rental/index.js'

const DELIVERY_OPTIONS = {
  pickup: { label: '上门自提', fee: 0 },
  station: { label: '快递驿站取件', fee: 3 },
}

const PAYMENT_OPTIONS = {
  alipay: '支付宝支付',
  wechat: '微信支付',
  wallet: '系统钱包',
}

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

function mapOrder(order) {
  if (!order)
    return null

  return {
    id: String(order.id),
    orderNo: order.orderNo || '',
    orderType: order.orderType || '',
    bookId: String(order.bookId || ''),
    title: order.title || '',
    sellerName: order.sellerName || '',
    buyerName: order.buyerName || '',
    unitPrice: Number(order.unitPrice || 0),
    quantity: Number(order.quantity || 1),
    rentalPeriod: order.rentalPeriod || '',
    depositAmount: Number(order.depositAmount || 0),
    deliveryFee: Number(order.deliveryFee || 0),
    totalAmount: Number(order.totalAmount || 0),
    deliveryMethod: order.deliveryMethod || '',
    paymentMethod: order.paymentMethod || '',
    status: order.status || '',
    escrowStatus: order.escrowStatus || '',
    autoConfirmAt: order.autoConfirmAt || '',
    createdAt: order.createdAt || '',
    statusText: order.status,
  }
}

export function createCheckoutDraft(book, orderType, options = {}) {
  const unitPrice = orderType === 'rental'
    ? Number(book?.rentPrice || 0)
    : Number(book?.price || 0)
  const rentalPeriod = options.rentalPeriod || '7天'
  const rentalDays = Number.parseInt(rentalPeriod, 10) || 7
  const depositAmount = orderType === 'rental'
    ? Number(book?.rentalDeposit || book?.deposit || 0)
    : 0
  const subtotal = orderType === 'rental'
    ? unitPrice * rentalDays + depositAmount
    : unitPrice

  return {
    orderType,
    bookId: String(book?.id || ''),
    title: book?.title || '',
    sellerName: book?.seller || '',
    sellerTag: book?.sellerTag || '',
    cover: book?.cover || '',
    unitPrice,
    quantity: 1,
    rentalPeriod,
    rentalDays,
    depositAmount,
    deliveryMethod: 'pickup',
    deliveryFee: 0,
    subtotal,
    totalAmount: subtotal,
  }
}

export const useOrderStore = defineStore(
  'order',
  () => {
    const catalogStore = useCatalogStore()
    const rentalStore = useRentalStore()
    const checkoutDraft = ref(null)
    const buyerOrders = ref([])
    const sellerOrders = ref([])

    async function refreshOrders() {
      const userStore = useUserStore()
      if (!userStore.isLogin) {
        clearOrders()
        return
      }

      const requestToken = userStore.token
      const [buyerRes, sellerRes] = await Promise.all([
        listBuyerOrders(),
        listSellerOrders(),
      ])
      if (requestToken !== userStore.token)
        return

      buyerOrders.value = (buyerRes.data || []).map(mapOrder).filter(Boolean)
      sellerOrders.value = (sellerRes.data || []).map(mapOrder).filter(Boolean)
      await catalogStore.refreshBooks().catch(() => {})
      await catalogStore.refreshMineBooks().catch(() => {})
    }

    onBeforeMount(() => {
      refreshOrders().catch(() => {})
    })

    const orders = computed(() => buyerOrders.value)
    const unavailableBookIds = computed(() => {
      const catalogUnavailableIds = catalogStore.books
        .filter(item => item.status !== '在售中')
        .map(item => item.id)
      const orderedBookIds = [...buyerOrders.value, ...sellerOrders.value].map(item => item.bookId)
      return [...new Set([...catalogUnavailableIds, ...orderedBookIds])]
    })

    function setCheckoutDraft(payload) {
      checkoutDraft.value = { ...payload }
    }

    function updateCheckoutDelivery(deliveryMethod) {
      if (!checkoutDraft.value)
        return

      checkoutDraft.value.deliveryMethod = deliveryMethod
      checkoutDraft.value.deliveryFee = DELIVERY_OPTIONS[deliveryMethod]?.fee || 0
      checkoutDraft.value.totalAmount = checkoutDraft.value.subtotal + checkoutDraft.value.deliveryFee
    }

    function clearCheckoutDraft() {
      checkoutDraft.value = null
    }

    function clearOrders() {
      checkoutDraft.value = null
      buyerOrders.value = []
      sellerOrders.value = []
    }

    async function submitOrder(paymentMethod) {
      if (!checkoutDraft.value)
        return ''

      const draft = checkoutDraft.value
      const res = await createOrder({
        bookId: Number(draft.bookId),
        orderType: draft.orderType,
        rentalPeriod: draft.orderType === 'rental' ? draft.rentalPeriod : '',
        deliveryMethod: draft.deliveryMethod,
        paymentMethod,
      })

      clearCheckoutDraft()
      await refreshOrders()
      if (draft.orderType === 'rental') {
        await rentalStore.refreshRentals().catch(() => {})
      }
      return String(res.data?.orderId || '')
    }

    async function shipOrderById(orderId) {
      await shipOrder(orderId)
      await refreshOrders()
      return true
    }

    async function confirmReceipt(orderId) {
      await confirmOrder(orderId)
      await refreshOrders()
      return true
    }

    async function refreshAutoConfirmedOrders() {
      const userStore = useUserStore()
      if (!userStore.isLogin) {
        clearOrders()
        return
      }

      const requestToken = userStore.token
      const now = Date.now()
      const pending = buyerOrders.value.filter(order => order.status === '待确认收货' && order.autoConfirmAt && new Date(order.autoConfirmAt).getTime() <= now)
      for (const order of pending) {
        if (requestToken !== userStore.token)
          return

        await confirmOrder(order.id)
      }
      if (pending.length) {
        await refreshOrders()
      }
    }

    function isBookUnavailable(bookId) {
      if (!bookId)
        return false

      return unavailableBookIds.value.includes(String(bookId))
    }

    function getLatestOrderByBookId(bookId) {
      return orders.value.find(item => item.bookId === String(bookId)) || null
    }

    return {
      checkoutDraft,
      orders,
      buyerOrders,
      sellerOrders,
      unavailableBookIds,
      setCheckoutDraft,
      updateCheckoutDelivery,
      clearCheckoutDraft,
      clearOrders,
      submitOrder,
      shipOrder: shipOrderById,
      confirmReceipt,
      refreshAutoConfirmedOrders,
      isBookUnavailable,
      getLatestOrderByBookId,
      refreshOrders,
      deliveryOptions: DELIVERY_OPTIONS,
      paymentOptions: PAYMENT_OPTIONS,
      formatPrice,
    }
  },
  {
    persist: {
      paths: ['checkoutDraft'],
    },
  },
)
