/**
 * @desc 使用async await 进项进行延时操作
 * @param {*} time
 */
export function sleep(time = 500) {
  return new Promise((resolve) => {
    setTimeout(() => resolve(true), time)
  })
}

function pad(n) {
  return String(n).padStart(2, '0')
}

/**
 * 将日期值格式化为 YYYY-MM-DD HH:mm
 */
export function formatDateTime(value) {
  if (!value) return ''
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return ''
  const y = d.getFullYear()
  const m = pad(d.getMonth() + 1)
  const day = pad(d.getDate())
  const h = pad(d.getHours())
  const min = pad(d.getMinutes())
  return `${y}-${m}-${day} ${h}:${min}`
}

/**
 * 将日期值格式化为 YYYY-MM-DD
 */
export function formatDate(value) {
  if (!value) return ''
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return ''
  const y = d.getFullYear()
  const m = pad(d.getMonth() + 1)
  const day = pad(d.getDate())
  return `${y}-${m}-${day}`
}

/**
 * 相对时间 + 绝对日期回退
 */
export function formatRelativeTime(value) {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''
  const diff = Date.now() - date.getTime()
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  if (diff < hour) return `${Math.max(1, Math.floor(diff / minute))} 分钟前`
  if (diff < day) return `${Math.max(1, Math.floor(diff / hour))} 小时前`
  if (diff < 7 * day) return `${Math.max(1, Math.floor(diff / day))} 天前`
  return formatDate(value)
}
