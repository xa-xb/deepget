<template>
    <!-- message detail dialog -->
    <el-dialog v-model="detailDialog.visible" title="对话详情" width="800">
        <el-descriptions border :column="2">
            <el-descriptions-item label="时间">{{ detailDialog.createdAt }}</el-descriptions-item>
            <el-descriptions-item label="用户">{{ detailDialog.userName }}</el-descriptions-item>
            <el-descriptions-item label="话题ID">{{ detailDialog.threadId }}</el-descriptions-item>
            <el-descriptions-item label="模型">{{ detailDialog.model }}</el-descriptions-item>
            <el-descriptions-item label="消息" :span="2">
                <el-input v-model="detailDialog.prompt" :rows="3" type="textarea" readonly />
            </el-descriptions-item>
            <el-descriptions-item label="回复" :span="2">
                <el-input v-model="detailDialog.completion" :rows="8" type="textarea" readonly />
            </el-descriptions-item>
        </el-descriptions>
        <template #footer>
            <span class="dialog-footer">
                <el-button type="primary" @click="detailDialog.visible = false">确定</el-button>
            </span>
        </template>
    </el-dialog>
    <el-row justify="start">
        <el-form :inline="true">
            <el-form-item label="用户名">
                <el-input v-model="queryData.username" placeholder="用户名" @keydown.enter="query" clearable />
            </el-form-item>
            <el-form-item label="话题id">
                <el-input-number v-model="queryData.threadId" type="number" :controls="false" placeholder="话题id"
                    @keydown.enter="query" clearable />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="query">查询</el-button>
            </el-form-item>
        </el-form>
    </el-row>
    <el-table v-loading="loading" :data="result.rows" style="width: 100%; margin-bottom: 20px" row-key="id"
        @sort-change="sortChange">
        <el-table-column prop="id" label="id" min-width="40" sortable="custom">
        </el-table-column>
        <el-table-column prop="userName" label="用户名" min-width="40" />
        <el-table-column prop="threadId" label="话题ID" min-width="40" />
        <el-table-column width="130">
            <template #header>
                模型名称
            </template>
            <template #default="scope">
                <el-text type="success">{{ scope.row.model }}</el-text>
            </template>
        </el-table-column>
        <el-table-column prop="prompt" label="提示词" min-width="160">
            <template #default="scope">
                <el-link type="primary" @click="showDetail(scope.row)">{{ scope.row.prompt }}</el-link>
            </template>
        </el-table-column>
        <el-table-column prop="totalTokens" label="tokens" width="80" />
        <el-table-column width="180">
            <template #header>
                请求用时(ms)<br> 时间
            </template>
            <template #default="scope">
                {{ scope.row.duration }}<br>
                {{ scope.row.createdAt }}
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
import { useRoute } from 'vue-router'
import aiApi from '@/api/ai'

const route = useRoute()

const queryData = reactive({
    username: '',
    threadId: null,
    page: 1,
    pageSize: 20,
    sort: ''
});
Object.assign(queryData, route.query);
const viewModes = reactive(
    [
        { id: 1, name: "全部" },
        { id: 2, name: "话题模式" },
    ]
)

const result = reactive({
    rows: [] as any,
    totalPages: 0 as number,
    totalRecords: 0 as number
});


const detailDialog = reactive({
    visible: false,
    userName: '',
    threadId: '',
    model: '',
    prompt: '',
    completion: '',
    createdAt: ''
})

const loading = ref(true)

const query = async () => {
    loading.value = true
    const resp = await aiApi.getChatList(queryData) as any
    Object.assign(result, resp.data)
    console.log(result)
    loading.value = false
}


const showDetail = (row: any) => {
    Object.assign(detailDialog, {
        visible: true,
        userName: row.userName,
        threadId: row.threadId,
        model: row.model,
        prompt: row.prompt,
        completion: row.completion,
        createdAt: row.createdAt
    })
}
const sortChange = (param: any) => {
    if (!param.order) {
        queryData.sort = ''
        return
    }
    const n = param.prop
    queryData.sort = `${n},${param.order[0] === 'a' ? 'asc' : 'desc'}`
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