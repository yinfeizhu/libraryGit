<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus' 
import { queryPageApi as queryUserPageApi } from '@/api/user'
import { queryPageApi as queryBookPageApi } from '@/api/bookAdmin'
import { querySoonExpireApi, queryHotBookApi, queryRecommendBookApi
  , queryOverdueApi, queryNewUserApi, queryNewBookApi
 } from '@/api/dashboard'
import { queryRecentAnnouncementsApi as queryNewThreeApi } from '@/api/announcement'

const router = useRouter()
const totalBookCount = ref(0)
const totalReaderCount = ref(0)
const BorrowingCount = ref(0)
const SoonExpireCount = ref(0)
const topBooks = ref([])
const recommendBooks = ref([])
const overdueCount = ref(0)
const newUserCount = ref(0)
const newBookCount = ref(0)
const announcements = ref([])
// 加载状态
const recommendBooksLoading = ref({})
const topBooksLoading = ref({})
// 获取图书总数
const getTotalBookCount = async () => {
  const res = await queryBookPageApi(1, 100000, '', '', '')
  if (res.code == 200) {
    totalBookCount.value = res.data.total
  }
}

// 获取读者数量
const getTotalReaderCount = async () => {
  const res = await queryUserPageApi(1, 100000, '', '')
  if (res.code == 200) {
    totalReaderCount.value = res.data.total
  }
}

// 借阅中图书数量
const getBorrowingBookCount = async () => {
  const res = await queryBookPageApi(1, 100000, '', '', 2)
  if (res.code == 200) {
    BorrowingCount.value = res.data.total
  }
}

// 即将到期图书数量
const getSoonExpireBookCount = async () => {
  const res = await querySoonExpireApi()
  if (res.code == 200) {
    SoonExpireCount.value = res.data
  }
}
// 热门借阅排行
const getHotBook = async () => {
  const res = await queryHotBookApi()
  if (res.code == 200) {
    topBooks.value = res.data
    // 初始化加载状态
    res.data.forEach(book => {
      topBooksLoading.value[book.bookTitle] = true
    })
  }
}
// 推荐图书
const getRecommendBook = async () => {
  const res = await queryRecommendBookApi()
  if (res.code == 200) {
    recommendBooks.value = res.data
    //初始化加载状态
    res.data.forEach(book => {
      recommendBooksLoading.value[book.bookTitle] = true
    })
  }
}
// 图片加载完成处理
const onImageLoad = (bookTitle, isRecommend = false) => {
  if (isRecommend) {
    recommendBooksLoading.value[bookTitle] = false
  } else {
    topBooksLoading.value[bookTitle] = false
  }
}

// 待办事项数据
const todoItems = ref([
  {id: 1, task: '处理逾期借阅', count: 0 },
  {id: 2, task: '处理新用户申请', count: 0 },
  {id: 3, task: '新图书盘点', count: 0 },
])
//待办事项-逾期图书数量
const getOverdueBookCount = async () => {
  const res = await queryOverdueApi()
  if (res.code == 200) {
    overdueCount.value = res.data
    todoItems.value[0].count = overdueCount.value
  }
}
//待办事项-新增用户数量
const getNewUserCount = async () => {
  const res = await queryNewUserApi()
  if (res.code == 200) {
    newUserCount.value = res.data
    todoItems.value[1].count = newUserCount.value
  }
}
//待办事项-新增图书数量
const getNewBookCount = async () => {
  const res = await queryNewBookApi()
  if (res.code == 200) {
    newBookCount.value = res.data
    todoItems.value[2].count = newBookCount.value
  }
}
//获取系统公告
const getAnnouncements = async () => {
  const res = await queryNewThreeApi()
  if (res.code == 200) {
    announcements.value = res.data;
  }
}
// 公告详情弹窗
const showDescription = (content) => {
  ElMessageBox.alert(
    `<div style="font-size: 16px; line-height: 1.8; white-space: pre-wrap;">${content || '无描述信息'}</div>`, 
    '公告详情', 
    {
      confirmButtonText: '关闭',
      type: 'info',
      dangerouslyUseHTMLString: true,
      size: 'large',
      customClass: 'announcement-dialog'
    }
  );
}
// 当前用户信息
const userInfo = ref({})
// 获取当前登录用户信息
const getUserInfo = async () => {
  const loginUser = await JSON.parse(localStorage.getItem('loginUserData'))
  if (loginUser) {
    userInfo.value = loginUser
  } else {
    ElMessage.error('用户信息查询失败')
  }
}
// 检查是否为普通管理员
const isAdmin = computed(() => {
  return userInfo.value && userInfo.value.role === 2
})
// 动态网格布局（根据角色和屏幕尺寸调整列数）
const gridColumns = computed(() => {
  // 优先根据屏幕宽度判断，再结合角色权限
  if (window.innerWidth < 576) {
    return '1fr'; // 超小屏幕：1列
  } else if (window.innerWidth < 768) {
    return 'repeat(2, 1fr)'; // 小屏幕：2列
  }
  // 大屏幕根据角色调整
  return isAdmin.value ? 'repeat(4, 1fr)' : 'repeat(2, 1fr)'
})
// 动态大卡片跨度
const featuredCardSpan = computed(() => {
  if (window.innerWidth < 768) {
    return 1; // 小屏幕下都占1列
  }
  return gridColumns.value === 'repeat(4, 1fr)' ? 2 : 1
})

