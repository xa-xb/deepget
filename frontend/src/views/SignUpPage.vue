<template>
    <div class="h-full bg-gray-100 flex flex-col justify-center py-8 sm:px-6 lg:px-8">
        <div class="sm:mx-auto sm:w-full sm:max-w-md">
            <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">注册账号</h2>
        </div>

        <div class="mt-4 sm:mx-auto sm:w-full sm:max-w-md">
            <div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
                <form class="space-y-6" @submit.prevent="handleSubmit">
                    <div>
                        <label for="username" class="block text-sm font-medium text-gray-700">用户名</label>
                        <div class="mt-1">
                            <input id="username" v-model="formData.username" type="text" required
                                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                autocomplete="off" />
                        </div>
                    </div>

                    <div>
                        <label for="password" class="block text-sm font-medium text-gray-700">密码</label>
                        <div class="mt-1">
                            <input id="password" v-model="formData.password" type="password" required
                                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
                        </div>
                    </div>

                    <div>
                        <label for="confirmPassword" class="block text-sm font-medium text-gray-700">重复密码</label>
                        <div class="mt-1">
                            <input id="confirmPassword" v-model="formData.confirmPassword" type="password" required
                                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
                        </div>
                    </div>

                    <div>
                        <label for="captcha" class="block text-sm font-medium text-gray-700">验证码</label>
                        <div class="mt-1 flex w-full">
                            <input id="captcha" v-model="formData.captcha" type="text" required
                                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-l-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                autocomplete="off" inputmode="numeric" />
                            <img :src="captchaUrl" alt="验证码" class="ml-2 cursor-pointer h-10" @click="refreshCaptcha"
                                title="点击刷新验证码" />
                        </div>
                    </div>

                    <div>
                        <button type="submit" :disabled="loading"
                            class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                            <svg v-if="loading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
                                xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor"
                                    stroke-width="4"></circle>
                                <path class="opacity-75" fill="currentColor"
                                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z">
                                </path>
                            </svg>
                            {{ loading ? '注册中' : '注册用户' }}
                        </button>
                    </div>
                </form>

                <div class="mt-6">
                    <div class="relative">
                        <div class="relative flex justify-center text-sm">
                            <span class="px-2 bg-white text-gray-500">
                                已有账号？
                                <router-link to="/sign_in" class="font-medium text-indigo-600 hover:text-indigo-500">
                                    立即登录
                                </router-link>
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { mainApi, NewUserDto } from '@/api/main'
import { toast } from 'vue3-toastify'
import { rsaEncrypt } from '@/utils'
import { useAppStore } from '@/stores/app'

const loading = ref(false)
const appStore = useAppStore()
const router = useRouter()
const formData = reactive({
    username: '',
    password: '',
    confirmPassword: '',
    captcha: ''
})

const captchaUrl = ref('/api/v1/main/captcha?t=' + Date.now())

function refreshCaptcha() {
    captchaUrl.value = '/api/v1/main/captcha?t=' + Date.now()
}

const handleSubmit = async () => {
    if (!formData.captcha) {
        toast.error('请输入验证码')
        return
    }
    if (formData.password !== formData.confirmPassword) {
        toast.error('重复密码错误')
        return
    }

    let postData = { ...formData } as NewUserDto & { confirmPassword?: String }
    delete postData.confirmPassword
    loading.value = true
    postData.password = await rsaEncrypt(postData.password)
    mainApi.signUp(postData).then(async (response: any) => {
        if (response.code === 1) {
            await appStore.initWithProfilePage()
            router.push('/')
        }
    }).catch(response => {
        console.warn(response)
    }).finally(() => {
        loading.value = false
    })
}
onMounted(() => {
  if (appStore.username) {
    router.push('/')
  }
})
</script>