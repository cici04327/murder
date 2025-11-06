<template>
  <div class="page-container">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <el-icon><QuestionFilled /></el-icon>
          <span>帮助中心</span>
        </div>
      </template>
      
      <div class="page-content">
        <!-- 搜索框容器 -->
        <div class="search-container">
          <el-input
            v-model="searchQuery"
            placeholder="搜索帮助内容，如：预约、退款、优惠券..."
            prefix-icon="Search"
            clearable
            size="large"
            class="search-input"
            @focus="showSearchSuggestions = true"
            @blur="setTimeout(() => showSearchSuggestions = false, 200)"
            @keyup.enter="handleSearch(searchQuery)"
          />
          
          <!-- 搜索建议弹窗 -->
          <div v-if="showSearchSuggestions && !searchQuery" class="search-suggestions">
            <!-- 热门搜索 -->
            <div class="suggestion-section">
              <div class="suggestion-title">
                <el-icon><TrendCharts /></el-icon>
                <span>热门搜索</span>
              </div>
              <div class="keyword-tags">
                <el-tag
                  v-for="keyword in hotSearchKeywords"
                  :key="keyword"
                  class="keyword-tag"
                  @click="handleSearch(keyword)"
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
        <div v-if="searchQuery" class="search-stats">
          <span>找到 <strong>{{ filteredHelpData.reduce((sum, cat) => sum + cat.questions.length, 0) }}</strong> 个相关问题</span>
          <el-button text size="small" @click="searchQuery = ''; activeNames = [0]">
            清空搜索
          </el-button>
        </div>
        
        <el-collapse v-model="activeNames">
          <el-collapse-item
            v-for="(category, index) in filteredHelpData"
            :key="index"
            :name="index"
          >
            <template #title>
              <div class="category-title">
                <el-icon :size="20">
                  <component :is="category.icon" />
                </el-icon>
                <span>{{ category.title }}</span>
              </div>
            </template>
            
            <div class="questions-list">
              <div
                v-for="(item, qIndex) in category.questions"
                :key="qIndex"
                class="question-item"
              >
                <h4 v-html="highlightKeyword(item.question, searchQuery)"></h4>
                <p v-html="highlightKeyword(item.answer, searchQuery)"></p>
                <!-- 关键词标签 -->
                <div v-if="item.keywords" class="question-keywords">
                  <el-tag
                    v-for="keyword in item.keywords"
                    :key="keyword"
                    size="small"
                    type="info"
                    effect="plain"
                  >
                    {{ keyword }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
        
        <!-- 无搜索结果提示 -->
        <el-empty
          v-if="searchQuery && filteredHelpData.length === 0"
          description="没有找到相关问题"
          :image-size="100"
        >
          <el-button type="primary" @click="searchQuery = ''">
            查看全部问题
          </el-button>
        </el-empty>
        
        <div class="help-footer">
          <el-alert
            title="没有找到您需要的帮助？"
            type="info"
            :closable="false"
          >
            <template #default>
              <p>您可以通过以下方式获取帮助：</p>
              <el-space>
                <el-button type="primary" size="small" @click="$router.push('/contact')">
                  联系客服
                </el-button>
                <el-button size="small">
                  在线咨询
                </el-button>
              </el-space>
            </template>
          </el-alert>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { QuestionFilled, Search, TrendCharts, Clock } from '@element-plus/icons-vue'

const searchQuery = ref('')
const activeNames = ref([0])
const searchHistory = ref([])
const showSearchSuggestions = ref(false)

// 热门搜索词
const hotSearchKeywords = [
  '预约', '取消', '退款', '优惠券', '支付', '注册', '密码'
]

const helpData = ref([
  {
    icon: 'User',
    title: '账号相关',
    questions: [
      {
        question: '如何注册账号？',
        answer: '点击页面右上角的"注册"按钮，填写手机号、验证码和密码即可完成注册。',
        keywords: ['注册', '账号', '手机', '验证码']
      },
      {
        question: '忘记密码怎么办？',
        answer: '在登录页面点击"忘记密码"，通过手机验证码即可重置密码。',
        keywords: ['密码', '忘记', '重置', '找回']
      },
      {
        question: '如何修改个人信息？',
        answer: '登录后进入"个人中心"，点击"编辑资料"即可修改昵称、头像等信息。',
        keywords: ['个人信息', '修改', '资料', '昵称', '头像']
      }
    ]
  },
  {
    icon: 'Calendar',
    title: '预约相关',
    questions: [
      {
        question: '如何预约剧本？',
        answer: '选择您想玩的剧本，点击"立即预约"，选择门店、时间和人数，确认后提交即可。',
        keywords: ['预约', '剧本', '门店', '时间']
      },
      {
        question: '预约后可以取消吗？',
        answer: '可以。在"我的预约"中找到对应订单，在开始时间前2小时可免费取消。',
        keywords: ['取消', '预约', '退款']
      },
      {
        question: '预约需要支付定金吗？',
        answer: '大部分门店需要支付定金，具体以门店规则为准。定金在完成游戏后可抵扣费用。',
        keywords: ['定金', '支付', '预约']
      },
      {
        question: '预约成功后如何查看？',
        answer: '在"我的预约"中可以查看所有预约记录，包括待确认、已确认、已完成等状态。',
        keywords: ['查看', '预约', '订单', '状态']
      }
    ]
  },
  {
    icon: 'Ticket',
    title: '优惠券相关',
    questions: [
      {
        question: '如何获得优惠券？',
        answer: '通过完成积分任务、参加活动、分享邀请等方式可以获得优惠券。',
        keywords: ['优惠券', '获得', '活动', '积分']
      },
      {
        question: '优惠券如何使用？',
        answer: '在预约支付时，选择可用的优惠券即可自动抵扣相应金额。',
        keywords: ['优惠券', '使用', '抵扣', '支付']
      },
      {
        question: '优惠券可以叠加使用吗？',
        answer: '每次预约只能使用一张优惠券，不支持叠加使用。',
        keywords: ['优惠券', '叠加', '限制']
      }
    ]
  },
  {
    icon: 'Star',
    title: '积分相关',
    questions: [
      {
        question: '如何获得积分？',
        answer: '通过每日签到、完善资料、收藏剧本、完成预约、发表评价、邀请好友等任务可以获得积分。',
        keywords: ['积分', '获得', '签到', '任务']
      },
      {
        question: '积分有什么用？',
        answer: '积分可以用来兑换优惠券、抵扣部分费用等。',
        keywords: ['积分', '用途', '兑换', '抵扣']
      },
      {
        question: '积分会过期吗？',
        answer: '积分长期有效，不会过期。但兑换的优惠券可能有使用期限。',
        keywords: ['积分', '过期', '有效期']
      }
    ]
  },
  {
    icon: 'Reading',
    title: '剧本相关',
    questions: [
      {
        question: '如何选择合适的剧本？',
        answer: '可以通过剧本类型、难度、人数、时长等筛选条件，找到适合您的剧本。也可以查看评分和评价作为参考。',
        keywords: ['剧本', '选择', '筛选', '类型', '难度']
      },
      {
        question: '如何收藏剧本？',
        answer: '在剧本详情页点击"收藏"按钮，即可将剧本添加到"我的收藏"中。',
        keywords: ['收藏', '剧本', '我的收藏']
      },
      {
        question: '剧本信息准确吗？',
        answer: '我们的剧本信息由门店提供，并经过平台审核。如有疑问，可以联系门店确认。',
        keywords: ['剧本', '信息', '准确', '审核']
      }
    ]
  },
  {
    icon: 'Shop',
    title: '门店相关',
    questions: [
      {
        question: '如何选择门店？',
        answer: '可以按照距离、评分、热度等排序查看门店列表，也可以搜索门店名称或地址。',
        keywords: ['门店', '选择', '搜索', '距离', '评分']
      },
      {
        question: '如何查看门店详情？',
        answer: '点击门店卡片即可查看门店的详细信息，包括地址、电话、营业时间、环境图片等。',
        keywords: ['门店', '详情', '地址', '电话', '营业时间']
      },
      {
        question: '门店信息有误怎么办？',
        answer: '如果发现门店信息有误，请联系客服反馈，我们会及时核实并更正。',
        keywords: ['门店', '信息', '错误', '反馈']
      }
    ]
  }
])

// 高亮关键词
const highlightKeyword = (text, keyword) => {
  if (!keyword) return text
  const regex = new RegExp(`(${keyword})`, 'gi')
  return text.replace(regex, '<mark class="highlight">$1</mark>')
}

// 从 localStorage 加载搜索历史
const loadSearchHistory = () => {
  try {
    const history = localStorage.getItem('help_search_history_simple')
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
  searchHistory.value = [keyword, ...searchHistory.value.filter(k => k !== keyword)].slice(0, 10)
  try {
    localStorage.setItem('help_search_history_simple', JSON.stringify(searchHistory.value))
  } catch (e) {
    console.error('保存搜索历史失败:', e)
  }
}

// 执行搜索
const handleSearch = (keyword) => {
  searchQuery.value = keyword
  saveSearchHistory(keyword)
  showSearchSuggestions.value = false
}

// 清空搜索历史
const clearSearchHistory = () => {
  searchHistory.value = []
  try {
    localStorage.removeItem('help_search_history_simple')
  } catch (e) {
    console.error('清空搜索历史失败:', e)
  }
}

// 组件挂载时加载搜索历史
onMounted(() => {
  loadSearchHistory()
})

const filteredHelpData = computed(() => {
  if (!searchQuery.value) return helpData.value
  
  const keyword = searchQuery.value.toLowerCase()
  
  return helpData.value.map(category => ({
    ...category,
    questions: category.questions.filter(item => {
      // 匹配问题标题
      if (item.question.toLowerCase().includes(keyword)) return true
      // 匹配答案内容
      if (item.answer.toLowerCase().includes(keyword)) return true
      // 匹配关键词标签
      if (item.keywords && item.keywords.some(k => k.toLowerCase().includes(keyword))) return true
      return false
    })
  })).filter(category => category.questions.length > 0)
})
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

.page-content {
  padding: 20px;
}

.search-input {
  margin-bottom: 30px;
}

.category-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.questions-list {
  padding: 10px 0;
}

.question-item {
  margin-bottom: 25px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
  border-left: 3px solid #409eff;
}

.question-item h4 {
  margin: 0 0 12px;
  color: #409eff;
  font-size: 16px;
}

.question-item p {
  margin: 0;
  color: #666;
  line-height: 1.8;
  font-size: 14px;
}

.help-footer {
  margin-top: 40px;
}

.help-footer p {
  margin-bottom: 15px;
}

/* 搜索容器 */
.search-container {
  position: relative;
  margin-bottom: 30px;
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
  max-height: 300px;
  overflow-y: auto;
}

.suggestion-section {
  margin-bottom: 16px;
}

.suggestion-section:last-child {
  margin-bottom: 0;
}

.suggestion-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}

.suggestion-title .clear-history {
  margin-left: auto;
  color: #909399;
  padding: 0;
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
  font-size: 13px;
  transition: all 0.3s;
}

.history-item:hover {
  background: #f5f7fa;
  color: #409eff;
}

.history-item .el-icon {
  font-size: 14px;
  color: #909399;
}

/* 搜索结果统计 */
.search-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #ecf5ff;
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
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #e4e7ed;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.question-keywords .el-tag {
  cursor: default;
  font-size: 12px;
}
</style>

