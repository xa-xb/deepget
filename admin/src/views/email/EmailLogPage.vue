<template>
    <el-row justify="start">
        <el-form :inline="true">
            <el-form-item label="action">
                <el-input v-model="queryData.action" placeholder="" @keydown.enter="query" clearable />
            </el-form-item>
            <el-form-item label="from">
                <el-input v-model="queryData.from" placeholder="" @keydown.enter="query" clearable />
            </el-form-item>
            <el-form-item label="to">
                <el-input v-model="queryData.to" placeholder="" @keydown.enter="query" clearable />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="query">查询</el-button>
            </el-form-item>
        </el-form>
    </el-row>
    <el-table v-loading="loading" :data="result.rows" style="width: 100%; margin-bottom: 20px" row-key="id" border
        default-expand-all>
        <el-table-column prop="id" label="id" width="80" />
        <el-table-column prop="from" label="from" />
        <el-table-column prop="to" label="to" />
        <el-table-column prop="action" label="action" />
        <el-table-column prop="duration" label="用时(ms)" />
        <el-table-column prop="createdAt" label="时间" />
    </el-table>
    <el-row justify="end">
        <el-pagination layout="total, sizes, prev, pager, next, jumper" v-model:current-page="queryData.page"
            v-model:page-size="queryData.pageSize" :page-count="result.totalPages" :page-sizes="[20, 50, 80, 100]"
            :total="result.totalRecords" />
    </el-row>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import emailApi from '@/api/email'


const queryData = reactive({
    action: '',
    from: '',
    to: '',
    page: 1,
    pageSize: 20,
    sort: ''
})

const result = reactive({
    rows: [] as any,
    totalPages: 0 as number,
    totalRecords: 0 as number
})



const loading = ref(true)

const query = async () => {
    loading.value = true
    const resp = await emailApi.getEmailLogList(queryData) as any
    Object.assign(result, resp.data)
    loading.value = false
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