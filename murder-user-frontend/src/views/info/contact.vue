<template>
  <div class="info-page contact-page">
    <!-- 面包屑导航 -->
    <InfoPageBreadcrumb />
    
    <!-- 头部横幅 -->
    <div class="hero-banner">
      <div class="hero-content">
        <h1 class="hero-title animate-fade-in-up">
          <span class="gradient-text">随时为您服务</span>
        </h1>
        <p class="hero-subtitle animate-fade-in-up delay-1">
          24小时在线客服，快速响应您的每一个需求
        </p>
      </div>
    </div>
    
    <el-card class="info-card animate-fade-in">
      <template #header>
        <div class="card-header">
          <h2><el-icon><Phone /></el-icon> 联系我们</h2>
        </div>
      </template>
      
      <div class="info-content">
        <el-row :gutter="30">
          <!-- 左侧联系方式 -->
          <el-col :span="12">
            <section class="info-section scroll-reveal">
              <h3>联系方式</h3>
              
              <div class="contact-item hover-lift">
                <div class="contact-icon">
                  <el-icon :size="30" color="#409EFF"><Phone /></el-icon>
                </div>
                <div class="contact-detail">
                  <h4>客服热线</h4>
                  <p>400-123-4567</p>
                  <span class="contact-tip">周一至周日 9:00-21:00</span>
                </div>
              </div>

              <div class="contact-item hover-lift">
                <div class="contact-icon">
                  <el-icon :size="30" color="#67C23A"><Message /></el-icon>
                </div>
                <div class="contact-detail">
                  <h4>电子邮箱</h4>
                  <p>contact@jubensha.com</p>
                  <span class="contact-tip">我们会在24小时内回复</span>
                </div>
              </div>

              <div class="contact-item hover-lift">
                <div class="contact-icon">
                  <el-icon :size="30" color="#E6A23C"><ChatDotSquare /></el-icon>
                </div>
                <div class="contact-detail">
                  <h4>在线客服</h4>
                  <p>微信客服：jubensha_service</p>
                  <span class="contact-tip">扫描右侧二维码添加</span>
                </div>
              </div>

              <div class="contact-item hover-lift">
                <div class="contact-icon">
                  <el-icon :size="30" color="#F56C6C"><Location /></el-icon>
                </div>
                <div class="contact-detail">
                  <h4>公司地址</h4>
                  <p>北京市朝阳区xx路xx号xx大厦xx层</p>
                  <span class="contact-tip">欢迎来访（需提前预约）</span>
                </div>
              </div>
            </section>

            <section class="info-section scroll-reveal">
              <h3>业务合作</h3>
              <div class="cooperation-list">
                <el-descriptions :column="1" border>
                  <el-descriptions-item label="门店入驻">
                    <div>
                      <p>邮箱：cooperation@jubensha.com</p>
                      <p>电话：010-12345678</p>
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item label="剧本合作">
                    <div>
                      <p>邮箱：script@jubensha.com</p>
                      <p>电话：010-12345679</p>
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item label="广告投放">
                    <div>
                      <p>邮箱：ads@jubensha.com</p>
                      <p>电话：010-12345680</p>
                    </div>
                  </el-descriptions-item>
                </el-descriptions>
              </div>
            </section>
          </el-col>

          <!-- 右侧反馈表单 -->
          <el-col :span="12">
            <section class="info-section scroll-reveal">
              <h3>在线留言</h3>
              <el-form
                ref="feedbackFormRef"
                :model="feedbackForm"
                :rules="feedbackRules"
                label-width="100px"
                class="feedback-form"
              >
                <el-form-item label="姓名" prop="name">
                  <el-input v-model="feedbackForm.name" placeholder="请输入您的姓名" />
                </el-form-item>

                <el-form-item label="联系电话" prop="phone">
                  <el-input v-model="feedbackForm.phone" placeholder="请输入您的联系电话" />
                </el-form-item>

                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="feedbackForm.email" placeholder="请输入您的邮箱" />
                </el-form-item>

                <el-form-item label="留言类型" prop="type">
                  <el-select v-model="feedbackForm.type" placeholder="请选择留言类型" style="width: 100%">
                    <el-option label="咨询问题" value="inquiry" />
                    <el-option label="投诉建议" value="complaint" />
                    <el-option label="业务合作" value="cooperation" />
                    <el-option label="其他" value="other" />
                  </el-select>
                </el-form-item>

                <el-form-item label="留言内容" prop="content">
                  <el-input
                    v-model="feedbackForm.content"
                    type="textarea"
                    :rows="6"
                    placeholder="请输入您的留言内容"
                    maxlength="500"
                    show-word-limit
                  />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" @click="submitFeedback" :loading="submitting">
                    提交留言
                  </el-button>
                  <el-button @click="resetForm">重置</el-button>
                </el-form-item>
              </el-form>
            </section>

            <section class="info-section scroll-reveal">
              <h3>关注我们</h3>
              <div class="social-media">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <div class="qrcode-item">
                      <div class="qrcode-placeholder">
                        <el-icon :size="60"><Iphone /></el-icon>
                      </div>
                      <p>微信公众号</p>
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="qrcode-item">
                      <div class="qrcode-placeholder">
                        <el-icon :size="60"><User /></el-icon>
                      </div>
                      <p>客服微信</p>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </section>
          </el-col>
        </el-row>
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
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Phone,
  Message,
  ChatDotSquare,
  Location,
  Iphone,
  User
} from '@element-plus/icons-vue'
import InfoPageBreadcrumb from '@/components/InfoPageBreadcrumb.vue'
import ShareButtons from '@/components/ShareButtons.vue'
import BackToTop from '@/components/BackToTop.vue'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'
import { useScrollReveal } from '@/composables/useScrollReveal'
import { useTheme } from '@/composables/useTheme'