// 监听窗口大小变化，优化响应式体验
onMounted(() => {
  // 初始化数据
  getTotalBookCount()
  getTotalReaderCount()
  getBorrowingBookCount()
  getSoonExpireBookCount()
  getHotBook()
  getRecommendBook()
  getUserInfo()
  getOverdueBookCount()
  getNewUserCount()
  getNewBookCount()
  getAnnouncements()

  // 监听窗口大小变化，刷新布局计算
  window.addEventListener('resize', () => {
    // 触发响应式计算更新
    gridColumns.value // 访问计算属性触发更新
  })
})

// 导航到指定页面
const navigateTo = (action) => {
  router.push(`/${action}`)
}
</script>

<template>
  <div class="home-container">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <h2>欢迎使用腾飞智能图书管理系统</h2>
      <p>高效管理图书资源，提升阅读体验</p>
    </div>

    <!-- 主要功能区域 -->
    <div class="dashboard-grid" :style="{ gridTemplateColumns: gridColumns }">
      <!-- 第一排：系统概览（仅管理员可见） -->
      <div class="dashboard-card overview-card small-card" v-show="isAdmin">
        <div class="card-header">
          <h3>系统概览</h3>
          <span class="card-icon">📊</span>
        </div>
        <div class="stats-container">
          <div class="stat-item">
            <div class="stat-value">{{ totalBookCount }}</div>
            <div class="stat-label">图书总数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ totalReaderCount }}</div>
            <div class="stat-label">读者数量</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ BorrowingCount }}</div>
            <div class="stat-label">借阅中</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ SoonExpireCount }}</div>
            <div class="stat-label">即将到期</div>
          </div>
        </div>
      </div>

      <!-- 快速操作 -->
      <div class="dashboard-card actions-card small-card">
        <div class="card-header">
          <h3>快速操作</h3>
          <span class="card-icon">⚡</span>
        </div>
        <div class="actions-grid">
          <button class="action-btn" @click="navigateTo('borrow')">
            <span class="btn-icon">📖</span>
            <span>图书借阅</span>
          </button>
          <button class="action-btn" @click="navigateTo('fine')" v-show="isAdmin">
            <span class="btn-icon">💰</span>
            <span>处理罚款</span>
          </button>
          <button class="action-btn" @click="navigateTo('user')" v-show="isAdmin">
            <span class="btn-icon">👥</span>
            <span>读者管理</span>
          </button>
          <button class="action-btn" @click="navigateTo('bookAdmin')" v-show="isAdmin">
            <span class="btn-icon">➕</span>
            <span>图书入库</span>
          </button>
          <button class="action-btn" @click="navigateTo('bookReader')">
            <span class="btn-icon">🔍</span>
            <span>图书查看</span>
          </button>
          <button class="action-btn" @click="navigateTo('announcement')" v-show="isAdmin">
            <span class="btn-icon">📢</span>
            <span>公告管理</span>
          </button>
        </div>
      </div>

      <!-- 系统公告 -->
      <div class="dashboard-card news-card small-card">
        <div class="card-header">
          <h3>系统公告</h3>
          <span class="card-icon">📢</span>
        </div>
        <div class="news-list">
          <div class="news-item" v-for="news in announcements" :key="news.id">
            <div class="news-title">{{ news.subject }}</div>
            <div class="news-date">{{ news.updateTime }}</div>
            <div class="news-content">
              {{ news.content.length > 20 ? news.content.substring(0, 20) + '...' : news.content }}
              <el-button type="text" @click="showDescription(news.content)">详情</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 待办事项（仅管理员可见） -->
      <div class="dashboard-card todo-card small-card" v-show="isAdmin">
        <div class="card-header">
          <h3>待办事项</h3>
          <span class="card-icon">📋</span>
        </div>
        <div class="todo-list">
          <div class="todo-item" v-for="todo in todoItems" :key="todo.id">
            <div class="todo-content">
              <span class="todo-icon">⏰</span>
              {{ todo.task }}
            </div>
            <div class="todo-count">{{ todo.count }}项</div>
          </div>
        </div>
      </div>

      <!-- 图书推荐 -->
      <div class="dashboard-card newbooks-card featured-card" :style="{ gridColumn: `span ${featuredCardSpan}` }">
        <div class="card-header">
          <h3>图书推荐</h3>
          <span class="card-icon">🌟</span>
        </div>
        <div class="newbooks-list">
          <div class="book-item" v-for="book in recommendBooks" :key="book.bookTitle">
            <!-- 图书封面 -->
            <img :src="book.bookCover" alt="图书封面" class="book-cover"
             @load="onImageLoad(book.bookTitle, true)"
             v-show="!recommendBooksLoading[book.bookTitle]">
            <div v-show="recommendBooksLoading[book.bookTitle]" class="book-cover-placeholder">
              <div class="loading-spinner"></div>
            </div>
            <!-- 图书详情 -->
            <div class="book-details">
              <div class="hotbook-title">{{ book.bookTitle }}</div>
              <div class="hotbook-author">{{ book.bookAuthor }}</div>
              <div class="hotbook-desc">{{ book.bookDescription }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 热门借阅排行 -->
      <div class="dashboard-card ranking-card featured-card" :style="{ gridColumn: `span ${featuredCardSpan}` }">
        <div class="card-header">
          <h3>热门借阅排行</h3>
          <span class="card-icon">🔥</span>
        </div>
       <div class="hotbooks-list">
          <div class="hotbook-item" v-for="(book, index) in topBooks" :key="book.bookTitle" >
            <div class="rank-number" >{{ index + 1}}</div>
            <div class="hotbook-borrow-count">{{ book.count }}次</div>
            <!-- 图书封面 -->
            <img :src="book.bookCover" alt="图书封面" class="hotbook-cover"
             @load="onImageLoad(book.bookTitle, false)"
             v-show="!topBooksLoading[book.bookTitle]">
            <div v-show="topBooksLoading[book.bookTitle]" class="book-cover-placeholder">
              <div class="loading-spinner"></div>
            </div>
            <!-- 图书详情 -->
            <div class="book-details">
              <div class="book-title">{{ book.bookTitle }}</div>
              <div class="book-author">{{ book.bookAuthor }}</div>
              <div class="book-desc">{{ book.bookDescription }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  margin: 0;
  background-color: #f8f9fa;
  min-height: calc(100vh - 120px);
  padding: 15px; /* 增加内边距，避免内容贴边 */
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 15px; /* 左右内边距减小，适应小屏幕 */
  border-radius: 12px;
  margin-bottom: 20px;
  text-align: center;
}
.welcome-banner h2 {
  margin: 0 0 10px 0;
  font-size: clamp(1.5rem, 5vw, 28px); /* 响应式字体大小 */
}
.welcome-banner p {
  margin: 0;
  opacity: 0.9;
  font-size: clamp(1rem, 3vw, 16px);
}

/* 网格布局 */
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px; /* 减小间距，适合小屏幕 */
  align-items: start; /* 改为顶部对齐，避免卡片高度不一致导致的问题 */
}

