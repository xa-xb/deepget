<template>
    <div class="h-full flex flex-col w-full relative" id="chatbox">
        <Settings :is-open="isSettingsOpen" @close="closeSettings" />
        <!-- 消息容器 -->
        <div class="h-full overflow-y-auto text-gray-900" ref="messageContainer">
            <!-- 顶部栏 -->
            <div class="w-full flex items-center bg-gray-50
   absolute top-0 z-20000 p-1 border-b border-gray-200">

                <div class="font-medium flex-1" :class="{
                    'pl-14': !appStore.isSidebarVisible || !appStore.isWideScreen
                }">
                    <model-selector />
                </div>
                <div class="flex-1 flex justify-end items-center">
                    <!-- 用户头像按钮 -->
                    <div class="relative" ref="userMenuWrapperRef">
                        <button @click="isUserMenuOpen = !isUserMenuOpen"
                            class="flex items-center justify-center size-8 rounded-full bg-gradient-to-br from-violet-500 to-fuchsia-500 text-white hover:opacity-90 transition-opacity cursor-pointer">
                            <span class="text-sm font-medium">DG</span>
                        </button>

                        <!-- 下拉菜单 -->
                        <div v-if="isUserMenuOpen"
                            class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-20"
                            ref="userMenuRef">
                            <div class="px-4 py-3 border-b border-gray-100">
                                <div class="font-medium" v-text="appStore.username"></div>
                                <div class="text-sm text-gray-500" v-text="appStore.email || '未设置邮箱'"></div>
                            </div>
                            <button @click="openSettings"
                                class="w-full text-left px-4 py-3 text-sm text-gray-700 hover:bg-gray-100 cursor-pointer flex items-center gap-1">
                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                                    stroke-width="1.5" stroke="currentColor" class="size-6">
                                    <path stroke-linecap="round" stroke-linejoin="round"
                                        d="M9.594 3.94c.09-.542.56-.94 1.11-.94h2.593c.55 0 1.02.398 1.11.94l.213 1.281c.063.374.313.686.645.87.074.04.147.083.22.127.325.196.72.257 1.075.124l1.217-.456a1.125 1.125 0 0 1 1.37.49l1.296 2.247a1.125 1.125 0 0 1-.26 1.431l-1.003.827c-.293.241-.438.613-.43.992a7.723 7.723 0 0 1 0 .255c-.008.378.137.75.43.991l1.004.827c.424.35.534.955.26 1.43l-1.298 2.247a1.125 1.125 0 0 1-1.369.491l-1.217-.456c-.355-.133-.75-.072-1.076.124a6.47 6.47 0 0 1-.22.128c-.331.183-.581.495-.644.869l-.213 1.281c-.09.543-.56.94-1.11.94h-2.594c-.55 0-1.019-.398-1.11-.94l-.213-1.281c-.062-.374-.312-.686-.644-.87a6.52 6.52 0 0 1-.22-.127c-.325-.196-.72-.257-1.076-.124l-1.217.456a1.125 1.125 0 0 1-1.369-.49l-1.297-2.247a1.125 1.125 0 0 1 .26-1.431l1.004-.827c.292-.24.437-.613.43-.991a6.932 6.932 0 0 1 0-.255c.007-.38-.138-.751-.43-.992l-1.004-.827a1.125 1.125 0 0 1-.26-1.43l1.297-2.247a1.125 1.125 0 0 1 1.37-.491l1.216.456c.356.133.751.072 1.076-.124.072-.044.146-.086.22-.128.332-.183.582-.495.644-.869l.214-1.28Z" />
                                    <path stroke-linecap="round" stroke-linejoin="round"
                                        d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
                                </svg>

                                设置</button>
                            <button @click="handleSignOut"
                                class="w-full px-4 py-3 text-sm text-red-600 hover:bg-gray-100 cursor-pointer flex items-center gap-1">
                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                                    stroke-width="1.5" stroke="currentColor" class="size-6">
                                    <path stroke-linecap="round" stroke-linejoin="round"
                                        d="M15.75 9V5.25A2.25 2.25 0 0 0 13.5 3h-6a2.25 2.25 0 0 0-2.25 2.25v13.5A2.25 2.25 0 0 0 7.5 21h6a2.25 2.25 0 0 0 2.25-2.25V15m3 0 3-3m0 0-3-3m3 3H9" />
                                </svg>
                                退出登录
                            </button>
                        </div>
                    </div>
                </div>

            </div>

            <div v-if="appStore.messages.length > 0 || appStore.threadUuid"
                class="p-0 max-w-[768px] m-auto mb-36 pt-14">
                <div v-for="(message, index) in appStore.messages" :key="index" :class="[
                    'mb-4 flex',
                    message.modleId ? 'justify-start' : 'justify-end',
                    message.modleId && index == appStore.messages.length - 1 ? 'min-h-[40dvh]' : ''
                ]">
                    <div :class="[
                        'break-words',
                        message.modleId ? 'bg-white p-3 max-w-[100%]' : 'bg-gray-100 max-w-[80%] p-3 pl-5 pr-5 rounded-2xl'
                    ]">

                        <!-- user prompt -->
                        <div v-if="message.modleId == null">{{ message.text }}</div>
                        <!-- ai completion -->
                        <MdPreview v-else :modelValue="message.text" language="en-US" preview-theme="github"
                            code-theme="github" />
                    </div>
                </div>

                <!-- 加载指示器 -->
                <div class="pl-4 h-5">
                    <div v-if="isLoading"
                        class="h-5 w-5 border-2 border-gray-500 border-t-transparent rounded-full animate-spin"></div>
                    <div v-else class="h-5 w-5"></div> <!-- 占位保持高度 -->
                </div>
            </div>
        </div>
        <!-- bottom area -->
        <div :class="['absolute left-0 right-0 m-auto w-[96%] max-w-[668px] bg-gray-50 border border-gray-300 shadow-sm rounded-xl z-11000',
            appStore.messages.length == 0 && !appStore.threadUuid ? 'top-1/2 -translate-y-[2em]' : 'bottom-3'
        ]">
            <!-- Welcome Message -->
            <div v-if="appStore.messages.length === 0 && !appStore.threadUuid"
                class="absolute flex justify-center w-full -top-[2em] font-normal text-2xl text-gray-600">
                {{ welcomeMessage }}
            </div>
            <!-- scroll to bottom-->
            <transition name="fade" enter-active-class="transition-opacity duration-300"
                leave-active-class="transition-opacity duration-300" enter-from-class="opacity-0"
                enter-to-class="opacity-100" leave-from-class="opacity-100" leave-to-class="opacity-0">
                <div v-show="showScrollToBottom" class="absolute -top-12 w-full flex justify-center">
                    <button
                        class="w-8 h-8 rounded-full cursor-pointer bg-white border border-gray-300 flex items-center justify-center outline-0"
                        @click="scrollToBottom()">
                        <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor"
                            xmlns="http://www.w3.org/2000/svg" class="">
                            <path
                                d="M9.33468 3.33333C9.33468 2.96617 9.6326 2.66847 9.99972 2.66829C10.367 2.66829 10.6648 2.96606 10.6648 3.33333V15.0609L15.363 10.3626L15.4675 10.2777C15.7255 10.1074 16.0762 10.1357 16.3034 10.3626C16.5631 10.6223 16.5631 11.0443 16.3034 11.304L10.4704 17.137C10.2108 17.3967 9.7897 17.3966 9.52999 17.137L3.69601 11.304L3.61105 11.1995C3.44054 10.9414 3.46874 10.5899 3.69601 10.3626C3.92328 10.1354 4.27479 10.1072 4.53292 10.2777L4.63741 10.3626L9.33468 15.0599V3.33333Z">
                            </path>
                        </svg>
                    </button>
                </div>
            </transition>

            <!-- input area -->
            <div class="flex items-center w-full relative">
                <textarea v-model="prompt" name="chat-input" @focus="handleFocus"
                    class="w-full px-4 py-2 focus:outline-none resize-none border-0 text-md"
                    :style="{ minHeight: minHeight + 'px' }" placeholder="" rows="1" @input="autoResize"
                    @keydown="handleKeydown" ref="textarea"></textarea>
            </div>
            <!-- 功能区 -->
            <div class="flex justify-between px-2 py-1">
                <!-- 附件按钮 -->
                <div>
                    <button
                        class="p-2 border border-gray-200 rounded-full text-gray-500 hover:bg-gray-100 hover:text-gray-700 focus:outline-none cursor-pointer"
                        title="添加附件">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                            stroke="currentColor" class="size-5">
                            <path stroke-linecap="round" stroke-linejoin="round"
                                d="m18.375 12.739-7.693 7.693a4.5 4.5 0 0 1-6.364-6.364l10.94-10.94A3 3 0 1 1 19.5 7.372L8.552 18.32m.009-.01-.01.01m5.699-9.941-7.81 7.81a1.5 1.5 0 0 0 2.112 2.13" />
                        </svg>
                    </button>
                </div>
                <!-- 右侧控件 -->
                <div class="flex items-center">
                    <button @click="chat" :class="[
                        'rounded-full size-9 flex items-center justify-center focus:outline-none transition-colors',
                        trimmedPrompt ? 'bg-gray-700 hover:bg-gray-400 cursor-pointer text-white' : 'bg-gray-100 text-gray-400 cursor-default'
                    ]" :disabled="!trimmedPrompt">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                            stroke="currentColor" class="size-5">
                            <path stroke-linecap="round" stroke-linejoin="round"
                                d="M4.5 10.5 12 3m0 0 7.5 7.5M12 3v18" />
                        </svg>
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { computed, ref, nextTick, onMounted, onUnmounted, watch, reactive } from 'vue';
import { useAppStore } from '@/stores/app';
import { aiApi, ChatDto } from '@/api/ai';
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';
import ModelSelector from './ModelSelector/ModelSelector.vue';
import Settings from '@/components/Settings.vue'
import { fixThinkNewlines } from '@/utils'
import { useRoute, useRouter } from 'vue-router';

