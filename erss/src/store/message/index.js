import { defineStore } from 'pinia'
import { deleteMessage as deleteMessageApi, listMessages, markAllMessagesRead, markMessageRead } from '@/api/messages/index.js'

const categoryMeta = {
  all: { label: '全部' },
  trade: { label: '交易' },
  rental: { label: '租赁' },
  system: { label: '系统' },
}

function mapNotification(item) {
  return {
    id: String(item.id),
    category: item.category || 'system',
    title: item.title || '',
    summary: item.summary || '',
    time: item.time || '',
    unread: Boolean(item.unread),
    status: item.status || '',
    actionText: item.actionText || '',
    route: item.route || '',
  }
}

export const useMessageStore = defineStore(
  'message',
  () => {
    const notifications = ref([])
    const assistantEnabled = ref(true)
    const activeCategory = ref('all')

    async function refreshMessages() {
      const userStore = useUserStore()
      if (!userStore.isLogin) {
        clearMessages()
        return
      }

      const requestToken = userStore.token
      const res = await listMessages()
      if (requestToken !== userStore.token)
        return

      notifications.value = (res.data || []).map(mapNotification)
    }

    function clearMessages() {
      notifications.value = []
    }

    const categories = computed(() => {
      return Object.entries(categoryMeta).map(([key, meta]) => ({
        key,
        label: meta.label,
        count: key === 'all'
          ? notifications.value.length
          : notifications.value.filter(item => item.category === key).length,
      }))
    })

    const unreadCount = computed(() => {
      return notifications.value.filter(item => item.unread).length
    })

    const pendingCount = computed(() => {
      return notifications.value.filter(item => item.status === '待处理' || item.status === '待确认').length
    })

    const filteredNotifications = computed(() => {
      if (activeCategory.value === 'all')
        return notifications.value

      return notifications.value.filter(item => item.category === activeCategory.value)
    })

    function setActiveCategory(category) {
      activeCategory.value = categoryMeta[category] ? category : 'all'
    }

    async function markAsRead(id) {
      await markMessageRead(id)
      await refreshMessages()
    }

    async function deleteMessage(id) {
      await deleteMessageApi(id)
      await refreshMessages()
    }

    async function markAllAsRead(category = activeCategory.value) {
      await markAllMessagesRead()
      if (category !== 'all') {
        activeCategory.value = category
      }
      await refreshMessages()
    }

    function toggleAssistant(enabled) {
      assistantEnabled.value = typeof enabled === 'boolean' ? enabled : !assistantEnabled.value
    }

    onBeforeMount(() => {
      refreshMessages().catch(() => {})
    })

    return {
      notifications,
      assistantEnabled,
      activeCategory,
      categories,
      unreadCount,
      pendingCount,
      filteredNotifications,
      setActiveCategory,
      markAsRead,
      markAllAsRead,
      deleteMessage,
      toggleAssistant,
      refreshMessages,
      clearMessages,
    }
  },
  {
    persist: {
      paths: ['assistantEnabled', 'activeCategory'],
    },
  },
)