/* 卡片基础样式 */
.dashboard-card {
  background: white;
  border-radius: 12px;
  padding: 15px; /* 减小内边距 */
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;
  display: flex;
  flex-direction: column;
}
.dashboard-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f1f3f4;
}
.card-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 16px; /* 减小标题字体 */
}
.card-icon {
  font-size: 20px;
}

/* 系统概览 */
.stats-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  flex: 1;
  align-items: center;
}
.stat-item {
  text-align: center;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 8px;
}
.stat-value {
  font-size: clamp(1.2rem, 4vw, 20px);
  font-weight: bold;
  color: #3498db;
  margin-bottom: 3px;
}
.stat-label {
  color: #7f8c8d;
  font-size: 14px;
}

/* 快速操作 */
.actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  flex: 1;
  align-items: center;
  justify-items: center;
  padding: 0 5px;
}
.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px 0;
  width: 90%;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 13px;
  min-height: 60px; /* 确保按钮有足够点击区域 */
}
.action-btn:hover {
  background: #2980b9;
}
.btn-icon {
  font-size: 18px;
  margin-bottom: 5px;
}

/* 热门借阅排行 */
.hotbooks-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr)); /* 自动适应列数 */
  gap: 10px;
  flex: 1;
  padding: 5px 0;
  justify-content: flex-start;
}
.hotbook-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #f8f9fa;
  border-radius: 6px;
  padding: 10px;
  box-shadow: 0 2px 4px rgba(195, 221, 244, 0.05);
}
.rank-number {
  width: 24px;
  height: 24px;
  background: #e74c3c;
  color: white;
  border-radius: 50%;
  align-items: center;
  font-size: 13px;
  display: flex;
  justify-content: center;
  margin-top: 10px;
}
.hotbook-cover {
  width: auto; /* 相对宽度，适应容器 */
  max-width: 150px;
  height: 200px; /* 保持比例 */
  aspect-ratio: 2/3; /* 保持图书封面比例 */
  object-fit: cover;
  border-radius: 4px;
  object-position: top;
  margin-top: 8px ;
}
.hotbook-title {
  font-weight: bold;
  margin-bottom: 2px;
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
  text-align: center;
}
.hotbook-author {
  font-size: 11px;
  color: #7f8c8d;
}
.hotbook-desc {
  font-size: 12px;
  color: #999;
  line-height: 1.4;
  display: -webkit-box;
 /* 限制显示行数 */
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.hotbook-borrow-count {
  color: #e74c3c;
  font-weight: bold;
  font-size: 12px;
  margin-bottom: 0px;
  text-align: center;
  margin-top: 10px ;
}

/* 系统公告 */
.news-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  justify-content: center;
}
.news-item {
  padding-bottom: 2px;
  border-bottom: 1px solid #f1f3f4;
}
.news-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}
.news-title {
  font-weight: bold;
  margin-bottom: 3px;
  font-size: 14px;
}
.news-date {
  font-size: 11px;
  color: #7f8c8d;
  margin-bottom: 3px;
}
.news-content {
  font-size: 12px;
  color: #5d6d7e;
  line-height: 1.4;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.news-content .el-button {
  padding: 0;
  margin-left: 5px;
  color: #409EFF;
  font-size: 12px;
}

/* 待办事项 */
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  justify-content: flex-start;
}
.todo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: #fff3cd;
  border-radius: 6px;
  border-left: 4px solid #ffc107;
}
.todo-content {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}
.todo-count {
  background: #e74c3c;
  color: white;
  padding: 2px 6px;
  border-radius: 12px;
  font-size: 11px;
}

