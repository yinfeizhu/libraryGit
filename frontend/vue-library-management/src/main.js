// main.js 文件是 Vue 应用的入口文件和全局配置文件
import { createApp } from 'vue' // 引入 Vue 库
import App from './App.vue' //根组件
import ElementPlus from 'element-plus' // 引入 Element Plus 库
import 'element-plus/dist/index.css' // 引入 Element Plus 样式
import zhCn from 'element-plus/es/locale/lang/zh-cn' // 引入中文语言包
import * as ElementPlusIconsVue from '@element-plus/icons-vue' // 引入所有图标组件
import router from './router'
import './assets/main.css'

const app = createApp(App)// 创建 Vue 应用实例

app.use(router)// 注册路由插件
app.use(ElementPlus, {locale: zhCn})// 注册 Element Plus 插件，设置语言为中文
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}// 注册所有图标组件
app.mount('#app')// 挂载根组件
