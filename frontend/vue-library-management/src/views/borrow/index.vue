<script setup>
import { ref, onMounted, computed } from 'vue';
import { queryPageApi, updateApi, addApi, deleteBatchApi } from '@/api/borrow';
import { ElMessage, ElMessageBox } from 'element-plus';
import { queryPageApi as queryUserPageApi } from '@/api/user';
import { queryPageApi as queryBookPageApi } from '@/api/bookAdmin';
// 搜索表单对象
const searchForm = ref({
  readerName: '',
  bookBarcode: '',
  bookTitle: ''
});

// 借阅记录列表
const borrowList = ref([]);
// 分页相关数据
const currentPage = ref(1);
const pageSize = ref(10);
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

// 查询借阅记录列表
const search = async () => {
  const params = {
    page: currentPage.value,
    pageSize: pageSize.value,
    readerName: searchForm.value.readerName,
    bookBarcode: searchForm.value.bookBarcode,
    bookTitle: searchForm.value.bookTitle
  };
  
  console.log('查询借阅记录参数', params);
  const result = await queryPageApi(params.page, params.pageSize, params.readerName, params.bookBarcode, params.bookTitle);
  
  if (result.code == 200) {
    borrowList.value = result.data.rows;
    total.value = result.data.total;
  }
}
// 清空查询条件
const onReset = () => {
  searchForm.value = { readerName: '', bookBarcode: '', bookTitle: '' };
  search();
}
// 日期格式化函数
const dateFormat = (row, column, cellValue, index) => {
  if (!cellValue) return '';
  
  // 处理借书时间、应还时间、实还时间、更新时间
  if (['borrowTime', 'dueTime', 'returnTime', 'updateTime'].includes(column.property)) {
    if (typeof cellValue === 'string' && cellValue.includes('T')) {
      return cellValue.replace('T', ' ').substring(0, 16);
    }
    return cellValue;
  }
  
  return cellValue;
};

// 还书操作
const handleReturn = async (id) => {
    // 确认还书
    ElMessageBox.confirm('确认还书吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
      const result = await updateApi(id);
      if (result.code == 200) {
      ElMessage.success('还书成功');
      search();
      } else {
      ElMessage.error(result.msg || '还书失败');
      }
    }).catch(() => {
    // 取消还书
    ElMessage.info('已取消还书');
  });
}

