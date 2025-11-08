<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { registerApi } from '@/api/register'

const router = useRouter()
const registerFormRef = ref(null)

// 定义注册表单数据，确保初始值为空
const registerForm = ref({
  userName: '',
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 自定义确认密码验证
function validateConfirmPassword(rule, value, callback) {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.value.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 注册方法
const register = async () => {
  if (registerFormRef.value) {
    registerFormRef.value.validate(async (valid) => {
      if (valid) {
        const result = await registerApi(registerForm.value)
        if (result.code == 200) {
        ElMessage.success('注册成功')
        router.push('/login')
        } else {
        ElMessage.error(result.msg)
        }         
      } else {
        ElMessage.warning('请填写正确的信息')
        return false
      }
    }) 
}
}

// 重置表单
const onReset = () => {
  if (registerFormRef.value) {
    registerFormRef.value.resetFields()
  }
}
// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}
</script>

<!-- src/views/register/index.vue -->
<template>
  <div id="container">
    <div class="register-form">
      <el-form label-width="80px" :model="registerForm" :rules="rules" ref="registerFormRef" key="register-form">
        <p class="title">用户注册</p>
        
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="registerForm.userName" placeholder="请输入用户名" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input 
            type="password" 
            v-model="registerForm.password" 
            placeholder="请输入密码"
            show-password autocomplete="new-password">
          </el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            type="password" 
            v-model="registerForm.confirmPassword" 
            placeholder="请再次输入密码"
            show-password>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button class="button" type="primary" @click="register">注 册</el-button>
          <el-button class="button" type="info" @click="onReset">重 置</el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-link">
        <span>已有账号？</span>
        <el-link type="primary" @click="goToLogin">去登录</el-link>
      </div>
    </div>
  </div>
</template>



<style scoped>
#container {
  padding: 10%;
  height: calc(100vh - 340px);
  background-image: url('../../assets/demo.png');
  background-repeat: no-repeat;
  background-size: cover;
}

.register-form {
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

.login-link {
  text-align: center;
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 5px;
}

.login-link span {
  color: #606266;
}

:deep(.el-form-item__label) {
  font-weight: bold;
}
</style>