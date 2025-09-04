<template>
    <el-form label-position="top" :model="record" v-loading="loading"
        :disabled="!userStore.hasAuthorize('/ai/ai_config/edit')">
        <el-card>
            <template #header> 系统 </template>
            <el-row>
                <el-col :span="6">
                    <el-form-item label="对话记忆">
                        <el-input-number inputmode="numeric" :min="0" v-model="record.memoryCount" />
                    </el-form-item>
                </el-col>
            </el-row>
        </el-card><br>
        <el-card>
            <template #header> open router </template>
            <el-row>
                <el-col :span="6">
                    <el-form-item label="api url">
                        <el-input v-model="record.openRouterConfig.apiUrl"  />
                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item label="api key">
                        <el-input v-model="record.openRouterConfig.apiKey" />
                    </el-form-item>
                </el-col>
            </el-row>
        </el-card><br>
        <el-row justify="center">
            <el-button type="primary" @click="saveRec()"
                v-if="userStore.hasAuthorize('/ai/ai_config/edit')">保存</el-button>
        </el-row>
    </el-form>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import aiApi from '@/api/ai'

const userStore = useUserStore()


const record = reactive({
    memoryCount: null as number | null,
    openRouterConfig: {} as any
}) as any

const recordDialog = reactive({
    id: null as number | null,
    name: '' as string,
    loading: false,
    visible: false
})


const loading = ref(true)

const query = async () => {
    loading.value = true
    const resp = await aiApi.getAiConfig() as any
    Object.assign(record, resp.data)
    console.log(record)
    loading.value = false
}

const saveRec = async () => {

    recordDialog.loading = true
    try {
        await aiApi.saveAiConfig(record)
        ElMessage.success(`保存Ai配置完成`)

        recordDialog.visible = false
        query()
    }
    finally {
        recordDialog.loading = false
    }

}

onMounted(() => {
    query()
})

</script>

<style lang="scss" scoped></style>