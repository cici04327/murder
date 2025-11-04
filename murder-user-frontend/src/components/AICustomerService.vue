<template>
  <div class="ai-customer-service">
    <!-- 悬浮球 -->
    <transition name="bounce">
      <div 
        v-if="!isOpen" 
        class="service-bubble" 
        @click="toggleChat"
        :class="{ 'has-unread': unreadCount > 0 }"
      >
        <el-icon class="service-icon"><Service /></el-icon>
        <div v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }}</div>
        <div class="bubble-tip">AI客服</div>
      </div>
    </transition>

    <!-- 聊天窗口 -->
    <transition name="slide-up">
      <div v-if="isOpen" class="chat-window">
        <!-- 头部 -->
        <div class="chat-header">
          <div class="header-left">
            <el-avatar :size="40" :src="PLACEHOLDERS.AVATAR">
              <el-icon><Service /></el-icon>
            </el-avatar>
            <div class="header-info">
              <div class="header-title">AI智能客服</div>
              <div class="header-status">
                <span class="status-dot"></span>
                在线服务中
              </div>
            </div>
          </div>
          <div class="header-actions">
            <el-button text @click="minimizeChat" circle>
              <el-icon><Minus /></el-icon>
            </el-button>
            <el-button text @click="closeChat" circle>
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
        </div>

        <!-- 快捷问题 -->
        <div v-if="messages.length === 0" class="quick-questions">
          <div class="quick-title">常见问题</div>
          <div class="quick-buttons">
            <el-button 
              v-for="question in quickQuestions" 
              :key="question.id"
              size="small"
              round
              @click="askQuestion(question.question)"
            >
              {{ question.label }}
            </el-button>
          </div>
        </div>

        <!-- 消息列表 -->
        <div class="chat-messages" ref="messagesContainer">
          <div 
            v-for="(message, index) in messages" 
            :key="index"
            class="message-item"
            :class="{ 'user-message': message.isUser, 'ai-message': !message.isUser }"
          >
            <el-avatar 
              v-if="!message.isUser" 
              :size="32" 
              class="message-avatar"
            >
              <el-icon><Cpu /></el-icon>
            </el-avatar>
            
            <div class="message-content">
              <div class="message-bubble" v-html="message.content"></div>
              <div class="message-time">{{ message.time }}</div>
            </div>

            <el-avatar 
              v-if="message.isUser" 
              :size="32" 
              class="message-avatar"
            >
              <el-icon><User /></el-icon>
            </el-avatar>
          </div>

          <!-- AI正在输入 -->
          <div v-if="isTyping" class="message-item ai-message">
            <el-avatar :size="32" class="message-avatar">
              <el-icon><Cpu /></el-icon>
            </el-avatar>
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>

        <!-- 输入框 -->
        <div class="chat-input">
          <el-input
            v-model="inputMessage"
            placeholder="输入您的问题..."
            @keyup.enter="sendMessage"
            :disabled="isTyping"
          >
            <template #append>
              <el-button 
                :icon="isTyping ? Loading : Promotion" 
                @click="sendMessage"
                :loading="isTyping"
                type="primary"
              >
                发送
              </el-button>
            </template>
          </el-input>
        </div>

        <!-- 底部提示 -->
        <div class="chat-footer">
          <el-text size="small" type="info">
            <el-icon><InfoFilled /></el-icon>
            AI客服24小时在线为您服务
          </el-text>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { PLACEHOLDERS } from '@/assets/placeholders'
import { ElMessage } from 'element-plus'

const isOpen = ref(false)
const messages = ref([])
const inputMessage = ref('')
const isTyping = ref(false)
const unreadCount = ref(0)
const messagesContainer = ref(null)

// 快捷问题
const quickQuestions = ref([
  { id: 1, label: '如何预约剧本？', question: '如何预约剧本？' },
  { id: 2, label: '支付方式', question: '支持哪些支付方式？' },
  { id: 3, label: '退款政策', question: '如何申请退款？' },
  { id: 4, label: '联系人工客服', question: '我想联系人工客服' }
])

