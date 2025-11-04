<template>
  <div class="page-container">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <el-icon><ChatDotSquare /></el-icon>
          <span>联系我们</span>
        </div>
      </template>
      
      <div class="page-content">
        <el-row :gutter="30">
          <!-- 左侧：联系表单 -->
          <el-col :xs="24" :md="12">
            <div class="form-section">
              <h3>在线留言</h3>
              <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
                <el-form-item label="姓名" prop="name">
                  <el-input v-model="form.name" placeholder="请输入您的姓名" />
                </el-form-item>
                
                <el-form-item label="联系方式" prop="contact">
                  <el-input v-model="form.contact" placeholder="手机号或邮箱" />
                </el-form-item>
                
                <el-form-item label="主题" prop="subject">
                  <el-select v-model="form.subject" placeholder="请选择主题" style="width: 100%">
                    <el-option label="平台使用问题" value="platform" />
                    <el-option label="预约相关问题" value="booking" />
                    <el-option label="账号相关问题" value="account" />
                    <el-option label="建议与反馈" value="feedback" />
                    <el-option label="商务合作" value="business" />
                    <el-option label="其他问题" value="other" />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="留言内容" prop="message">
                  <el-input
                    v-model="form.message"
                    type="textarea"
                    :rows="6"
                    placeholder="请详细描述您的问题或建议"
                  />
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" @click="handleSubmit" :loading="submitting">
                    提交留言
                  </el-button>
                  <el-button @click="handleReset">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-col>
          
          <!-- 右侧：联系方式 -->
          <el-col :xs="24" :md="12">
            <div class="contact-section">
              <h3>联系方式</h3>
              
              <div class="contact-item">
                <el-icon :size="24" color="#409eff"><Phone /></el-icon>
                <div>
                  <h4>客服电话</h4>
                  <p>400-123-4567</p>
                  <p class="tip">工作时间：9:00-21:00</p>
                </div>
              </div>
              
              <div class="contact-item">
                <el-icon :size="24" color="#67c23a"><Message /></el-icon>
                <div>
                  <h4>客服邮箱</h4>
                  <p>service@jubensha.com</p>
                  <p class="tip">我们会在24小时内回复</p>
                </div>
              </div>
              
              <div class="contact-item">
                <el-icon :size="24" color="#e6a23c"><ChatLineSquare /></el-icon>
                <div>
                  <h4>在线客服</h4>
                  <p>点击右下角AI客服图标</p>
                  <p class="tip">7x24小时在线服务</p>
                </div>
              </div>
              
              <div class="contact-item">
                <el-icon :size="24" color="#f56c6c"><Location /></el-icon>
                <div>
                  <h4>公司地址</h4>
                  <p>北京市朝阳区xxx街道xxx号</p>
                  <p class="tip">欢迎莅临指导</p>
                </div>
              </div>
              
              <div class="social-media">
                <h4>关注我们</h4>
                <div class="social-icons">
                  <el-tag>微信公众号：剧本杀预约</el-tag>
                  <el-tag type="success">微博：@剧本杀在线预约</el-tag>
                  <el-tag type="warning">抖音：剧本杀预约平台</el-tag>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotSquare, Phone, Message, ChatLineSquare, Location } from '@element-plus/icons-vue'

const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  name: '',
  contact: '',
  subject: '',
  message: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  contact: [{ required: true, message: '请输入联系方式', trigger: 'blur' }],
  subject: [{ required: true, message: '请选择主题', trigger: 'change' }],
  message: [{ required: true, message: '请输入留言内容', trigger: 'blur' }]
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  
  // 模拟提交
  setTimeout(() => {
    submitting.value = false
    ElMessage.success('留言提交成功！我们会尽快与您联系')
    handleReset()
  }, 1000)
}

const handleReset = () => {
  formRef.value.resetFields()
}
</script>

<style scoped>
.page-container {
  max-width: 1200px;
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

.page-content {
  padding: 20px;
}

.form-section h3,
.contact-section h3 {
  margin-bottom: 25px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
  color: #333;
}

.contact-item {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
}

.contact-item h4 {
  margin: 0 0 8px;
  color: #333;
}

.contact-item p {
  margin: 5px 0;
  color: #666;
}

.contact-item .tip {
  font-size: 13px;
  color: #999;
}

.social-media {
  margin-top: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: #fff;
}

.social-media h4 {
  margin: 0 0 15px;
}

.social-icons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>

