<template>
  <div class="info-page agreement-page">
    <!-- 阅读进度条 -->
    <ReadingProgress />
    
    <!-- 面包屑导航 -->
    <InfoPageBreadcrumb />
    
    <!-- 头部横幅 -->
    <div class="hero-banner">
      <div class="hero-content">
        <h1 class="hero-title animate-fade-in-up">
          <span class="gradient-text">用户服务协议</span>
        </h1>
        <p class="hero-subtitle animate-fade-in-up delay-1">
          请仔细阅读以下条款，使用我们的服务即表示您同意本协议
        </p>
        <div class="agreement-meta animate-fade-in-up delay-2">
          <span>📅 更新日期：2024年11月</span>
          <span>📄 版本：V2.0</span>
          <span>⏱️ 预计阅读时间：10分钟</span>
        </div>
      </div>
    </div>
    
    <el-card class="info-card animate-fade-in">
      <template #header>
        <div class="card-header">
          <h2><el-icon><Document /></el-icon> 用户协议详情</h2>
        </div>
      </template>
      
      <div class="info-content agreement-content">
        <div class="agreement-header scroll-reveal">
          <h3>剧本杀在线预约平台用户服务协议</h3>
          <p class="update-time">最后更新时间：2024年1月1日</p>
          <el-alert
            title="重要提示"
            type="warning"
            :closable="false"
            class="notice-alert"
          >
            <p>在使用本平台服务前，请您务必仔细阅读并充分理解本协议的全部内容，特别是加粗部分。如您不同意本协议的任何内容，请勿注册或使用本平台服务。</p>
          </el-alert>
        </div>

        <section class="agreement-section scroll-reveal">
          <h4>一、协议的范围</h4>
          <p>1.1 本协议是您（以下称"用户"）与剧本杀在线预约平台（以下称"本平台"）之间关于用户使用本平台服务所订立的协议。</p>
          <p>1.2 本协议内容包括协议正文及本平台已经发布或将来可能发布的各类规则。所有规则为本协议不可分割的一部分，与协议正文具有同等法律效力。</p>
        </section>

        <section class="agreement-section">
          <h4>二、账号注册与使用</h4>
          <p>2.1 用户在使用本平台服务前需要注册一个账号。账号应当使用手机号码绑定注册。</p>
          <p>2.2 用户应当提供真实、准确、完整的注册信息，并及时更新。如因提供虚假信息造成的一切后果，由用户自行承担。</p>
          <p>2.3 <strong>用户应当妥善保管账号及密码，不得将账号转让、出售或出借给他人使用。因用户保管不善导致账号被盗用的，用户应自行承担责任。</strong></p>
          <p>2.4 用户不得注册多个账号进行恶意操作或牟取不正当利益。</p>
        </section>

        <section class="agreement-section">
          <h4>三、服务内容</h4>
          <p>3.1 本平台提供剧本杀相关的在线预约、信息展示、评价分享等服务。</p>
          <p>3.2 本平台有权根据实际情况随时调整所提供的服务内容，并通过网站公告等方式通知用户。</p>
          <p>3.3 <strong>本平台仅作为预约平台，实际服务由各合作门店提供。因门店服务质量产生的纠纷，用户应与门店协商解决。</strong></p>
        </section>

        <section class="agreement-section">
          <h4>四、用户行为规范</h4>
          <p>4.1 用户在使用本平台服务时，应当遵守国家法律法规，不得利用本平台从事违法违规活动。</p>
          <p>4.2 用户不得发布任何违法、虚假、侵权、骚扰、诽谤他人的信息。</p>
          <p>4.3 <strong>用户不得恶意刷单、刷评价、恶意差评或进行其他不正当竞争行为。</strong></p>
          <p>4.4 用户不得利用技术手段干扰本平台的正常运营。</p>
        </section>

        <section class="agreement-section">
          <h4>五、预约与支付</h4>
          <p>5.1 用户通过本平台预约时，应如实填写预约信息，并按时到店消费。</p>
          <p>5.2 <strong>预约成功后，如需取消，应在规定时间内操作。逾期取消可能产生违约金。具体规则以各门店政策为准。</strong></p>
          <p>5.3 用户应使用合法的支付方式完成支付，不得使用他人账户或盗用他人银行卡信息。</p>
          <p>5.4 因支付过程中用户操作不当或第三方支付平台原因导致的支付失败，本平台不承担责任。</p>
        </section>

        <section class="agreement-section">
          <h4>六、优惠券与积分</h4>
          <p>6.1 本平台可能向用户发放优惠券、积分等优惠。</p>
          <p>6.2 优惠券和积分的获取、使用规则以平台规定为准，不得转让、买卖。</p>
          <p>6.3 优惠券和积分均有有效期，过期自动失效。</p>
        </section>

        <section class="agreement-section">
          <h4>七、知识产权</h4>
          <p>7.1 本平台的所有内容，包括但不限于文字、图片、标识、界面设计、软件等，均属于本平台或相关权利人所有。</p>
          <p>7.2 <strong>未经本平台书面许可，用户不得擅自复制、传播、修改本平台的任何内容。</strong></p>
          <p>7.3 用户在本平台发布的内容，应确保拥有相应权利，如侵犯他人知识产权，用户应自行承担法律责任。</p>
        </section>

        <section class="agreement-section">
          <h4>八、隐私保护</h4>
          <p>8.1 本平台重视用户隐私保护，将按照《隐私政策》的规定收集、使用、存储用户信息。</p>
          <p>8.2 <strong>本平台承诺不会向第三方出售或出租用户个人信息，但法律法规另有规定的除外。</strong></p>
          <p>8.3 用户理解并同意，为提供更好的服务，本平台可能需要收集用户的位置信息、联系方式等。</p>
        </section>

        <section class="agreement-section">
          <h4>九、免责声明</h4>
          <p>9.1 本平台对服务的及时性、安全性、准确性不作任何保证。</p>
          <p>9.2 <strong>因不可抗力（包括但不限于自然灾害、政府行为、黑客攻击等）导致的服务中断，本平台不承担责任。</strong></p>
          <p>9.3 用户因使用本平台服务与第三方产生纠纷的，应自行解决，本平台不承担责任。</p>
          <p>9.4 本平台对用户上传或发布的内容不承担审查义务，如发现违法违规内容，有权随时删除。</p>
        </section>

        <section class="agreement-section">
          <h4>十、协议的变更</h4>
          <p>10.1 本平台有权根据需要修改本协议，修改后的协议将在平台公布。</p>
          <p>10.2 <strong>如用户不同意修改后的协议，应立即停止使用本平台服务。继续使用则视为接受修改后的协议。</strong></p>
        </section>

        <section class="agreement-section">
          <h4>十一、法律适用与争议解决</h4>
          <p>11.1 本协议的订立、执行和解释及争议的解决均适用中华人民共和国法律。</p>
          <p>11.2 如双方就本协议内容或其执行发生任何争议，应友好协商解决；协商不成的，任何一方均可向本平台所在地人民法院提起诉讼。</p>
        </section>

        <section class="agreement-section">
          <h4>十二、其他</h4>
          <p>12.1 本协议的标题仅为方便阅读而设，不影响本协议的解释。</p>
          <p>12.2 如本协议的任何条款被认定为无效，不影响其他条款的效力。</p>
          <p>12.3 本平台对本协议拥有最终解释权。</p>
        </section>

        <div class="agreement-footer">
          <el-alert
            type="success"
            :closable="false"
          >
            <p>如您对本协议有任何疑问，请联系我们的客服：400-123-4567</p>
          </el-alert>
        </div>
      </div>
    </el-card>
    
    <!-- 分享按钮 -->
    <ShareButtons />
    
    <!-- 返回顶部 -->
    <BackToTop />
    
    <!-- 快捷导航 -->
    <PageAnchor :anchors="anchorList" />
    
    <!-- 主题切换器 -->
    <ThemeSwitcher />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Document } from '@element-plus/icons-vue'
