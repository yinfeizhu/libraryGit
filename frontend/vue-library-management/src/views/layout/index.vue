<script setup>
import { computed, ref, onMounted, watch, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessageBox, ElMessage } from 'element-plus';

const router = useRouter();

// 新增：控制侧边栏显示状态
const sidebarVisible = ref(true);
// 新增：判断是否为移动设备
const isMobile = ref(false);

// 修改密码
const changePassword = () => {
  router.push('/changePassword');
};

// 退出登录
const logout = () => {
  ElMessageBox.confirm('确定退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    // 清除本地存储中的用户信息
    localStorage.removeItem('userInfo');
    // 清除登录用户信息
    localStorage.removeItem('loginUserData');
    // 跳转到登录页面
    router.push('/login');
    ElMessage.success('退出登录成功');
  }).catch(() => {
    // 用户点击取消
  });
};

// 定义角色信息
const userInfo = ref({})
// 获取当前登录用户信息
const getUserInfo = async () => {
  const loginUser = await JSON.parse(localStorage.getItem('loginUserData'));
  if (loginUser) {
    userInfo.value = loginUser;
  } else {
    ElMessage.error('用户信息查询失败');
  }
}
// 检查是否为普通管理员
const isAdmin = computed(() => {
  return userInfo.value && userInfo.value.role == 2;
});

// 新增：切换侧边栏显示状态
const toggleSidebar = () => {
  sidebarVisible.value = !sidebarVisible.value;
};

// 新增：监听窗口大小变化
const handleResize = () => {
  isMobile.value = window.innerWidth < 768;
  // 在非移动设备上默认显示侧边栏
  if (!isMobile.value) {
    sidebarVisible.value = true;
  }
};

onMounted(() => {
  getUserInfo();
  handleResize();
  window.addEventListener('resize', handleResize);
});

// 新增：组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
});

// 新增：监听路由变化，在移动设备上自动隐藏侧边栏
watch(() => router.currentRoute.value.path, () => {
  if (isMobile.value) {
    sidebarVisible.value = false;
  }
});
</script>

