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
        <el-input
          v-model="searchQuery"
          placeholder="搜索帮助内容"
          prefix-icon="Search"
          clearable
          class="search-input"
        />
        
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
                <h4>{{ item.question }}</h4>
                <p>{{ item.answer }}</p>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
        
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
import { ref, computed } from 'vue'
import { QuestionFilled } from '@element-plus/icons-vue'

const searchQuery = ref('')
const activeNames = ref([0])

const helpData = ref([
  {
    icon: 'User',
    title: '账号相关',
    questions: [
      {
        question: '如何注册账号？',
        answer: '点击页面右上角的"注册"按钮，填写手机号、验证码和密码即可完成注册。'
      },
      {
        question: '忘记密码怎么办？',
        answer: '在登录页面点击"忘记密码"，通过手机验证码即可重置密码。'
      },
      {
        question: '如何修改个人信息？',
        answer: '登录后进入"个人中心"，点击"编辑资料"即可修改昵称、头像等信息。'
      }
    ]
  },
  {
    icon: 'Calendar',
    title: '预约相关',
    questions: [
      {
        question: '如何预约剧本？',
        answer: '选择您想玩的剧本，点击"立即预约"，选择门店、时间和人数，确认后提交即可。'
      },
      {
        question: '预约后可以取消吗？',
        answer: '可以。在"我的预约"中找到对应订单，在开始时间前2小时可免费取消。'
      },
      {
        question: '预约需要支付定金吗？',
        answer: '大部分门店需要支付定金，具体以门店规则为准。定金在完成游戏后可抵扣费用。'
      },
      {
        question: '预约成功后如何查看？',
        answer: '在"我的预约"中可以查看所有预约记录，包括待确认、已确认、已完成等状态。'
      }
    ]
  },
  {
    icon: 'Ticket',
    title: '优惠券相关',
    questions: [
      {
        question: '如何获得优惠券？',
        answer: '通过完成积分任务、参加活动、分享邀请等方式可以获得优惠券。'
      },
      {
        question: '优惠券如何使用？',
        answer: '在预约支付时，选择可用的优惠券即可自动抵扣相应金额。'
      },
      {
        question: '优惠券可以叠加使用吗？',
        answer: '每次预约只能使用一张优惠券，不支持叠加使用。'
      }
    ]
  },
  {
    icon: 'Star',
    title: '积分相关',
    questions: [
      {
        question: '如何获得积分？',
        answer: '通过每日签到、完善资料、收藏剧本、完成预约、发表评价、邀请好友等任务可以获得积分。'
      },
      {
        question: '积分有什么用？',
        answer: '积分可以用来兑换优惠券、抵扣部分费用等。'
      },
      {
        question: '积分会过期吗？',
        answer: '积分长期有效，不会过期。但兑换的优惠券可能有使用期限。'
      }
    ]
  },
  {
    icon: 'Reading',
    title: '剧本相关',
    questions: [
      {
        question: '如何选择合适的剧本？',
        answer: '可以通过剧本类型、难度、人数、时长等筛选条件，找到适合您的剧本。也可以查看评分和评价作为参考。'
      },
      {
        question: '如何收藏剧本？',
        answer: '在剧本详情页点击"收藏"按钮，即可将剧本添加到"我的收藏"中。'
      },
      {
        question: '剧本信息准确吗？',
        answer: '我们的剧本信息由门店提供，并经过平台审核。如有疑问，可以联系门店确认。'
      }
    ]
  },
  {
    icon: 'Shop',
    title: '门店相关',
    questions: [
      {
        question: '如何选择门店？',
        answer: '可以按照距离、评分、热度等排序查看门店列表，也可以搜索门店名称或地址。'
      },
      {
        question: '如何查看门店详情？',
        answer: '点击门店卡片即可查看门店的详细信息，包括地址、电话、营业时间、环境图片等。'
      },
      {
        question: '门店信息有误怎么办？',
        answer: '如果发现门店信息有误，请联系客服反馈，我们会及时核实并更正。'
      }
    ]
  }
])

const filteredHelpData = computed(() => {
  if (!searchQuery.value) return helpData.value
  
  return helpData.value.map(category => ({
    ...category,
    questions: category.questions.filter(item =>
      item.question.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      item.answer.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
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
</style>