// AI知识库
const knowledgeBase = {
  // 预约相关
  '预约|怎么预约|如何预约|订票': {
    answer: `
      <div class="answer-content">
        <p><strong>📋 预约剧本流程：</strong></p>
        <ol>
          <li>浏览剧本列表，选择喜欢的剧本</li>
          <li>点击"立即预约"按钮</li>
          <li>选择门店、日期和时间</li>
          <li>填写联系信息和参与人数</li>
          <li>选择优惠券并完成支付</li>
        </ol>
        <p><strong>💡 温馨提示：</strong></p>
        <ul>
          <li>建议提前3-7天预约，避免档期紧张</li>
          <li>热门剧本和周末档期更需提前预约</li>
          <li>确认人数后再预约，避免临时改动</li>
        </ul>
        <p>如需帮助，请联系客服：400-123-4567</p>
      </div>
    `
  },
  
  // 支付相关
  '支付|付款|支付方式|怎么付钱': {
    answer: `
      <div class="answer-content">
        <p><strong>💳 支持的支付方式：</strong></p>
        <ul>
          <li>💰 支付宝支付</li>
          <li>💚 微信支付</li>
          <li>🏦 银行卡支付（借记卡/信用卡）</li>
          <li>🎁 优惠券抵扣</li>
          <li>⭐ 积分兑换</li>
        </ul>
        <p><strong>🔒 安全保障：</strong></p>
        <ul>
          <li>所有支付均采用SSL加密传输</li>
          <li>支付平台经过官方认证</li>
          <li>支持7天无理由退款</li>
        </ul>
      </div>
    `
  },
  
  // 退款相关
  '退款|取消|退订|退钱': {
    answer: `
      <div class="answer-content">
        <p><strong>💰 退款政策：</strong></p>
        <ul>
          <li>📅 预约前7天以上取消：<span style="color:#67c23a">全额退款</span></li>
          <li>📅 预约前3-7天取消：<span style="color:#e6a23c">退款80%</span></li>
          <li>📅 预约前1-3天取消：<span style="color:#f56c6c">退款50%</span></li>
          <li>📅 预约当天取消：<span style="color:#909399">不予退款</span></li>
        </ul>
        <p><strong>📝 申请退款步骤：</strong></p>
        <ol>
          <li>进入"个人中心-我的预约"</li>
          <li>找到对应订单，点击"申请退款"</li>
          <li>选择退款原因</li>
          <li>提交申请，等待审核</li>
          <li>审核通过后3-5个工作日到账</li>
        </ol>
        <p>特殊情况请联系客服：400-123-4567</p>
      </div>
    `
  },
  
  // 优惠券相关
  '优惠券|折扣|优惠|活动|促销': {
    answer: `
      <div class="answer-content">
        <p><strong>🎁 获取优惠券的方式：</strong></p>
        <ul>
          <li>🎉 新用户注册立即送<strong>50元</strong>优惠券</li>
          <li>📅 每日签到可获得<strong>积分</strong>兑换优惠券</li>
          <li>🎊 参与限时活动赢取专属优惠券</li>
          <li>🎂 生日当月自动发放<strong>生日礼券</strong></li>
          <li>👥 邀请好友注册送<strong>30元</strong>优惠券</li>
          <li>⭐ 完成预约评价获得<strong>随机优惠券</strong></li>
        </ul>
        <p><strong>💡 使用规则：</strong></p>
        <ul>
          <li>单笔订单只能使用一张优惠券</li>
          <li>优惠券有使用期限，请及时使用</li>
          <li>部分优惠券有满减限制</li>
        </ul>
        <p>前往"个人中心-我的优惠券"查看详情</p>
      </div>
    `
  },
  
  // 积分相关
  '积分|会员|等级': {
    answer: `
      <div class="answer-content">
        <p><strong>⭐ 积分获取方式：</strong></p>
        <ul>
          <li>📅 每日签到：+10积分</li>
          <li>✅ 完成预约：+50积分</li>
          <li>💬 发表评价：+20积分</li>
          <li>👥 邀请好友：+100积分</li>
          <li>🎯 完成任务：+30积分</li>
          <li>❤️ 收藏剧本：+5积分</li>
        </ul>
        <p><strong>🎁 积分用途：</strong></p>
        <ul>
          <li>兑换优惠券（100积分=10元券）</li>
          <li>兑换精美礼品</li>
          <li>参与积分抽奖活动</li>
          <li>升级会员等级享受更多权益</li>
        </ul>
        <p><strong>👑 会员等级体系：</strong></p>
        <ul>
          <li>🥉 青铜会员：0-999积分</li>
          <li>🥈 白银会员：1000-4999积分</li>
          <li>🥇 黄金会员：5000-9999积分</li>
          <li>💎 钻石会员：10000+积分</li>
        </ul>
      </div>
    `
  },
  
  // 人工客服
  '人工|客服|联系|咨询': {
    answer: `
      <div class="answer-content">
        <p><strong>📞 联系我们：</strong></p>
        <ul>
          <li>☎️ 客服热线：<strong>400-123-4567</strong></li>
          <li>⏰ 服务时间：9:00-21:00（全年无休）</li>
          <li>📧 邮箱：service@jubensha.com</li>
          <li>💬 微信公众号：剧本杀预约平台</li>
          <li>🎯 在线客服：工作日9:00-18:00</li>
        </ul>
        <p><strong>🏢 公司地址：</strong></p>
        <p>北京市朝阳区xxx大厦18层</p>
        <p><strong>⚡ 快速响应：</strong></p>
        <ul>
          <li>紧急问题请拨打客服热线</li>
          <li>一般咨询可发送邮件</li>
          <li>关注公众号获取最新资讯</li>
        </ul>
      </div>
    `
  },
  
  // 门店相关
  '门店|地址|位置|店铺': {
    answer: `
      <div class="answer-content">
        <p><strong>🏪 查找门店：</strong></p>
        <ol>
          <li>进入"门店列表"页面</li>
          <li>查看附近的合作门店</li>
          <li>筛选区域、评分和距离</li>
          <li>查看详细地址和联系方式</li>
          <li>使用地图导航功能</li>
        </ol>
        <p><strong>🌟 门店特色：</strong></p>
        <ul>
          <li>💯 全国已有100+合作门店</li>
          <li>🎨 精心布置的主题场景</li>
          <li>🎭 专业DM主持团队</li>
          <li>🍕 部分门店提供餐饮服务</li>
          <li>🚗 大型门店配有停车场</li>
        </ul>
        <p><strong>⭐ 门店评分系统：</strong></p>
        <p>所有门店均经过严格审核，用户真实评价公开透明</p>
      </div>
    `
  },
  
  // 剧本相关
  '剧本|推荐|热门|好玩|类型': {
    answer: `
      <div class="answer-content">
        <p><strong>🎭 发现好剧本：</strong></p>
        <ul>
          <li>🔥 查看热门剧本排行榜</li>
          <li>💎 根据个人喜好获取AI推荐</li>
          <li>⭐ 参考用户评分和评价</li>
          <li>🎯 使用分类筛选功能</li>
          <li>🔍 搜索特定主题的剧本</li>
        </ul>
        <p><strong>📚 剧本分类：</strong></p>
        <ul>
          <li>🔪 推理悬疑：烧脑解谜</li>
          <li>😱 恐怖惊悚：刺激体验</li>
          <li>💔 情感沉浸：感人至深</li>
          <li>⚔️ 机制对抗：策略博弈</li>
          <li>🎉 欢乐互动：轻松搞笑</li>
          <li>🏛️ 还原历史：穿越时空</li>
        </ul>
        <p><strong>💡 选择建议：</strong></p>
        <ul>
          <li>新手推荐：欢乐互动、简单推理</li>
          <li>高手推荐：硬核推理、复杂机制</li>
        </ul>
        <p>500+精选剧本，总有一款适合你！</p>
      </div>
    `
  },
  
  // 注册登录
  '注册|登录|账号|密码|忘记密码': {
    answer: `
      <div class="answer-content">
        <p><strong>👤 账号管理：</strong></p>
        <p><strong>📝 注册账号：</strong></p>
        <ol>
          <li>点击右上角"登录/注册"</li>
          <li>选择"注册新账号"</li>
          <li>输入手机号获取验证码</li>
          <li>设置登录密码</li>
          <li>完成注册即送50元优惠券</li>
        </ol>
        <p><strong>🔑 忘记密码：</strong></p>
        <ol>
          <li>点击登录页"忘记密码"</li>
          <li>输入注册手机号</li>
          <li>验证身份后重置密码</li>
        </ol>
        <p><strong>🔒 安全提示：</strong></p>
        <ul>
          <li>请使用强密码，包含字母和数字</li>
          <li>不要将密码告诉他人</li>
          <li>定期更换密码更安全</li>
        </ul>
      </div>
    `
  },
  
  // 预约查询
  '订单|查询|预约记录|我的预约': {
    answer: `
      <div class="answer-content">
        <p><strong>📋 查看预约记录：</strong></p>
        <ol>
          <li>登录账号</li>
          <li>进入"个人中心"</li>
          <li>点击"我的预约"</li>
          <li>查看所有历史预约</li>
        </ol>
        <p><strong>📊 订单状态说明：</strong></p>
        <ul>
          <li>⏳ 待支付：请尽快完成支付</li>
          <li>✅ 已支付：预约成功，等待体验</li>
          <li>🎉 已完成：体验结束，可以评价</li>
          <li>❌ 已取消：订单已取消</li>
          <li>💰 退款中：正在处理退款</li>
        </ul>
        <p><strong>💡 温馨提示：</strong></p>
        <p>预约后会收到短信提醒，请留意手机短信</p>
      </div>
    `
  },
  
  // 评价相关
  '评价|评论|反馈|评分': {
    answer: `
      <div class="answer-content">
        <p><strong>⭐ 发表评价：</strong></p>
        <ol>
          <li>进入"个人中心-我的预约"</li>
          <li>找到已完成的订单</li>
          <li>点击"评价"按钮</li>
          <li>对剧本和门店进行评分</li>
          <li>撰写体验感受</li>
          <li>提交后获得20积分奖励</li>
        </ol>
        <p><strong>💬 评价内容建议：</strong></p>
        <ul>
          <li>剧本剧情、机制设计</li>
          <li>DM主持水平</li>
          <li>门店环境和服务</li>
          <li>整体游戏体验</li>
        </ul>
        <p><strong>🎁 评价奖励：</strong></p>
        <ul>
          <li>每次评价获得20积分</li>
          <li>优质评价有机会获得额外优惠券</li>
          <li>精华评价会被推荐展示</li>
        </ul>
      </div>
    `
  },
  
  // 收藏相关
  '收藏|喜欢|关注': {
    answer: `
      <div class="answer-content">
        <p><strong>❤️ 收藏功能：</strong></p>
        <p><strong>📚 可以收藏：</strong></p>
        <ul>
          <li>喜欢的剧本</li>
          <li>优质门店</li>
          <li>有用的攻略文章</li>
        </ul>
        <p><strong>✨ 收藏好处：</strong></p>
        <ul>
          <li>随时查看收藏列表</li>
          <li>收到相关更新通知</li>
          <li>每次收藏获得5积分</li>
          <li>方便分享给好友</li>
        </ul>
        <p><strong>📍 查看收藏：</strong></p>
        <p>进入"个人中心-我的收藏"即可查看所有收藏内容</p>
      </div>
    `
  },
  
  // 游戏规则
  '规则|怎么玩|新手|攻略': {
    answer: `
      <div class="answer-content">
        <p><strong>🎮 剧本杀游戏指南：</strong></p>
        <p><strong>📖 基本流程：</strong></p>
        <ol>
          <li>阅读剧本：了解角色背景</li>
          <li>搜证环节：寻找线索</li>
          <li>讨论推理：分享信息</li>
          <li>投票环节：找出凶手</li>
          <li>复盘总结：DM揭晓真相</li>
        </ol>
        <p><strong>💡 新手建议：</strong></p>
        <ul>
          <li>选择简单的新手本入门</li>
          <li>认真阅读剧本和线索</li>
          <li>积极参与讨论和推理</li>
          <li>不要剧透，尊重其他玩家</li>
          <li>遵守DM的游戏规则</li>
        </ul>
        <p><strong>⏰ 游戏时长：</strong></p>
        <ul>
          <li>简单本：2-3小时</li>
          <li>中等本：3-4小时</li>
          <li>硬核本：4-6小时</li>
        </ul>
      </div>
    `
  },
  
  // 价格相关
  '价格|多少钱|费用|收费': {
    answer: `
      <div class="answer-content">
        <p><strong>💰 价格说明：</strong></p>
        <p><strong>📊 价格范围：</strong></p>
        <ul>
          <li>入门级剧本：50-80元/人</li>
          <li>中档剧本：80-150元/人</li>
          <li>高端剧本：150-300元/人</li>
          <li>豪华沉浸本：300+元/人</li>
        </ul>
        <p><strong>🎁 优惠方式：</strong></p>
        <ul>
          <li>使用优惠券立减</li>
          <li>积分抵扣现金</li>
          <li>团购更优惠</li>
          <li>工作日价格更低</li>
          <li>会员专享折扣</li>
        </ul>
        <p><strong>💡 价格因素：</strong></p>
        <p>价格受剧本质量、门店位置、时间段等因素影响</p>
      </div>
    `
  },
  
  // 团队预约
  '团队|团购|包场|人数': {
    answer: `
      <div class="answer-content">
        <p><strong>👥 团队预约：</strong></p>
        <p><strong>🎯 人数要求：</strong></p>
        <ul>
          <li>小型本：4-6人</li>
          <li>中型本：6-8人</li>
          <li>大型本：8-12人</li>
          <li>超大本：12人以上</li>
        </ul>
        <p><strong>🎉 团队优惠：</strong></p>
        <ul>
          <li>满6人享9折优惠</li>
          <li>满10人享8折优惠</li>
          <li>包场可享受更多折扣</li>
          <li>企业团建可定制方案</li>
        </ul>
        <p><strong>📞 团队预约建议：</strong></p>
        <ul>
          <li>提前1周联系门店</li>
          <li>确认人数和时间</li>
          <li>大型团队可定制专属剧本</li>
        </ul>
        <p>团队预约请联系：400-123-4567</p>
      </div>
    `
  },
  
  // 默认回复
  'default': {
    answer: `
      <div class="answer-content">
        <p>😊 抱歉，我暂时无法理解您的问题。</p>
        <p><strong>您可以尝试：</strong></p>
        <ul>
          <li>📞 拨打客服热线：<strong>400-123-4567</strong></li>
          <li>💬 点击快捷问题获取帮助</li>
          <li>📧 发送邮件至：service@jubensha.com</li>
          <li>🔍 换个方式描述您的问题</li>
        </ul>
        <p><strong>🔥 热门问题：</strong></p>
        <ul>
          <li>如何预约剧本？</li>
          <li>支持哪些支付方式？</li>
          <li>如何申请退款？</li>
          <li>怎么获取优惠券？</li>
        </ul>
      </div>
    `
  }
}