<template>
  <div class="common-layout">
    <el-container class="layout-container">
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-content">
          <!-- 新增：移动端汉堡菜单按钮 -->
          <el-button 
            icon="Menu" 
            class="hamburger-btn" 
            @click="toggleSidebar"
            v-if="isMobile"
          ></el-button>
          <span class="title">腾飞智能图书管理系统</span>
          <div class="right_tool">
            <el-button 
              link 
              @click="changePassword" 
              class="tool-btn"
            >
              <el-icon><EditPen /></el-icon> 
              <span>修改密码</span>
            </el-button>
            <span class="divider">|</span>
            <el-button 
              link 
              @click="logout" 
              class="tool-btn"
            >
              <el-icon><SwitchButton /></el-icon> 
              <span>退出登录</span>
            </el-button>
          </div>
        </div>
      </el-header>
      
      <el-container class="main-container">
        <!-- 左侧菜单 - 修改：根据状态控制显示 -->
        <el-aside 
          class="aside" 
          :class="{ 'aside-hidden': !sidebarVisible && isMobile }"
        >
          <el-menu 
            router 
            class="sidebar-menu"
            :default-active="$route.path"
          >
            <!-- 首页菜单 -->
            <el-menu-item index="/index">
              <el-icon><Promotion /></el-icon> 
              <span>首页</span>
            </el-menu-item>

            <!-- 图书信息管理菜单 -->
            <el-sub-menu index="/manage">
              <template #title>
                <el-icon><Management /></el-icon>
                <span>图书信息</span>
              </template>
              <el-menu-item index="/bookAdmin" v-if="isAdmin">
                <el-icon><Reading /></el-icon>
                <span>实物图书</span>
              </el-menu-item>
              <el-menu-item index="/bookReader">
                <el-icon><Notebook /></el-icon>
                <span>图书展示</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 借阅管理菜单 -->
            <el-sub-menu index="/borrow-manage"> 
              <template #title>
                <el-icon><Menu /></el-icon>
                <span>借阅管理</span>
              </template>
              <el-menu-item index="/borrow">
                <el-icon><Memo /></el-icon>
                <span>借阅记录</span>
              </el-menu-item>
              <el-menu-item index="/fine" v-if="isAdmin">
                <el-icon><Money /></el-icon>
                <span>罚款记录</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 系统信息管理 -->
            <el-sub-menu index="/system" v-if="isAdmin">
              <template #title>
                <el-icon><Tools /></el-icon>
                <span>系统信息</span>
              </template>
              <el-menu-item index="/user">
                <el-icon><User /></el-icon>
                <span>用户信息</span>
              </el-menu-item>
              <el-menu-item index="/announcement">
                <el-icon><Bell /></el-icon>
                <span>公告信息</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 数据统计管理 -->
            <el-sub-menu index="/report">
              <template #title>
                <el-icon><Histogram /></el-icon>
                <span>数据统计</span>
              </template>
              <el-menu-item index="/userReport">
                <el-icon><UserFilled /></el-icon>
                <span>用户信息</span>
              </el-menu-item>
              <el-menu-item index="/bookReport">
                <el-icon><Postcard /></el-icon>
                <span>图书信息</span>
              </el-menu-item>
              <el-menu-item index="/fineReport">
                <el-icon><DocumentCopy /></el-icon>
                <span>罚款信息</span>
              </el-menu-item>
              <el-menu-item index="/log" v-if="isAdmin">
                <el-icon><Document /></el-icon>
                <span>日志信息</span>
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-aside>
        
        <!-- 主内容区域 - 修改：添加遮罩层 -->
        <div class="main-content-wrapper">
          <!-- 新增：移动端遮罩层 -->
          <div 
            class="sidebar-mask" 
            v-if="sidebarVisible && isMobile"
            @click="toggleSidebar"
          ></div>
          
          <el-main class="main-content" :class="{ 'full-width': !sidebarVisible && isMobile }">
            <router-view></router-view>
          </el-main>
        </div>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
/* 整体布局 */
.common-layout {
  height: 100vh;
  overflow: hidden;
  margin: 0;
}

.layout-container {
  height: 100%;
}

/* 新增：汉堡菜单按钮 */
.hamburger-btn {
  color: white !important;
  margin-right: 12px;
  background: transparent !important;
  padding: 8px;
  font-size: 20px;
}

.hamburger-btn:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

