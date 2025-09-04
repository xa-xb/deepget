<template>
    <div class="relative inline-block" ref="dropdownRef">
        <!-- 触发器按钮 -->
        <button @click="toggleDropdown"
            class="cursor-pointer inline-flex items-center gap-2 px-4 py-2.5 backdrop-blur-sm rounded-xl hover:bg-gray-200 focus:outline-none focus:ring-0 focus:border-0 transition-all duration-200 text-sm font-medium text-gray-700 min-w-[160px] max-w-[280px]"
            type="button">
            <span v-if="selectedOption" class="w-4 h-4 flex-shrink-0 opacity-80" v-html="selectedOption.iconSvg"></span>
            <span class="truncate flex-1 text-left">{{ selectedOption ? selectedOption.name : '请选择模型' }}</span>
            <svg class="w-4 h-4 text-gray-400 flex-shrink-0 transition-transform duration-200"
                :class="{ 'rotate-180': open }" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
            </svg>
        </button>

        <!-- 下拉菜单 -->
        <div v-if="open"
            class="absolute z-50 mt-2 bg-white/95 backdrop-blur-md border border-gray-200/60 rounded-2xl shadow-xl max-h-72 overflow-hidden min-w-[280px] max-w-[400px] animate-in fade-in-0 zoom-in-95 duration-200">
            <!-- 选项列表 -->
            <div class="max-h-72 overflow-y-auto custom-scrollbar">
                <ul style="padding: 0; margin: 0;">
                    <li v-for="option in appStore.models" :key="option.id" @click="selectOption(option)"
                        class="flex items-center gap-3 px-4 py-3 cursor-pointer hover:bg-blue-50/80 transition-colors duration-150 group border-b border-gray-50/60 last:border-b-0">
                        <span class="w-5 h-5 flex-shrink-0 opacity-70 group-hover:opacity-100 transition-opacity"
                            v-html="option.iconSvg"></span>
                        <div class="flex-1 min-w-0">
                            <div class="font-medium text-gray-800 truncate">{{ option.name }}</div>
                            <div class="text-xs text-gray-500 mt-0.5">{{ option.providerName }}</div>
                        </div>
                        <div v-if="option.id === appStore.modelId"
                            class="w-2 h-2 bg-blue-500 rounded-full flex-shrink-0">
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { Model } from '@/types/api';
import { useAppStore } from '@/stores/app';

const appStore = useAppStore()
const dropdownRef = ref<HTMLElement | null>(null)
const open = ref(false)

const selectedOption = computed(() =>
    appStore.models.find(opt => opt.id === appStore.modelId)
)

function toggleDropdown() {
    open.value = !open.value
}

function selectOption(option: Model) {
    appStore.setModleId(option.id)
    open.value = false
}

// Close dropdown on click outside
function handleClickOutside(event: MouseEvent) {
    if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
        open.value = false
    }
}

watch(open, (val) => {
    if (val) {
        document.addEventListener('click', handleClickOutside)
    } else {
        document.removeEventListener('click', handleClickOutside)
    }
})

onMounted(async () => {
    appStore.updateModels()
})

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.custom-scrollbar {
    scrollbar-width: thin;
    scrollbar-color: #d1d5db transparent;
}

.custom-scrollbar::-webkit-scrollbar {
    width: 4px;
}

.custom-scrollbar::-webkit-scrollbar-track {
    background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
    background: #d1d5db;
    border-radius: 2px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
    background: #9ca3af;
}

/* 动画效果 */
@keyframes fade-in-0 {
    from {
        opacity: 0;
    }

    to {
        opacity: 1;
    }
}

@keyframes zoom-in-95 {
    from {
        opacity: 0;
        transform: scale(0.95);
    }

    to {
        opacity: 1;
        transform: scale(1);
    }
}

.animate-in {
    animation-fill-mode: both;
}

.fade-in-0 {
    animation-name: fade-in-0;
}

.zoom-in-95 {
    animation-name: zoom-in-95;
}

.duration-200 {
    animation-duration: 200ms;
}
</style>
