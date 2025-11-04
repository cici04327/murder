<template>
  <div class="slider-captcha">
    <div class="captcha-header">
      <span class="captcha-title">{{ verified ? '✓ 验证成功' : '请完成安全验证' }}</span>
      <el-button 
        text 
        @click="refresh" 
        :icon="RefreshRight"
        size="small"
        v-if="!verified"
      >
        刷新
      </el-button>
    </div>
    
    <div class="captcha-container" :class="{ verified: verified, failed: showError }">
      <!-- 背景图 -->
      <div class="captcha-canvas">
        <canvas ref="bgCanvas" :width="canvasWidth" :height="canvasHeight"></canvas>
        <!-- 滑块拼图 -->
        <canvas 
          ref="blockCanvas" 
          :width="blockSize" 
          :height="canvasHeight"
          :style="{ left: blockLeft + 'px' }"
          class="block-canvas"
        ></canvas>
      </div>
      
      <!-- 滑动条 -->
      <div class="slider-track" :class="{ verified: verified, failed: showError }">
        <div class="slider-fill" :style="{ width: sliderLeft + 'px' }"></div>
        <div 
          class="slider-button"
          :style="{ left: sliderLeft + 'px' }"
          @mousedown="handleDragStart"
          @touchstart="handleDragStart"
        >
          <el-icon v-if="!verified && !showError"><DArrowRight /></el-icon>
          <el-icon v-if="verified" color="#67c23a"><Check /></el-icon>
          <el-icon v-if="showError" color="#f56c6c"><Close /></el-icon>
        </div>
        <span class="slider-text" v-if="!verified && !showError">
          {{ isDragging ? '继续拖动滑块' : '向右拖动滑块填充拼图' }}
        </span>
        <span class="slider-text success" v-if="verified">验证成功</span>
        <span class="slider-text error" v-if="showError">验证失败，请重试</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { RefreshRight, DArrowRight, Check, Close } from '@element-plus/icons-vue'

const props = defineProps({
  // 验证成功的误差范围（像素）
  tolerance: {
    type: Number,
    default: 5
  }
})

const emit = defineEmits(['success', 'fail', 'refresh'])

// Canvas 尺寸
const canvasWidth = 350
const canvasHeight = 200
const blockSize = 50

// 状态
const bgCanvas = ref(null)
const blockCanvas = ref(null)
const blockLeft = ref(0)
const sliderLeft = ref(0)
const isDragging = ref(false)
const verified = ref(false)
const showError = ref(false)

// 拼图位置
const blockX = ref(0)
const blockY = ref(0)

// 鼠标起始位置
const startX = ref(0)

// 背景图片
const bgImages = [
  'https://images.unsplash.com/photo-1557683316-973673baf926?w=400',
  'https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=400',
  'https://images.unsplash.com/photo-1557682250-33bd709cbe85?w=400',
  'https://images.unsplash.com/photo-1579547945413-497e1b99dac0?w=400',
  'https://images.unsplash.com/photo-1557682224-5b8590cd9ec5?w=400'
]

// 初始化验证码
const init = () => {
  verified.value = false
  showError.value = false
  sliderLeft.value = 0
  blockLeft.value = 0
  
  // 随机生成拼图位置
  blockX.value = Math.floor(Math.random() * (canvasWidth - blockSize - 50)) + 50
  blockY.value = Math.floor(Math.random() * (canvasHeight - blockSize - 20)) + 10
  
  // 绘制背景和拼图
  drawCaptcha()
}

