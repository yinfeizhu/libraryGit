<script setup>
import { ref, watch, onMounted } from 'vue';
import { queryPageApi, addApi, queryByIdApi, updateApi, deleteApi } from '@/api/bookReader';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
// 图书分类列表数据（假设一些常见分类）
const categories = ref([
  { name: '文学小说类', value: '文学小说类' },
  { name: '科学技术类', value: '科学技术类' },
  { name: '经管法律类', value: '经管法律类' },
  { name: '生活艺术类', value: '生活艺术类' },
  { name: '人文社科类', value: '人文社科类' },
  { name: '电子资源类', value: '电子资源类' }
]);

// 搜索表单对象
const searchBook = ref({
  bookTitle: '',
  category: ''
});
// 图书列表
const bookList = ref([]);
// 分页相关数据
const currentPage = ref(1);
const pageSize = ref(5);
const total = ref(0);

// 每页条数改变时触发
const handleSizeChange = (val) => {
  pageSize.value = val;
  search();
}
// 当前页改变时触发
const handleCurrentChange = (val) => {
  currentPage.value = val;
  search();
}

// 查询图书列表
const search = async () => {
  const params = {
    page: currentPage.value,
    pageSize: pageSize.value,
    bookTitle: searchBook.value.bookTitle,
    category: searchBook.value.category
  };
  console.log('查询图书列表参数',params);
  const result = await queryPageApi(params.page,params.pageSize,params.bookTitle,params.category);
  
  if (result.code == 200) {
    bookList.value = result.data.rows;
    total.value = result.data.total;
  }
}
// 清空查询条件
const onReset = () => {
  searchBook.value = { bookTitle: '', category: '' };
  search();
}

