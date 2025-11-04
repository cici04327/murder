<template>
  <div class="profile-container">
    <el-card>
      <template #header>
        <h2>个人信息</h2>
      </template>
      
      <div class="profile-content">
        <div class="avatar-section">
          <el-avatar :src="userStore.avatar" :size="120" />
          <el-button type="primary" size="small" @click="showAvatarDialog = true">
            修改头像
          </el-button>
        </div>
        
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          class="profile-form"
        >
          <el-form-item label="用户名">
            <el-input v-model="form.username" disabled />
          </el-form-item>
          
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="form.nickname" placeholder="请输入昵称" />
          </el-form-item>
          
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          
          <el-form-item label="性别">
            <el-radio-group v-model="form.gender">
              <el-radio label="男">男</el-radio>
              <el-radio label="女">女</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleUpdate" :loading="updating">
              保存修改
            </el-button>
            <el-button @click="showPasswordDialog = true">
              修改密码
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    
    <!-- 修改头像对话框 -->
    <el-dialog v-model="showAvatarDialog" title="修改头像" width="400px">
      <el-upload
        class="avatar-uploader"
        action="#"
        :show-file-list="false"
        :before-upload="handleAvatarUpload"
        accept="image/*"
      >
        <img v-if="newAvatar" :src="newAvatar" class="avatar" />
        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
      </el-upload>
      <template #footer>
        <el-button @click="showAvatarDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAvatar">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 修改密码对话框 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdatePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { updateUserInfo, updatePassword, uploadAvatar, completeProfileTask } from '@/api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const formRef = ref(null)
const updating = ref(false)
const showAvatarDialog = ref(false)
const showPasswordDialog = ref(false)
const newAvatar = ref('')

const form = reactive({
  username: '',
  nickname: '',
  phone: '',
  gender: '男'
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const loadUserInfo = () => {
  form.username = userStore.username
  form.nickname = userStore.nickname
  form.phone = userStore.userInfo?.phone || ''
  form.gender = userStore.userInfo?.gender || '男'
}

const handleUpdate = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      updating.value = true
      try {
        const res = await updateUserInfo(form)
        if (res.code === 1 || res.code === 200) {
          ElMessage.success('更新成功')
          userStore.updateUserInfo(form)
          
          // 检查完善资料任务
          try {
            const taskRes = await completeProfileTask()
            if (taskRes.code === 1 || taskRes.code === 200) {
              if (taskRes.data?.completed && !taskRes.data?.alreadyCompleted) {
                ElMessage.success('恭喜！完成完善资料任务，获得30积分')
              }
            }
          } catch (err) {
            console.error('检查完善资料任务失败:', err)
          }
        }
      } catch (error) {
        console.error('更新失败:', error)
      } finally {
        updating.value = false
      }
    }
  })
}

const handleAvatarUpload = (file) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    newAvatar.value = e.target.result
  }
  reader.readAsDataURL(file)
  return false
}

const confirmAvatar = async () => {
  if (!newAvatar.value) {
    ElMessage.warning('请选择头像')
    return
  }
  
  try {
    // 这里应该上传到服务器
    ElMessage.success('头像更新成功')
    userStore.updateUserInfo({ avatar: newAvatar.value })
    showAvatarDialog.value = false
  } catch (error) {
    console.error('上传头像失败:', error)
  }
}

const handleUpdatePassword = async () => {
  try {
    const res = await updatePassword(passwordForm)
    if (res.code === 1 || res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      showPasswordDialog.value = false
      userStore.logout()
      router.push('/login')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.profile-content {
  display: flex;
  gap: 40px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.profile-form {
  flex: 1;
}

.avatar-uploader {
  display: flex;
  justify-content: center;
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
}

.avatar-uploader-icon:hover {
  border-color: #409eff;
}
</style>
