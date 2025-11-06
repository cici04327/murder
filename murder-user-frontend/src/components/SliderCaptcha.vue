<template>
  <div class="slider-captcha">
    <div class="captcha-header">
      <span class="captcha-title">{{ verified ? '✓ 验证成功' : '请完成安全验证' }}</span>
      <el-button 
        circle
        @click="refresh" 
        :icon="RefreshRight"
        size="small"
        class="refresh-btn"
        v-if="!verified"
      />
    </div>
    
    <div class="captcha-container" :class="{ verified: verified, failed: showError }">
      <!-- 背景图 -->
      <div class="captcha-canvas">
        <canvas ref="bgCanvas" :width="canvasWidth" :height="canvasHeight"></canvas>
        <!-- 滑块拼图 -->
        <canvas 
          ref="blockCanvas" 
          :width="blockCanvasWidth" 
          :height="canvasHeight"
          :style="{ left: (blockLeft - 10) + 'px' }"
          class="block-canvas"
        ></canvas>
        <!-- 左侧拼图图标 -->
        <div class="puzzle-icon left">
          <svg viewBox="0 0 40 40" width="40" height="40">
            <path d="M10,10 L10,15 Q10,20 15,20 Q10,20 10,25 L10,30 L15,30 Q20,30 20,25 Q20,30 25,30 L30,30 L30,25 Q30,20 25,20 Q30,20 30,15 L30,10 L25,10 Q20,10 20,15 Q20,10 15,10 Z" 
                  fill="rgba(255,255,255,0.9)" 
                  stroke="rgba(0,0,0,0.2)" 
                  stroke-width="1"/>
          </svg>
        </div>
        <!-- 右侧拼图图标 -->
        <div class="puzzle-icon right">
          <svg viewBox="0 0 40 40" width="40" height="40">
            <path d="M10,10 L10,15 Q10,20 15,20 Q10,20 10,25 L10,30 L15,30 Q20,30 20,25 Q20,30 25,30 L30,30 L30,25 Q30,20 25,20 Q30,20 30,15 L30,10 L25,10 Q20,10 20,15 Q20,10 15,10 Z" 
                  fill="rgba(255,255,255,0.9)" 
                  stroke="rgba(0,0,0,0.2)" 
                  stroke-width="1"/>
          </svg>
        </div>
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
          向右滑动完成验证
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
    default: 3
  }
})

const emit = defineEmits(['success', 'fail', 'refresh'])

// Canvas 尺寸
const canvasWidth = 350
const canvasHeight = 200
const blockSize = 50
const blockCanvasWidth = 70 // 拼图块画布宽度（包含凸起）

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

// 背景图片 - 精选高质量风景图
const bgImages = [
  'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=250&fit=crop', // 山脉风景
  'https://images.unsplash.com/photo-1511884642898-4c92249e20b6?w=400&h=250&fit=crop', // 海滩日落
  'https://images.unsplash.com/photo-1472214103451-9374bd1c798e?w=400&h=250&fit=crop', // 森林小路
  'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400&h=250&fit=crop', // 湖泊倒影
  'https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=400&h=250&fit=crop', // 云海山峰
  'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400&h=250&fit=crop', // 田园风光
  'https://images.unsplash.com/photo-1501594907352-04cda38ebc29?w=400&h=250&fit=crop', // 海边落日
  'https://images.unsplash.com/photo-1433086966358-54859d0ed716?w=400&h=250&fit=crop', // 雪山草地
  'https://images.unsplash.com/photo-1426604966848-d7adac402bff?w=400&h=250&fit=crop', // 森林阳光
  'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=400&h=250&fit=crop'  // 樱花小道
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
    
    // 在背景上绘制拼图凹槽（带凸起的形状）
    drawPuzzleShape(bgCtx, blockX.value, blockY.value, 'destination-out')
    bgCtx.globalCompositeOperation = 'source-over'
    drawPuzzleOutline(bgCtx, blockX.value, blockY.value)
    
    // 绘制拼图块（完整的拼图形状，包含凸起）
    blockCtx.clearRect(0, 0, blockCanvasWidth, canvasHeight)
    // 先绘制完整的图片
    blockCtx.drawImage(img, blockX.value - 10, 0, blockSize + 20, canvasHeight, 0, 0, blockSize + 20, canvasHeight)
    // 使用拼图形状裁剪
    drawPuzzleShape(blockCtx, 10, blockY.value, 'destination-in')
    // 绘制轮廓
    blockCtx.globalCompositeOperation = 'source-over'
    drawPuzzleOutline(blockCtx, 10, blockY.value)
  }
}

