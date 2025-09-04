<template>
    <div class="h-full bg-gray-50 flex flex-col justify-center py-8 sm:px-6 lg:px-8">
        <div class="sm:mx-auto sm:w-full sm:max-w-md">
            <h2 class="mt-4 text-center text-3xl font-extrabold text-gray-900 cursor-pointer" @click="handleTitleClick">
                {{ t('forgotPassword.title') }}
            </h2>
        </div>

        <div class="mt-6 sm:mx-auto sm:w-full sm:max-w-md">
            <div class="bg-white py-6 px-4 shadow sm:rounded-lg sm:px-10">
                <form class="space-y-4" @submit.prevent="handleForgotPassword">
                    <div>
                        <label for="email" class="block text-sm font-medium text-gray-700">
                            {{ t('forgotPassword.email') }}
                        </label>
                        <div class="mt-1">
                            <input id="email" v-model="email" type="email" required
                                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                :placeholder="t('forgotPassword.emailPlaceholder')" autocomplete="off">
                        </div>
                    </div>

                    <div>
                        <label for="emailCode" class="block text-sm font-medium text-gray-700">
                            {{ t('forgotPassword.emailCode') }}
                        </label>
                        <div class="mt-1 flex items-center">
                            <input id="emailCode" v-model="emailCode" type="text" inputmode="numeric" required
                                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                :placeholder="t('forgotPassword.emailCodePlaceholder')" autocomplete="off">
                            <button type="button" @click="openCaptchaModal" :disabled="countdown > 0"
                                class="ml-2 w-40 flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                                :class="{ 'cursor-not-allowed': countdown > 0 }">
                                <span v-if="countdown > 0">{{ countdown }}s</span>
                                <span v-else>{{ t('forgotPassword.sendCode') }}</span>
                            </button>
                        </div>
                    </div>

                    <div>
                        <label for="newPassword" class="block text-sm font-medium text-gray-700">
                            {{ t('forgotPassword.newPassword') }}
                        </label>
                        <div class="mt-1">
                            <input id="newPassword" v-model="newPassword" type="password" required
                                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                :placeholder="t('forgotPassword.newPasswordPlaceholder')">
                        </div>
                    </div>
                    <div>
                        <label for="confirmPassword" class="block text-sm font-medium text-gray-700">
                            {{ t('forgotPassword.confirmPassword') }}
                        </label>
                        <div class="mt-1">
                            <input id="confirmPassword" v-model="confirmPassword" type="password" required
                                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                :placeholder="t('forgotPassword.confirmPasswordPlaceholder')">
                        </div>
                    </div>

                    <div>
                        <button type="submit" :disabled="loading"
                            class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                            <svg v-if="loading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
                                xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor"
                                    stroke-width="4"></circle>
                                <path class="opacity-75" fill="currentColor"
                                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z">
                                </path>
                            </svg>
                            {{ loading ? t('common.saving') : t('forgotPassword.resetPassword') }}
                        </button>
                    </div>

                    <div class="text-sm text-center">
                        <router-link to="/sign_in" class="font-medium text-blue-600 hover:text-blue-500">
                            {{ t('forgotPassword.backToLogin') }}
                        </router-link>
                    </div>
                </form>
            </div>
        </div>
        <Modal :is-open="isCaptchaModalOpen" @close="isCaptchaModalOpen = false" max-width="sm">
            请输入图片中的验证码
            <form @submit.prevent="handleSendCode">
                <div class="flex items-center space-x-2">
                    <img :src="captchaUrl" alt="Captcha" @click="refreshCaptcha"
                        class="h-10 cursor-pointer border border-gray-300 rounded-md">
                    <input id="captcha" v-model="captcha" type="text" inputmode="numeric" required
                        class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                        :placeholder="t('forgotPassword.captchaPlaceholder')" autocomplete="off">
                </div>
                <div class="flex justify-end mt-4">
                    <button type="button" @click="isCaptchaModalOpen = false"
                        class="mr-2 py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                        {{ t('common.cancel') }}
                    </button>
                    <button type="submit" :disabled="isSendingCode"
                        class="py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                        <svg v-if="isSendingCode" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
                            xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4">
                            </circle>
                            <path class="opacity-75" fill="currentColor"
                                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z">
                            </path>
                        </svg>
                        {{ isSendingCode ? t('forgotPassword.sending') : t('forgotPassword.sendCode') }}
                    </button>
                </div>
            </form>
        </Modal>
        <Modal :is-open="isSuccessModalOpen" @close="handleSuccessModalClose" max-width="sm">
            <div class="text-center">
                <h3 class="text-lg leading-6 font-medium text-gray-900">
                    {{ t('forgotPassword.resetSuccess') }}
                </h3>
                <div class="mt-4">
                    <button type="button" @click="handleSuccessModalClose"
                        class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:text-sm">
                        {{ t('common.ok') }}
                    </button>
                </div>
            </div>
        </Modal>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'
import { mainApi } from '@/api/main'
import { rsaEncrypt } from '@/utils'
import Modal from '@/components/Modal.vue'

const { t } = useI18n()
const router = useRouter()
const email = ref('')
const captcha = ref('')
const captchaUrl = ref('')
const isCaptchaModalOpen = ref(false)
const isSuccessModalOpen = ref(false)
const countdown = ref(0)
let timer: any = undefined

const openCaptchaModal = () => {
    if (!email.value) {
        toast.warn(t('forgotPassword.fillEmail'))
        return
    }
    if (countdown.value > 0) return
    captcha.value = ''
    refreshCaptcha()
    isCaptchaModalOpen.value = true
}

function refreshCaptcha() {
    captchaUrl.value = '/api/v1/main/captcha?t=' + Date.now()
}
const emailCode = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const isSendingCode = ref(false)

onMounted(refreshCaptcha)

onUnmounted(() => {
    if (timer) {
        clearInterval(timer)
    }
})

const startCountdown = () => {
    countdown.value = 60
    timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
            clearInterval(timer)
        }
    }, 1000)
}

const handleSendCode = async () => {
    
    if (!email.value || !captcha.value) {
        toast.warn(t('forgotPassword.fillCaptcha'))
        return
    }
    try {
        isSendingCode.value = true
        await mainApi.sendEmailCode({
            email: email.value,
            captcha: captcha.value,
            name: "reset_password"
        })
        toast.success(t('forgotPassword.sendCodeSuccess'))
        isCaptchaModalOpen.value = false
        startCountdown()
    } catch (err: any) {
    } finally {
        isSendingCode.value = false
    }
}

const handleForgotPassword = async () => {
    if (!emailCode.value) {
        toast.warn(t('forgotPassword.fillEmailCode'))
        return
    }
    if (newPassword.value !== confirmPassword.value) {
        toast.error(t('forgotPassword.passwordMismatch'))
        return
    }
    if (!newPassword.value) {
        toast.error(t('forgotPassword.fillNewPassword'))
        return
    }
    try {
        loading.value = true
        await mainApi.resetPassword({
            email: email.value,
            code: emailCode.value,
            password: await rsaEncrypt(newPassword.value),
        })
        isSuccessModalOpen.value = true
    } catch (err: any) {
    } finally {
        loading.value = false
    }
}

const handleSuccessModalClose = () => {
    isSuccessModalOpen.value = false
    router.push('/sign_in')
}

const handleTitleClick = () => {
    toast.info(t('forgotPassword.title'))
}
</script>