/* 图片加载占位符 */
.book-cover-placeholder {
  width: 100%;
  max-width: 220px;
  height: auto;
  aspect-ratio: 2/3;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f0f0;
  border-radius: 4px;
  margin-bottom: 10px;
}

/* 加载动画 */
.loading-spinner {
  width: 30px;
  height: 30px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #309de6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 图书推荐 */
.newbooks-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 10px;
  flex: 1;
  padding: 5px 0;
  justify-items: center;
}
.book-item {
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
  border-radius: 6px;
  align-items: center;/* 垂直居中 */
  text-align: center;/* 居中对齐 */
  padding: 10px;
  box-shadow: 0 2px 4px rgba(195, 221, 244, 0.05);
  width: 100%;
  max-width: 200px;
}
.book-cover {
  width: 90%;
  height: 280px;
  aspect-ratio: 2/3;
  object-fit: cover;
  object-position: top;
  border-radius: 4px;
  margin-bottom: 10px;
}
.book-details {
  text-align: center;
  width: 100%;
}
.book-title {
  font-size: 14px;
  font-weight: bold;
  margin-top: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.book-author {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}
.book-desc {
  margin-top: 6px;
  font-size: 12px;
  color: #999;
  line-height: 1.4;
  display: -webkit-box;
  
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 公告弹窗自定义样式 */
.announcement-dialog {
  max-width: 90vw; /* 使用视口宽度，适应小屏幕 */
  max-height: 80vh;
}

/* 响应式调整 */
/* 小卡片和大卡片基础样式 */
.small-card {
  height: auto;
  min-height: 300px; /* 改为自适应高度 */
  
}

.featured-card {
  min-height: 450px;
  height: auto;
  grid-column: span 2;
}

/* 超小屏幕 (手机) */
@media (max-width: 575px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
  
  .featured-card, .small-card {
    grid-column: span 1;
  }
  
  .hotbooks-list, .newbooks-list {
    grid-template-columns: repeat(2, 1fr); /* 手机上显示2列图书 */
  }
  
  .action-btn {
    font-size: 12px;
    min-height: 50px;
  }
  
  .btn-icon {
    font-size: 16px;
  }
}

/* 小屏幕 (平板竖屏) */
@media (min-width: 576px) and (max-width: 767px) {
  .dashboard-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .featured-card, .small-card {
    grid-column: span 1;
  }
  
  .hotbooks-list {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .newbooks-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 中等屏幕 (平板横屏) */
@media (min-width: 768px) and (max-width: 991px) {
  .dashboard-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .featured-card {
    grid-column: span 2;
  }
  
  .hotbooks-list {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .newbooks-list {
    grid-template-columns: repeat(3, 1fr);
  }
}

/* 大屏幕 */
@media (min-width: 992px) and (max-width: 1199px) {
  .dashboard-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .hotbooks-list {
    grid-template-columns: repeat(3, 1fr);
  }
}

/* 超大屏幕 */
@media (min-width: 1200px) {
  .hotbooks-list {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .newbooks-list {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>