// 打开聊天
const toggleChat = () => {
  isOpen.value = true
  unreadCount.value = 0
  
  // 欢迎消息
  if (messages.value.length === 0) {
    setTimeout(() => {
      addAIMessage('您好！我是AI智能客服，很高兴为您服务！😊<br>请问有什么可以帮您的吗？')
    }, 300)
  }
}

// 关闭聊天
const closeChat = () => {
  isOpen.value = false
}

// 最小化
const minimizeChat = () => {
  isOpen.value = false
}

// 快捷提问
const askQuestion = (question) => {
  inputMessage.value = question
  sendMessage()
}

// 发送消息
const sendMessage = () => {
  if (!inputMessage.value.trim() || isTyping.value) {
    return
  }

  const userMessage = inputMessage.value.trim()
  
  // 添加用户消息
  addUserMessage(userMessage)
  inputMessage.value = ''

  // AI思考
  isTyping.value = true
  
  setTimeout(() => {
    const aiResponse = getAIResponse(userMessage)
    addAIMessage(aiResponse)
    isTyping.value = false
  }, 1000 + Math.random() * 1000) // 1-2秒随机延迟
}

// 添加用户消息
const addUserMessage = (content) => {
  messages.value.push({
    content,
    isUser: true,
    time: getCurrentTime()
  })
  scrollToBottom()
}

