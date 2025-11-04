<template>
  <div class="page-container">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <el-icon><InfoFilled /></el-icon>
          <span>关于我们</span>
        </div>
      </template>
      
      <div class="page-content">
        <h2>剧本杀在线预约平台</h2>
        
        <div class="section">
          <h3>平台简介</h3>
          <p>
            剧本杀在线预约平台是一个专注于为剧本杀爱好者提供便捷预约服务的综合性平台。
            我们致力于连接优质剧本杀门店与玩家，为用户提供丰富的剧本选择和便捷的预约体验。
          </p>
        </div>
        
        <div class="section">
          <h3>我们的优势</h3>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="6" v-for="item in advantages" :key="item.title">
              <div class="advantage-card">
                <el-icon :size="40" :color="item.color">
                  <component :is="item.icon" />
                </el-icon>
                <h4>{{ item.title }}</h4>
                <p>{{ item.desc }}</p>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <div class="section">
          <h3>平台数据</h3>
          <el-row :gutter="20">
            <el-col :xs="12" :sm="6" v-for="stat in stats" :key="stat.label">
              <div class="stat-item">
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-label">{{ stat.label }}</div>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <div class="section">
          <h3>联系方式</h3>
          <div class="contact-info">
            <p><el-icon><Phone /></el-icon> 客服电话：400-123-4567</p>
            <p><el-icon><Message /></el-icon> 客服邮箱：service@jubensha.com</p>
            <p><el-icon><Location /></el-icon> 公司地址：北京市朝阳区xxx街道xxx号</p>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { InfoFilled, Phone, Message, Location } from '@element-plus/icons-vue'

const advantages = ref([
  {
    icon: 'Reading',
    title: '剧本丰富',
    desc: '数千个优质剧本，各种题材应有尽有',
    color: '#409eff'
  },
  {
    icon: 'Shop',
    title: '门店精选',
    desc: '严格筛选优质门店，保证服务质量',
    color: '#67c23a'
  },
  {
    icon: 'Timer',
    title: '便捷预约',
    desc: '在线预约，快速响应，节省时间',
    color: '#e6a23c'
  },
  {
    icon: 'Star',
    title: '积分奖励',
    desc: '完成任务获得积分，兑换优惠券',
    color: '#f56c6c'
  }
])

const stats = ref([
  { value: '10000+', label: '注册用户' },
  { value: '5000+', label: '精选剧本' },
  { value: '500+', label: '合作门店' },
  { value: '50000+', label: '成功预约' }
])
</script>

<style scoped>
.page-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.page-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: bold;
}

.page-content h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #409eff;
}

.section {
  margin-bottom: 40px;
}

.section h3 {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
  color: #333;
}

.section p {
  line-height: 1.8;
  color: #666;
  font-size: 15px;
}

.advantage-card {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
  background: #f9f9f9;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.advantage-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.advantage-card h4 {
  margin: 15px 0 10px;
  color: #333;
}

.advantage-card p {
  font-size: 14px;
  color: #666;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: #fff;
  margin-bottom: 20px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.contact-info {
  background: #f9f9f9;
  padding: 30px;
  border-radius: 8px;
}

.contact-info p {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  font-size: 16px;
}
</style>

