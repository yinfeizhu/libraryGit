<script setup>
import { ref, onMounted } from 'vue';
import { queryPageApi, deleteBeforeApi } from '@/api/log';
import { ElMessage } from 'element-plus'


const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const day = ref()
const currentPage = (val) => {
  page.value = val;
  getTableData();
}
const currentpageSize = (val) => {
  pageSize.value = val;
  getTableData();
}

//查询日志列表
const getTableData = async () => {
  console.log('查询日志列表',page.value,pageSize.value);
  const res = await queryPageApi(page.value,pageSize.value)
  console.log('日志列表数据',res);
  if (res.code == 200) {
    tableData.value = res.data.rows
    total.value = res.data.total
  }else{
    ElMessage.error(res.msg)
  }
  
}
//对话框
const dialogVisible = ref(false)

//打开弹窗清空输入框
const openDialog = () => {
    dialogVisible.value = true
    day.value = ''
}
//删除day天前的日志
const deleteBeforeDay = async () => {
if (day.value >= 0) {
    const res = await deleteBeforeApi(day.value)
    if (res.code == 200) {
        ElMessage.success('删除成功')
        dialogVisible.value = false
        day.value = ''
        getTableData()
    }else{
        ElMessage.error(res.msg)
    }
}
else{
    ElMessage.error('请输入正确的天数')
}
}
//取消删除提示
const cancelDelete = () => {
    dialogVisible.value = false
    ElMessage.info('已取消删除')
}
//钩子函数
onMounted(() => {
  getTableData();
})

</script>

<template>
  <h1>日志管理</h1>
  <!-- 删除day天前的日志-->
   <div class="container">
   <el-button type="danger" @click="openDialog">根据范围删除日志</el-button> 
   </div>
  <!-- 列表展示 -->
  <div class="table-container">
  <el-table :data="tableData" border style="width: 100%" fit size="medium" >
    <el-table-column prop="operatorName" label="操作人" align="center" width="100px"/>
    <el-table-column prop="operationTime" label="操作时间" align="center" width="200px"/>
    <el-table-column prop="operationTarget" label="操作对象" align="center" width="120px">
        <template #default="scope">
          <span v-if="scope.row.operationTarget==0">其它</span>
          <span v-else-if="scope.row.operationTarget==1">实物图书</span>
          <span v-else-if="scope.row.operationTarget==2">展示图书</span>
          <span v-else-if="scope.row.operationTarget==3">借阅记录</span>
          <span v-else-if="scope.row.operationTarget==4">罚款记录</span>
          <span v-else-if="scope.row.operationTarget==5">用户信息</span>
        </template>
    </el-table-column> 
    <el-table-column prop="operationType" label="操作类型" align="center" width="100px" >
        <template #default="scope">
          <span v-if="scope.row.operationType==0">其它</span>
          <span v-else-if="scope.row.operationType==1">新增</span>
          <span v-else-if="scope.row.operationType==2">删除</span>
          <span v-else-if="scope.row.operationType==3">修改</span>
          <span v-else-if="scope.row.operationType==4">还书</span>
          <span v-else-if="scope.row.operationType==5">借书</span>
        </template>
    </el-table-column> 
    <el-table-column prop="ipAddress" label="IP地址" align="center" width="120px"/>
    <el-table-column prop="methodParams" label="请求参数" align="center" width="280px">
      <template #default="scope">
        <el-popover effect="light" trigger="hover" placement="top" width="auto" popper-style="font-size:12px">
          <template #default>
            <div>参数: {{ scope.row.methodParams }}</div>
          </template>
          <template #reference>
            <el-tag v-if="scope.row.methodParams.length <= 30">{{ scope.row.methodParams}}</el-tag>
            <el-tag v-else>{{ scope.row.methodParams.substring(0,30) + '...' }}</el-tag>
          </template>
        </el-popover>
      </template>
    </el-table-column>
    <el-table-column prop="returnResult"  label="返回值" align="center"></el-table-column>
    <el-table-column prop="costTime"  label="耗时(ms)" align="center" width="100px"/>
    </el-table>
    <br>
  </div>
  <!-- 分页条 -->
  <div class="container">
    <el-pagination
      v-model:current-page="page"
      v-model:page-size="pageSize"
      :page-sizes="[5,10, 20, 30, 50,75,100]"
      :background="true"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="currentpageSize"
      @current-change="currentPage"
    />
   </div>
   <!-- 对话框 -->
   <div class="container"> 
    <el-dialog v-model="dialogVisible" title="删除指定天数前的日志" width="400px" top="15%">
      <el-form :model="day" label-width="80px" ref="scoreForm" >
        <el-form-item label="指定天数" prop="day">
          <el-input v-model="day" placeholder="请输入天数"></el-input>
        </el-form-item>
      </el-form>
      <!-- 底部按钮 -->
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="deleteBeforeDay">确认</el-button>
          <el-button @click="cancelDelete">取消</el-button>
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
</style>