/* 头部样式优化 */
.header {
  height: 64px;
  background: linear-gradient(135deg, #1a3a6c 0%, #2a5b8c 25%, #3a7cae 50%, #4a9dcf 75%, #5abcee 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  padding: 0 24px;
  position: relative;
  z-index: 1000;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  color: white;
  font-size: 28px;
  font-family: '楷体', 'Microsoft YaHei', sans-serif;
  font-weight: bold;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  letter-spacing: 1px;
}

.right_tool {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tool-btn {
  color: white !important;
  font-size: 18px;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.tool-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-1px);
}

.divider {
  color: rgba(255, 255, 255, 0.5);
  margin: 0 8px;
}

/* 主容器 */
.main-container {
  height: calc(100vh - 64px);
  overflow: hidden;
  position: relative;
}

/* 新增：主内容容器 */
.main-content-wrapper {
  flex: 1;
  position: relative;
  transition: all 0.3s ease;
}

/* 新增：移动端遮罩层 */
.sidebar-mask {
  position: fixed;
  top: 64px;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 998;
  transition: all 0.3s ease;
}

/* 侧边栏优化 */
.aside {
  width: 240px !important;
  background: linear-gradient(to bottom, #1a3658 0%, #2c5282 30%, #3182ce 100%) !important;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  overflow-y: auto;
  transition: all 0.3s ease;
  z-index: 999;
  position: relative;
}

/* 新增：侧边栏隐藏状态 */
.aside-hidden {
  transform: translateX(-100%);
  width: 0 !important;
}

.sidebar-menu {
  border: none;
  padding: 16px 0;
}

/* 主内容区域 */
.main-content {
  padding: 24px;
  background-color: #f5f7fa;
  overflow-y: auto;
  height: 100%;
  transition: all 0.3s ease;
}

/* 新增：主内容区域全屏状态 */
.full-width {
  margin-left: 0 !important;
}

/* 滚动条优化 */
.aside::-webkit-scrollbar,
.main-content::-webkit-scrollbar {
  width: 6px;
}

.aside::-webkit-scrollbar-track,
.main-content::-webkit-scrollbar-track {
  background: transparent;
}

.aside::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.main-content::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.aside::-webkit-scrollbar-thumb:hover,
.main-content::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .title {
    font-size: 20px;
  }
  
  .aside {
    width: 220px !important;
  }
  
  .main-content {
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .header {
    padding: 0 16px;
  }
  
  .title {
    font-size: 18px;
  }
  
  .right_tool .tool-btn span {
    display: none;
  }
  
  .divider {
    display: none;
  }
  
  /* 修改：移动端默认隐藏侧边栏 */
  .aside {
    position: absolute;
    height: 100%;
    width: 240px !important;
    transform: translateX(-100%);
  }
  
  .aside:not(.aside-hidden) {
    transform: translateX(0);
  }
  
  /* 移动端菜单文字显示 */
  :deep(.sidebar-menu span) {
    display: inline-block !important;
  }
  
  :deep(.sidebar-menu .el-sub-menu__title) {
    padding: 0 20px !important;
  }
  
  .main-content {
    margin-left: 0 !important;
    padding: 12px;
  }
}

/* 新增：超小屏幕适配 */
@media (max-width: 375px) {
  .title {
    font-size: 16px;
  }
  
  .aside {
    width: 200px !important;
  }
  
  .main-content {
    padding: 8px;
  }
}
</style>

<style>
/* 修复子菜单背景色问题 - 使用全局样式 */
.sidebar-menu .el-sub-menu .el-menu {
  background: linear-gradient(to bottom, #1a3658 0%, #2c5282 30%, #3182ce 100%) !important;
  border: none !important;
}

/* 菜单样式深度定制 */
.sidebar-menu {
  background: transparent !important;
  border: none !important;
}

/* 菜单文字颜色优化 */
.sidebar-menu .el-menu-item,
.sidebar-menu .el-sub-menu__title {
  height: 48px;
  line-height: 48px;
  color: #ffffff !important;
  font-size: 14px;
  font-weight: 600;
  margin: 4px 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.sidebar-menu .el-menu-item:hover,
.sidebar-menu .el-sub-menu__title:hover {
  background-color: rgba(255, 255, 255, 0.2) !important;
  color: #ffffff !important;
  transform: translateX(4px);
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%) !important;
  color: #ffffff !important;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.4);
  font-weight: 700;
}

/* 图标颜色优化 */
.sidebar-menu .el-icon {
  color: #ffffff !important;
  font-size: 18px;
  margin-right: 8px;
  filter: drop-shadow(0 1px 1px rgba(0, 0, 0, 0.3));
}

.sidebar-menu .el-menu-item.is-active .el-icon {
  color: #ffffff !important;
}

/* 子菜单项样式 */
.sidebar-menu .el-sub-menu .el-menu-item {
  background-color: rgba(0, 0, 0, 0.1) !important;
  margin: 2px 16px;
  padding-left: 40px !important;
  color: #ffffff !important;
}

.sidebar-menu .el-sub-menu .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

.sidebar-menu .el-sub-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%) !important;
  color: #ffffff !important;
}

/* 子菜单图标颜色 */
.sidebar-menu .el-sub-menu .el-menu-item .el-icon {
  color: #ffffff !important;
}
</style>