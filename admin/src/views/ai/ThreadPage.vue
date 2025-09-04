<template>
    <!-- record dialog -->
    <el-dialog v-model="recordDialog.visible" :title="(recordDialog.id === null ? '新建' : '修改') + `对话主题`" width="560"
        @close="formRef?.clearValidate()">
        <el-form label-width="110px" :model="recordDialog" v-loading="recordDialog.loading" ref="formRef"
            :rules="formRules">
            <el-form-item label="名称" prop="name">
                <el-input v-model="recordDialog.name" type="text" autocomplete="off" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="recordDialog.visible = false">取消</el-button>
                <el-button type="primary" @click="saveRec">确定</el-button>
            </span>
        </template>
    </el-dialog>

    <el-row justify="start">
        <el-form :inline="true">
            <el-form-item label="用户名">
                <el-input v-model="queryData.username" placeholder="用户名" @keydown.enter="query" clearable />
            </el-form-item>
            <el-form-item label="标题">
                <el-input v-model="queryData.title" placeholder="标题" @keydown.enter="query" clearable />
            </el-form-item>
            <el-form-item label="状态">
                <el-select v-model="queryData.status">
                    <el-option v-for="item in statuses" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="query">查询</el-button>
            </el-form-item>
        </el-form>
    </el-row>
    <el-table v-loading="loading" :data="result.rows" style="width: 100%; margin-bottom: 20px" row-key="id" border
        default-expand-all>
        <el-table-column prop="id" label="id" width="80" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="createdAt" label="建立时间" width="100" />
        <el-table-column label="操作" width="120">
            <template #default="scope">
                <el-button link type="primary" size="small" :icon="Memo"
                    @click="router.push({ path: '/ai/chat', query: { threadId: scope.row.id } })"
                    v-if="userStore.hasAuthorize('/ai/chat/query')">对话记录</el-button>
            </template>
        </el-table-column>
    </el-table>
    <el-row justify="end">
        <el-pagination layout="total, sizes, prev, pager, next, jumper" v-model:current-page="queryData.page"
            v-model:page-size="queryData.pageSize" :page-count="result.totalPages" :page-sizes="[20, 50, 80, 100]"
            :total="result.totalRecords" />
    </el-row>
</template>
<script setup lang="ts">
import { ref, reactive, useTemplateRef, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import aiApi from '@/api/ai'
import type { FormInstance } from 'element-plus'
import { Delete, Edit, Memo } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

const statuses = ref([
    { id: 0, name: '全部' },
    { id: 1, name: '正常' },
    { id: 2, name: '已删除' }
])

const queryData = reactive({
    title: '',
    // 0:all, 1:active, 2:deleted
    status: 0,
    username: '',
    page: 1,
    pageSize: 20,
    sort: ''
})

const result = reactive({
    rows: [] as any,
    totalPages: 0 as number,
    totalRecords: 0 as number
})

const recordDialog = reactive({
    id: null as number | null,
    name: '' as string,
    loading: false,
    visible: false
})

const formRef = useTemplateRef<FormInstance>('formRef')
const formRules = {
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' }
    ],
}

const loading = ref(true)

const query = async () => {
    loading.value = true
    const resp = await aiApi.getChatThreadList(queryData) as any
    Object.assign(result, resp.data)
    loading.value = false
}

const newRec = () => {
    Object.assign(recordDialog, {
        id: null,
        name: '',
        visible: true
    })
}
const saveRec = async () => {
    formRef.value?.validate(async (valid: boolean) => {
        if (!valid) return
        recordDialog.loading = true
        const { loading, visible, ...postData } = recordDialog
        try {
            if (recordDialog.id == null) {
                await aiApi.addChatThread(postData)
                ElMessage.success(`新建对话主题完成`)
            } else {
                await aiApi.editChatThread(postData)
                ElMessage.success(`修改对话主题完成`)
            }
            recordDialog.visible = false
            query()
        }
        finally {
            recordDialog.loading = false
        }
    })
}

const editRec = (row: any) => {
    Object.assign(recordDialog, row)
    recordDialog.visible = true
}

const deleteRec = (row: any) => {
    ElMessageBox.confirm(`确定删除该对话主题吗？`).then(() => {
        aiApi.deleteChatThread({ id: row.id }).then(() => {
            ElMessage.success(`删除对话主题完成`)
            query()
        })
    })
}

onMounted(() => {
    query()
})

watch(() => ({
    page: queryData.page,
    sort: queryData.sort
}), () => {
    query();
}, { deep: true });

</script>

<style lang="scss" scoped></style>