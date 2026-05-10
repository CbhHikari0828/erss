const localBookCovers = [
  'https://images.pexels.com/photos/3782249/pexels-photo-3782249.jpeg?cs=srgb&dl=pexels-polina-zimmerman-3782249.jpg&fm=jpg',
  'https://images.pexels.com/photos/4219041/pexels-photo-4219041.jpeg?cs=srgb&dl=pexels-karola-g-4219041.jpg&fm=jpg',
  'https://images.pexels.com/photos/27668991/pexels-photo-27668991.jpeg?cs=srgb&dl=pexels-fiona-murray-537687299-27668991.jpg&fm=jpg',
  'https://images.pexels.com/photos/5009227/pexels-photo-5009227.jpeg?auto=compress&cs=tinysrgb&w=800',
  'https://images.pexels.com/photos/11340667/pexels-photo-11340667.jpeg?auto=compress&cs=tinysrgb&w=800',
  'https://images.pexels.com/photos/14548367/pexels-photo-14548367.jpeg?auto=compress&cs=tinysrgb&w=800',
]

export const campusOptions = ['主校区', '南校区', '北校区', '研究生院']

export const categoryTree = [
  {
    key: 'textbook',
    label: '教材教辅',
    children: ['高等数学', '大学英语', '计算机基础', '电路与机械'],
  },
  {
    key: 'exam',
    label: '考研考公',
    children: ['考研英语', '考研政治', '数学真题', '复试资料'],
  },
  {
    key: 'language',
    label: '语言证书',
    children: ['四六级', '雅思托福', '日语韩语', '写作表达'],
  },
  {
    key: 'reading',
    label: '课外阅读',
    children: ['文学小说', '社会科学', '设计艺术', '传记纪实'],
  },
]

export const quickCategories = [
  { label: '教材', icon: 'i-carbon-book' },
  { label: '考研', icon: 'i-carbon-notebook' },
  { label: '英语', icon: 'i-carbon-translate' },
  { label: '文学', icon: 'i-carbon-catalog' },
]

export const hotKeywords = ['毛概', '高数同济', '408', '六级真题', '线代', '新传资料']

export const searchHistory = ['线性代数', '法学教材', '英语六级', '408 王道']

export const conditionOptions = ['全新未写', '九成新', '八成新', '有少量笔记']

export const bannerCards = [
  {
    title: '同校面交更安心',
    subtitle: '支持按校区筛选，课间就能完成交易',
    metric: '1h 内约见',
  },
  {
    title: '考研资料集中上新',
    subtitle: '真题、笔记、讲义按学科整理',
    metric: '320+ 套资料',
  },
]

