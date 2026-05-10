import { createAlovaMockAdapter, defineMock } from '@alova/mock'
import adapterFetch from 'alova/fetch'
import avatarUrl from '~@/assets/images/avatar.gif'

function mockRequestAdapter() {
  const userMock = defineMock({
    '[POST]/login': {
      token: 'mock-campus-token',
      code: 200,
    },
    '/getInfo': {
      data: {
        id: 1,
        username: '\u9648\u4e88\u5b89',
        avatar: avatarUrl,
        campus: '\u4e3b\u6821\u533a',
        department: '\u65b0\u95fb\u4e0e\u4f20\u64ad\u5b66\u9662',
        bio: '\u504f\u7231\u7eb8\u8d28\u4e66\uff0c\u4e5f\u613f\u610f\u628a\u597d\u4e66\u7ee7\u7eed\u6d41\u8f6c\u51fa\u53bb\u3002',
        shopName: '\u4e3b\u6821\u533a\u4e8c\u624b\u6559\u6750\u5c0f\u5e97',
        shopCampus: '\u4e3b\u6821\u533a',
        shopIntro: '\u4e3b\u8425\u6559\u6750\u6559\u8f85\u3001\u8003\u7814\u8d44\u6599\u548c\u8bed\u8a00\u8bc1\u4e66\u7528\u4e66\uff0c\u652f\u6301\u79df\u501f\u4e0e\u9762\u4ea4\u3002',
        shopRegistered: true,
      },
      code: 200,
    },
  })

  return createAlovaMockAdapter([userMock], {
    httpAdapter: adapterFetch(),
    matchMode: 'methodurl',
    enable: true,
    delay: 500,
    mockRequestLogger: true,
    onMockResponse: async response => ({
      response: {
        ...response,
        data: response.body,
      },
    }),
  })
}

export default mockRequestAdapter