// 借阅信息弹窗相关数据
const dialogVisible = ref(false);
const borrowFormRef = ref()
const record = ref({
  readerId: '',
  bookAdminId: '',
  dueTime: '',
});
// 添加借阅信息
const handleAdd = () => {
  dialogVisible.value = true;
  dialogTitle.value = '添加借阅信息';  borrowForm.value = { readerId: '', bookAdminId: '', dueTime: '' };
  // 重置表单校验状态
  if (borrowFormRef.value) {
    borrowFormRef.value.resetFields();
  }
}
//定义取消按钮点击事件
const handleCancel = () => {
  dialogVisible.value = false;
  // 清空表单数据
  record.value = { readerId: '', bookAdminId: '', dueTime: '' };
  // 重置表单校验状态
  if (borrowFormRef.value) {
    borrowFormRef.value.resetFields();
  }
}
//读者姓名下拉框数据
const readers = ref([]);
// 查询所有读者姓名
const queryReaderName = async () => {
  const result = await queryUserPageApi(1, 1000, '', '', '');
  if (result.code == 200) {
     readers.value = result.data.rows;
    }else {
      ElMessage.error('读者查询失败');
    }
}
//书名下拉框数据
const books = ref([]);
// 查询所有书名
const queryBookTitle = async () => {
  const result = await queryBookPageApi(1, 1000, '', '', 1);
  if (result.code == 200) {
     books.value = result.data.rows;
    }else {
      ElMessage.error('图书查询失败');
    }
}
//定义校验规则
const rules = ref({
  readerId:
   [{ required: true, message: '请选择借阅人', trigger: 'blur' }],
  bookAdminId:
   [{ required: true, message: '请选择图书条码', trigger: 'blur' }],
  dueTime:
   [{ required: false, message: '请选择应还时间', trigger: 'blur' }],
});
// 保存借阅信息
const save = async () => {
  // 使用表单引用触发校验
  if (borrowFormRef.value) {
    borrowFormRef.value.validate(async (valid) => {
      if (valid) {
        // 校验通过，执行保存操作
        const result = await addApi(record.value);
        if (result.code == 200) {
          ElMessage.success('保存成功');
          dialogVisible.value = false;
          search();
        } else {
          ElMessage.error(result.msg || '保存失败');
        }
      } else {
        // 校验不通过，提示用户
        ElMessage.error('请填写必填项');
        return false;
      }
    });
  }
}
// 记录勾选的图书id
const selectedIds = ref([]);
// 复选框发生变化时触发
const handleSelectionChange = (selections) => {
  selectedIds.value = selections.map(book => book.id);
}
//批量删除
const deleteSelected = async () => {
  // 确认删除
  ElMessageBox.confirm('确认删除吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const result = await deleteBatchApi(selectedIds.value);
    if (result.code == 200) {
      ElMessage.success('删除成功');
      search();
    } else {
      ElMessage.error(result.msg || '删除失败');
    }

  }).catch(() => {
    // 取消删除
    ElMessage.info('已取消删除');
  });
}
const userInfo = ref({});
//获取当前登录用户信息
const getUserInfo = async  () => {
  const loginUser= await JSON.parse(localStorage.getItem('loginUserData'));
  if (loginUser) {
    userInfo.value = loginUser;
  } else {
    ElMessage.error('用户信息查询失败');
  }
}
// 检查是否为超级管理员
const isSuperAdmin = computed(() => {
  return userInfo.value && userInfo.value.id == 1 && userInfo.value.role == 2;
});
// 检查是否为普通管理员
const isAdmin = computed(() => {
  return userInfo.value &&  userInfo.value.role == 2;
});
// 页面加载时查询数据
onMounted(() => {
  search();
  queryReaderName();
  queryBookTitle();
  getUserInfo();
})
</script>

<template>
  <h1>借阅记录</h1>
  <!-- 搜索栏 -->
  <div class="container">
    <el-form :inline="true" :model="searchForm" class="demo-form-inline" v-if="isAdmin">
      <el-form-item label="读者姓名" >
        <el-input v-model="searchForm.readerName" placeholder="请输入读者姓名" clearable />
      </el-form-item>
      <el-form-item label="图书条码">
        <el-input v-model="searchForm.bookBarcode" placeholder="请输入图书条码" clearable />
      </el-form-item>
      <el-form-item label="书名">
        <el-input v-model="searchForm.bookTitle" placeholder="请输入书名" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button type="info" @click="onReset">清空</el-button>
      </el-form-item>
    </el-form>
  </div>

  <!-- 功能按钮 -->
  <div>
    <el-button type="success" @click="handleAdd">借阅图书</el-button>
    <el-button type="danger" @click="deleteSelected" 
    v-if="isSuperAdmin">
    批量删除
    </el-button>
  </div>

  <!-- 数据表格 -->
  <div class="table-container">
    <el-table 
      :data="borrowList" 
      style="width: 100%" 
      border 
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="30" align="center" />
      <el-table-column prop="readerName" label="读者姓名" width="100" align="center" />
      <el-table-column prop="bookTitle" label="书名" width="180" align="center" />
      <el-table-column prop="readerPhone" label="读者手机号" width="150" align="center" />
      <el-table-column prop="bookBarcode" label="条码号" width="140" align="center" />
      <el-table-column prop="borrowTime" label="借书时间" width="180" align="center" :formatter="dateFormat" />
      <el-table-column prop="dueTime" label="应还时间" width="180" align="center" :formatter="dateFormat" />
      <el-table-column prop="returnTime" label="实还时间" width="180" align="center" :formatter="dateFormat" />
      <el-table-column prop="operatorName" label="操作人" width="100" align="center" />
      <el-table-column label="操作" align="center" auto >
        <template #default="scope">
            <el-button type="warning" size="small" @click="handleReturn(scope.row.id)">还书</el-button>
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
  <!-- 借阅信息弹窗 -->
  <el-dialog v-model="dialogVisible" title="借阅图书" width="30%">
    <el-form ref="borrowFormRef" :model="record" :rules="rules" label-width="80px">
          
            <el-form-item label="借阅人" prop="readerId">
                <el-select v-model="record.readerId" placeholder="请选择借阅人" filterable style="width: 80%;"> 
                <el-option v-for="reader in readers" :value="reader.id" :key="reader.id" :label="reader.name" />
                </el-select>
            </el-form-item>    
            <el-form-item label="图书条码" prop="bookAdminId">
                <el-select v-model="record.bookAdminId" placeholder="请选择图书条码" filterable style="width: 80%;"> 
                <el-option v-for="book in books" :value="book.id" :key="book.id" :label="book.barcode" />
                </el-select>
            </el-form-item>    
            <el-form-item label="应还时间" prop="dueTime">
                <el-date-picker v-model="record.dueTime" type="date"  
                 placeholder="选择应还时间，默认三十天后" style="width: 80%;"
                 format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
            </el-form-item>
    </el-form>
    <!-- 按钮部分 -->
    <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </span>
      </template>
  </el-dialog>