// 日期格式化函数
const dateFormat = (row, column, cellValue, index) => {
  if (!cellValue) return '';
  
  // 处理上架日期
  if (column.property === 'acquire_date') {
    if (typeof cellValue === 'string' && /^\d{4}-\d{2}-\d{2}/.test(cellValue)) {
      return cellValue.substring(0, 10);
    }
    
    const date = new Date(cellValue);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
  
  // 处理创建/更新时间
  if (column.property === 'create_time' || column.property === 'update_time') {
    if (typeof cellValue === 'string' && cellValue.includes('T')) {
      return cellValue.replace('T', ' ');
    }
    return cellValue;
  }
  
  return cellValue;
};

// 记录勾选的图书id
const selectedIds = ref([]);
// 复选框发生变化时触发
const handleSelectionChange = (selections) => {
  selectedIds.value = selections.map(book => book.id);
}
// 查看图书描述
const showDescription = (description) => {
  ElMessageBox.alert(
    `<div style="font-size: 20px; line-height: 1.6; white-space: pre-wrap;">${description || '无描述信息'}</div>`, 
    '图书描述', 
    {
      confirmButtonText: '确定',
      type: 'info',
      dangerouslyUseHTMLString: true,
      size: 'large'
    }
  );
}
// 新增图书
const addBook = () => {
  dialogVisible.value = true;
  dialogTitle.value = '新增图书';
  book.value = {
    code: '',
    title: '',
    coverImage: '',
    location: '',
    description: '',
    publisher: '',
    acquireDate: '',
    category: '',
    availableNum: 0,
  };

  // 清空表单校验
  if (bookForm.value) {
    bookForm.value.resetFields();
  }
}

// 新增/修改表单数据
const book = ref({
  code: '',
  title: '',
  coverImage: '',
  location: '',
  description: '',
  publisher: '',
  acquireDate: '',
  category: '',
  availableNum: ''
})

// 控制弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('');

// 保存图书
const save = async () => {
  // 校验表单
  if (!bookForm.value) return;
  bookForm.value.validate(async (valid) => {
    if (valid) { // 校验成功
      if (dialogTitle.value == '新增图书') {
        const result = await addApi(book.value);
        if (result.code == 200) {
          ElMessage.success('新增成功');
          dialogVisible.value = false;
          search();
        } else {
          ElMessage.error(result.msg);
        }
      } else if (dialogTitle.value == '修改图书') {
        const result = await updateApi(book.value);
        if (result.code == 200) {
          ElMessage.success('修改成功');
          dialogVisible.value = false;
          search();
        } else {
          ElMessage.error(result.msg);
        }
      } else { // 校验失败
        ElMessage.error('保存方式异常');
      }
    } else { // 校验失败
      ElMessage.error('请填写正确的图书信息');
    }
  });
}
// 表单的引用
const bookForm = ref(null);
// 表单校验规则
const rules = ref({
  code: [
    { required: true, message: '请输入书籍编号', trigger: 'blur' },
    { min: 1, max: 10, message: '书籍编号长度应在1到10个字符之间', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请输入书名', trigger: 'blur' },
    { min: 1, max: 100, message: '书名长度应在1到100个字符之间', trigger: 'blur' }
  ],
  publisher: [
    { required: true, message: '请输入出版社', trigger: 'blur' },
    { min: 1, max: 16, message: '出版社长度应在1到16个字符之间', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入存放位置', trigger: 'blur' },
    { min: 1, max: 18, message: '存放位置长度应在1到18个字符之间', trigger: 'blur' }
  ]
});

// 修改图书
const editBook = async (id) => {
  // 根据id查询图书详情
  const result = await queryByIdApi(id);
  if (result.code == 200) {
    // 打开对话框
    dialogVisible.value = true;
    // 将查询到的图书详情赋值给图书对象
    book.value = result.data;
    // 设置对话框标题
    dialogTitle.value = '修改图书';
  } else {
    ElMessage.error(result.msg);
  }
}
// 删除图书
const deleteById = async (id) => {
  // 确认删除
  ElMessageBox.confirm('确定删除该图书吗？', '提示',
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
      // 取消删除
      ElMessage.info('已取消删除');
    });
}
// 批量删除图书
const deleteBatch = async () => {
  // 确认删除
  ElMessageBox.confirm('确定删除选中的图书吗？', '提示',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    .then(async () => { // 确定删除
      if (selectedIds.value.length > 0) {
        const result = await deleteApi(selectedIds.value);
        if (result.code == 200) {
          ElMessage.success('删除成功');
          search();
        } else {
          ElMessage.error(result.msg);
        }
      } else { // 没有选择图书
        ElMessage.warning('请选择要删除的记录');
      }
    }).catch(() => {
      // 取消删除
      ElMessage.info('已取消删除');
    });
}
//----------图片加载动画----------
const readerBookLoading = ref({});
// 图片加载完成处理
const onImageLoad = (title) => {
   readerBookLoading.value[title] = false; 
}
// 封面图片展示,本地存储时使用
// const getAvatarUrl = (bookCover) => {
//   if (!bookCover) return '';
//   return `http://localhost:8080/coverImage/${bookCover}`;
// }
//获取token,单独处理图片上传
const getToken = () => { 
  const loginUser = JSON.parse(localStorage.getItem('loginUserData'));
  if(loginUser && loginUser.token) {
    return loginUser.token;
  }
}


// 文件上传之前触发
const beforeCoverUpload = (rawFile) => {
  if (rawFile.type !== 'image/png' && rawFile.type !== 'image/jpeg') {
    ElMessage.error('只支持上传图片')
    return false
  }
  if (rawFile.size > 10 * 1024 * 1024) { // 10MB
    ElMessage.error('只能上传10M以内图片')
    return false
  }
  return true
}

// 文件上传成功后触发
const handleCoverSuccess = (response) => {
  console.log('上传成功响应:', response);
  if (response.code == 200) {
    book.value.coverImage = response.data; // 获取相对路径
    //初始化加载状态
    response.data.forEach(book => {
       readerBookLoading.value[book.title] = true
    })
  } else {
    ElMessage.error('上传失败，请重试');
  }
}
// 文件上传失败时触发
const handleCoverError = () => {
  ElMessage.error('上传失败，请检查网络或文件格式')
}

// 页面加载时查询数据
onMounted(() => {
  search(); // 查询图书列表
  //getToken(); // 获取token
})
</script>

<template> 
<h1>图书管理</h1>
  <!-- 搜索栏 -->
  <div class="container">
    <el-form :inline="true" :model="searchBook" class="demo-form-inline">
      <el-form-item label="书名">
        <el-input v-model="searchBook.bookTitle" placeholder="请输入书名" clearable />
      </el-form-item>
      <el-form-item label="类别">
        <el-select v-model="searchBook.category" placeholder="请选择类别" clearable>
          <el-option v-for="category in categories" :key="category.value" 
            :label="category.name" :value="category.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button type="info" @click="onReset">清空</el-button>
      </el-form-item>
    </el-form>
  </div>
   <!-- 功能按钮 -->
  <div>
    <el-button type="primary" @click="addBook()">新增图书</el-button>
    <el-button type="danger" @click="deleteBatch">批量删除</el-button>
  </div>
  
  <!-- 数据表格 -->
  <div class="table-container">
    <el-table :data="bookList" style="width: 100%" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="30" align="center" />
      <el-table-column prop="code" label="编号" width="90" align="center" />
      <el-table-column prop="title" label="书名" width="130" align="center" />
      <el-table-column prop="bookAuthor" label="作者" width="140" align="center" />
      <!-- 在template中的封面图片列修改为如下代码 -->
      <el-table-column prop="coverImage" label="封面图片" width="100" align="center">
        <template #default="scope">
          <el-image 
            v-if="scope.row.coverImage"
            :src="scope.row.coverImage"
            :preview-src-list="[scope.row.coverImage]"
            preview-teleported
            fit="cover"
            style="width: 60px; height: 80px; border-radius: 10%"
            :zoom-rate="1.2"
            :max-scale="7"
            :min-scale="0.2"
            :initial-index="0"
            hide-on-click-modal
          />
          <span v-else>无封面</span>
        </template>
      </el-table-column>
      <el-table-column prop="location" label="位置" width="180" align="center" />
      <el-table-column prop="description" label="描述" width="80" align="center">
        <template #default="scope">
          <el-button type="text" @click="showDescription(scope.row.description)" >查看</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="publisher" label="出版社" width="160" align="center" />
      <el-table-column prop="acquireDate" label="上架日期" :formatter="dateFormat" width="130" align="center" />
      <el-table-column prop="category" label="类别" width="100" align="center" />
      <el-table-column prop="availableNum" label="可借数量" width="90" align="center" />
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" @click="editBook(scope.row.id)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteById(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
  
  <!-- 分页 -->
  <div class="container">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[5, 10, 20, 30, 50]"
      :background="true"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>

   <!-- 新增/修改图书对话框 -->
  <div class="container">
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="50%">
      <el-form :model="book" label-width="80px" :rules="rules" ref="bookForm">
        <!-- 第一行：编号、书名 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="编号" prop="code">
              <el-input v-model="book.code" placeholder="请输入书籍编号，1-10个字符"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="书名" prop="title">
              <el-input v-model="book.title" placeholder="请输入书名，1-100个字符"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第二行：位置、 上架时间 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="位置" prop="location">
              <el-input v-model="book.location" placeholder="请输入图书位置"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上架日期">
              <el-date-picker v-model="book.acquireDate" type="date" style="width: 100%;" 
                placeholder="选择日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第三行：出版社、类别 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出版社">
              <el-input v-model="book.publisher" placeholder="请输入出版社"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类别" prop="category">
              <el-select v-model="book.category" placeholder="请选择图书类别" style="width: 100%;">
                <el-option v-for="category in categories" :key="category.value" 
                  :label="category.name" :value="category.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第四行：描述 -->
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="描述">
              <el-input v-model="book.description" placeholder="请输入图书描述" type="textarea" 
                :rows="4"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第五行：图片上传 -->
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="封面图片">
              <el-upload
                class="avatar-uploader"
                action="/api/bookReader/upload"  
                :show-file-list="false"
                :on-success="handleCoverSuccess"
                :on-error="handleCoverError"
                :before-upload="beforeCoverUpload"
                :headers="{'token': getToken()}"
              >
                <img v-if="book.coverImage" :src="book.coverImage" class="avatar"
                @load="onImageLoad(book.title, true)"
                v-show="readerBookLoading[book.title]" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                <div v-show="readerBookLoading[book.title]" class="book-cover-placeholder">
                  <div class="loading-spinner"></div>
                </div>
              </el-upload>
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
</template>

<style scoped>
.container {
  margin-top: 20px;
}
.table-container {
  margin-top: 20px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  width: 100%;
  /* 限制容器最大宽度，确保在宽屏上也能触发滚动逻辑 */
  max-width: 100vw; /* 100vw 表示视口宽度 */
}
/* 图片加载占位符 */
.book-cover-placeholder {
  width: 100px;
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f0f0;
  border-radius: 4px;
  margin-bottom: 10px;
}
.avatar + .book-cover-placeholder {
  width: 40px;
  height: 60px;
}
/* 加载动画 */
.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.avatar {
  height: 160px;
  width: auto;
  border-radius: 4px;
}
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  line-height: 120px;
  border-radius: 10px;
  border: 1px dashed var(--el-border-color);
}

</style>