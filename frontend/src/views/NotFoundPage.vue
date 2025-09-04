<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { t } = useI18n()

const goBack = () => router.back()
const goHome = () => router.push('/')
</script>

<template>
  <div class="flex min-h-screen items-center justify-center bg-gray-100">
    <div class="text-center">
      <h1 class="text-9xl font-bold text-gray-400">{{ t('notFound.title') }}</h1>
      <p class="mt-4 text-xl text-gray-600">{{ t('notFound.message') }}</p>
      <div class="mt-6 space-x-4">
        <button 
          @click="goBack"
          class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
        >
          {{ t('notFound.backButton') }}
        </button>
        <button 
          @click="goHome"
          class="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
        >
          {{ t('notFound.homeButton') }}
        </button>
      </div>
    </div>
  </div>
</template> 