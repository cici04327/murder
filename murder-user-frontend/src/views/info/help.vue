<template>
  <div class="info-page help-page">
    <!-- 面包屑导航 -->
    <InfoPageBreadcrumb />
    
    <!-- 头部横幅 -->
    <div class="hero-banner">
      <div class="hero-content">
        <h1 class="hero-title animate-fade-in-up">
          <span class="gradient-text">帮助中心</span>
        </h1>
        <p class="hero-subtitle animate-fade-in-up delay-1">
          常见问题解答，让您快速上手剧本杀预约
        </p>
        <!-- 搜索框 -->
        <div class="search-box animate-fade-in-up delay-2">
          <el-input
            v-model="searchQuery"
            placeholder="搜索您的问题..."
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </div>
    </div>
    
    <el-card class="info-card animate-fade-in">
      <template #header>
        <div class="card-header">
          <h2><el-icon><QuestionFilled /></el-icon> 常见问题</h2>
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
              <div class="search-container">
                <el-input
                  v-model="searchKeyword"
                  placeholder="搜索问题关键词"
                  :prefix-icon="Search"
                  clearable
                  size="large"
                  class="search-input"
                  @focus="showSearchSuggestions = true"
                  @keyup.enter="handleSearch(searchKeyword)"
                />
                
                <!-- 搜索建议（热门搜索和历史记录） -->
                <div v-if="showSearchSuggestions && !searchKeyword" class="search-suggestions">
                  <!-- 热门搜索 -->
                  <div v-if="hotSearchKeywords.length > 0" class="suggestion-section">
                    <div class="suggestion-title">
                      <el-icon><TrendCharts /></el-icon>
                      <span>热门搜索</span>
                    </div>
                    <div class="keyword-tags">
                      <el-tag
                        v-for="keyword in hotSearchKeywords"
                        :key="keyword"
                        class="keyword-tag"
                        @click="handleHotKeywordClick(keyword)"
                      >
                        {{ keyword }}
                      </el-tag>
                    </div>
                  </div>
                  
                  <!-- 搜索历史 -->
                  <div v-if="searchHistory.length > 0" class="suggestion-section">
                    <div class="suggestion-title">
                      <el-icon><Clock /></el-icon>
                      <span>搜索历史</span>
                      <el-button
                        text
                        size="small"
                        class="clear-history"
                        @click="clearSearchHistory"
                      >
                        清空
                      </el-button>
                    </div>
                    <div class="history-list">
                      <div
                        v-for="keyword in searchHistory"
                        :key="keyword"
                        class="history-item"
                        @click="handleSearch(keyword)"
                      >
                        <el-icon><Search /></el-icon>
                        <span>{{ keyword }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 搜索结果统计 -->
              <div v-if="searchKeyword" class="search-stats">
                <span>找到 <strong>{{ searchResultCount }}</strong> 个相关问题</span>
                <el-button text size="small" @click="searchKeyword = ''">
                  清空搜索
                </el-button>
              </div>

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
                        <span v-html="highlightKeyword(question.question, searchKeyword)"></span>
                      </div>
                    </template>
                    <div class="question-answer">
                      <div v-html="question.answer"></div>
                      <!-- 关键词标签 -->
                      <div v-if="question.keywords" class="question-keywords">
                        <el-tag
                          v-for="keyword in question.keywords"
                          :key="keyword"
                          size="small"
                          type="info"
                          effect="plain"
                        >
                          {{ keyword }}
                        </el-tag>
                      </div>
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
              
              <!-- 无搜索结果 -->
              <el-empty
                v-else-if="searchKeyword"
                description="没有找到相关问题"
                :image-size="120"
              >
                <template #default>
                  <p>尝试其他关键词或</p>
                  <el-button type="primary" @click="goToContact">
                    <el-icon><Service /></el-icon>
                    联系客服
                  </el-button>
                </template>
              </el-empty>

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
import { ref, computed, onMounted } from 'vue'
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
  Phone,
  TrendCharts,
  Clock
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

const router = useRouter()

const activeCategory = ref('getting-started')
const activeQuestions = ref([])
const searchKeyword = ref('')
const searchQuery = ref('')
const searchHistory = ref([])
const showSearchSuggestions = ref(false)

const questionsData = {
  'getting-started': [
    {
      id: 'gs-1',
      question: '如何注册账号？',
      answer: '<p>注册非常简单，只需3步：</p><ol><li>点击页面右上角的"注册"按钮</li><li>填写手机号、密码等基本信息</li><li>点击"获取验证码"，输入收到的验证码后提交即可</li></ol>',
      keywords: ['注册', '账号', '手机', '验证码']
    },
    {
      id: 'gs-2',
      question: '如何搜索剧本和门店？',
      answer: '<p>我们提供了多种搜索方式：</p><ul><li>在首页可以浏览推荐的热门剧本和优质门店</li><li>点击"剧本大厅"或"门店列表"进行筛选查看</li><li>使用搜索框输入关键词进行搜索</li></ul>',
      keywords: ['搜索', '剧本', '门店', '筛选', '查找']
    }
  ],
  'reservation': [
    {
      id: 'res-1',
      question: '如何预约剧本杀？',
      answer: '<p>预约流程：</p><ol><li>选择您喜欢的剧本或门店</li><li>查看可预约时间</li><li>选择日期和时间段</li><li>填写预约信息（人数、联系方式等）</li><li>提交预约并完成支付</li></ol>',
      keywords: ['预约', '剧本杀', '流程', '时间', '支付']
    },
    {
      id: 'res-2',
      question: '可以取消预约吗？',
      answer: '<p>可以取消预约。在预约时间前24小时取消，可全额退款；24小时内取消，将扣除30%手续费。具体以各门店规定为准。</p>',
      keywords: ['取消', '预约', '退款', '手续费']
    },
    {
      id: 'res-3',
      question: '预约成功后如何修改？',
      answer: '<p>预约成功后可以修改：</p><ul><li>进入"我的预约"</li><li>找到对应订单</li><li>点击"修改预约"</li><li>调整时间、人数等信息</li></ul><p>注意：修改次数有限，且需在预约时间前完成。</p>',
      keywords: ['修改', '预约', '调整', '变更']
    }
  ],
  'payment': [
    {
      id: 'pay-1',
      question: '支持哪些支付方式？',
      answer: '<p>我们支持以下支付方式：</p><ul><li>支付宝</li><li>微信支付</li><li>银行卡支付</li><li>优惠券抵扣</li></ul>',
      keywords: ['支付', '支付宝', '微信', '银行卡', '优惠券']
    },
    {
      id: 'pay-2',
      question: '支付失败怎么办？',
      answer: '<p>如遇支付失败，请检查：</p><ol><li>账户余额是否充足</li><li>网络连接是否正常</li><li>支付密码是否正确</li></ol><p>如问题仍未解决，请联系客服。</p>',
      keywords: ['支付', '失败', '余额', '网络', '密码']
    },
    {
      id: 'pay-3',
      question: '退款多久到账？',
      answer: '<p>退款到账时间：</p><ul><li>支付宝：1-3个工作日</li><li>微信支付：1-3个工作日</li><li>银行卡：3-7个工作日</li></ul>',
      keywords: ['退款', '到账', '时间', '工作日']
    }
  ],
  'coupon': [
    {
      id: 'coup-1',
      question: '如何获得优惠券？',
      answer: '<p>获取优惠券的方式：</p><ul><li>新用户注册赠送</li><li>参与平台活动</li><li>完成预约后获得</li><li>推荐好友获得</li></ul>',
      keywords: ['优惠券', '获得', '赠送', '活动', '推荐']
    },
    {
      id: 'coup-2',
      question: '优惠券如何使用？',
      answer: '<p>使用步骤：</p><ol><li>在支付页面选择可用优惠券</li><li>系统会自动抵扣相应金额</li><li>完成剩余金额支付即可</li></ol>',
      keywords: ['优惠券', '使用', '抵扣', '支付']
    },
    {
      id: 'coup-3',
      question: '优惠券过期了怎么办？',
      answer: '<p>优惠券过期后将自动失效，无法使用。建议：</p><ul><li>及时查看优惠券有效期</li><li>优先使用即将过期的券</li><li>关注平台活动获取新券</li></ul>',
      keywords: ['优惠券', '过期', '有效期', '失效']
    }
  ],
  'account': [
    {
      id: 'acc-1',
      question: '如何修改个人信息？',
      answer: '<p>登录后进入"个人中心"，点击"编辑资料"即可修改昵称、头像、性别等信息。</p>',
      keywords: ['个人信息', '修改', '资料', '昵称', '头像']
    },
    {
      id: 'acc-2',
      question: '忘记密码怎么办？',
      answer: '<p>在登录页面点击"忘记密码"，通过手机验证码重置密码。</p>',
      keywords: ['密码', '忘记', '重置', '找回', '验证码']
    },
    {
      id: 'acc-3',
      question: '如何绑定手机号？',
      answer: '<p>绑定手机号步骤：</p><ol><li>进入"个人中心"</li><li>点击"账号安全"</li><li>选择"绑定手机"</li><li>输入手机号和验证码</li></ol>',
      keywords: ['手机', '绑定', '账号', '安全']
    }
  ]
}

// 热门搜索词
const hotSearchKeywords = [
  '预约', '取消', '退款', '优惠券', '支付', '注册', '密码'
]

// 获取所有问题（用于全局搜索）
const allQuestions = computed(() => {
  const questions = []
  for (const category in questionsData) {
    questionsData[category].forEach(q => {
      questions.push({
        ...q,
        category
      })
    })
  }
  return questions
})

// 搜索过滤后的问题
const filteredQuestions = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  
  if (!keyword) {
    // 没有搜索词时，显示当前分类的问题
    return questionsData[activeCategory.value] || []
  }
  
  // 有搜索词时，全局搜索
  return allQuestions.value.filter(q => {
    // 匹配问题标题
    if (q.question.toLowerCase().includes(keyword)) return true
    // 匹配答案内容
    if (q.answer.toLowerCase().includes(keyword)) return true
    // 匹配关键词标签
    if (q.keywords && q.keywords.some(k => k.toLowerCase().includes(keyword))) return true
    return false
  })
})

