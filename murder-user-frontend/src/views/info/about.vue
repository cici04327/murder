<template>
  <div class="info-page">
    <!-- 面包屑导航 -->
    <InfoPageBreadcrumb />
    
    <el-card class="info-card animate-fade-in">
      <template #header>
        <div class="card-header">
          <h2><el-icon><InfoFilled /></el-icon> 关于我们</h2>
        </div>
      </template>
      
      <div class="info-content">
        <section class="info-section scroll-reveal">
          <h3>公司简介</h3>
          <p>
            剧本杀在线预约平台是一家专注于为剧本杀爱好者提供便捷预约服务的互联网平台。
            我们致力于连接优质的剧本杀门店和广大玩家，让每一次剧本杀体验都更加便捷、愉快。
          </p>
        </section>

        <section class="info-section scroll-reveal">
          <h3>我们的使命</h3>
          <p>
            让剧本杀文化走进千家万户，为玩家提供最优质的剧本推荐和门店选择，
            为门店提供最高效的运营管理工具，推动整个剧本杀行业的健康发展。
          </p>
        </section>

        <section class="info-section scroll-reveal">
          <h3>核心优势</h3>
          <div class="advantage-list">
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="advantage-item hover-lift hover-shine">
                  <el-icon :size="40" color="#409EFF"><Checked /></el-icon>
                  <h4>精选剧本</h4>
                  <p>汇聚全国优质剧本资源，专业评分系统，帮你找到最适合的剧本</p>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="advantage-item hover-lift hover-shine">
                  <el-icon :size="40" color="#67C23A"><Shop /></el-icon>
                  <h4>优质门店</h4>
                  <p>严格筛选合作门店，确保环境舒适、服务优质、体验出色</p>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="advantage-item hover-lift hover-shine">
                  <el-icon :size="40" color="#E6A23C"><Clock /></el-icon>
                  <h4>便捷预约</h4>
                  <p>在线实时预约，随时查看档期，一键完成预约流程</p>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="advantage-item hover-lift hover-shine">
                  <el-icon :size="40" color="#F56C6C"><Discount /></el-icon>
                  <h4>优惠活动</h4>
                  <p>定期推出各类优惠券和活动，让你以更优惠的价格享受精彩体验</p>
                </div>
              </el-col>
            </el-row>
          </div>
        </section>

        <section class="info-section scroll-reveal">
          <h3>发展历程</h3>
          <el-timeline>
            <el-timeline-item timestamp="2024年" placement="top">
              <el-card>
                <h4>平台上线</h4>
                <p>正式推出剧本杀在线预约服务，服务覆盖全国主要城市</p>
              </el-card>
            </el-timeline-item>
            <el-timeline-item timestamp="2023年" placement="top">
              <el-card>
                <h4>技术升级</h4>
                <p>完成平台技术架构升级，引入智能推荐系统</p>
              </el-card>
            </el-timeline-item>
            <el-timeline-item timestamp="2022年" placement="top">
              <el-card>
                <h4>快速发展</h4>
                <p>合作门店突破1000家，累计服务用户超过10万人次</p>
              </el-card>
            </el-timeline-item>
            <el-timeline-item timestamp="2021年" placement="top">
              <el-card>
                <h4>项目启动</h4>
                <p>公司成立，开始研发剧本杀在线预约系统</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </section>

        <section class="info-section scroll-reveal">
          <h3>联系方式</h3>
          <div class="contact-info">
            <p><el-icon><Location /></el-icon> 地址：北京市朝阳区xx路xx号xx大厦xx层</p>
            <p><el-icon><Phone /></el-icon> 电话：400-123-4567</p>
            <p><el-icon><Message /></el-icon> 邮箱：contact@jubensha.com</p>
            <p><el-icon><Clock /></el-icon> 工作时间：周一至周日 9:00-21:00</p>
          </div>
        </section>
      </div>
    </el-card>
    
    <!-- 分享按钮 -->
    <ShareButtons />
    
    <!-- 返回顶部 -->
    <BackToTop />
    
    <!-- 主题切换器 -->
    <ThemeSwitcher />
  </div>
</template>

<script setup>
import { 
  InfoFilled, 
  Checked, 
  Shop, 
  Clock, 
  Discount,
  Location,
  Phone,
  Message
} from '@element-plus/icons-vue'
import InfoPageBreadcrumb from '@/components/InfoPageBreadcrumb.vue'
import ShareButtons from '@/components/ShareButtons.vue'
import BackToTop from '@/components/BackToTop.vue'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'
import { useScrollReveal } from '@/composables/useScrollReveal'
import { useTheme } from '@/composables/useTheme'

// 初始化滚动显示动画
useScrollReveal()

// 初始化主题
const { initTheme } = useTheme()
initTheme()
</script>

<style scoped>
@import '@/styles/info-theme.css';

.info-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.info-card {
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.info-card:hover {
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  color: #303133;
}

.info-content {
  line-height: 1.8;
  color: #606266;
}

.info-section {
  margin-bottom: 40px;
}

.info-section:last-child {
  margin-bottom: 0;
}

.info-section h3 {
  font-size: 20px;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409EFF;
  position: relative;
  display: inline-block;
}

.info-section h3::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 30%;
  height: 2px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.info-section p {
  margin: 15px 0;
  font-size: 15px;
  text-indent: 2em;
}

.advantage-list {
  margin-top: 20px;
}

.advantage-item {
  text-align: center;
  padding: 30px 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-radius: 12px;
  margin-bottom: 20px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #e4e7ed;
  position: relative;
  overflow: hidden;
}

.advantage-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transform: scaleX(0);
  transition: transform 0.4s;
}

.advantage-item:hover::before {
  transform: scaleX(1);
}

.advantage-item:hover {
  background: linear-gradient(135deg, #ecf5ff 0%, #ffffff 100%);
  transform: translateY(-8px);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.2);
  border-color: #409EFF;
}

.advantage-item h4 {
  margin: 15px 0 10px;
  font-size: 18px;
  color: #303133;
}

.advantage-item p {
  margin: 0;
  font-size: 14px;
  color: #606266;
  text-indent: 0;
}

.contact-info p {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 15px 0;
  font-size: 15px;
  text-indent: 0;
}

.el-timeline {
  padding-left: 20px;
}
</style>
