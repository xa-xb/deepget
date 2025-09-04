<template>
    <Teleport to="body">
        <Transition enter-active-class="transition-opacity ease-out duration-200" enter-from-class="opacity-0"
            enter-to-class="opacity-100" leave-active-class="transition-opacity ease-in duration-200"
            leave-from-class="opacity-100" leave-to-class="opacity-0">
            <div v-if="isOpen"
                class="fixed inset-0 z-21000 flex items-start justify-center overflow-y-auto bg-black/50 p-4 pt-16"
                @click.self="close">
                <Transition enter-active-class="transition ease-out duration-200" enter-from-class="opacity-0 scale-95"
                    enter-to-class="opacity-100 scale-100" leave-active-class="transition ease-in duration-200"
                    leave-from-class="opacity-100 scale-100" leave-to-class="opacity-0 scale-95">
                    <div v-if="isOpen" class="relative z-21100 w-full max-w-2xl rounded-lg bg-white shadow-xl">
                        <!-- Tabs Navigation & Header -->
                        <div class="flex items-center justify-between rounded-t-lg bg-gray-100 p-3">
                            <nav class="flex space-x-2">
                                <button v-for="tab in tabs" :key="tab.id"
                                    class="cursor-pointer whitespace-nowrap rounded-md px-3 py-1.5 text-base font-medium transition-colors duration-150"
                                    :class="{
                                        'bg-white text-gray-900 font-semibold shadow-sm': activeTab === tab.id,
                                        'text-gray-500 hover:bg-white hover:text-gray-800': activeTab !== tab.id,
                                    }" @click="activeTab = tab.id">
                                    {{ tab.name }}
                                </button>
                            </nav>
                            <button
                                class="ml-4 cursor-pointer rounded-full p-1 text-gray-400 hover:bg-gray-200 hover:text-gray-600"
                                @click="close">
                                <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M6 18L18 6M6 6l12 12" />
                                </svg>
                            </button>
                        </div>

                        <!-- Tab Content -->
                        <div class="p-6 pt-2">
                            <!-- General Settings -->
                            <div v-if="activeTab === 'general'">
                                <h3 class="text-lg font-semibold text-gray-800">通用设置</h3>
                                <div class="mt-4 space-y-4">
                                    <div class="flex items-center justify-between">
                                        <div>
                                            <h4 class="font-medium text-gray-700">语言</h4>
                                            <p class="text-sm text-gray-500">选择界面显示的语言</p>
                                        </div>
                                        <select v-model="selectedLanguage"
                                            class="rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50">
                                            <option value="zh-CN">简体中文</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <!-- Account Management -->
                            <div v-if="activeTab === 'account'">
                                <h3 class="text-lg font-semibold text-gray-800">账号管理</h3>
                                <div class="mt-4 space-y-4">
                                    <div class="flex items-center justify-between">
                                        <div>
                                            <h4 class="font-medium text-gray-700">用户名</h4>
                                            <p class="text-sm text-gray-500">{{ appStore.username }}</p>
                                        </div>
                                    </div>
                                    <div class="flex items-center justify-between">
                                        <div>
                                            <h4 class="font-medium text-gray-700">邮箱</h4>
                                            <p class="text-sm text-gray-500">{{ appStore.email || '未绑定' }}</p>
                                        </div>
                                        <button @click="openBindEmailModal"
                                            class="cursor-pointer rounded-md bg-blue-500 px-4 py-2 text-sm font-medium text-white hover:bg-blue-600">
                                            绑定邮箱
                                        </button>
                                    </div>
                                </div>
                            </div>

                            <!-- Security -->
                            <div v-if="activeTab === 'security'">
                                <h3 class="text-lg font-semibold text-gray-800">安全设置</h3>
                                <div class="mt-4 space-y-4">
                                    <div class="flex items-center justify-between">
                                        <div>
                                            <h4 class="font-medium text-gray-700">退出当前登录</h4>
                                            <p class="text-sm text-gray-500">仅退出当前正在使用的设备</p>
                                        </div>
                                        <button @click="logoutCurrent"
                                            class="cursor-pointer rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50">
                                            退出登录
                                        </button>
                                    </div>
                                    <div class="flex items-center justify-between">
                                        <div>
                                            <h4 class="font-medium text-gray-700">退出所有登录</h4>
                                            <p class="text-sm text-gray-500">将从所有已登录的设备上退出</p>
                                        </div>
                                        <button @click="logoutAll"
                                            class="cursor-pointer rounded-md border border-red-500 bg-red-500 px-4 py-2 text-sm font-medium text-white hover:bg-red-600">
                                            全部退出
                                        </button>
                                    </div>
                                    <div class="flex items-center justify-between">
                                        <div>
                                            <h4 class="font-medium text-gray-700">删除所有对话</h4>
                                            <p class="text-sm text-gray-500">将永久删除所有对话记录</p>
                                        </div>
                                        <button @click="deleteAllConversations"
                                            class="cursor-pointer rounded-md border border-yellow-400 bg-yellow-300 px-4 py-2 text-sm font-medium text-gray-800 hover:bg-yellow-400">
                                            全部删除
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </Transition>
            </div>
        </Transition>
    </Teleport>

    <!-- Bind Email Modal -->
    <Modal :is-open="isBindEmailModalOpen" @close="closeBindEmailModal">
        <template #header>
            <h3 class="text-lg font-semibold text-gray-800">绑定邮箱</h3>
        </template>
        <template #default>
            <div class="mt-4 space-y-4">
                <div>
                    <label for="email" class="block text-sm font-medium text-gray-700">邮箱地址</label>
                    <div class="mt-1 flex items-center space-x-2">
                        <input type="email" inputmode="email" v-model="emailToBind" id="email" autocomplete="off"
                            class="flex-grow appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                            placeholder="邮箱地址">
                        <button @click="sendEmailVerificationCode" :disabled="isSendingCode"
                            class="w-32 justify-center rounded-md border border-transparent bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50">
                            {{ isSendingCode ? `${countdown}s后重发` : '发送验证码' }}
                        </button>
                    </div>
                </div>
                <div>
                    <label for="email-code" class="block text-sm font-medium text-gray-700">邮箱验证码</label>
                    <input type="text" inputmode="numeric" v-model="emailCode" id="email-code" autocomplete="off"
                        class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                        placeholder="邮箱验证码">
                </div>
            </div>
        </template>
        <template #footer>
            <div class="flex justify-end space-x-4">
                <button @click="closeBindEmailModal"
                    class="w-24 justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2">
                    取消
                </button>
                <button @click="submitBindEmail"
                    class="w-24 justify-center rounded-md border border-transparent bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2">
                    确认绑定
                </button>
            </div>
        </template>
    </Modal>