// 绘制验证码
const drawCaptcha = () => {
  const bgCtx = bgCanvas.value.getContext('2d')
  const blockCtx = blockCanvas.value.getContext('2d')
  
  // 随机选择背景图
  const img = new Image()
  img.crossOrigin = 'anonymous'
  img.src = bgImages[Math.floor(Math.random() * bgImages.length)]
  
  img.onload = () => {
    // 绘制背景
    bgCtx.clearRect(0, 0, canvasWidth, canvasHeight)
    bgCtx.drawImage(img, 0, 0, canvasWidth, canvasHeight)
    
    // 在背景上绘制拼图凹槽（灰色半透明）
    drawPuzzleShape(bgCtx, blockX.value, blockY.value, 'destination-out')
    bgCtx.globalCompositeOperation = 'source-over'
    drawPuzzleOutline(bgCtx, blockX.value, blockY.value)
    
    // 绘制拼图块
    blockCtx.clearRect(0, 0, blockSize, canvasHeight)
    blockCtx.drawImage(img, blockX.value, 0, blockSize, canvasHeight, 0, 0, blockSize, canvasHeight)
    drawPuzzleShape(blockCtx, 0, blockY.value, 'destination-in')
    blockCtx.globalCompositeOperation = 'source-over'
    drawPuzzleOutline(blockCtx, 0, blockY.value)
  }
}

// 绘制拼图形状（带凸起）
const drawPuzzleShape = (ctx, x, y, compositeOperation) => {
  ctx.save()
  ctx.globalCompositeOperation = compositeOperation || 'source-over'
  ctx.beginPath()
  
  const radius = 10
  const protrusion = 8
  
  // 顶边
  ctx.moveTo(x, y)
  ctx.lineTo(x + blockSize / 2 - protrusion, y)
  ctx.arc(x + blockSize / 2, y, protrusion, Math.PI, 0, false)
  ctx.lineTo(x + blockSize, y)
  
  // 右边
  ctx.lineTo(x + blockSize, y + blockSize / 2 - protrusion)
  ctx.arc(x + blockSize, y + blockSize / 2, protrusion, 1.5 * Math.PI, 0.5 * Math.PI, false)
  ctx.lineTo(x + blockSize, y + blockSize)
  
  // 底边
  ctx.lineTo(x + blockSize / 2 + protrusion, y + blockSize)
  ctx.arc(x + blockSize / 2, y + blockSize, protrusion, 0, Math.PI, false)
  ctx.lineTo(x, y + blockSize)
  
  // 左边
  ctx.lineTo(x, y + blockSize / 2 + protrusion)
  ctx.arc(x, y + blockSize / 2, protrusion, 0.5 * Math.PI, 1.5 * Math.PI, false)
  ctx.lineTo(x, y)
  
  ctx.closePath()
  ctx.fill()
  ctx.restore()
}

// 绘制拼图轮廓
const drawPuzzleOutline = (ctx, x, y) => {
  ctx.save()
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.8)'
  ctx.lineWidth = 2
  ctx.beginPath()
  
  const radius = 10
  const protrusion = 8
  
  ctx.moveTo(x, y)
  ctx.lineTo(x + blockSize / 2 - protrusion, y)
  ctx.arc(x + blockSize / 2, y, protrusion, Math.PI, 0, false)
  ctx.lineTo(x + blockSize, y)
  ctx.lineTo(x + blockSize, y + blockSize / 2 - protrusion)
  ctx.arc(x + blockSize, y + blockSize / 2, protrusion, 1.5 * Math.PI, 0.5 * Math.PI, false)
  ctx.lineTo(x + blockSize, y + blockSize)
  ctx.lineTo(x + blockSize / 2 + protrusion, y + blockSize)
  ctx.arc(x + blockSize / 2, y + blockSize, protrusion, 0, Math.PI, false)
  ctx.lineTo(x, y + blockSize)
  ctx.lineTo(x, y + blockSize / 2 + protrusion)
  ctx.arc(x, y + blockSize / 2, protrusion, 0.5 * Math.PI, 1.5 * Math.PI, false)
  ctx.lineTo(x, y)
  
  ctx.stroke()
  ctx.restore()
}