</template>

<style scoped>
.container {
  margin-top: 20px;
  padding: 0 10px; /* 增加左右内边距，避免内容贴边 */
}
.table-container {
  margin-top: 20px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  width: 100%;
  /* 限制容器最大宽度，确保在宽屏上也能触发滚动逻辑 */
  max-width: 100vw; /* 100vw 表示视口宽度 */
}
/* 基础响应式设置 */
@media screen and (max-width: 768px) {
  /* 调整标题大小 */
  h1 {
    font-size: 1.5rem;
    text-align: center;
    margin: 15px 0;
  }

  /* 搜索栏适配 */
  .demo-form-inline {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .demo-form-inline .el-form-item {
    width: 100%;
    margin-right: 0 !important;
  }

  .demo-form-inline .el-input {
    width: 100% !important;
  }

  /* 功能按钮区域 */
  .el-button-group,
  div:has(> .el-button) {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    padding: 10px;
    justify-content: flex-start;
  }

  .el-button {
    flex: 1;
    min-width: 60px;
  }

  /* 表格适配 */
  .el-table {
    font-size: 12px;
  }

  /* 隐藏部分不关键的列 */
  .el-table-column:nth-child(4), /* 读者手机号 */
  .el-table-column:nth-child(10) { /* 操作人 */
    display: none;
  }

  /* 调整表格列宽 */
  .el-table-column {
    min-width: auto !important;
  }

  /* 分页控件适配 */
  .el-pagination {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    padding: 10px 0;
  }

  .el-pagination__sizes {
    margin-bottom: 10px;
    width: 100%;
    text-align: center;
  }

  /* 弹窗适配 */
  .el-dialog {
    width: 90% !important;
    margin: 10px auto !important;
  }

  .el-dialog .el-form-item {
    margin-bottom: 15px;
  }

  .el-dialog .el-select,
  .el-dialog .el-date-picker {
    width: 100% !important;
  }

  .dialog-footer {
    display: flex;
    justify-content: center;
    gap: 10px;
    width: 100%;
  }

  .dialog-footer .el-button {
    flex: 1;
  }
}

/* 针对更小的屏幕（如手机横屏）进一步优化 */
@media screen and (max-width: 480px) {
  /* 进一步简化表格显示 */
  .el-table-column:nth-child(6), /* 借书时间 */
  .el-table-column:nth-child(7) { /* 应还时间 */
    display: none;
  }

  .el-button {
    padding: 2px 4px;
    font-size: 12px;
  }

  .el-table .el-button--small {
    padding: 4px 8px;
    font-size: 11px;
  }
}
</style>