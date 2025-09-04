<template>
    <el-config-provider :size="appStore.elementSize">
        <router-view></router-view>
    </el-config-provider>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { useAppStore } from './stores/app'
export default defineComponent({
    name: 'App',
    setup() {
        const appStore = useAppStore()
        return {
            appStore
        }
    }
})
</script>

<style>
#app {
    -webkit-text-size-adjust: 100%;
    font-feature-settings: normal;
    font-family: ui-sans-serif, -apple-system, system-ui, Segoe UI, Helvetica, Apple Color Emoji, Arial, sans-serif, Segoe UI Emoji, Segoe UI Symbol;
    font-variation-settings: normal;
    line-height: 1.5;
    tab-size: 4;
    width: 100%;
    height: 100vh;
}
</style>
