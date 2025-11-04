<template>
  <div class="info-page">
    <!-- 面包屑导航 -->
    <InfoPageBreadcrumb />
    
    <el-card class="info-card animate-fade-in">
      <template #header>
        <div class="card-header">
          <h2><el-icon><QuestionFilled /></el-icon> 帮助中心</h2>
        </div>
      </template>
      
      <div class="info-content">
        <el-row :gutter="30">
          <!-- 左侧目录 -->
          <el-col :span="6" class="scroll-reveal">
            <el-menu
              :default-active="activeCategory"
              @select="handleCategorySelect"
              class="help-menu"
            >
              <el-menu-item index="getting-started">
                <el-icon><Guide /></el-icon>
                <span>新手入门</span>
              </el-menu-item>
              <el-menu-item index="reservation">
                <el-icon><Tickets /></el-icon>
                <span>预约相关</span>
              </el-menu-item>
              <el-menu-item index="payment">
                <el-icon><Wallet /></el-icon>
                <span>支付相关</span>
              </el-menu-item>
              <el-menu-item index="coupon">
                <el-icon><Discount /></el-icon>
                <span>优惠券使用</span>
              </el-menu-item>
              <el-menu-item index="account">
                <el-icon><User /></el-icon>
                <span>账号管理</span>
              </el-menu-item>
            </el-menu>
          </el-col>

          <!-- 右侧内容 -->
          <el-col :span="18" class="scroll-reveal">
            <div class="help-content">
              <!-- 搜索框 -->
              <el-input
                v-model="searchKeyword"
                placeholder="搜索问题关键词"
                :prefix-icon="Search"
                clearable
                size="large"
                class="search-input"
              />

              <!-- 问题列表 -->
              <div v-if="currentQuestions.length > 0" class="questions-list">
                <el-collapse v-model="activeQuestions" accordion>
                  <el-collapse-item
                    v-for="question in currentQuestions"
                    :key="question.id"
                    :name="question.id"
                  >
                    <template #title>
                      <div class="question-title">
                        <el-icon><ChatLineRound /></el-icon>
                        <span>{{ question.question }}</span>
                      </div>
                    </template>
                    <div class="question-answer" v-html="question.answer"></div>
                  </el-collapse-item>
                </el-collapse>
              </div>

              <!-- 联系客服 -->
              <div class="help-footer">
                <el-alert
                  title="没有找到您想要的答案？"
                  type="info"
                  :closable="false"
                >
                  <template #default>
                    <p>您可以通过以下方式联系我们：</p>
                    <div class="contact-methods">
                      <el-button type="primary" size="small" @click="goToContact">
                        <el-icon><Service /></el-icon>
                        在线客服
                      </el-button>
                      <el-button type="success" size="small">
                        <el-icon><Phone /></el-icon>
                        400-123-4567
                      </el-button>
                    </div>
                  </template>
                </el-alert>
              </div>
            </div>
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  QuestionFilled,
  Guide,
  Tickets,
  Wallet,
  Discount,
  User,
  Search,
  ChatLineRound,
  Service,
  Phone
} from '@element-plus/icons-vue'
import InfoPageBreadcrumb from '@/components/InfoPageBreadcrumb.vue'
import ShareButtons from '@/components/ShareButtons.vue'
import BackToTop from '@/components/BackToTop.vue'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'
import { useScrollReveal } from '@/composables/useScrollReveal'
import { useTheme } from '@/composables/useTheme'

// 初始化
useScrollReveal()
const { initTheme } = useTheme()
initTheme()

const router = useRouter()

const activeCategory = ref('getting-started')
const activeQuestions = ref([])
const searchKeyword = ref('')

const questionsData = {
  'getting-started': [
    {
      id: 'gs-1',
      question: '如何注册账号？',
      answer: '<p>注册非常简单，只需3步：</p><ol><li>点击页面右上角的"注册"按钮</li><li>填写手机号、密码等基本信息</li><li>点击"获取验证码"，输入收到的验证码后提交即可</li></ol>'
    },
    {
      id: 'gs-2',
      question: '如何搜索剧本和门店？',
      answer: '<p>我们提供了多种搜索方式：</p><ul><li>在首页可以浏览推荐的热门剧本和优质门店</li><li>点击"剧本大厅"或"门店列表"进行筛选查看</li><li>使用搜索框输入关键词进行搜索</li></ul>'
    }
  ],
  'reservation': [
    {
      id: 'res-1',
      question: '如何预约剧本杀？',
      answer: '<p>预约流程：</p><ol><li>选择您喜欢的剧本或门店</li><li>查看可预约时间</li><li>选择日期和时间段</li><li>填写预约信息（人数、联系方式等）</li><li>提交预约并完成支付</li></ol>'
    },
    {
      id: 'res-2',
      question: '可以取消预约吗？',
      answer: '<p>可以取消预约。在预约时间前24小时取消，可全额退款；24小时内取消，将扣除30%手续费。具体以各门店规定为准。</p>'
    }
  ],
  'payment': [
    {
      id: 'pay-1',
      question: '支持哪些支付方式？',
      answer: '<p>我们支持以下支付方式：</p><ul><li>支付宝</li><li>微信支付</li><li>银行卡支付</li><li>优惠券抵扣</li></ul>'
    },
    {
      id: 'pay-2',
      question: '支付失败怎么办？',
      answer: '<p>如遇支付失败，请检查：</p><ol><li>账户余额是否充足</li><li>网络连接是否正常</li><li>支付密码是否正确</li></ol><p>如问题仍未解决，请联系客服。</p>'
    }
  ],
  'coupon': [
    {
      id: 'coup-1',
      question: '如何获得优惠券？',
      answer: '<p>获取优惠券的方式：</p><ul><li>新用户注册赠送</li><li>参与平台活动</li><li>完成预约后获得</li><li>推荐好友获得</li></ul>'
    },
    {
      id: 'coup-2',
      question: '优惠券如何使用？',
      answer: '<p>使用步骤：</p><ol><li>在支付页面选择可用优惠券</li><li>系统会自动抵扣相应金额</li><li>完成剩余金额支付即可</li></ol>'
    }
  ],
  'account': [
    {
      id: 'acc-1',
      question: '如何修改个人信息？',
      answer: '<p>登录后进入"个人中心"，点击"编辑资料"即可修改昵称、头像、性别等信息。</p>'
    },
    {
      id: 'acc-2',
      question: '忘记密码怎么办？',
      answer: '<p>在登录页面点击"忘记密码"，通过手机验证码重置密码。</p>'
    }
  ]
}

const currentQuestions = computed(() => {
  return questionsData[activeCategory.value] || []
})

const handleCategorySelect = (index) => {
  activeCategory.value = index
  activeQuestions.value = []
}

const goToContact = () => {
  router.push('/contact')
}
</script>

<style scoped>
@import '@/styles/info-theme.css';

.info-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.info-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  margin-bottom: 20px;
}

.info-card:hover {
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

.card-header h2 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  color: #303133;
}

.help-menu {
  border-right: none;
}

.search-input {
  margin-bottom: 20px;
}

.questions-list {
  margin-top: 20px;
}

.question-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: bold;
}

.question-answer {
  padding: 15px;
  line-height: 1.8;
  color: #606266;
}

.question-answer :deep(ol),
.question-answer :deep(ul) {
  padding-left: 20px;
}

.question-answer :deep(li) {
  margin: 8px 0;
}

.help-footer {
  margin-top: 40px;
}

.contact-methods {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}
</style>