// 初始化
useScrollReveal()
const { loadTheme } = useTheme()
loadTheme()

// 反馈表单
const feedbackFormRef = ref(null)
const submitting = ref(false)
const feedbackForm = reactive({
  name: '',
  phone: '',
  email: '',
  type: '',
  content: ''
})

const feedbackRules = {
  name: [
    { required: true, message: '请输入您的姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入您的联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入您的邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择留言类型', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入留言内容', trigger: 'blur' },
    { min: 10, message: '留言内容至少10个字符', trigger: 'blur' }
  ]
}

// 提交反馈
const submitFeedback = async () => {
  if (!feedbackFormRef.value) return
  
  await feedbackFormRef.value.validate((valid) => {
    if (valid) {
      submitting.value = true
      // 模拟提交
      setTimeout(() => {
        submitting.value = false
        ElMessage.success('感谢您的留言，我们会尽快与您联系！')
        resetForm()
      }, 1000)
    }
  })
}

// 重置表单
const resetForm = () => {
  if (!feedbackFormRef.value) return
  feedbackFormRef.value.resetFields()
}
</script>

<style scoped>
@import '@/styles/info-enhanced.css';
@import '@/styles/info-theme.css';

/* 联系方式卡片特殊样式 */
.contact-methods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 25px;
  margin: 30px 0;
}

.contact-method-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 35px;
  border-radius: 16px;
  text-align: center;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.contact-method-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s;
}

.contact-method-card:hover::before {
  opacity: 1;
}

.contact-method-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 35px rgba(102, 126, 234, 0.4);
}

.contact-icon-large {
  font-size: 60px;
  margin-bottom: 15px;
}

.contact-title {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 10px;
}

.contact-detail {
  font-size: 16px;
  opacity: 0.9;
}

/* 地图容器 */
.map-container {
  margin-top: 30px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  height: 400px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

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
  font-size: 18px;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409EFF;
}

.contact-item {
  display: flex;
  align-items: flex-start;
  padding: 20px;
  margin-bottom: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.contact-item:hover {
  background: #ecf5ff;
  transform: translateX(5px);
}

.contact-icon {
  margin-right: 20px;
  padding: 10px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.contact-detail h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}

.contact-detail p {
  margin: 5px 0;
  font-size: 15px;
  color: #409EFF;
  font-weight: bold;
}

.contact-tip {
  font-size: 13px;
  color: #909399;
}

.cooperation-list {
  margin-top: 15px;
}

.cooperation-list p {
  margin: 5px 0;
  font-size: 14px;
}

.feedback-form {
  margin-top: 20px;
}

.social-media {
  margin-top: 20px;
}

.qrcode-item {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.qrcode-placeholder {
  width: 150px;
  height: 150px;
  margin: 0 auto 15px;
  background: white;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.qrcode-item p {
  margin: 0;
  font-size: 14px;
  color: #606266;
  font-weight: bold;
}
</style>
