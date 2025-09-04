<template>
    <div class="h-full flex p-0">
        <!-- 顶栏控制按钮 -->
        <!-- 显示/隐藏侧边栏按钮 -->
        <button @click="toggleSidebar" class="absolute z-22000 left-4 top-3 cursor-pointer text-gray-500
        hover:text-gray-900 rounded-sm p-1">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M0 6h16M0 12h14M0 18h16"></path>
            </svg>
        </button>
        <!-- 侧边栏 -->
        <div id="sidebar" :class="{
            '-translate-x-full': !appStore.isSidebarVisible,
            'md:w-0': !appStore.isSidebarVisible,
            'md:w-64  md:opacity-100': appStore.isSidebarVisible
        }" class="bg-slate-50 w-60 h-full fixed md:relative
         transition-transform duration-200 md:transition-all ease-in-out 
         transform  z-21000 border-r border-gray-200 flex flex-col overflow-y-auto">
            <!-- 固定的标题区域 -->
            <div
                class="px-3 pl-5 pb-3 pt-15 border-b border-gray-100 sticky bg-slate-50 z-50 shadow-sm backdrop-blur-sm">
                <!-- 新对话链接 -->
                <div @click="startNewChat"
                    class="hover:bg-gray-200 hover:rounded-lg text-base cursor-pointer transition-all duration-300 ease-in-out flex items-center gap-2 mb-4 px-3 py-2 rounded-lg ">
                    <svg width="22" height="22" viewBox="0 0 20 20" fill="currentColor"
                        xmlns="http://www.w3.org/2000/svg"
                        class="icon transition-transform duration-300 hover:rotate-12" aria-hidden="true">
                        <path
                            d="M2.6687 11.333V8.66699C2.6687 7.74455 2.66841 7.01205 2.71655 6.42285C2.76533 5.82612 2.86699 5.31731 3.10425 4.85156L3.25854 4.57617C3.64272 3.94975 4.19392 3.43995 4.85229 3.10449L5.02905 3.02149C5.44666 2.84233 5.90133 2.75849 6.42358 2.71582C7.01272 2.66769 7.74445 2.66797 8.66675 2.66797H9.16675C9.53393 2.66797 9.83165 2.96586 9.83179 3.33301C9.83179 3.70028 9.53402 3.99805 9.16675 3.99805H8.66675C7.7226 3.99805 7.05438 3.99834 6.53198 4.04102C6.14611 4.07254 5.87277 4.12568 5.65601 4.20313L5.45581 4.28906C5.01645 4.51293 4.64872 4.85345 4.39233 5.27149L4.28979 5.45508C4.16388 5.7022 4.08381 6.01663 4.04175 6.53125C3.99906 7.05373 3.99878 7.7226 3.99878 8.66699V11.333C3.99878 12.2774 3.99906 12.9463 4.04175 13.4688C4.08381 13.9833 4.16389 14.2978 4.28979 14.5449L4.39233 14.7285C4.64871 15.1465 5.01648 15.4871 5.45581 15.7109L5.65601 15.7969C5.87276 15.8743 6.14614 15.9265 6.53198 15.958C7.05439 16.0007 7.72256 16.002 8.66675 16.002H11.3337C12.2779 16.002 12.9461 16.0007 13.4685 15.958C13.9829 15.916 14.2976 15.8367 14.5447 15.7109L14.7292 15.6074C15.147 15.3511 15.4879 14.9841 15.7117 14.5449L15.7976 14.3447C15.8751 14.128 15.9272 13.8546 15.9587 13.4688C16.0014 12.9463 16.0017 12.2774 16.0017 11.333V10.833C16.0018 10.466 16.2997 10.1681 16.6667 10.168C17.0339 10.168 17.3316 10.4659 17.3318 10.833V11.333C17.3318 12.2555 17.3331 12.9879 17.2849 13.5771C17.2422 14.0993 17.1584 14.5541 16.9792 14.9717L16.8962 15.1484C16.5609 15.8066 16.0507 16.3571 15.4246 16.7412L15.1492 16.8955C14.6833 17.1329 14.1739 17.2354 13.5769 17.2842C12.9878 17.3323 12.256 17.332 11.3337 17.332H8.66675C7.74446 17.332 7.01271 17.3323 6.42358 17.2842C5.90135 17.2415 5.44665 17.1577 5.02905 16.9785L4.85229 16.8955C4.19396 16.5601 3.64271 16.0502 3.25854 15.4238L3.10425 15.1484C2.86697 14.6827 2.76534 14.1739 2.71655 13.5771C2.66841 12.9879 2.6687 12.2555 2.6687 11.333ZM13.4646 3.11328C14.4201 2.334 15.8288 2.38969 16.7195 3.28027L16.8865 3.46485C17.6141 4.35685 17.6143 5.64423 16.8865 6.53613L16.7195 6.7207L11.6726 11.7686C11.1373 12.3039 10.4624 12.6746 9.72827 12.8408L9.41089 12.8994L7.59351 13.1582C7.38637 13.1877 7.17701 13.1187 7.02905 12.9707C6.88112 12.8227 6.81199 12.6134 6.84155 12.4063L7.10132 10.5898L7.15991 10.2715C7.3262 9.53749 7.69692 8.86241 8.23218 8.32715L13.2791 3.28027L13.4646 3.11328ZM15.7791 4.2207C15.3753 3.81702 14.7366 3.79124 14.3035 4.14453L14.2195 4.2207L9.17261 9.26856C8.81541 9.62578 8.56774 10.0756 8.45679 10.5654L8.41772 10.7773L8.28296 11.7158L9.22241 11.582L9.43433 11.543C9.92426 11.432 10.3749 11.1844 10.7322 10.8271L15.7791 5.78027L15.8552 5.69629C16.185 5.29194 16.1852 4.708 15.8552 4.30371L15.7791 4.2207Z">
                        </path>
                    </svg>
                    开始新对话
                </div>
                <!-- 对话记录标题 -->
                <div class="text-base">对话记录</div>
            </div>
            <!-- 内容区域 -->
            <div class="px-3 pt-3  flex-1">
                <ul class="space-y-1">
                    <li v-for="thread in appStore.threads" :key="thread.uuid" @click="handleThreadClick(thread.uuid)"
                        :class="[
                            'px-3 pr-4 py-2 border-b border-gray-50 transition text-sm cursor-pointer group relative',
                            'rounded-md',
                            'hover:bg-gray-200 hover:rounded-lg',
                            thread.uuid === appStore.threadUuid ? 'bg-gray-200 font-semibold' : ''
                        ]">
                        <!-- 对话标题 -->
                        <div class="truncate pr-8">{{ thread.title || '无标题会话' }}</div>

                        <!-- 三个点菜单按钮 -->
                        <div class="absolute right-2 top-1/2 transform -translate-y-1/2">
                            <button @click.stop="toggleThreadMenu(thread.uuid)" :data-thread-id="thread.uuid" :class="[
                                'p-1 rounded hover:bg-gray-300 transition-colors duration-200',
                                'md:opacity-0 md:group-hover:opacity-100', // 桌面端悬停显示
                                'opacity-100' // 移动端一直显示
                            ]">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <circle cx="12" cy="12" r="1.5" />
                                    <circle cx="6" cy="12" r="1.5" />
                                    <circle cx="18" cy="12" r="1.5" />
                                </svg>
                            </button>
                        </div>
                    </li>
                </ul>
                <div v-if="!appStore.threads || appStore.threads.length === 0" class="text-gray-400 text-center py-8">
                    暂无对话记录
                </div>
            </div>
        </div>

        <!-- 遮罩层 -->
        <div v-if="appStore.isSidebarVisible && !appStore.isWideScreen" @click="toggleSidebar"
            class="fixed inset-0 bg-gray-200 opacity-45 md:hidden z-20100">
        </div>

        <!-- 全局菜单容器 -->
        <div v-if="openMenuThreadId"
            class="fixed w-24 bg-white rounded-lg shadow-2xl border border-gray-300 py-1 z-[22000]"
            :style="getMenuPosition(openMenuThreadId)">
            <button @click.stop="renameThread(getCurrentThread())"
                class="w-full px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 text-left">
                重命名
            </button>
            <button @click.stop="deleteThread(openMenuThreadId)"
                class="w-full px-3 py-2 text-sm text-red-600 hover:bg-gray-100 text-left">
                删除
            </button>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { useAppStore } from '@/stores/app'
