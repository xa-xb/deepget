<template>
    <el-dialog v-model="dialogVisible" :title="record.userId === null ? '新建管理账号' : '修改管理账号'" width="460"
        @close="formRef?.clearValidate()">

        <el-form label-width="78px" :model="record" :rules="rules" inline ref="formRef">
            <el-row>
                <el-col :span="24">
                    <el-form-item label="账号" prop="account" v-if="record.id == null">
                        <el-input v-model="record.account" style="width:328px" />
                    </el-form-item>
                    <el-form-item label="用户名" v-if="record.id != null">
                        <el-input v-model="record.name" disabled style="width:328px" />
                    </el-form-item>
                    <el-form-item label="手机号码" v-if="record.id != null">
                        <el-input v-model="record.mobile" disabled style="width:328px" />
                    </el-form-item>
                    <el-form-item label="邮箱" v-if="record.id != null">
                        <el-input v-model="record.email" disabled style="width:328px" />
                    </el-form-item>
                    <el-form-item label="姓名">
                        <el-input v-model="record.trueName" placeholder="姓名" style="width:328px" />
                    </el-form-item>

                    <el-form-item label="角色">
                        <el-select v-model="record.roleIds" multiple placeholder="Select" style="width: 328px">
                            <el-option v-for="item in roles.value" :key="item.id" :label="item.name" :value="item.id" />
                        </el-select>
                    </el-form-item>

                    <el-form-item label="使用状态" required>
                        <el-radio-group v-model="record.enabled" style="width:328px">
                            <el-radio :value="true">启用</el-radio>
                            <el-radio :value="false">停用</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submit">确定</el-button>
            </span>
        </template>
    </el-dialog>
    <el-row justify="start">
        <el-button type="primary" plain @click="add" v-if="userStore.hasAuthorize('/system/admin/add')">新增</el-button>
    </el-row><br>
    <el-table v-loading="loading" :data="result.data.rows" style="width: 100%; margin-bottom: 20px" row-key="id" border>
        <el-table-column fixed prop="name" label="用户名" width="120" />
        <el-table-column fixed prop="mobile" label="手机号码" width="100" />
        <el-table-column prop="trueName" label="姓名" width="150" />
        <el-table-column prop="roleNames" label="角色" />
        <el-table-column prop="lastActionAt" label="最近活动时间" width="160" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column prop="enable" label="状态" width="75">
            <template #default="scope">
                <el-tag type="success" size="small" v-if="scope.row.enabled">启用</el-tag>
                <el-tag type="warning" size="small" v-if="!scope.row.enabled">停用</el-tag>
            </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="190">
            <template #default="scope">
                <el-button link type="primary" size="small" @click="edit(scope.row)" :icon="Edit"
                    v-if="userStore.hasAuthorize('/system/admin/edit')">编辑</el-button>
                <el-button link type="primary" size="small" @click="del(scope.row)" :icon="Delete"
                    v-if="userStore.hasAuthorize('/system/admin/delete')">删除</el-button>
                <el-button link type="primary" size="small" :icon="Memo"
                    @click="router.push({ path: '/system/log', query: { name: scope.row.name } })"
                    v-if="userStore.hasAuthorize('/system/log/query')">日志</el-button>
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
import { ref, reactive, useTemplateRef, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import systemApi from '@/api/system'
import { Delete, Edit, Memo } from '@element-plus/icons-vue'
import { objAssign } from '@/utils/index'
import { useUserStore } from '@/stores/user'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// Constants and State
const RECORD_EMPTY = {
    id: null,
    userId: null,
    account: '',
    trueName: '',
    roleIds: [] as any[],
    enabled: true,
} as any

const dialogVisible = ref(false)
const loading = ref(true)
const record = reactive({ ...RECORD_EMPTY }) as any
const queryData = reactive({
    page: 1,
    pageSize: 20,
})
const result = reactive({
    data: {
        rows: [],
        totalPages: 0,
        totalRecords: 0
    },
})
const roles = reactive([]) as any
const rules = {
    account: [{ required: true, message: '请输入账号', trigger: 'blur' }]
}

const formRef = useTemplateRef<FormInstance>('formRef')

// Methods
const query = async () => {
    loading.value = true
    const queryParams = { ...record, ...queryData }
    systemApi.getAdminList(queryParams).then(res => {
        result.data = res.data
    }).finally(() => {
        loading.value = false
    })
}

const add = () => {
    Object.assign(record, { ...RECORD_EMPTY })
    dialogVisible.value = true
}

const edit = (row: any) => {
    Object.assign(record, row)
    dialogVisible.value = true
}

const del = (row: any) => {
    ElMessageBox.confirm(`确认删除后台账号 ${row.name}`, 'Warning', { type: 'warning' })
        .then(async () => {
            await systemApi.deleteAdmin(row)
            ElMessage.success('删除成功')
            await query()
        })
        .catch(() => ElMessage.info('删除已取消'))
}

const submit = () => {
    formRef.value?.validate(async (valid: boolean) => {
        if (!valid) return
        const data = { ...RECORD_EMPTY }
        objAssign(data, record)
        const save = record.userId == null ? systemApi.addAdmin : systemApi.editAdmin
        const msg = record.userId == null ? '新建' : '修改'
        await save(data)
        dialogVisible.value = false
        ElMessage.success(`${msg}管理账号成功`)
        await query()
    })
}

// Watchers
watch(queryData, query, { deep: true })

// Lifecycle Hooks
onMounted(async () => {
    roles.value = (await systemApi.getRoleListSimple()).data.rows
    query()
})
</script>

<style lang="scss" scoped></style>