// 开始拖动
const handleDragStart = (e) => {
  if (verified.value) return
  
  isDragging.value = true
  showError.value = false
  startX.value = e.clientX || e.touches[0].clientX
  
  // 添加移动和结束事件监听
  document.addEventListener('mousemove', handleDragMove)
  document.addEventListener('mouseup', handleDragEnd)
  document.addEventListener('touchmove', handleDragMove)
  document.addEventListener('touchend', handleDragEnd)
}

// 拖动中
const handleDragMove = (e) => {
  if (!isDragging.value) return
  
  const moveX = (e.clientX || e.touches[0].clientX) - startX.value
  const maxMove = canvasWidth - blockSize
  
  // 限制滑动范围
  sliderLeft.value = Math.max(0, Math.min(moveX, maxMove))
  blockLeft.value = sliderLeft.value
}

// 结束拖动
const handleDragEnd = () => {
  if (!isDragging.value) return
  
  isDragging.value = false
  
  // 移除事件监听
  document.removeEventListener('mousemove', handleDragMove)
  document.removeEventListener('mouseup', handleDragEnd)
  document.removeEventListener('touchmove', handleDragMove)
  document.removeEventListener('touchend', handleDragEnd)
  
  // 验证位置
  verify()
}

// 验证
const verify = () => {
  const distance = Math.abs(blockLeft.value - blockX.value)
  
  if (distance <= props.tolerance) {
    // 验证成功
    verified.value = true
    showError.value = false
    emit('success', {
      verified: true,
      distance: distance
    })
  } else {
    // 验证失败
    showError.value = true
    emit('fail', {
      verified: false,
      distance: distance
    })
    
    // 2秒后重置
    setTimeout(() => {
      refresh()
    }, 1500)
  }
}

// 刷新验证码
const refresh = () => {
  emit('refresh')
  init()
}

// 重置验证码
const reset = () => {
  init()
}

// 暴露方法给父组件
defineExpose({
  reset,
  refresh
})

onMounted(() => {
  init()
})
</script>

<style scoped>
.slider-captcha {
  width: 100%;
  user-select: none;
}

.captcha-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 0 4px;
}

.captcha-title {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.captcha-container {
  width: 100%;
  background: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  border: 2px solid #e4e7ed;
}

.captcha-container.verified {
  border-color: #67c23a;
  background: #f0f9ff;
}

.captcha-container.failed {
  border-color: #f56c6c;
  background: #fef0f0;
  animation: shake 0.5s;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-10px); }
  75% { transform: translateX(10px); }
}

.captcha-canvas {
  position: relative;
  width: 100%;
  height: 200px;
  background: #fff;
}

.captcha-canvas canvas {
  display: block;
}

.block-canvas {
  position: absolute;
  top: 0;
  left: 0;
  transition: none;
}

.slider-track {
  position: relative;
  height: 50px;
  background: #e4e7ed;
  display: flex;
  align-items: center;
  transition: all 0.3s;
}

.slider-track.verified {
  background: #67c23a;
}

.slider-track.failed {
  background: #f56c6c;
}

.slider-fill {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  background: linear-gradient(90deg, #409eff, #667eea);
  transition: width 0.1s;
  pointer-events: none;
}

.slider-track.verified .slider-fill {
  background: linear-gradient(90deg, #67c23a, #85ce61);
}

.slider-track.failed .slider-fill {
  background: linear-gradient(90deg, #f56c6c, #f78989);
}

.slider-button {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 50px;
  background: #fff;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s;
  z-index: 10;
}

.slider-button:hover {
  background: #f5f7fa;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.slider-button:active {
  transform: translateY(-50%) scale(0.95);
}

.slider-button .el-icon {
  font-size: 24px;
}

.slider-text {
  position: absolute;
  left: 0;
  right: 0;
  text-align: center;
  color: #909399;
  font-size: 14px;
  pointer-events: none;
  z-index: 1;
}

.slider-text.success {
  color: #fff;
}

.slider-text.error {
  color: #fff;
}
</style>
