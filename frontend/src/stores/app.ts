import { defineStore } from 'pinia'
import { mainApi } from '@/api/main'
import { aiApi } from '@/api/ai';

import { ref } from 'vue';
import { Model, Thread, Message } from '@/types/api';

export const useAppStore = defineStore('app', () => {
    // 是否为宽屏
    const isWideScreen = ref<boolean>(true)
    // 侧边栏是否显示
    const isSidebarVisible = ref<boolean>(true)
    // 用户名
    const username = ref<string>('')
    // 邮箱
    const email = ref<string>('')
    // ai models
    const models = ref<Model[]>([])
    const modelId = ref<number>(0)
    // ai chat threads
    const threads = ref<Thread[]>([])
    //  thread uuid
    const threadUuid = ref<string | null>(null)
    // 消息数组
    const messages = ref<Message[]>([])

    async function initWithProfilePage() {
        await mainApi.getProfile().then(data => {
            if (data) {
                username.value = data.username
                email.value = data.email
                threadUuid.value = null
            }
        })
    }
    async function setModleId(id: number) {
        modelId.value = id
    }
    function setThreadUuid(uuid: string | null) {
        threadUuid.value = uuid;
    }
    async function updateModels() {
        const resp = await aiApi.listModels()
        modelId.value = resp[0].id
        models.value = resp
    }
    async function updateThreads() {
        const resp = await aiApi.listThreads()
        threads.value = resp
    }
    // 添加消息
    function addMessage(message: Message) {
        messages.value.push(message)
    }
    // 清空消息
    function clearMessages() {
        messages.value = []
    }

    function init() {
        username.value = ''
        email.value = ''
        threads.value = []
        threadUuid.value = null
        clearMessages()
    }

    async function signOut() {
        await mainApi.signOut()
        init()

    }

    async function signOutAll() {
        await mainApi.signOutAll()
        init()
    }

    function setMessages(newMessages: Message[]) {
        messages.value = newMessages;
    }

    return {
        isWideScreen,
        isSidebarVisible,
        username,
        email,
        models,
        modelId,
        threads,
        threadUuid,
        messages,
        initWithProfilePage,
        signOut,
        signOutAll,
        setModleId,
        setThreadUuid,
        updateModels,
        updateThreads,
        addMessage,
        clearMessages,
        setMessages
    }
}, { persist: false })