// 绘制完整的拼图形状（只有凸起，没有凹陷）
const drawPuzzleShape = (ctx, x, y, compositeOperation) => {
  ctx.save()
  ctx.globalCompositeOperation = compositeOperation || 'source-over'
  ctx.beginPath()
  
  const r = 10 // 凸起的半径
  const PI = Math.PI
  
  // 从左上角开始，顺时针绘制完整的拼图块
  ctx.moveTo(x, y)
  
  // 顶边 - 凸起（向上）
  ctx.lineTo(x + blockSize / 2 - r, y)
  ctx.arc(x + blockSize / 2, y - r, r, PI, 0, false)
  ctx.lineTo(x + blockSize, y)
  
  // 右边 - 凸起（向右）
  ctx.lineTo(x + blockSize, y + blockSize / 2 - r)
  ctx.arc(x + blockSize + r, y + blockSize / 2, r, PI, 0, false)
  ctx.lineTo(x + blockSize, y + blockSize)
  
  // 底边 - 平直（无凸起）
  ctx.lineTo(x, y + blockSize)
  
  // 左边 - 平直（无凸起）
  ctx.lineTo(x, y)
  
  ctx.closePath()
  ctx.fill()
  ctx.restore()
}

// 绘制拼图轮廓
const drawPuzzleOutline = (ctx, x, y) => {
  ctx.save()
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.9)'
  ctx.lineWidth = 2
  ctx.shadowColor = 'rgba(0, 0, 0, 0.5)'
  ctx.shadowBlur = 3
  ctx.beginPath()
  
  const r = 10
  const PI = Math.PI
  
  // 从左上角开始，顺时针绘制轮廓
  ctx.moveTo(x, y)
  
  // 顶边 - 凸起（向上）
  ctx.lineTo(x + blockSize / 2 - r, y)
  ctx.arc(x + blockSize / 2, y - r, r, PI, 0, false)
  ctx.lineTo(x + blockSize, y)
  
  // 右边 - 凸起（向右）
  ctx.lineTo(x + blockSize, y + blockSize / 2 - r)
  ctx.arc(x + blockSize + r, y + blockSize / 2, r, PI, 0, false)
  ctx.lineTo(x + blockSize, y + blockSize)
  
  // 底边 - 平直
  ctx.lineTo(x, y + blockSize)
  
  // 左边 - 平直
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
  // 拼图块画布向左偏移了10px，但拼图形状在画布上从x=10开始
  // 所以实际拼图的左边缘位置就是 blockLeft
  const distance = Math.abs(blockLeft.value - blockX.value)
  
  // 调试信息
  console.log('验证信息:', {
    滑块位置: blockLeft.value,
    目标位置: blockX.value,
    距离差: distance,
    容忍度: props.tolerance,
    是否通过: distance <= props.tolerance
  })
  
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
    
    // 1.5秒后重置
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
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.refresh-btn {
  color: #67c23a;
  border-color: #67c23a;
}

.refresh-btn:hover {
  background: #f0f9ff;
  color: #67c23a;
  border-color: #67c23a;
}

.captcha-container {
  width: 100%;
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
  transition: all 0.3s;
  border: 1px solid #dcdfe6;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
}

.captcha-container.verified {
  border-color: #67c23a;
}

.captcha-container.failed {
  border-color: #f56c6c;
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

.puzzle-icon {
  position: absolute;
  top: 20px;
  z-index: 5;
  opacity: 0.95;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.puzzle-icon.left {
  left: 15px;
}

.puzzle-icon.right {
  right: 15px;
}

.slider-track {
  position: relative;
  height: 50px;
  background: #f7f8fa;
  display: flex;
  align-items: center;
  transition: all 0.3s;
  border-top: 1px solid #e4e7ed;
}

.slider-track.verified {
  background: #f0f9ff;
}

.slider-track.failed {
  background: #fef0f0;
}

.slider-fill {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  background: rgba(103, 194, 58, 0.1);
  transition: width 0.1s;
  pointer-events: none;
}

.slider-track.verified .slider-fill {
  background: rgba(103, 194, 58, 0.2);
}

.slider-track.failed .slider-fill {
  background: rgba(245, 108, 108, 0.2);
}

.slider-button {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 50px;
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 2px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
  transition: all 0.2s;
  z-index: 10;
}

.slider-button:hover {
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.15);
}

.slider-button:active {
  cursor: grabbing;
  transform: translateY(-50%) scale(0.98);
  box-shadow: 0 0 6px rgba(0, 0, 0, 0.15);
}

.slider-track.verified .slider-button {
  background: #67c23a;
  border-color: #67c23a;
}

.slider-track.failed .slider-button {
  background: #f56c6c;
  border-color: #f56c6c;
}

.slider-button .el-icon {
  font-size: 22px;
  color: #909399;
}

.slider-track.verified .slider-button .el-icon,
.slider-track.failed .slider-button .el-icon {
  color: #fff;
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
  color: #67c23a;
  font-weight: 500;
}

.slider-text.error {
  color: #f56c6c;
  font-weight: 500;
}
</style>