// 添加AI消息
const addAIMessage = (content) => {
  messages.value.push({
    content,
    isUser: false,
    time: getCurrentTime()
  })
  scrollToBottom()
}

// 获取AI回复
const getAIResponse = (question) => {
  // 遍历知识库匹配关键词
  for (const [keywords, response] of Object.entries(knowledgeBase)) {
    if (keywords === 'default') continue
    
    const keywordList = keywords.split('|')
    if (keywordList.some(keyword => question.includes(keyword))) {
      return response.answer
    }
  }
  
  // 没有匹配到，返回默认回复
  return knowledgeBase.default.answer
}

// 获取当前时间
const getCurrentTime = () => {
  const now = new Date()
  return `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 模拟未读消息（可选）
onMounted(() => {
  // 5秒后显示提示
  setTimeout(() => {
    if (!isOpen.value) {
      unreadCount.value = 1
    }
  }, 5000)
})
</script>

<style scoped>
.ai-customer-service {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 9999;
}

/* 悬浮球 */
.service-bubble {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  transition: all 0.3s;
  position: relative;
}

.service-bubble:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 30px rgba(102, 126, 234, 0.6);
}

.service-bubble.has-unread {
  animation: shake 0.5s ease infinite;
}

@keyframes shake {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(-10deg); }
  75% { transform: rotate(10deg); }
}

.service-icon {
  font-size: 28px;
  color: white;
}

.unread-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #f56c6c;
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  border: 2px solid white;
}

.bubble-tip {
  position: absolute;
  bottom: -35px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.75);
  color: white;
  padding: 5px 12px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  opacity: 0;
  transition: opacity 0.3s;
  pointer-events: none;
}

.service-bubble:hover .bubble-tip {
  opacity: 1;
}

/* 聊天窗口 */
.chat-window {
  width: 380px;
  height: 600px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 头部 */
.chat-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.header-title {
  font-size: 16px;
  font-weight: bold;
}

.header-status {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  opacity: 0.9;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #67c23a;
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.header-actions {
  display: flex;
  gap: 8px;
}

.header-actions .el-button {
  color: white;
}

/* 快捷问题 */
.quick-questions {
  padding: 16px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.quick-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
  font-weight: 500;
}

.quick-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-buttons .el-button {
  font-size: 12px;
}

/* 消息列表 */
.chat-messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f5f7fa;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-message .message-content {
  align-items: flex-end;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.5;
  word-wrap: break-word;
}

.user-message .message-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.ai-message .message-bubble {
  background: white;
  color: #303133;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-bottom-left-radius: 4px;
}

.message-time {
  font-size: 12px;
  color: #909399;
  padding: 0 8px;
}

/* AI正在输入 */
.typing-indicator {
  background: white;
  padding: 12px 16px;
  border-radius: 12px;
  display: flex;
  gap: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #909399;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

/* 输入框 */
.chat-input {
  padding: 16px;
  background: white;
  border-top: 1px solid #e4e7ed;
}

/* 底部提示 */
.chat-footer {
  padding: 12px 16px;
  background: #f5f7fa;
  text-align: center;
  border-top: 1px solid #e4e7ed;
}

.chat-footer .el-text {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

/* 消息内容样式 */
.answer-content {
  font-size: 14px;
}

.answer-content p {
  margin: 8px 0;
}

.answer-content strong {
  color: #409eff;
  font-weight: 600;
}

.answer-content ul,
.answer-content ol {
  margin: 8px 0;
  padding-left: 20px;
}

.answer-content li {
  margin: 6px 0;
  line-height: 1.6;
}

/* 动画 */
.bounce-enter-active {
  animation: bounce-in 0.5s;
}

.bounce-leave-active {
  animation: bounce-in 0.3s reverse;
}

@keyframes bounce-in {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s;
}

.slide-up-enter-from {
  transform: translateY(100%);
  opacity: 0;
}

.slide-up-leave-to {
  transform: translateY(100%);
  opacity: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .chat-window {
    width: 100vw;
    height: 100vh;
    border-radius: 0;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
  }

  .service-bubble {
    bottom: 20px;
    right: 20px;
    width: 50px;
    height: 50px;
  }

  .service-icon {
    font-size: 24px;
  }
}
</style>