const currentQuestions = computed(() => {
  return filteredQuestions.value
})

// 搜索结果统计
const searchResultCount = computed(() => {
  return filteredQuestions.value.length
})

// 高亮关键词
const highlightKeyword = (text, keyword) => {
  if (!keyword) return text
  const regex = new RegExp(`(${keyword})`, 'gi')
  return text.replace(regex, '<mark class="highlight">$1</mark>')
}

// 从 localStorage 加载搜索历史
const loadSearchHistory = () => {
  try {
    const history = localStorage.getItem('help_search_history')
    if (history) {
      searchHistory.value = JSON.parse(history)
    }
  } catch (e) {
    console.error('加载搜索历史失败:', e)
  }
}

// 保存搜索历史
const saveSearchHistory = (keyword) => {
  if (!keyword.trim()) return
  
  // 去重并添加到开头
  searchHistory.value = [
    keyword,
    ...searchHistory.value.filter(k => k !== keyword)
  ].slice(0, 10) // 最多保存10条
  
  try {
    localStorage.setItem('help_search_history', JSON.stringify(searchHistory.value))
  } catch (e) {
    console.error('保存搜索历史失败:', e)
  }
}

// 执行搜索
const handleSearch = (keyword) => {
  searchKeyword.value = keyword
  searchQuery.value = keyword
  saveSearchHistory(keyword)
  showSearchSuggestions.value = false
}