import { aiApi } from '@/api/ai';
import { Message } from '@/types/api';
import { fixThinkNewlines } from '@/utils';
import { useRoute, useRouter } from 'vue-router'

const appStore = useAppStore()
const windowWidth = ref(window.innerWidth)
const openMenuThreadId = ref<string | null>(null)
const route = useRoute()
const router = useRouter()

const handleResize = () => {
    windowWidth.value = window.innerWidth
}

onMounted(async () => {
    window.addEventListener('resize', handleResize)
    // 添加点击外部关闭菜单的监听器
    document.addEventListener('click', handleClickOutside)
    if (windowWidth.value < 768) {
        appStore.isSidebarVisible = false
        appStore.isWideScreen = false
    }
    await appStore.updateThreads()
})

// 点击外部关闭菜单
function handleClickOutside(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.thread-menu')) {
        openMenuThreadId.value = null;
    }
}

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    document.removeEventListener('click', handleClickOutside)
})

// 切换线程菜单显示状态
function toggleThreadMenu(threadId: string) {
    if (openMenuThreadId.value === threadId) {
        openMenuThreadId.value = null;
    } else {
        openMenuThreadId.value = threadId;
    }
}

// 重命名线程
async function renameThread(thread: any) {
    const newName = prompt('请输入新的对话名称:', thread.title || '无标题会话');
    if (newName && newName.trim() !== '') {
        try {
            // 这里需要调用API来重命名线程
            await aiApi.renameThread({ uuid: thread.uuid, newName: newName });
            await appStore.updateThreads();
            console.log('重命名功能待实现:', thread.uuid, newName.trim());
        } catch (error) {
            console.error('重命名失败:', error);
        }
    }
    openMenuThreadId.value = null;
}

