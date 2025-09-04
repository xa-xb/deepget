<template>
    <el-dialog v-model="dialogVisible" title="日志详情" width="760">
        <el-descriptions border :column="2">
            <el-descriptions-item label="时间">{{ record.createdAt }}</el-descriptions-item>
            <el-descriptions-item label="用户">{{ record.name }}</el-descriptions-item>
            <el-descriptions-item label="ip">{{ record.ip }}</el-descriptions-item>
            <el-descriptions-item label="操作">{{ record.operation }}</el-descriptions-item>
            <el-descriptions-item label="内容">
                <el-input v-model="record.content" :rows="5" type="textarea" readonly />
            </el-descriptions-item>
        </el-descriptions>
        <template #footer>
            <span class="dialog-footer">
                <el-button type="primary" @click="dialogVisible = false">确定</el-button>
            </span>
        </template>
    </el-dialog>
    <el-row justify="start">
        <el-form :inline="true">
            <el-form-item label="用户名">
                <el-input v-model="queryData.name" placeholder="用户名" @keydown.enter="submit" clearable />
            </el-form-item>
            <el-form-item label="ip地址">
                <el-input v-model="queryData.ip" placeholder="ip地址" @keydown.enter="submit" clearable />
            </el-form-item>
            <el-form-item label="操作">
                <el-input v-model="queryData.operation" placeholder="操作" @keydown.enter="submit" clearable />
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="submit">查询</el-button>
            </el-form-item>
        </el-form>
    </el-row>
    <el-table v-loading="loading" :data="result.data.rows" style="width: 100%; margin-bottom: 20px" row-key="id" border
        @sort-change="sortChange">
        <el-table-column prop="id" label="id" sortable="custom" width="100" />
        <el-table-column prop="createdAt" label="时间" width="160" />
        <el-table-column prop="name" label="用户" />
        <el-table-column prop="ip" label="ip" />
        <el-table-column prop="operation" label="操作" />
        <el-table-column align="right" prop="duration" label="用时(ms)" sortable="custom" width="110" />
        <el-table-column prop="address" label="详情" width="60">
            <template #default="scope">
                <el-button link type="primary" size="small" @click="show(scope.row)">详情</el-button>
            </template>
        </el-table-column>
    </el-table>
    <el-row justify="end">
        <el-pagination layout="total, sizes, prev, pager, next, jumper" v-model:current-page="queryData.page"
            v-model:page-size="queryData.pageSize" :page-count="result.data.totalPages" :page-sizes="[20, 50, 80, 100]"
            :total="result.data.totalRecords" />
    </el-row>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue';
import systemApi from '@/api/system';
import { useRoute } from 'vue-router';

const dialogVisible = ref(false);
const loading = ref(true);

const queryData = reactive({
    ip: '',
    name: '',
    operation: '',
    page: 1,
    pageSize: 20,
    sort: ''
});

const result = reactive({
    data: {
        rows: [],
        totalPages: 0,
        totalRecords: 0
    }
});

const record = reactive({
    content: '',
    ip: '',
    name: '',
    createdAt: '',
    operation: ''
});


const route = useRoute();
Object.assign(queryData, route.query);

const show = (row: any) => {
    Object.assign(record, row);
    dialogVisible.value = true;
};

const query = async () => {
    loading.value = true;
    const queryParams = { ...queryData };
    const resultData = await systemApi.getLogList(queryParams);
    Object.assign(result.data, resultData.data);
    loading.value = false;
};

const sortChange = (param: any) => {
    if (param.order) {
        queryData.sort = `${param.prop},${param.order[0] === 'a' ? 'asc' : 'desc'}`;
    } else {
        queryData.sort = '';
    }
    if (queryData.page == 1) {
        query()
    } else {
        queryData.page = 1
    }
};

const submit = () => {
    if (queryData.page !== 1) {
        queryData.page = 1;
    } else {
        query();
    }
};

watch(() => ({
    page: queryData.page
}), () => {
    query();
}, { deep: true });

onMounted(() => {
    query();
});
</script>
<style lang="scss" scoped></style>
