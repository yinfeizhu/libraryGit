<script setup>
import { ref,onBeforeMount } from 'vue';
import { loginApi } from '@/api/login';
import { ElMessage } from 'element-plus';
import { useRouter,onBeforeRouteLeave } from 'vue-router';

//定义登录表单数据
let loginForm = ref({userName:'', password:''});
const router = useRouter();
// 登录
const login = async() => { 
  // 添加基本验证
  if (!loginForm.value.userName || !loginForm.value.password) {
    ElMessage.warning('请输入用户名和密码');
    return;
  }
  
  try {
    const result = await loginApi(loginForm.value);
    console.log('登录结果:', result);
    if(result.code == 200){
      ElMessage.success('登录成功');
      
      // 后端传来的本来是一个result对象 先存储为JSON字符串，再跳转,
      // 这里是存储了result的所有数据包含了姓名、用户名、id、role和token等用户信息
      localStorage.setItem('loginUserData', JSON.stringify(result.data)); 
      // 跳转到首页
      router.push('/index');
    } else {
      ElMessage.error(result.msg);
    }
  } catch (error) {
    ElMessage.error('登录请求失败，请稍后重试');
    console.error('登录错误:', error);
  }
};

// 重置
const onReset = () => {
  loginForm.value = {userName:'', password:''};
};

// 离开页面时重置表单
onBeforeRouteLeave(() => {
  onReset();
})
// 跳转到注册页面
const goToRegister = () => {
  router.push('/register'); // 假设注册页面路由为 /register
};
</script>

<template>
  <div id="container">
    <div class="login-form">
      <el-form label-width="80px" :model="loginForm" key="login-form">
        <p class="title">腾飞智能图书管理系统</p>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.userName" placeholder="请输入用户名"></el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button class="button" type="primary" @click="login">登 录</el-button>
          <el-button class="button" type="info" @click="onReset">重 置</el-button>
        </el-form-item>
      </el-form>
      <!-- 添加注册 -->
       <div class="register-link" >
          <span>还没有账号？</span>
          <el-link type="primary" @click="goToRegister">去注册</el-link>
        </div>
    </div>
  </div>
</template>

<style scoped>
#container {
  padding: 10%;
  height: calc(100vh - 360px);
  background-image: url('../../assets/login-background.png');
  background-repeat: no-repeat;
  background-size: cover;
}

.login-form {
  max-width: 400px;
  padding: 30px;
  margin: 0 auto;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  background-color: white;
}

.title {
  font-size: 30px;
  font-family: '楷体';
  text-align: center;
  margin-bottom: 30px;
  font-weight: bold;
}

.button {
  margin-top: 30px;
  width: 120px;
}
.register-link {
  text-align: center;
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 5px;
}
.register-link span {
  color: #606266;
}
</style>