import { defineStore } from 'pinia'
import { primaryColor as defaultPrimaryColor } from '@/settings/index.mjs'

export const useAppStore = defineStore(
  'app',
  () => {
    const currentTheme = ref('ink')

    const themeModel = {
      ink: { color: defaultPrimaryColor, name: '墨黑' },
    }

    const currentThemeInfo = computed(() => themeModel[currentTheme.value] || themeModel.ink)
    const primaryColor = computed(() => currentThemeInfo.value.color)

    return {
      currentTheme,
      themeModel,
      currentThemeInfo,
      primaryColor,
    }
  },
  {
    persist: true,
  },
)
