<script setup>
import { ref, watch, onMounted } from 'vue';
import { queryPageApi, addApi, queryByIdApi, updateApi, deleteApi } from '@/api/bookAdmin';
import { ElMessage, ElMessageBox } from 'element-plus';

// 搜索表单对象
const searchForm = ref({
  bookTitle: '',
  barcode: '',
  status: ''
});

// 图书列表
const bookList = ref([]);
// 分页相关数据
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 状态选项
const statusOptions = ref([
  { label: '在馆', value: 1 },
  { label: '借出', value: 2 },
  { label: '遗失', value: 3 },
  { label: '损坏', value: 4 }
]);

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
    bookTitle: searchForm.value.bookTitle,
    barcode: searchForm.value.barcode,
    status: searchForm.value.status
  };
  
  console.log('查询参数', params);
  const result = await queryPageApi(params.page, params.pageSize, params.bookTitle, params.barcode, params.status);
  
  if (result.code == 200) {
    bookList.value = result.data.rows;
    total.value = result.data.total;
  }
}

// 清空查询条件
const onReset = () => {
  searchForm.value = { bookTitle: '', barcode: '', status: '' };
  search();
}

// 日期格式化函数
const dateFormat = (row, column, cellValue, index) => {
  if (!cellValue) return '';
  
  // 处理出版日期
  if (column.property === 'publish_date') {
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
      return cellValue.replace('T', ' ').substring(0, 16);
    }
    return cellValue;
  }
  
  return cellValue;
};

// 状态格式化
const statusFormat = (row, column, cellValue) => {
  const statusMap = {
    1: '可借',
    2: '借出', 
    3: '遗失',
    4: '损坏'
  };
  return statusMap[cellValue] || '未知状态';
}

// 记录勾选的图书id
const selectedIds = ref([]);
// 复选框发生变化时触发
const handleSelectionChange = (selections) => {
  selectedIds.value = selections.map(book => book.id);
}

// 新增图书
const addBook = () => {
  dialogVisible.value = true;
  dialogTitle.value = '新增实物书籍';
  bookForm.value = {
    bookCode: '',
    barcode: '',
    status: 1,
    supplier: '',
    author: '',
    publishDate: '',
    price: 0
  };

  // 清空表单校验
  if (formRef.value) {
    formRef.value.resetFields();
  }
}

// 表单数据
const bookForm = ref({
  bookCode: '',
  barcode: '',
  status: 1,
  supplier: '',
  author: '',
  publishDate: '',
  price: 0
})

// 控制弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('');

// 保存图书
const save = async () => {
  if (!formRef.value) return;
  
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let result;
        if (dialogTitle.value === '新增实物书籍') {
          result = await addApi(bookForm.value);
        } else {
          result = await updateApi(bookForm.value);
        }
        
        if (result.code == 200) {
          ElMessage.success(dialogTitle.value === '新增实物书籍' ? '新增成功' : '修改成功');
          dialogVisible.value = false;
          search();
        } else {
          ElMessage.error(result.msg);
        }
      } catch (error) {
        ElMessage.error('操作失败');
      }
    } else {
      ElMessage.error('请填写正确的信息');
    }
  });
}

// 表单引用
const formRef = ref(null);

