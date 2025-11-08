<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { queryPageApi, queryByIdApi, addApi, updateApi, deleteApi } from '@/api/fine'
import {ElMessage,ElMessageBox} from 'element-plus'
import { queryPageApi as queryUserPageApi } from '@/api/user';
import { queryPageApi as queryBorrowPageApi } from '@/api/borrow';
//罚款对象
const fineRecord =ref ([]);
// 搜索栏数据
const searchBook = reactive({
  readerName: '',
  readerPhone: '',
  bookBarcode: ''
});
//定义分页
const currentPage = ref(1);
//定义每页显示条数
const pageSize = ref(10);
//定义总数
const total = ref(0);
//定义原因
const reasons = ref([
    { label:'逾期', value: 'OVERDUE'
    },
    { label:'损坏', value: 'DAMAGE'
    },
    { label:'丢失', value: 'LOSE'
    }
]);
//定义对话框标题
const dialogTitle = ref('');
const dialogVisible = ref(false);
// 每页条数改变时触发
const handleSizeChange = (val) => {
  pageSize.value = val;
  search();
}
// 分页改变时触发
const handleCurrentChange = (val) => {
  currentPage.value = val;
  search();
}
//定义状态类型
const payStatus = ([
  { label:'已支付', value: 1
  },
  { label:'未支付', value: 2
  },
  { label:'已减免', value: 3
  }
]);
//条码号下拉框数据
const borrows = ref([]);
// 查询所有借阅记录
const queryBorrowRecord = async () => {
  const result = await queryBorrowPageApi(1, 1000, '', '', '');
  if (result.code == 200) {
     borrows.value = result.data.rows
    }else {
      ElMessage.error('图书查询失败');
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
// 查询
const search = async () => {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      readerName: searchBook.readerName,
      readerPhone: searchBook.readerPhone,
      bookBarcode: searchBook.bookBarcode
    };
    console.log('查询参数', params);
    const result = await queryPageApi(
        params.page,
        params.pageSize, 
        params.readerName, 
        params.readerPhone, 
        params.bookBarcode
    );
    if (result.code == 200) {
      fineRecord.value = result.data.rows;
      total.value = result.data.total;
    } else {
      ElMessage({
        type: 'error',
        message: result.msg || '查询失败'
      })
    }
};
// 清空
const onReset = () => {
  searchBook.readerName = '';
  searchBook.readerPhone = '';
  searchBook.bookBarcode = '';
  search();
};
//定义表单引用
const fineRecordFormRef = ref(null);
//表单数据
const fineRecordForm = ref({
  readerId: '',
  borrowId: '',
  reason: '',
  amount: 0,
  status: 2
});
//清空表单数据
const clearForm = () => {
  fineRecordForm.readerId = '';
  fineRecordForm.borrowId = '';
  fineRecordForm.reason = '';
  fineRecordForm.amount = 0;
  fineRecordForm.status = 2;
}
// 新增罚款记录
const addBook = () => {
  dialogTitle.value = '新增罚款记录';
  dialogVisible.value = true;
  // 清空表单数据
  clearForm();
  // 清空表单校验状态
  if (fineRecordFormRef.value) {
    fineRecordFormRef.value.clearValidate();
  }
}
//修改罚款记录
const editBook = async(id) => {
    const result = await queryByIdApi(id);
    console.log('查询结果', result);
    if(result.code == 200 && result.data){
      fineRecordForm.value = result.data;
      dialogTitle.value = '修改罚款记录';
      dialogVisible.value = true;
    } else {
      ElMessage({
        type: 'error',
        message: result.msg || '数据不存在'
      })
    }
    // 清空表单校验状态
    if (fineRecordFormRef.value) {
        fineRecordFormRef.value.clearValidate();
    }
}
//定义校验规则
const rules = reactive({
  readerId: [{ required: true, message: '请输入读者ID', trigger: 'blur' }],
  borrowId: [{ required: true, message: '请输入借阅记录ID', trigger: 'blur' }],
  reason: [{ required: true, message: '请选择罚款原因', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入罚款金额', trigger: 'blur' }],
});
// 保存或修改罚款记录
const save = async () => {
    fineRecordFormRef.value.validate(async (valid) => {
    if (valid) { // 校验成功
      if (dialogTitle.value == '新增罚款记录') {
        const result = await addApi(fineRecordForm.value);
        if (result.code == 200) {
          ElMessage.success('新增成功');
          dialogVisible.value = false;
          search();
        } else {
          ElMessage.error(result.msg);
        }
      } else if (dialogTitle.value == '修改罚款记录') {
        console.log('修改罚款记录', fineRecordForm.value);
        const result = await updateApi(fineRecordForm.value);
        if (result.code == 200) {
          ElMessage.success('修改成功');
          dialogVisible.value = false;
          search();
        } else {
          ElMessage.error(result.msg);
        }
      }
    }else {
      ElMessage.error('请填写完整信息');
    }
    }) 
};
// 选中的ID集合
const selectedIds = ref([]);
// 选中ID集合
const handleSelectionChange = (selections) => {
  selectedIds.value = selections.map(fineRecord => fineRecord.id);
};
// 批量删除
const deleteBatch = () => {
  ElMessageBox.confirm('确定删除选中的罚款记录吗？', '提示', {
    confirmButtonText: '确定',cancelButtonText: '取消',type: 'warning'
  }).then(async() => {
    if (selectedIds.value.length > 0) {
      const result = await deleteApi(selectedIds.value);
      if (result.code == 200) { 
        ElMessage.success('删除成功');
        search();
      } else {
        ElMessage.error(result.msg);
      } 
    } else { //没有选择罚款记录
      ElMessage.warning('请选择要删除的罚款记录');
    }   
  }).catch(() => {
    // 取消删除
    ElMessage.info('已取消删除');
  });
};

//删除单条罚款记录
const deleteById = async (id) => { 
  //确认删除
  ElMessageBox.confirm('确定删除该罚款记录吗？', '提示', 
  {confirmButtonText: '确定',cancelButtonText: '取消',type: 'warning'})
  .then(async () => { 
    // 确定删除
    const result = await deleteApi(id);
    if(result.code == 200) { 
      ElMessage.success('删除成功');
      search();
    }else{
      ElMessage.error(result.msg);
    }
  }).catch(() => {
    // 取消删除
    ElMessage.info('已取消删除');
  });
  
}

//钩子函数
onMounted(() => {
  search();
  // 查询所有读者姓名
  queryReaderName();
  // 查询所有借阅记录
  queryBorrowRecord();
});
</script>
<template>   
  <h1>罚款管理</h1>
  <!-- 搜索栏 -->
  <div class="container">
    <el-form :inline="true" :model="searchBook" class="demo-form-inline">
      <el-form-item label="读者姓名">
        <el-input v-model="searchBook.readerName" placeholder="请输入读者姓名" clearable />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="searchBook.readerPhone" placeholder="请输入手机号" clearable />
      </el-form-item>
      <el-form-item label="条码号">
        <el-input v-model="searchBook.bookBarcode" placeholder="请输入条码号" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button type="info" @click="onReset">清空</el-button>
      </el-form-item>
    </el-form>
  </div>
  <!-- 操作按钮 -->
  <div>
    <el-button type="primary" @click="addBook()">新增罚款</el-button>
    <el-button type="danger" @click="deleteBatch">批量删除</el-button>
  </div>

  <!-- 表格数据 -->
  <div class="table-container">
    <el-table :data="fineRecord" style="width: 100%" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="35" align="center" />
      <el-table-column prop="readerName" label="借阅人" width="100" align="center" />
      <el-table-column prop="readerPhone" label="手机号" width="180" align="center" />
      <el-table-column prop="bookBarcode" label="条码号" width="160" align="center" />
      <el-table-column prop="amount" label="金额" width="100" align="center" />
      <el-table-column prop="reason" label="原因" width="120" align="center" >
        <template #default="scope">
          {{ reasons.find(item => item.value == scope.row.reason)?.label || '未知' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="支付状态" width="150" align="center" >
        <template #default="scope">
          {{ payStatus.find(item => item.value == scope.row.status)?.label || '未知' }}
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="260" align="center" />
     <el-table-column  label="操作"  align="center" >
      <template #default="scope">
        <el-button type="primary" size="medium" @click="editBook(scope.row.id)">编辑</el-button>
        <el-button type="danger" size="medium" @click="deleteById(scope.row.id)">删除</el-button>
      </template>
     </el-table-column>
    </el-table>
  </div>
    <!-- 分页条 -->
  <div class="container">
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
     <!-- 新增/修改罚款对话框 -->
  <div class="container">
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="450px" top="10%">
      <el-form :model="fineRecordForm" label-width="80px" :rules="rules" ref="fineRecordFormRef" >
        <!-- 第一行：姓名 -->
        <el-form-item label="姓名" prop="readerId">
            <el-select v-model="fineRecordForm.readerId" placeholder="请选择读者" style="width: 80%;" filterable>
                <el-option v-for="reader in readers" :key="reader.id" :label="reader.name" :value="reader.id" ></el-option>
            </el-select>
        </el-form-item>
        <!-- 第二行：原因 -->
        <el-form-item label="原因" prop="reason">
            <el-select v-model="fineRecordForm.reason" placeholder="请选择原因" style="width: 80%;">
                <el-option v-for="reason in reasons" :key="reason.value" :label="reason.label" :value="reason.value"></el-option>
            </el-select>
        </el-form-item>
        <!-- 第三行：金额 -->
        <el-form-item label="金额" prop="amount">
            <el-input v-model="fineRecordForm.amount" placeholder="请输入金额" style="width: 80%;"></el-input>
        </el-form-item>
        <!-- 第四行：状态 -->
        <el-form-item label="状态" prop="status">
            <el-select v-model="fineRecordForm.status" placeholder="请选择状态" style="width: 80%;">
                <el-option v-for="item in payStatus" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
        </el-form-item>
        <!-- 第五行：借阅记录 -->
        <el-form-item label="条码号" prop="borrowId">
            <el-select v-model="fineRecordForm.borrowId" placeholder="请选择图书条码号" style="width: 80%;" filterable>
                <el-option v-for="borrow in borrows" :key="borrow.id" :label="borrow.bookBarcode" :value="borrow.id"></el-option>
            </el-select>
        </el-form-item>
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
.container{
    margin-top: 20px;
}
/* 表格容器样式 - 允许横向滚动 */
.table-container {
  margin-top: 20px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  width: 100%;
  /* 限制容器最大宽度，确保在宽屏上也能触发滚动逻辑 */
  max-width: 100vw; /* 100vw 表示视口宽度 */
}
</style>