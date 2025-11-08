<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { updatePasswordApi,queryByIdApi } from '@/api/user'

const router = useRouter()
const formRef = ref()

// 表单数据
const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 当前登录用户信息
const currentUser = ref([])
// 表单验证规则
const rules = reactive({
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.newPassword) {
          callback(new Error('两次输入密码不一致!'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 获取当前登录的用户信息
onMounted(() => {
  const loginUser = JSON.parse(localStorage.getItem('loginUserData'))
  if (loginUser) {
    currentUser.value = loginUser
  }
})
//验证用户旧密码是否正确
const testOldPassword = async () => {
  try {
    
    const result = await queryByIdApi(currentUser.value.id) 
    if (result.code == 200 && result.data.password == form.oldPassword) {
      return true
    } else {
      ElMessage.error('旧密码错误')
      return false
    }
  } catch (error) {
    ElMessage.error('验证旧密码失败')
    return false
  }
}
// 提交表单
const submitForm = () => {
  formRef.value.validate((valid) => {
    if (form.newPassword == form.oldPassword) {
        ElMessage.warning('新密码不能与旧密码相同')
        result 
      } 
    
    if (valid) {
      ElMessageBox.confirm(
        '确定要修改密码吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      ).then(async () => {
        try {
          // 验证旧密码
          const testResult = await testOldPassword();
          if (!testResult) {
            return
          }
          console.log('旧密码正确')
          // 使用用户更新API来修改密码
          const submitData = {
            id: currentUser.value.id,
            oldPassword: form.oldPassword,
            newPassword: form.newPassword
          }
          console.log('执行修改密码',submitData)
          const result = await updatePasswordApi(submitData.id,submitData.oldPassword,submitData.newPassword)
          if (result.code == 200) {
            ElMessage.success('密码修改成功')
            // 清除本地存储的登录信息，强制重新登录
            localStorage.removeItem('token')
            localStorage.removeItem('loginUserData')
            router.push('/login')
          } else {
            ElMessage.error(result.msg || '密码修改失败')
          }
        } catch (error) {
          ElMessage.error('密码修改失败，请重试')
        }
      }).catch(() => {
        ElMessage.info('已取消修改')
      })
    } else {
      ElMessage.error('请完善表单信息')
    }
  })
}

// 重置表单
const resetForm = () => {
  formRef.value.resetFields()
}
</script>

<template>
  <div class="change-password-container">
    <el-card class="password-card">
      <template #header>
        <div class="card-header">
          <span style="font-size: 18px; font-weight: bold;">修改密码</span>
        </div>
      </template>
      
      <div class="user-info">
        <el-text type="warning"><h1>当前用户：{{ currentUser.name || currentUser.username }}</h1></el-text>
      </div>
      <!-- 表单内容 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="password-form" 
      >
      <!-- 第一行 -->
        <el-form-item label="旧密码：" prop="oldPassword">
          <el-input 
            v-model="form.oldPassword" 
            type="password" 
            placeholder="请输入旧密码"
            show-password
            style="width: 300px;"
          />
        </el-form-item>
        <!-- 第二行 -->
        <el-form-item label="新密码：" prop="newPassword">
          <el-input 
            v-model="form.newPassword" 
            type="password" 
            placeholder="请输入新密码"
            show-password
            style="width: 300px;"
          />
        </el-form-item>
        <!-- 第三行 --> 
        <el-form-item label="确认新密码：" prop="confirmPassword">
          <el-input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码"
            show-password
            style="width: 300px;"
          />
        </el-form-item>
      <!-- 按钮 -->    
        <el-form-item class="form-actions">
          <el-button type="primary" @click="submitForm">提交</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button @click="router.push('/index')">返回首页</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.change-password-container {
  padding: 20px;
  margin: 0;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  height: calc(100vh - 40px);
  background-image: url('../../assets/changePassword-bg.png');
}

.password-card {
  margin-top: 120px;
  width: 600px;
  height: 450px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.password-form {
  padding: 20px;
}

.user-info {
  text-align: center;
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.form-actions {
  text-align: right;
  padding-right: 50px;
}

.card-header {
  text-align: center;
  padding: 10px;
}

:deep(.el-form-item__label) {
  font-weight: bold;
}
</style>