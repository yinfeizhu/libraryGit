<script setup> 
import { ref, onMounted, nextTick, onUnmounted } from "vue";
import { queryReaderTypeCountApi, queryUserGenderCountApi } from "@/api/report.js";
import * as echarts from "echarts";


// 数据和图表引用定义
const typeName = ref([]);
const typeCount = ref([]);
const genderData = ref([]);
const barChartRef = ref(null);
const pieChartRef = ref(null);
let barChart = null;
let pieChart = null;

// 数据获取方法
const queryReaderTypeData = async () => { 
  const result = await queryReaderTypeCountApi();
  if(result.code == 200 && result.data){
    typeName.value = result.data.nameList;
    typeCount.value = result.data.numList;
    await nextTick();
    initBarChart();
  }
};

const queryGenderData = async () => { 
  const result = await queryUserGenderCountApi();
  if(result.code == 200 && result.data){
    const nameList = result.data.nameList;
    const numList = result.data.numList;
    genderData.value = nameList.map((name, index) => ({
      name: name,
      value: numList[index]
    }));
    await nextTick();
    initPieChart();
  }
};

// 图表初始化方法（增加响应式配置）
const initBarChart = () => {
  if(!barChartRef.value) return;
  if(barChart) barChart.dispose();
  
  barChart = echarts.init(barChartRef.value);
  // 动态计算Y轴最大值（处理数据为空的边界情况）
  const maxCount = typeCount.value.length ? Math.max(...typeCount.value) : 0;
  const yAxisMax = maxCount > 0 ? Math.ceil(maxCount * 1.2) : 10;
  
  const option = {
    backgroundColor: "white",
    title: {
      text: '读者类型统计',
      left: 'center',
      textStyle: {
        fontSize: window.innerWidth < 768 ? 18 : 24 // 响应式标题大小
      }
    },
    // 增加网格边距，避免小屏幕内容溢出
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: typeName.value,
      axisLabel: {
        rotate: window.innerWidth < 768 ? 30 : 0, // 小屏幕X轴标签旋转防重叠
        interval: 0,
        fontSize: window.innerWidth < 768 ? 12 : 14 // 响应式标签大小
      }
    },
    yAxis: {
      type: 'value',
      max: yAxisMax,
      splitLine: { show: true, lineStyle: { color: '#eee' } },
      axisLabel: {
        fontSize: window.innerWidth < 768 ? 12 : 14
      }
    },
    series: [{
      type: 'bar',
      data: typeCount.value,
      itemStyle: { color: '#409EFF' },
      label: {
        show: true,
        position: 'top',
        color: '#333',
        fontSize: window.innerWidth < 768 ? 10 : 12 // 响应式标签大小
      }
    }]
  };
  
  barChart.setOption(option);
};

const initPieChart = () => {
  if(!pieChartRef.value) return;
  if(pieChart) pieChart.dispose();
  
  pieChart = echarts.init(pieChartRef.value);
  
  const option = {
    backgroundColor: "white",
    title: {
      text: '用户性别分布',
      left: 'center',
      textStyle: {
        fontSize: window.innerWidth < 768 ? 18 : 24
      }
    },
    // 新增图例配置
    legend: {
      orient: 'horizontal', // 横向排列（适合底部/顶部）
      bottom: 20, // 放在底部，距离底部20px
      textStyle: {
        fontSize: window.innerWidth < 768 ? 12 : 14, // 响应式文字大小
        overflow: 'none' // 禁止文字截断（默认可能自动截断）
      }
    },
    series: [{
      type: 'pie',
      radius: window.innerWidth < 768 ? '45%' : '35%', // 小屏幕增大饼图占比
      center: ['50%', '55%'], // 垂直居中微调
      data: genderData.value,
      itemStyle: {
        borderRadius: 5,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{d}%',
        fontSize: window.innerWidth < 768 ? 12 : 14
      }
    }]
  };
  
  pieChart.setOption(option);
};

// 窗口大小变化时重绘图表
const handleResize = () => {
  if (barChart) barChart.resize();
  if (pieChart) pieChart.resize();
};

// 生命周期钩子
onMounted(() => { 
  queryReaderTypeData();
  queryGenderData();
  // 监听窗口 resize 事件
  window.addEventListener('resize', handleResize);
});

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
});
</script>

<template>
  <div class="report-container">
    <!-- 左侧：读者类型分布 -->
    <div class="stat-item">
      <div ref="barChartRef" class="chart-container"></div>
    </div>

    <!-- 右侧：用户性别分布 -->
    <div class="stat-item">
      <div ref="pieChartRef" class="chart-container"></div>
    </div>
  </div>
</template>

<style scoped>
.report-container {
  display: flex;
  flex-direction: row; /* 默认横向排列 */
  padding: 10px; /* 减小默认内边距 */
  gap: 15px; /* 减小间距 */
  box-sizing: border-box; /* 确保padding不影响整体宽度 */
}

.stat-item {
  flex: 1;
  min-width: 0; /* 移除最小宽度限制，允许自适应 */
  border-radius: 12px; /* 适当减小圆角 */
  background-color: #f5f5f5;
  padding: 10px;
  box-sizing: border-box;
}

.chart-container {
  width: 100%;
  height: 400px; /* 默认高度 */
}

/* 移动端适配 (屏幕宽度 < 768px) */
@media (max-width: 768px) {
  .report-container {
    flex-direction: column; /* 改为纵向排列 */
    gap: 20px;
    padding: 10px 5px;
  }

  .stat-item {
    min-width: 280px; /* 移动端最小宽度 */
  }

  .chart-container {
    height: 300px; /* 减小图表高度 */
  }
}

/* 平板适配 (768px ~ 1024px) */
@media (min-width: 769px) and (max-width: 1024px) {
  .chart-container {
    height: 350px;
  }
}
</style>