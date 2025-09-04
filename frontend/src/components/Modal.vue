<script setup lang="ts">
import { watchEffect, computed } from 'vue';

const props = defineProps<{
    isOpen: boolean;
    maxWidth?: 'sm' | 'md' | 'lg' | 'xl' | '2xl';
}>();

const emit = defineEmits(['close']);

const close = () => {
    emit('close');
};

const handleKeydown = (e: KeyboardEvent) => {
    if (e.key === 'Escape' && props.isOpen) {
        close();
    }
};

watchEffect((onInvalidate) => {
    if (props.isOpen) {
        document.addEventListener('keydown', handleKeydown);
    }

    onInvalidate(() => {
        document.removeEventListener('keydown', handleKeydown);
    });
});

const maxWidthClass = computed(() => {
    switch (props.maxWidth) {
        case 'sm': return 'max-w-sm';
        case 'md': return 'max-w-md';
        case 'xl': return 'max-w-xl';
        case '2xl': return 'max-w-2xl';
        default: return 'max-w-lg';
    }
});
</script>

<template>
    <Teleport to="body">
        <Transition enter-active-class="transition-opacity ease-out duration-200" enter-from-class="opacity-0"
            enter-to-class="opacity-100" leave-active-class="transition-opacity ease-in duration-200"
            leave-from-class="opacity-100" leave-to-class="opacity-0">
            <div v-if="isOpen" class="fixed inset-0 z-30000 bg-black/50" @click.self="close">
                <Transition enter-active-class="transition ease-out duration-200" enter-from-class="opacity-0 scale-95"
                    enter-to-class="opacity-100 scale-100" leave-active-class="transition ease-in duration-200"
                    leave-from-class="opacity-100 scale-100" leave-to-class="opacity-0 scale-95">
                    <div v-if="isOpen" :class="['relative z-31000 mx-auto mt-20 rounded-lg bg-white p-6 shadow-xl', maxWidthClass]">
                        <div class="flex items-start justify-between">
                            <div class="text-xl font-semibold">
                                <slot name="header">
                                </slot>
                            </div>
                            <button class="cursor-pointer text-gray-500 hover:text-gray-800" @click="close">
                                <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M6 18L18 6M6 6l12 12"></path>
                                </svg>
                            </button>
                        </div>

                        <div class="mt-4">
                            <slot>
                                <p>This is the default modal content.</p>
                            </slot>
                        </div>

                        <div class="mt-6 flex justify-end space-x-4">
                            <slot name="footer">
                            </slot>
                        </div>
                    </div>
                </Transition>
            </div>
        </Transition>
    </Teleport>
</template>

