export function promptLoginAction(options = {}) {
  const {
    title = '\u8bf7\u5148\u767b\u5f55',
    content = '\u5f53\u524d\u64cd\u4f5c\u9700\u8981\u767b\u5f55\u540e\u624d\u80fd\u4f7f\u7528\uff0c\u662f\u5426\u524d\u5f80\u767b\u5f55\u9875\uff1f',
    confirmText = '\u53bb\u767b\u5f55',
    cancelText = '\u5148\u770b\u770b',
    onConfirm,
  } = options

  uni.showModal({
    title,
    content,
    confirmText,
    cancelText,
    success: ({ confirm }) => {
      if (confirm)
        onConfirm?.()
    },
  })
}

export function ensureLogin(userStore, onAuthed, options = {}) {
  if (userStore.isLogin) {
    onAuthed?.()
    return true
  }

  promptLoginAction(options)
  return false
}
