<script setup>
import { ref, onMounted } from 'vue';
import { 
  queryAnnouncementApi, 
  addAnnouncementApi, 
  updateAnnouncementApi,
  queryAnnouncementByIdApi, 
  deleteAnnouncementApi 
} from '@/api/announcement';
import { ElMessage, ElMessageBox } from 'element-plus';

// 公告数据
const announcements = ref([]);
// 弹窗状态
const dialogVisible = ref(false);
// 弹窗标题
const dialogTitle = ref('');
// 表单数据
const form = ref({
  subject: '',
  content: ''
});
// 获取公告列表
const getAnnouncements = async () => {
    const res = await queryAnnouncementApi();
    if (res.code == 200) {
        announcements.value = res.data;
    }
};


// 查看详情
const showDescription = (content) => {
  ElMessageBox.alert(
    `<div style="white-space: pre-wrap;">${content || '无内容'}</div>`,
    '公告详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭' 
    }
    
  );
};

// 新增公告
const handleAdd = () => {
  dialogTitle.value = '新增公告';
  form.value = { subject: '', content: '' };
  dialogVisible.value = true;
};

// 编辑公告
const handleEdit = async (id) => {
  dialogTitle.value = '编辑公告';
  dialogVisible.value = true;
  form.value = { subject: '', content: '' };
  const result = await queryAnnouncementByIdApi(id);
  if (result.code == 200) {
    form.value = result.data;
  }
};

// 保存公告
const handleSave = async () => {
  if (!form.value.subject || !form.value.content) {
    ElMessage.warning('请填写标题和内容');
    return;
  }
  if (form.value) {
      // 编辑
    if(dialogTitle.value == '编辑公告') {
      const res = await updateAnnouncementApi(form.value);
      if (res.code == 200) {
        ElMessage.success('更新成功');
        getAnnouncements(); 
      } else {
        ElMessage.error('更新失败');
      }
    }else {
      // 新增
      dialogTitle.value = '新增公告';
      const result = await addAnnouncementApi(form.value);
      if (result.code == 200) {
        ElMessage.success('新增成功');
        getAnnouncements(); 
      } else {
        ElMessage.error('新增失败');
      }
    }
    dialogVisible.value = false;
    
}
};

// 删除公告
const handleDelete = async (id) => {
    await ElMessageBox.confirm('确定要删除这条公告吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    .then(async () => {
      const result = await deleteAnnouncementApi(id);
      if (result.code == 200) {
        ElMessage.success('删除成功');
        getAnnouncements(); 
      } else {
        ElMessage.error('删除失败');
      }
    }) .catch (() => {
      ElMessage.info('已取消删除');
    })
    
};

// 页面加载时获取数据
onMounted(() => {
  getAnnouncements();
});
</script>
<template>
  <h1>公告管理</h1>
<div class="container">
    <!-- 操作按钮 -->
    <div class="container">
      <el-button type="primary" @click="handleAdd">新增公告</el-button>
    </div>
    <!-- 公告表格 -->
    <div class="container table-container">   
    <el-table :data="announcements" border style="width: 100%;">
      <el-table-column type="index" label="序号" width="80" align="center"></el-table-column>
      <el-table-column prop="subject" label="标题" align="center" width="180"></el-table-column>
      <el-table-column prop="adminName" label="操作人" align="center"width="120"></el-table-column>
      <el-table-column prop="content" label="内容" align="center"width="500">
        <template #default="scope">
          <div class="content-cell">
            {{ scope.row.content.length > 30 ? 
              scope.row.content.substring(0, 30) + '...' : 
              scope.row.content }}
            <el-button 
              type="text" 
              size="small" 
              @click="showDescription(scope.row.content)"
            >
              详情
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" align="center"width="200" >
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row.id)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 新增/编辑公告弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" label-width="80px"style="margin-top: 20px;">
        <el-form-item label="标题" required>
          <el-input 
            v-model="form.subject" 
            placeholder="请输入公告标题"
            style="width: 100%;"
          ></el-input>
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input 
            v-model="form.content" 
            type="textarea" 
            rows="6"
            placeholder="请输入公告内容"
            style="width: 100%;"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
  </div>
</template>



<style scoped>
.container {
  margin-top: 20px;
}

.content-cell {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.table-container {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  width: 100%;
  /* 可选：限制容器最大宽度，确保在宽屏上也能触发滚动逻辑 */
  max-width: 100vw; /* 100vw 表示视口宽度 */
}
</style>