// 表单校验规则
const rules = ref({
  bookCode: [
    { required: true, message: '请输入书籍编码', trigger: 'blur' }
  ],
  barcode: [
    { required: true, message: '请输入条码号', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  price: [
    { required: false, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格必须大于0', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入作者', trigger: 'blur' }
  ]
});

// 修改图书
const editBook = async (id) => {
  const result = await queryByIdApi(id);
  if (result.code == 200) {
    dialogVisible.value = true;
    dialogTitle.value = '修改实物书籍';
    bookForm.value = result.data;
  } else {
    ElMessage.error(result.msg);
  }
}

// 删除图书
const deleteById = async (id) => {
  ElMessageBox.confirm('确定删除该实物书籍吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const result = await deleteApi(id);
    if (result.code == 200) {
      ElMessage.success('删除成功');
      search();
    } else {
      ElMessage.error(result.msg);
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
}

// 批量删除图书
const deleteBatch = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的记录');
    return;
  }

  ElMessageBox.confirm('确定删除选中的实物书籍吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const result = await deleteApi(selectedIds.value);
    if (result.code == 200) {
      ElMessage.success('删除成功');
      search();
    } else {
      ElMessage.error(result.msg);
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
}

// 页面加载时查询数据
onMounted(() => {
  search();
})
</script>

<template>
  <h1>实物书籍管理</h1>
  
  <!-- 搜索栏 -->
  <div class="container">
    <el-form :inline="true" :model="searchForm" class="demo-form-inline">
      <el-form-item label="书名">
        <el-input v-model="searchForm.bookTitle" placeholder="请输入书名" clearable />
      </el-form-item>
      <el-form-item label="条码号">
        <el-input v-model="searchForm.barcode" placeholder="请输入条码号" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
          <el-option 
            v-for="status in statusOptions" 
            :key="status.value" 
            :label="status.label" 
            :value="status.value" 
          />
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
    <el-button type="primary" @click="addBook">新增书籍</el-button>
    <el-button type="danger" @click="deleteBatch">批量删除</el-button>
  </div>

  <!-- 数据表格 -->
  <div class="container table-container">
    <el-table 
      :data="bookList" 
      style="width: 100%;"  border 
      @selection-change="handleSelectionChange"
      
    >
      <el-table-column type="selection" width="30" align="center" />
      <el-table-column prop="bookCode" label="编码" width="100" align="center" />
      <el-table-column prop="bookTitle" label="书名" width="130" align="center" />
      <el-table-column prop="price" label="价格" width="80" align="center">
        <template #default="scope">
          ¥{{ scope.row.price }}
        </template>
      </el-table-column>
      <el-table-column prop="barcode" label="条码号" width="120" align="center" />
      <el-table-column prop="status" label="状态" width="80" align="center" :formatter="statusFormat" />
      <el-table-column prop="bookLocation" label="位置" width="160" align="center" />
      <el-table-column prop="publishDate" label="出版日期" width="110" align="center" :formatter="dateFormat" />
      <el-table-column prop="supplier" label="供应商" width="160" align="center" />
      <el-table-column prop="operatorName" label="最后操作人" width="100" align="center" />
      <el-table-column prop="updateTime" label="更新时间" width="180" align="center" :formatter="dateFormat" />
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

  <!-- 新增/修改对话框 -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="750px" top="12%">
    <el-form :model="bookForm" :rules="rules" ref="formRef" label-width="80px">
      <!-- 第一行：编码、条码号 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="编码" prop="bookCode">
            <el-input v-model="bookForm.bookCode" placeholder="请输入书籍编码" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="条码号" prop="barcode">
            <el-input v-model="bookForm.barcode" placeholder="请输入条码号" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 第二行：作者、状态 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="作者" prop="author">
            <el-input v-model="bookForm.author" placeholder="请输入作者" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="bookForm.status" placeholder="请选择状态" style="width: 100%">
              <el-option 
                v-for="status in statusOptions" 
                :key="status.value" 
                :label="status.label" 
                :value="status.value" 
              />
            </el-select>
          </el-form-item>
        </el-col>
        
      </el-row>

      <!-- 第三行：供应商、出版日期 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="供应商">
            <el-input v-model="bookForm.supplier" placeholder="请输入供应商" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="出版日期">
            <el-date-picker 
              v-model="bookForm.publishDate" 
              type="date" 
              style="width: 100%" 
              placeholder="选择出版日期" 
              format="YYYY-MM-DD" 
              value-format="YYYY-MM-DD" 
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 价格 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="价格" prop="price">
            <el-input 
              v-model.number="bookForm.price" 
              type="number" 
              placeholder="请输入价格" 
              min="0" 
              step="0.01" 
            >
              <template #prepend>¥</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped>
.container {
  margin-top: 20px;
}
.table-container {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  width: 100%;
  /* 限制容器最大宽度，确保在宽屏上也能触发滚动逻辑 */
  max-width: 100vw; /* 100vw 表示视口宽度 */
}
</style>