// 清空搜索历史
const clearSearchHistory = () => {
  searchHistory.value = []
  try {
    localStorage.removeItem('help_search_history')
  } catch (e) {
    console.error('清空搜索历史失败:', e)
  }
}

// 点击热门搜索
const handleHotKeywordClick = (keyword) => {
  handleSearch(keyword)
}

const handleCategorySelect = (index) => {
  activeCategory.value = index
  activeQuestions.value = []
  // 切换分类时清空搜索
  if (!searchKeyword.value) {
    searchKeyword.value = ''
  }
}

const goToContact = () => {
  router.push('/contact')
}

// 组件挂载时加载搜索历史
onMounted(() => {
  loadSearchHistory()
})
</script>

<style scoped>
@import '@/styles/info-enhanced.css';
@import '@/styles/info-theme.css';

/* 搜索框样式 */
.search-box {
  max-width: 600px;
  margin: 40px auto 0;
}

.search-box :deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.95);
  border-radius: 25px;
  padding: 5px 20px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.15);
}

.search-box :deep(.el-input__inner) {
  font-size: 16px;
}

/* FAQ 分类卡片 */
.faq-category {
  margin-bottom: 30px;
}

.category-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 20px;
  padding: 15px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
}

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

/* 搜索容器 */
.search-container {
  position: relative;
  margin-bottom: 20px;
}

/* 搜索建议弹窗 */
.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 8px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 16px;
  z-index: 100;
  max-height: 400px;
  overflow-y: auto;
}

.suggestion-section {
  margin-bottom: 20px;
}

.suggestion-section:last-child {
  margin-bottom: 0;
}

.suggestion-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.suggestion-title .clear-history {
  margin-left: auto;
  color: #909399;
}

/* 关键词标签 */
.keyword-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.keyword-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.keyword-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

/* 搜索历史列表 */
.history-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  color: #606266;
  font-size: 14px;
  transition: all 0.3s;
}

.history-item:hover {
  background: #f5f7fa;
  color: #409eff;
}

.history-item .el-icon {
  font-size: 16px;
  color: #909399;
}

/* 搜索结果统计 */
.search-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f0f9ff;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #606266;
}

.search-stats strong {
  color: #409eff;
  font-size: 18px;
  margin: 0 4px;
}

/* 关键词高亮 */
:deep(.highlight) {
  background: linear-gradient(120deg, #ffeaa7 0%, #fdcb6e 100%);
  padding: 2px 4px;
  border-radius: 3px;
  font-weight: 600;
  color: #2d3436;
}

/* 问题关键词标签 */
.question-keywords {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #e4e7ed;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.question-keywords .el-tag {
  cursor: default;
}

/* 空状态 */
.el-empty {
  padding: 60px 0;
}

.el-empty p {
  margin-bottom: 15px;
  color: #909399;
}
</style>
