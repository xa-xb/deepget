<template>
  <div class="container">
    <div class="box">
      <div class="login-content-left">
        <img :src="loginLeftPng" />
        <div class="login-content-left-mask">
          <div>{{ systemTitle }}</div>
          <div>{{ systemSubTitle }}</div>
        </div>
      </div>

      <div class="box-inner" v-loading="formLoading">
        <h2 style="text-align: center;">Welcome</h2>
        <el-form class="form">
          <el-input size="large" v-model="form.account" placeholder="account" type="text" maxlength="50">
            <template #prepend>
              <el-icon>
                <User />
              </el-icon>
            </template>
          </el-input>
          <el-input size="large" ref="password" v-model="form.password" :type="passwordType" placeholder="password" name="password"
            maxlength="50" @keydown.enter="submit">
            <template #prepend>
              <el-icon>
                <Lock />
              </el-icon>
            </template>
            <template #append>

              <el-icon @click="passwordTypeChange">
                <Hide v-if="!passwordType" />
                <View v-if="passwordType" />
              </el-icon>
            </template>
          </el-input>

          <el-button type="primary" :disabled="formLoading" @click="submit" style="width: 100%;" size="large">
            sign in
          </el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute, RouteLocationRaw } from 'vue-router'
import { ElMessage } from 'element-plus'
import loginLeftPng from '@/assets/login/left.jpg'
import { useUserStore } from '@/stores/user'
import { rsaEncrypt } from '@/utils'
import { systemTitle, systemSubTitle } from '@/config'
import { SignInRequestDto } from '@/types/main'

// 创建响应式变量
const form = reactive<SignInRequestDto>({
  account: '',
  password: ''
})

const formLoading = ref(false)
const passwordType = ref('password')

// 使用 Vue Router 和 Vuex 的组合式 API
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 检查表单方法
const checkForm = () => {
  return new Promise((resolve, reject) => {
    if (form.account === '') {
      ElMessage.warning('账号不能为空')
      reject('账号不能为空')
    }
    if (form.password === '') {
      ElMessage.warning('密码不能为空')
      reject('密码不能为空')
    }
    resolve(true)
  })
}

// 切换密码显示类型
const passwordTypeChange = () => {
  passwordType.value = passwordType.value === '' ? 'password' : ''
}

// 提交表单方法
const submit = async () => {
  try {
    await checkForm()
    formLoading.value = true
    const passEnc = await rsaEncrypt(form.password)
    const params: SignInRequestDto = {
      account: form.account,
      password: passEnc
    }
    await userStore.signIn(params)
    ElMessage.success('登录成功')
    router.push(route.query.redirect as RouteLocationRaw || '/home')
  } catch {
    formLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
.container {
  position: relative;
  width: 100vw;
  height: 100vh;
  background: #fff url('@/assets/login/bg.png') no-repeat center;
  overflow: hidden;
  background-size: cover;
  cursor: pointer;
  user-select: none;

  .el-icon {
    font-size: 1.2em;
  }

  .box {
    align-items: center;
    width: 1160px;
    display: flex;
    position: absolute;
    left: 50%;
    top: 50%;
    background: white;
    border-radius: 8px;
    transform: translate(-50%, -50%);
    height: 440px;
    overflow: hidden;
    box-shadow: 0 6px 20px 5px rgba(152, 152, 152, 0.1),
      0 16px 24px 2px rgba(117, 117, 117, 0.14);

    .login-content-left {
      position: relative;

      img {
        height: 440px;
      }

      .login-content-left-mask {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        background-image: linear-gradient(rgba(0, 204, 222, 0.8), rgba(51, 132, 224, 0.8));
        text-align: center;
        color: #fff;
        font-size: 1.8rem;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        letter-spacing: 2px;

        div:nth-child(1) {
          font-size: 3.5rem;
          margin-bottom: 1em;
        }
      }
    }

    .box-inner {
      width: 500px;
 
      .form {
        width: 80%;
        margin: 40px auto 15px;

        .el-input {
          margin-bottom: 20px;
        }

        .password-icon {
          cursor: pointer;
          color: #409eff;
        }
      }

      .fixed-top-right {
        position: absolute;
        top: 10px;
        right: 10px;
      }
    }
  }
}

@media screen and (max-width: 1260px) {
  .login-content-left {
    display: none;
  }

  .box {
    width: 500px !important;
    max-width: 500px;
    height: 330px !important;
  }
}

@media screen and (max-width: 750px) {
  .container .box {
    width: 90vw !important;
  }
}
</style>