export const books = [
  {
    id: 'book-001',
    title: '高等数学 同济第七版 上册',
    category: '教材教辅',
    subcategory: '高等数学',
    price: 18,
    originalPrice: 49.8,
    condition: '九成新',
    campus: '主校区',
    seller: '林知远',
    sellerTag: '数学系大三',
    sellerRating: 4.9,
    images: [localBookCovers[0], localBookCovers[1]],
    cover: localBookCovers[0],
    summary: '课后习题页完整，无缺页，封面有轻微折痕。',
    tags: ['可小刀', '自提优先', '附笔记'],
    description: '大一时使用的教材，重点章节有铅笔标注，已擦除大部分。适合本校理工类专业基础课复习。',
    publishedAt: '2 小时前',
  },
  {
    id: 'book-002',
    title: '张剑黄皮书 2026 考研英语真题',
    category: '考研考公',
    subcategory: '考研英语',
    price: 26,
    originalPrice: 79,
    condition: '全新未写',
    campus: '研究生院',
    seller: '周可',
    sellerTag: '新闻传播研一',
    sellerRating: 5,
    images: [localBookCovers[1], localBookCovers[2]],
    cover: localBookCovers[1],
    summary: '买重了，塑封还在。',
    tags: ['价格稳', '考研季', '可邮寄'],
    description: '适合 2027 届考研同学提前备考，书脊无压痕，支持主校区地铁口面交。',
    publishedAt: '今天',
  },
  {
    id: 'book-003',
    title: '大学英语四级词汇闪过',
    category: '语言证书',
    subcategory: '四六级',
    price: 12,
    originalPrice: 39.8,
    condition: '八成新',
    campus: '南校区',
    seller: '程一',
    sellerTag: '外国语学院大二',
    sellerRating: 4.8,
    images: [localBookCovers[2]],
    cover: localBookCovers[2],
    summary: '有重点划线，背词效率很高。',
    tags: ['笔记友好', '四六级', '当天可拿'],
    description: '书内有一部分荧光笔划线，适合临时冲刺备考，不影响继续使用。',
    publishedAt: '昨天',
  },
  {
    id: 'book-004',
    title: '传播学教程 第三版',
    category: '教材教辅',
    subcategory: '大学英语',
    price: 22,
    originalPrice: 56,
    condition: '九成新',
    campus: '主校区',
    seller: '顾言',
    sellerTag: '新闻学院大四',
    sellerRating: 4.7,
    images: [localBookCovers[3]],
    cover: localBookCovers[3],
    summary: '老师指定教材，期末复习非常够用。',
    tags: ['专业课', '本学期热门'],
    description: '内页很干净，仅目录页有课程安排记录，适合新闻传播类同学接手。',
    publishedAt: '3 天前',
  },
  {
    id: 'book-005',
    title: '线性代数辅导讲义',
    category: '考研考公',
    subcategory: '数学真题',
    price: 15,
    originalPrice: 36,
    condition: '有少量笔记',
    campus: '北校区',
    seller: '沈屿',
    sellerTag: '自动化大三',
    sellerRating: 4.9,
    images: [localBookCovers[4]],
    cover: localBookCovers[4],
    summary: '附自己整理的公式页，适合期末前突击。',
    tags: ['可送讲义', '理工专用'],
    description: '课本配套辅导资料，线代重点题型归纳完整，晚上图书馆附近可见面。',
    publishedAt: '5 天前',
  },
  {
    id: 'book-006',
    title: '局外人 + 月亮与六便士 合售',
    category: '课外阅读',
    subcategory: '文学小说',
    price: 20,
    originalPrice: 58,
    condition: '九成新',
    campus: '主校区',
    seller: '许雾',
    sellerTag: '中文系大二',
    sellerRating: 5,
    images: [localBookCovers[5]],
    cover: localBookCovers[5],
    summary: '两本一起出，适合文学课外阅读。',
    tags: ['成套出售', '晚间可约'],
    description: '封面保存良好，内页无划线。宿舍搬迁清书，价格打包更合适。',
    publishedAt: '1 周前',
  },
]

export const messages = [
  {
    id: 'chat-1',
    name: '林知远',
    preview: '可以在主教楼一层见面，我下课后过去。',
    time: '14:20',
    unread: 2,
    topic: '高等数学 同济第七版',
  },
  {
    id: 'chat-2',
    name: '周可',
    preview: '黄皮书还在，今晚九点前都可以。',
    time: '昨天',
    unread: 0,
    topic: '张剑黄皮书 2026',
  },
  {
    id: 'chat-3',
    name: '校园助手',
    preview: '欢迎来到校书，记得优先选择同校区见面交易。',
    time: '周日',
    unread: 1,
    topic: '平台提醒',
  },
]

export const mineMenus = [
  { label: '我的发布', value: '12', icon: 'i-carbon-document-add' },
  { label: '收藏夹', value: '27', icon: 'i-carbon-favorite' },
  { label: '待成交', value: '4', icon: 'i-carbon-activity' },
  { label: '设置', value: '', icon: 'i-carbon-settings-adjust' },
]

export function getBookById(id) {
  return books.find(book => book.id === id) || books[0]
}