const appStore = useAppStore()
const route = useRoute()
const router = useRouter()
const messageContainer = ref<HTMLElement>();
const isLoading = ref<boolean>(false)


// 欢迎语数组
const welcomeMessages = [
    "Ask me anything!",
    "Let's get started.",
    "How can I help you today?",
    "Ready when you are.",
    "What are you working on?",
    "What's on the agenda today?",
    "What's on your mind today?",
    "Where should we begin?"
]
// 随机选择一个欢迎语
const welcomeMessage = ref('')

const randomMessage = () => {
    const randomIndex = Math.floor(Math.random() * welcomeMessages.length)
    welcomeMessage.value = welcomeMessages[randomIndex]
}


const showScrollToBottom = ref(false)
const handleScroll = async () => {
    if (!messageContainer.value) return;
    const { scrollHeight, scrollTop, clientHeight } = messageContainer.value;
    showScrollToBottom.value = scrollHeight - clientHeight - scrollTop > 20
}

// Settings Modal
const isSettingsOpen = ref(false);
const openSettings = () => {
    isUserMenuOpen.value = false; // Close user menu before opening settings
    isSettingsOpen.value = true;
};
const closeSettings = () => {
    isSettingsOpen.value = false;
};


// textarea 最小高度
const minHeight = ref(58);

