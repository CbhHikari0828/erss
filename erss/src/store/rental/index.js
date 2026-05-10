import { defineStore } from 'pinia'
import { confirmReceipt as confirmReceiptApi, confirmReturn as confirmReturnApi, finishRentalReturn, initiateReturn as initiateReturnApi, listRentals, submitDamageReport as submitDamageReportApi } from '@/api/rentals/index.js'
import { formatDate } from '@/utils/index.js'

function mapDamageReport(report) {
  if (!report)
    return null

  return {
    id: String(report.id),
    status: report.status || '',
    description: report.description || '',
    images: Array.isArray(report.images) ? report.images : [],
    createdAt: report.createdAt || '',
    reviewNote: report.reviewNote || '',
    compensationAmount: report.compensationAmount ?? '',
  }
}

function mapRentalOrder(order) {
  if (!order)
    return null

  return {
    id: String(order.id),
    title: order.title || '',
    period: order.period || '',
    deposit: Number(order.deposit || 0),
    returnDeliveryMethod: order.returnDeliveryMethod || '',
    status: order.status || '',
    dueAt: order.dueAt || '',
    overdueDays: order.overdueDays ?? 0,
    dueText: order.dueAt
      ? (order.overdueDays > 0
        ? `已逾期 ${order.overdueDays} 天`
        : `到期 ${formatDate(order.dueAt)}`)
      : order.status,
    borrowerName: order.borrowerName || '',
    ownedByMe: Boolean(order.ownedByMe),
    canReportDamage: Boolean(order.canReportDamage),
    canConfirmReturn: Boolean(order.canConfirmReturn),
    canInitiateReturn: Boolean(order.canInitiateReturn),
    damageReport: mapDamageReport(order.damageReport),
  }
}

export const useRentalStore = defineStore(
  'rental',
  () => {
    const rentalOrders = ref([])

    async function refreshRentals() {
      const userStore = useUserStore()
      if (!userStore.isLogin) {
        clearRentals()
        return
      }

      const requestToken = userStore.token
      const res = await listRentals()
      if (requestToken !== userStore.token)
        return

      rentalOrders.value = (res.data || []).map(mapRentalOrder).filter(Boolean)
    }

    function clearRentals() {
      rentalOrders.value = []
    }

    onBeforeMount(() => {
      refreshRentals().catch(() => {})
    })

    const pendingDamageCount = computed(() => {
      return rentalOrders.value.filter(item => item.ownedByMe && item.damageReport?.status === 'pending').length
    })

    async function submitDamageReport(orderId, payload = {}) {
      await submitDamageReportApi(orderId, payload)
      await refreshRentals()
      return true
    }

    async function initiateReturn(orderId, deliveryMethod) {
      await initiateReturnApi(orderId, deliveryMethod)
      await refreshRentals()
      return true
    }

    async function confirmReturn(orderId) {
      await confirmReturnApi(orderId)
      await refreshRentals()
      return true
    }

    async function confirmReceipt(orderId) {
      await confirmReceiptApi(orderId)
      await refreshRentals()
      return true
    }

    async function finishReturn(orderId) {
      await finishRentalReturn(orderId)
      await refreshRentals()
      return true
    }

    return {
      rentalOrders,
      pendingDamageCount,
      submitDamageReport,
      initiateReturn,
      confirmReturn,
      confirmReceipt,
      finishReturn,
      refreshRentals,
      clearRentals,
    }
  },
  {
    persist: {
      paths: [],
    },
  },
)