// 删除线程
async function deleteThread(threadId: string) {
    if (confirm('确定要删除这个对话吗？')) {
        try {
            // 这里需要调用API来删除线程
            await aiApi.deleteThread({ uuid: threadId });
            await appStore.updateThreads();
        } catch (error) {
            console.error('删除失败:', error);
        }
    }
    openMenuThreadId.value = null;
}

// 计算菜单位置
function getMenuPosition(threadId: string) {
    const button = document.querySelector(`[data-thread-id="${threadId}"]`) as HTMLElement;
    if (button) {
        const rect = button.getBoundingClientRect();
        return {
            top: `${rect.bottom + 4}px`,
            left: `${rect.left - 48}px` // 48px = 24 * 2 (w-24的一半)，让菜单偏右侧显示
        };
    }
    return {};
}

// 获取当前线程对象
function getCurrentThread() {
    if (openMenuThreadId.value) {
        return appStore.threads.find(thread => thread.uuid === openMenuThreadId.value);
    }
    return null;
}



// 切换侧边栏的显示状态
const toggleSidebar = () => {
    appStore.isSidebarVisible = !appStore.isSidebarVisible
}

// click thread title
async function handleThreadClick(uuid: string) {
    router.push({
        path: route.path,
        query: { uuid: uuid }
    })
}

// 开始新对话
async function startNewChat() {
    router.push({
        path: route.path
    })
}

watch(
    [windowWidth, () => route.query],
    async ([newWidth, newQuery], [, oldQuery]) => {

        // ✅ 处理窗口宽度变化
        if (newWidth >= 768) {
            if (!appStore.isWideScreen && !appStore.isSidebarVisible) {
                appStore.isSidebarVisible = true
            }
            appStore.isWideScreen = true
        } else {
            if (appStore.isWideScreen && appStore.isSidebarVisible) {
                appStore.isSidebarVisible = false
            }
            appStore.isWideScreen = false
        }

        // ✅ 处理 query 参数变化（示例）
        if (newQuery.uuid !== oldQuery?.uuid) {
            if (newQuery.uuid == appStore.threadUuid) return
            const uuid = newQuery.uuid as string
            // 你可以在这里处理 query 变化后的逻辑
            if (newQuery.uuid) {
                appStore.clearMessages(); // Clear messages for immediate feedback
                appStore.setThreadUuid(uuid);
                const resp = await aiApi.getThread({ uuid: uuid });
                const list: Message[] = [];
                for (const item of resp) {
                    // User message
                    list.push({
                        uuid: null,
                        modleId: null,
                        text: item.prompt
                    });
                    // AI message
                    list.push({
                        uuid: item.uuid,
                        modleId: item.modelId,
                        text: fixThinkNewlines(item.completion, false)
                    });
                }
                appStore.setMessages(list);
            } else {
                // 清空当前消息
                appStore.clearMessages();
                // 清空当前线程UUID
                appStore.setThreadUuid(null);
                // 滚动对话记录到最上方
                const sidebarElement = document.getElementById('sidebar') as HTMLElement;
                if (sidebarElement) {
                    sidebarElement.scrollTo({
                        top: 0,
                        behavior: 'smooth'
                    });
                }
            }
            // 在移动端自动隐藏侧边栏
            if (!appStore.isWideScreen) {
                appStore.isSidebarVisible = false;
            }

        }

    }, { immediate: true }
)
</script>

<style scoped>
li.bg-gray-100 {
    border-left: 3px solid #6366f1;
}

/* 侧边栏滚动条样式 */
.overflow-y-auto::-webkit-scrollbar {
    width: 3px;
    transition: width 0.2s ease-in-out;
}

.overflow-y-auto::-webkit-scrollbar-track {
    background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
    background: #d1d5db;
    border-radius: 3px;
}

/* 悬停时滚动条变粗 */
.overflow-y-auto:hover::-webkit-scrollbar {
    width: 4px;
}

/* 悬停时滑块也变粗 */
.overflow-y-auto:hover::-webkit-scrollbar-thumb {
    background: #9ca3af;
    border-radius: 4px;
}
</style>