// 用户提示词
const prompt = ref('');
const trimmedPrompt = computed(() => prompt.value.trim())

const chat = async () => {
    if (!trimmedPrompt.value || isLoading.value) return;
    appStore.addMessage({
        uuid: null,
        modleId: null,
        text: trimmedPrompt.value
    });

    if (textarea.value) {
        textarea.value.style.height = minHeight.value + 'px';
    }

    isLoading.value = true;
    await scrollToBottom()
    try {
        const chatDto = {
            threadUuid: appStore.threadUuid,
            prompt: trimmedPrompt.value,
            modelId: appStore.modelId,
        } as ChatDto
        // 清空输入框
        prompt.value = '';
        const lastResp = reactive({
            uuid: null,
            modleId: appStore.modelId,
            text: ''
        })
        appStore.addMessage(lastResp);
        const chatUuid = await aiApi.prepare(chatDto)
        lastResp.uuid = chatUuid
        const evtSource = new EventSource(`/api/v1/ai/chat/stream?chatUuid=${chatUuid}`);
        evtSource.addEventListener('end', () => {
            evtSource.close();
            isLoading.value = false;
        })

        evtSource.addEventListener('error', (event: MessageEvent) => {
            let obj = JSON.parse(event.data)
            lastResp.text = `<font color="red">${obj.m}</font>`
            evtSource.close()
            isLoading.value = false;
        })
        evtSource.addEventListener('thread', (event: MessageEvent) => {
            let uuid = JSON.parse(event.data).uuid
            if (uuid !== appStore.threadUuid) {
                appStore.updateThreads()
                appStore.threadUuid = uuid
                router.push({
                    path: route.path,
                    query: { uuid: uuid }
                })
            }
        });

        evtSource.onmessage = async (event) => {
            let obj = JSON.parse(event.data)
            lastResp.text = fixThinkNewlines(lastResp.text + obj.m)
            await handleScroll()

        };

    } catch (error) {
        console.error("Error fetching AI response:", error);
        isLoading.value = false;
    }
};


