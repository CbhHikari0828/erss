import { isH5 } from '@uni-helper/uni-env'

function getBaseURL() {
  if (process.env.VITE_PROXY_USE === '1' && isH5)
    return process.env.VITE_PROXY_PATH

  return `${process.env.VITE_API_ORIGIN}${process.env.VITE_API_PATH}`
}

function parseUploadResponse(raw) {
  if (typeof raw === 'string') {
    try {
      return JSON.parse(raw)
    }
    catch {
      throw new Error('上传响应格式不正确')
    }
  }
  return raw
}

export function uploadImage(filePath, folder = 'misc') {
  const userStore = useUserStore()
  const headers = {}
  if (userStore.token) {
    headers[process.env.VITE_API_TOKEN_KEY] = `Bearer ${userStore.token}`
  }

  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: `${getBaseURL()}/upload/images`,
      filePath,
      name: 'file',
      formData: {
        folder,
      },
      header: headers,
      success: (response) => {
        try {
          if (response.statusCode >= 400) {
            reject(new Error(response.errMsg || '图片上传失败'))
            return
          }
          const payload = parseUploadResponse(response.data)
          if (payload.code !== Number(process.env.VITE_API_SUCCESS_CODE)) {
            reject(new Error(payload.message || '图片上传失败'))
            return
          }
          resolve(payload.data)
        }
        catch (error) {
          reject(error)
        }
      },
      fail: (error) => {
        reject(new Error(error.errMsg || '图片上传失败'))
      },
    })
  })
}

export async function uploadImages(filePaths = [], folder = 'misc') {
  const uploaded = await Promise.all(filePaths.map(filePath => uploadImage(filePath, folder)))
  return uploaded.map(item => item.url).filter(Boolean)
}
