<script setup>
import { ref, watch, onMounted,computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { queryPageApi, queryByIdApi, addApi, updateApi, deleteApi } from '@/api/user'

//----------------------------------------------------分页及条件查询-----------
// 定义用户列表
const userList = ref([]);
// 读者类型列表
const readerTypes = ref([
  { label: '学生', value: '1' },
  { label: '教师', value: '2' },
  { label: '职工', value: '3' },
  { label: '其他', value: '4' }
]);
// 状态列表
const statusTypes = ref([
  { label: '正常', value: '1' },
  { label: '冻结', value: '2' }
]);
// 角色列表
const roleTypes = ref([
  { label: '用户', value: '1' },
  { label: '管理员', value: '2' }
]);
// 性别列表
const genders = ref([
  { label: '男', value: '1' },
  { label: '女', value: '2' }
]);
// 查询参数
const searchUser = ref({
  name: '',
  readerType: ''
});
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 分页大小改变时触发
const handleSizeChange = (val) => {
  pageSize.value = val;
  search();
};
// 分页当前页改变时触发
const handleCurrentChange = (val) => {
  currentPage.value = val;
  search();
};
// 查询用户
const search = async () => { 
  //定义查询参数
  const params = {
    name: searchUser.value.name,
    readerType: searchUser.value.readerType,
    page: currentPage.value,
    pageSize: pageSize.value
  };
  console.log('查询参数', params);
  // 调用查询接口
  const result = await queryPageApi(
    params.page, params.pageSize, params.name, params.readerType);
  console.log('查询结果', result);
  if (result.code == 200) {
    userList.value = result.data.rows;
    total.value = result.data.total;
  } else {
    ElMessage.error(result.msg);
  }
  console.log('赋值后的查询结果', userList.value);
};

// 重置查询条件
const onReset = () => {
  searchUser.value.name = '';
  searchUser.value.readerType = '';
  search();
};
//--------------------------------------------------表单相关------------
//清空表单数据的方法
const resetUserForm = () => { 
  // 重置表单数据
  user.value = {
    userName: '',
    password: '123456', // 默认密码
    name: '',
    gender: '',
    phone: '',
    address: '',
    readerType: '',
    borrowedNum: '',
    status: '1', // 默认正常状态
    role: '1' // 默认普通用户
  };
};
//对话框相关参数
const dialogVisible = ref(false);
const dialogTitle = ref();
// 添加用户
const addUser = async () => {
  // 重置表单验证状态
  if (userForm.value) {
    userForm.value.resetFields();
  }
  // 重置表单数据
  resetUserForm();

  dialogTitle.value = '添加用户';
  dialogVisible.value = true;
};
//定义表单参数
const user = ref({
  userName: '',
  password: '',
  name: '',
  gender: '',
  phone: '',
  address: '',
  readerType: '',
  borrowedNum: 0,
  status: '1',
  role: '1'
});
//表单的引用
const userForm = ref(null);
//表单校验规则
const rules = ref({
  name: [
    { required: true, message: '请输入用户姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度应在2到20个字符之间', trigger: 'blur' }
  ],
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 5, max: 20, message: '用户名长度应在5到20个字符之间', trigger: 'blur' }
  ],
  gender: [
    { required: false, message: '请选择性别', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ],
  readerType: [
    { required: false, message: '请选择读者类型', trigger: 'change' }
  ],
  status: [
    { required: false, message: '请选择状态', trigger: 'change' }
  ],
  role: [
    { required: false, message: '请选择角色', trigger: 'change' }
  ],
  borrowedNum: [
    { required: false, message: '请输入借书数量', trigger: 'blur' },
    { type: 'number', min: 0, message: '借书数量不能为负数', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' },
    { min: 0, max: 100, message: '地址长度应在0到100个字符之间', trigger: 'blur' }
  ]
});
//保存
const save = async () => { 
  //validate()作用：校验表单数据是否符合rules规则
  const res = await userForm.value.validate();
  // 如果校验通过，res为true
  if (res) {
    // 准备提交的数据，将字符串转换为数字
    const submitData = {
      ...user.value,
      gender: Number(user.value.gender),
      readerType: Number(user.value.readerType),
      borrowedNum: Number(user.value.borrowedNum),
      status: Number(user.value.status),
      role: Number(user.value.role)
    };
    
    if (dialogTitle.value == '添加用户') {
      const result = await addApi(submitData);
      if (result.code == 200) {
        ElMessage.success('添加成功');
        dialogVisible.value = false;
        search();
      } else {
        ElMessage.error(result.msg);
      }
    } else {
      console.log('修改用户信息', submitData);
      const result = await updateApi(submitData);
      if (result.code == 200) {
        ElMessage.success('修改成功');
        dialogVisible.value = false;
        search();
      } else {
        ElMessage.error(result.msg);
      }
    }
  } else { 
    ElMessage.error('请填写符合要求的信息');
  }
};
//----------------------------------------编辑用户-----------------------------------------
const editUser = async (id) => {
  console.log('编辑用户id', id);
  const result = await queryByIdApi(id);
  console.log('查询结果', result);
  if (result.code == 200 && result.data) {
    user.value = {
      ...result.data,
      gender: String(result.data.gender),
      readerType: String(result.data.readerType),
      status: String(result.data.status),
      role: String(result.data.role)
    };
    dialogTitle.value = '编辑用户';
    dialogVisible.value = true;
  } else {
    ElMessage.error(result.msg);
  }
};

//----------------------------------------删除用户------------
//记录勾选的用户id
const selectedIds = ref([]);
//复选框发生变化时触发
const handleSelectionChange = (selections) => {
  selectedIds.value = selections.map(user => user.id);
};
// 批量删除用户
const deleteBatch = async () => {
  //确认删除
  ElMessageBox.confirm('确定删除选中的用户吗？', '提示', 
  { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
  .then(async () => { //确定删除
    if (selectedIds.value.length > 0) {
      const result = await deleteApi(selectedIds.value);
      if (result.code == 200) { 
        ElMessage.success('删除成功');
        search();
      } else {
        ElMessage.error(result.msg);
      } 
    } else { //没有选择用户
      ElMessage.warning('请选择要删除的记录');
    }   
  }).catch(() => {
    // 取消删除
    ElMessage.info('已取消删除');
  });
};
//删除单个用户
const deleteUserById = async (id) => { 
  //确认删除
  ElMessageBox.confirm('确定删除选中的用户吗？', '提示', 
  { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
  .then(async () => { 
    // 确定删除
    const result = await deleteApi(id);
    if (result.code == 200) { 
      ElMessage.success('删除成功');
      search();
    } else {
      ElMessage.error(result.msg);
    }
  }).catch(() => {
    //取消删除
    ElMessage.info('已取消删除');
  });
};
//------------------------------------权限控制-----------------------------------------
const userInfo=ref({})
//获取当前登录用户信息
const getUserInfo = async  () => {
  const loginUser= await JSON.parse(localStorage.getItem('loginUserData'));
  if (loginUser) {
    userInfo.value = loginUser;
  } else {
    ElMessage.error('用户信息查询失败');
  }
}
// 检查是否为普通管理员
const isAdmin = computed(() => {
  return userInfo.value &&  userInfo.value.role == 2;
});
// ---------------------------------------------------钩子函数------------------------------
onMounted(() => {
  search();
  getUserInfo();
});
</script>

<template>
  <div v-if="isAdmin"> 
  <h1>用户管理</h1>
  <!-- 搜索栏  -->
  <div class="container">
    <el-form :inline="true" :model="searchUser" class="demo-form-inline">
      <el-form-item label="姓名">
        <el-input v-model="searchUser.name" placeholder="请输入用户名称" clearable />
      </el-form-item>
      <el-form-item label="读者类型">
        <el-select v-model="searchUser.readerType" placeholder="请选择读者类型">
          <el-option v-for="item in readerTypes" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button type="info" @click="onReset">清空</el-button>
      </el-form-item>
    </el-form>
  </div>
  <!-- 功能按钮 -->
  <div class="container" >
    <el-button type="primary" @click="addUser" >添加用户</el-button>
    <el-button type="danger" @click="deleteBatch" >批量删除</el-button>
  </div>
  <!-- 数据表格 -->
   <div class="table-container">
      <el-table :data="userList" style="width: 100%" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="30" align="center"/>
        <el-table-column prop="name" label="姓名" width="100" align="center"/>
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="scope">
            {{ scope.row.gender == 1 ? '男' : '女' }}
          </template> 
        </el-table-column> 
        <el-table-column prop="phone" label="手机号" width="130" align="center"/>
        <el-table-column prop="address" label="地址" width="280" align="center"/>
        <el-table-column prop="readerType" label="读者类型" width="100" align="center">
          <template #default="scope">
            {{ readerTypes.find(item => item.value == scope.row.readerType)?.label || '其他' }}
          </template>
        </el-table-column>
        <el-table-column prop="borrowedNum" label="借书数量" width="100" align="center"/>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="scope">
            {{ scope.row.status == 1 ? '正常' : '冻结' }}
          </template>
        </el-table-column>
        <el-table-column prop="role" label="身份" width="100" align="center">
          <template #default="scope">
            {{ scope.row.role == 1 ? '用户' : '管理员' }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="200" align="center" />
        <el-table-column label="操作" align="center">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editUser(scope.row.id)" align="center">编辑</el-button>
            <el-button type="danger" size="small" align="center" @click="deleteUserById(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
  </div>
  <!-- 分页 -->
  <div class="container" >
  <el-pagination
    v-model:current-page="currentPage"
    v-model:page-size="pageSize"
    :page-sizes="[5,10, 20, 30, 50,75,100]"
    :background="true"
    layout="total, sizes, prev, pager, next, jumper"
    :total="total"
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
  />
  </div>
  <!-- 新增/修改用户对话框 -->
  <div class="container">
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" top="10%">
      <el-form :model="user" label-width="80px" :rules="rules" ref="userForm" >
        <!-- 第一行：姓名、手机号 -->
        <el-row :gutter="30">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="user.name" placeholder="请输入用户姓名"></el-input>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="user.phone" placeholder="请输入手机号"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第二行：用户名、性别 -->
        <el-row :gutter="30">
          <el-col :span="12">
            <el-form-item label="用户名" prop="userName">
              <el-input v-model="user.userName" placeholder="请输入用户名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地址" prop="address">
              <el-input v-model="user.address" placeholder="请输入地址"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第三行：地址、读者类型 -->
        <el-row :gutter="30">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="user.gender" placeholder="请选择性别" style="width: 100%;">
                <el-option v-for="item in genders" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="读者类型" prop="readerType">
              <el-select v-model="user.readerType" placeholder="请选择读者类型" style="width: 100%;">
                <el-option v-for="item in readerTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第四行：借书数量、状态 -->
        <el-row :gutter="30">
          <el-col :span="12">
            <el-form-item label="借书数量" prop="borrowedNum">
              <el-input v-model.number="user.borrowedNum" placeholder="请输入借书数量"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="user.status" placeholder="请选择状态" style="width: 100%;">
                <el-option v-for="item in statusTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第五行：角色权限 -->
        <el-row :gutter="30">
          <el-col :span="12">
            <el-form-item label="角色权限" prop="role">
              <el-select v-model="user.role" placeholder="请选择角色权限" style="width: 100%;">
                <el-option v-for="item in roleTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="dialogTitle.value == '添加用户'">
            <el-form-item label="初始密码">
              <el-input v-model="user.password" placeholder="请输入初始密码" disabled></el-input>
              <div style="font-size: 12px; color: #666; margin-top: 5px;">
                初始密码默认为: 123456
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        
      </el-form>
      
      <!-- 底部按钮 -->
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </span>
      </template>
  </el-dialog>
  </div>
  </div>
</template>

<style scoped>
.container{
  margin-top: 20px;
}
/* 强制标签不换行 */
:deep(.no-wrap-label .el-form-item__label) {
  white-space: nowrap;/*强制不换行*/
}
.table-container {
  margin-top: 20px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  width: 100%;
  /* 限制容器最大宽度，确保在宽屏上也能触发滚动逻辑 */
  max-width: 100vw; /* 100vw 表示视口宽度 */
}
</style>