</template>

<script setup lang="ts">
import { ref, watchEffect } from 'vue'
import { useAppStore } from '@/stores/app'
import { useRouter } from 'vue-router';
import Modal from './Modal.vue';
import { settingApi } from '@/api/setting';
import { mainApi } from '@/api/main';
import { aiApi } from '@/api/ai'
import { toast } from 'vue3-toastify'

const appStore = useAppStore()
const router = useRouter()
defineProps<{
    isOpen: boolean;
}>();

const emit = defineEmits(['close']);

const activeTab = ref('general');
const tabs = [
    { id: 'general', name: '通用设置' },
    { id: 'account', name: '账号管理' },
    { id: 'security', name: '安全' },
];

const selectedLanguage = ref('zh-CN');

const isBindEmailModalOpen = ref(false);
const emailToBind = ref('');
const emailCode = ref('');
const isSendingCode = ref(false);
const countdown = ref(60);
let intervalId: number | undefined;

const openBindEmailModal = () => {
    emailToBind.value = '';
    emailCode.value = '';
    isBindEmailModalOpen.value = true;
};

const closeBindEmailModal = () => {
    isBindEmailModalOpen.value = false;
    if (intervalId) {
        clearInterval(intervalId);
        intervalId = undefined;
    }
    isSendingCode.value = false;
    countdown.value = 60;
};

const sendEmailVerificationCode = async () => {
    if (!emailToBind.value) {
        toast.warn('请输入邮箱地址');
        return;
    }
    isSendingCode.value = true;
    try {
        await mainApi.sendEmailCode({ name: 'bind_email', email: emailToBind.value })
        intervalId = window.setInterval(() => {
            countdown.value--;
            if (countdown.value === 0) {
                if (intervalId) {
                    clearInterval(intervalId);
                    intervalId = undefined;
                }
                isSendingCode.value = false;
                countdown.value = 60;
            }
        }, 1000);
    } catch (error) {
        console.error(error);
        isSendingCode.value = false;
    }
};

const submitBindEmail = async () => {
    if (!emailToBind.value || !emailCode.value) {
        toast.warn('请输入邮箱和邮箱验证码');
        return;
    }
    try {
        await settingApi.bindEmail({ email: emailToBind.value, code: emailCode.value });
        await appStore.initWithProfilePage();
        closeBindEmailModal();
        toast.success('邮箱绑定成功')
    } catch (error) {
        console.error(error);
    }
};

const close = () => {
    emit('close');
};

const handleKeydown = (e: KeyboardEvent) => {
    if (e.key === 'Escape') {
        close();
    }
};

watchEffect((onInvalidate) => {
    if (document) {
        document.addEventListener('keydown', handleKeydown);
    }

    onInvalidate(() => {
        if (document) {
            document.removeEventListener('keydown', handleKeydown);
        }
    });
});

const logoutCurrent = async () => {
    await appStore.signOut()
    router.push('/sign_in')
};

const logoutAll = async () => {
    if (confirm("真的要从所有设备上退出么？")) {
        await appStore.signOutAll()
        router.push('/sign_in')
    }
};

const deleteAllConversations = async () => {
    if (confirm("真的要删除所有对话记录么？该操作不可恢复！")) {
        try {
            await aiApi.deleteAllThreads()
            await appStore.updateThreads();
            appStore.setThreadUuid(null);
            appStore.clearMessages();
            toast.success('所有对话记录已成功删除');
            close();
        } catch (error) {
            console.error(error)
        }
    }
};
</script>