// 滚动到底部
const scrollToBottom = async () => {
    if (!messageContainer.value) return
    messageContainer.value.scrollTo({
        top: messageContainer.value.scrollHeight,
        behavior: 'smooth',
    })

}

// textarea ref
const textarea = ref<HTMLTextAreaElement | null>(null);

// 自动调整 textarea 高度
const autoResize = () => {
    const element = textarea.value;
    if (element) {
        element.style.height = minHeight.value + 'px';
        if (element.scrollHeight > minHeight.value) {
            var height = Math.min(element.scrollHeight, 280);
            element.style.height = height + 'px';
        }
    }
}

// 键盘事件处理
const handleKeydown = (event: KeyboardEvent) => {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        chat()
    }
};

// 用户菜单相关
const isUserMenuOpen = ref(false);
const userMenuRef = ref<HTMLElement | null>(null);
const userMenuWrapperRef = ref<HTMLElement | null>(null);

const hideMenu = () => {
    isUserMenuOpen.value = false;
};

const handleClickOutside = (event: MouseEvent) => {
    if (
        isUserMenuOpen.value &&
        userMenuWrapperRef.value &&
        !userMenuWrapperRef.value.contains(event.target as Node)
    ) {
        hideMenu();
    }
};
// resolve iOS Safari keyboard overlap issue on input focus
const handleFocus = () => {
    setTimeout(() => {
        textarea.value?.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }, 300); // 延迟是为了等键盘完全弹出
};


// Watch for changes in messages and scroll to bottom
watch(() => appStore.messages.length, (val) => {
    if (val === 0) {
        randomMessage()
        return
    }
    nextTick(() => {
        scrollToBottom();
    });
});

onMounted(() => {
    document.addEventListener('click', handleClickOutside);
    messageContainer.value?.addEventListener('scroll', handleScroll)
    handleScroll() // init
    randomMessage()
});

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside);
});

const handleSignOut = async () => {
    await appStore.signOut()
    router.push('/sign_in')
};
</script>

<style scoped>
/* 自定义滚动条样式（保持不变） */
::-webkit-scrollbar {
    width: 5px;
}

::-webkit-scrollbar-track {
    background: transparent;
}

::-webkit-scrollbar-thumb {
    background: #d1d5db;
    border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
    background: #9ca3af;
}

/* Markdown 解析后的 HTML 样式 */
:deep(think) {
    display: block;
    padding: 0.75em 1em;
    font-size: 0.85em;
    line-height: 1.6;
    /* 柔和灰白背景 */
    background-color: #f9fafb;
    /* 中性灰文字，tailwind 的 gray-500 */
    color: #6b7280;
    /* 轻灰色边框 */
    border-left: 2px solid #d1d5db;
    border-radius: 4px;
    font-style: italic;
}
</style>