import InfoPageBreadcrumb from '@/components/InfoPageBreadcrumb.vue'
import ShareButtons from '@/components/ShareButtons.vue'
import BackToTop from '@/components/BackToTop.vue'
import ReadingProgress from '@/components/ReadingProgress.vue'
import PageAnchor from '@/components/PageAnchor.vue'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'
import { useScrollReveal } from '@/composables/useScrollReveal'
import { useTheme } from '@/composables/useTheme'

// 初始化
useScrollReveal()
const { loadTheme } = useTheme()
loadTheme()

const anchorList = ref([
  '一、协议的范围',
  '二、账号注册与使用',
  '三、服务内容',
  '四、用户行为规范',
  '五、预约与支付',
  '六、优惠券与积分',
  '七、知识产权',
  '八、隐私保护',
  '九、免责声明',
  '十、协议的变更',
  '十一、法律适用与争议解决',
  '十二、其他'
])
</script>

<style scoped>
@import '@/styles/info-enhanced.css';
@import '@/styles/info-theme.css';

/* 协议元信息 */
.agreement-meta {
  display: flex;
  gap: 30px;
  justify-content: center;
  margin-top: 30px;
  font-size: 14px;
}

.agreement-meta span {
  padding: 8px 16px;
  background: rgba(255,255,255,0.2);
  border-radius: 20px;
  backdrop-filter: blur(10px);
}

/* 协议内容样式 */
.agreement-content h3 {
  font-size: 22px;
  color: #2c3e50;
  margin: 30px 0 15px;
  padding-left: 15px;
  border-left: 4px solid #667eea;
}

.agreement-content h4 {
  font-size: 18px;
  color: #34495e;
  margin: 20px 0 10px;
}

.agreement-content p {
  line-height: 1.8;
  color: #555;
  margin-bottom: 15px;
  text-align: justify;
}

.agreement-content ul {
  padding-left: 30px;
  margin: 15px 0;
}

.agreement-content li {
  line-height: 1.8;
  color: #555;
  margin-bottom: 8px;
}

/* 重要提示框 */
.important-notice {
  background: linear-gradient(135deg, #fff3cd 0%, #fff8e1 100%);
  border-left: 4px solid #ffc107;
  padding: 20px;
  margin: 20px 0;
  border-radius: 8px;
}

.important-notice h4 {
  color: #856404;
  margin-bottom: 10px;
}

/* 协议接受按钮 */
.agreement-actions {
  text-align: center;
  margin-top: 40px;
  padding: 30px;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-radius: 12px;
}

.info-page {
  padding: 20px;
  max-width: 1000px;
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

.agreement-content {
  line-height: 2;
  color: #606266;
}

.agreement-header h3 {
  text-align: center;
  font-size: 22px;
  color: #303133;
  margin-bottom: 10px;
}

.update-time {
  text-align: center;
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
}

.notice-alert {
  margin-bottom: 30px;
}

.agreement-section {
  margin-bottom: 30px;
}

.agreement-section h4 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 15px;
  padding-left: 10px;
  border-left: 4px solid #409EFF;
}

.agreement-section p {
  margin: 10px 0;
  font-size: 15px;
  text-indent: 0;
  padding-left: 20px;
}

.agreement-section p strong {
  color: #F56C6C;
  font-weight: bold;
}

.agreement-footer {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 2px solid #EBEEF5;
}
</style>
