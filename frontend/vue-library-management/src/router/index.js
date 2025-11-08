// router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import IndexView from '../views/index/index.vue'
import LoginView from '../views/login/index.vue'
import changePasswordView from '../views/changePassword/index.vue'
import BorrowView from '../views/borrow/index.vue'
import FineView from '../views/fine/index.vue'
import BookAdminView from '../views/bookAdmin/index.vue'
import BookReaderView from '../views/bookReader/index.vue'
import LayoutView from '../views/layout/index.vue'
import LogView from '../views/log/index.vue'
import UserView from '../views/user/index.vue'
import BookReportView from '../views/report/book/index.vue'
import FineReportView from '../views/report/fine/index.vue'
import UserReportView from '../views/report/user/index.vue'
import RegisterView from '../views/register/index.vue'
import AnnouncementView from '../views/announcement/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'root',
      component: LayoutView,
      redirect: '/login',
      children: [
        { path: '/borrow', name: 'borrow', component: BorrowView },
        { path: '/fine', name: 'fine', component: FineView },
        { path: '/bookAdmin', name: 'bookAdmin', component: BookAdminView },
        { path: '/bookReader', name: 'bookReader', component: BookReaderView },
        { path: '/user', name: 'user', component: UserView },
        { path: '/bookReport', name: 'bookReport', component: BookReportView },
        { path: '/fineReport', name: 'fineReport', component: FineReportView },
        { path: '/userReport', name: 'userReport', component: UserReportView },
        { path: '/layout', name: 'layout', component: LayoutView },
        { path: '/log', name: 'log', component: LogView },
        { path: '/announcement', name: 'announcement', component: AnnouncementView },
      ]
    },
    // 首页单独配置，不包含在 LayoutView 中
    { path: '/index', name: 'index', component: IndexView },
    // 登录页单独配置，不包含在 LayoutView 中
    { path: '/login', name: 'login', component: LoginView },
    // 注册页单独配置，不包含在 LayoutView 中
    { path: '/register', name: 'register', component: RegisterView },
    // 修改密码页单独配置，不包含在 LayoutView 中
    { path: '/changePassword', name: 'changePassword', component: changePasswordView },
    // 404 页面，放在最后
   
  ]
})

export default router