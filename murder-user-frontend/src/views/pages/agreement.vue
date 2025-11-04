<template>
  <div class="page-container">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <el-icon><Document /></el-icon>
          <span>用户协议</span>
        </div>
      </template>
      
      <div class="page-content">
        <div class="agreement-meta">
          <p>生效日期：2024年1月1日</p>
          <p>更新日期：2024年1月1日</p>
        </div>
        
        <div class="agreement-section">
          <h3>一、协议的接受</h3>
          <p>
            欢迎您使用剧本杀在线预约平台（以下简称"本平台"）。在使用本平台服务之前，请您仔细阅读并充分理解本协议的全部内容。
            一旦您注册成为本平台用户或使用本平台服务，即表示您已充分理解并完全同意本协议的全部内容。
          </p>
        </div>
        
        <div class="agreement-section">
          <h3>二、账号注册与使用</h3>
          <p><strong>2.1 账号注册</strong></p>
          <ul>
            <li>用户应提供真实、准确、完整的个人信息</li>
            <li>每个手机号只能注册一个账号</li>
            <li>用户应妥善保管账号和密码，对账号下的所有行为负责</li>
          </ul>
          
          <p><strong>2.2 账号使用</strong></p>
          <ul>
            <li>账号仅供本人使用，不得转让、出售或出借</li>
            <li>如发现账号被盗用，应立即通知平台</li>
            <li>平台有权对违规账号采取限制、冻结或注销等措施</li>
          </ul>
        </div>
        
        <div class="agreement-section">
          <h3>三、平台服务</h3>
          <p><strong>3.1 服务内容</strong></p>
          <ul>
            <li>提供剧本杀门店信息浏览服务</li>
            <li>提供剧本预约和管理服务</li>
            <li>提供用户评价和社交互动服务</li>
            <li>提供优惠券和积分奖励服务</li>
          </ul>
          
          <p><strong>3.2 服务变更</strong></p>
          <ul>
            <li>平台有权根据业务发展需要调整服务内容</li>
            <li>平台会提前通知用户重大服务变更</li>
            <li>用户有权选择接受或停止使用服务</li>
          </ul>
        </div>
        
        <div class="agreement-section">
          <h3>四、用户行为规范</h3>
          <p>用户在使用本平台服务时，应遵守以下规范：</p>
          <ul>
            <li>遵守国家法律法规和社会公德</li>
            <li>不发布违法、虚假、侵权或不当信息</li>
            <li>不恶意刷单、刷评分或进行其他作弊行为</li>
            <li>不利用平台进行任何违法活动</li>
            <li>尊重其他用户和门店的合法权益</li>
            <li>如实填写预约信息，按时履约</li>
          </ul>
        </div>
        
        <div class="agreement-section">
          <h3>五、预约与支付</h3>
          <p><strong>5.1 预约规则</strong></p>
          <ul>
            <li>用户应如实填写预约信息</li>
            <li>预约成功后应按时到店，如需取消应提前通知</li>
            <li>多次无故爽约可能导致账号受限</li>
          </ul>
          
          <p><strong>5.2 支付与退款</strong></p>
          <ul>
            <li>支付金额以订单确认页面显示为准</li>
            <li>退款规则依据门店政策和平台规定执行</li>
            <li>特殊情况下的退款纠纷由平台协调处理</li>
          </ul>
        </div>
        
        <div class="agreement-section">
          <h3>六、隐私保护</h3>
          <p>
            本平台高度重视用户隐私保护，具体内容请参阅《隐私政策》。主要包括：
          </p>
          <ul>
            <li>收集用户必要的个人信息用于提供服务</li>
            <li>采取安全措施保护用户信息不被泄露</li>
            <li>不向第三方出售或泄露用户个人信息</li>
            <li>用户有权查看、修改或删除个人信息</li>
          </ul>
        </div>
        
        <div class="agreement-section">
          <h3>七、知识产权</h3>
          <p>
            本平台的所有内容，包括但不限于文字、图片、音频、视频、软件、程序、版面设计等，
            均受著作权、商标权及其他法律保护。未经平台许可，用户不得复制、传播或用于商业用途。
          </p>
        </div>
        
        <div class="agreement-section">
          <h3>八、免责声明</h3>
          <ul>
            <li>平台仅提供信息展示和预约服务，不对门店的服务质量负责</li>
            <li>因不可抗力导致的服务中断，平台不承担责任</li>
            <li>用户与门店之间的纠纷应自行协商解决</li>
            <li>平台对用户发布的内容不承担审查义务</li>
          </ul>
        </div>
        
        <div class="agreement-section">
          <h3>九、协议修改</h3>
          <p>
            本平台有权根据需要修改本协议内容。修改后的协议将在平台上公布，
            公布之日起生效。如用户不同意修改内容，应停止使用本平台服务。
            继续使用服务即视为接受修改后的协议。
          </p>
        </div>
        
        <div class="agreement-section">
          <h3>十、联系我们</h3>
          <p>如您对本协议有任何疑问，请通过以下方式联系我们：</p>
          <ul>
            <li>客服电话：400-123-4567</li>
            <li>客服邮箱：service@jubensha.com</li>
            <li>在线客服：点击右下角AI客服图标</li>
          </ul>
        </div>
        
        <div class="agreement-footer">
          <el-alert type="info" :closable="false">
            <p>
              <strong>重要提示：</strong>
              请您仔细阅读并充分理解本协议。如有任何疑问，请在使用服务前联系我们。
              使用本平台服务即表示您同意本协议的全部内容。
            </p>
          </el-alert>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Document } from '@element-plus/icons-vue'
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

.agreement-meta {
  padding: 15px;
  background: #f0f9ff;
  border-left: 3px solid #409eff;
  margin-bottom: 30px;
}

.agreement-meta p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.agreement-section {
  margin-bottom: 35px;
}

.agreement-section h3 {
  margin: 0 0 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
  color: #333;
  font-size: 18px;
}

.agreement-section p {
  margin: 10px 0;
  line-height: 1.8;
  color: #666;
  font-size: 15px;
}

.agreement-section ul {
  margin: 15px 0;
  padding-left: 25px;
}

.agreement-section li {
  margin: 8px 0;
  line-height: 1.8;
  color: #666;
  font-size: 14px;
}

.agreement-section strong {
  color: #333;
  font-weight: 600;
}

.agreement-footer {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px dashed #ddd;
}

.agreement-footer p {
  margin: 0;
  line-height: 1.8;
}
</style>

