<script setup> 
import { ref, onMounted, nextTick, onUnmounted } from "vue";
import { queryBookTypeCountApi, queryBookStatusCountApi } from "@/api/report.js";
import * as echarts from "echarts";


//前端定义对象存储图书相关数据
const typeName = ref([]);
const typeCount = ref([]);
const statusData = ref([]);
// 图表容器引用
const barChartRef = ref(null);
const pieChartRef = ref(null);
let barChart = null;
let pieChart = null;

//从后台获取图书种类数据
const queryTypeData = async () => { 
  const result = await queryBookTypeCountApi();
  if(result.code == 200 && result.data){
    // 从后台获取图书种类名称和数量
    typeName.value = result.data.nameList;
    typeCount.value = result.data.numList;
    
    // 数据获取完成后，等待DOM更新再初始化图表
    await nextTick();
    initBarChart();
  }
};

//从后台获取图书状态数据
const queryStatusData = async () => { 
  const result = await queryBookStatusCountApi();
  if(result.code == 200 && result.data){
    // 从后台获取图书状态名称和数量
    const nameList = result.data.nameList;
    const numList = result.data.numList;
    
    //修改为饼图需要的数据格式
    statusData.value = nameList.map((name, index) => {
      return {
        name: name,
        value: numList[index]
      };
    });
    // 数据获取完成后，等待DOM更新再初始化图表
    await nextTick();
    initPieChart();
  }
};

// 加载柱状图
const initBarChart = () => {
  if(!barChartRef.value) return;//如果图表容器不存在，直接返回
  
  // 如果图表已存在，先销毁重新创建
  if(barChart) {
    barChart.dispose();
  }
  // ECharts 需要真实的 DOM 元素来渲染图表
  barChart = echarts.init(barChartRef.value);
  // 可以设置为最大值的 1.2 倍，确保柱状图顶部有空间显示标签
  // 动态计算Y轴最大值（处理数据为空的边界情况）
  const maxCount = typeCount.value.length ? Math.max(...typeCount.value) : 0;
  const yAxisMax = maxCount > 0 ? Math.ceil(maxCount * 1.2) : 10;
  const option = {
    backgroundColor: "white",//背景色",
    title: {
      text: '图书种类统计',
      left: 'center',
      textStyle: {
        fontSize: 24
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
      type: 'category',//横坐标
      data: typeName.value,
      axisLabel: {//X轴标签样式
        rotate: window.innerWidth < 768 ? 30 : 0, // 小屏幕X轴标签旋转防重叠
        interval: 0,
        fontSize: window.innerWidth < 768 ? 12 : 14 // 响应式标签大小
      }
    },
    yAxis: {
      type: 'value',//纵坐标
      max: yAxisMax,
      splitLine: {//Y轴网格线
        show: true,//显示网格线
        lineStyle: {
          color: '#eee'//网格线颜色
        }
      },
      axisLabel: {//Y轴标签样式
        fontSize: 14,  // Y轴标签字体大小
        color: '#333' ,  // Y轴标签颜色（可选）
        fontSize: window.innerWidth < 768 ? 12 : 14
      }
    },
    series: [{
      type: 'bar',//柱状图
      data: typeCount.value,
      itemStyle: {
        color: '#409EFF' ,//柱子颜色
      },
      label: {//数据标签
        show: true,           // 显示标签
        position: 'top',      // 标签位置在柱子顶部
        color: '#333',        // 标签文字颜色
        fontSize: 12,         // 标签文字大小
        fontWeight: 'bold',    // 标签文字粗细
        fontSize: window.innerWidth < 768 ? 10 : 12 // 响应式标签大小
      }
    }]
  };
  
  barChart.setOption(option);//设置图表数据
};
//加载饼状图
const initPieChart = () => {
  if(!pieChartRef.value) return;
  
  // 如果图表已存在，先销毁重新创建
  if(pieChart) {
    pieChart.dispose();
  }
  // ECharts 需要真实的 DOM 元素来渲染图表
  pieChart = echarts.init(pieChartRef.value);
  
  
  const option = {
    backgroundColor: "white",
    title: {
      text: '图书状态统计',
      left: 'center',
      textStyle: {
        fontSize: 24
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
      type: 'pie',              // 饼状图类型
      radius: window.innerWidth < 768 ? '50%' : '40%', // 小屏幕增大饼图占比
      center: ['50%', '55%'],   // 饼图中心位置
      data: statusData.value,            // 数据
      itemStyle: {
        borderRadius: 5,        // 扇形圆角
        borderColor: '#fff',    // 扇形边框
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{d}%'   // 显示名称和百分比
      },
      emphasis: {//高亮
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
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
//钩子函数
onMounted(() => { 
  queryTypeData();
  queryStatusData();
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
    <!-- 左侧：图书种类分布 -->
    <div class="stat-item left">
      <!-- 容器柱状图 -->
      <div ref="barChartRef" style="width: 100%; height: 400px;"></div>

    </div>

    <!-- 右侧：图书状态分布 -->
    <div class="stat-item right" >
      <!-- 容器饼状图 -->
      <div ref="pieChartRef" style="width: 100%; height: 400px;"></div>
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
  border-radius: 12px; /* 适当减小圆角 */
  background-color: #f5f5f5;
  box-sizing: border-box;
  min-width: 280px; /* 确保最小宽度，避免过窄 */
  padding: 15px; /* 适当增加内边距，给图例留空间 */
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