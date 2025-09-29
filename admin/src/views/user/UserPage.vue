<template>
    <el-dialog v-model="detailVisible" title="用户详情" width="1060">
        <el-descriptions border :column="4">
            <el-descriptions-item label="ID">{{ record.id }}</el-descriptions-item>
            <el-descriptions-item label="账号">{{ record.name }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ record.statusStr }}</el-descriptions-item>
            <el-descriptions-item label="credits">{{ record.credits }}</el-descriptions-item>
            <el-descriptions-item label="用户级别">{{ record.level }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ record.genderStr }}</el-descriptions-item>
            <el-descriptions-item label="手机号码">{{ record.phone }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ record.email }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ record.createdAt }}</el-descriptions-item>
            <el-descriptions-item label="注册IP">{{ record.createdIp }}</el-descriptions-item>
            <el-descriptions-item label="最近登录">{{ record.lastSignInAt }}</el-descriptions-item>
            <el-descriptions-item label="最近登录IP">{{ record.lastSignInIp }}</el-descriptions-item>
        </el-descriptions>
        <br>
        <el-table border :data="record.address">
            <el-table-column label="收货地址">
                <template #default="scope">
                    {{ `${scope.row.regionStr} ${scope.row.address} ${scope.row.consignee} ${scope.row.phone} ` }}
                </template>

            </el-table-column>
        </el-table>
        <template #footer>
            <span class="dialog-footer">
                <el-button type="primary" @click="detailVisible = false">确定</el-button>
            </span>
        </template>
    </el-dialog>

    <!-- change balance -->
    <el-dialog v-model="balanceDialog.visible" title="调整余额" width="560">
        <el-form label-width="110px" v-loading="balanceDialog.loading">
            <el-form-item label="用户">
                <el-input v-model="record.name" type="text" disabled />
            </el-form-item>
            <el-form-item label="余额">
                <el-input v-model="record.balance" type="text" disabled />
            </el-form-item>
            <el-form-item label="调整值" required>
                <el-input-number :controls="false" v-model="balanceDialog.change" :precision="2" />
            </el-form-item>
            <el-form-item label="备注" required>
                <el-input v-model="balanceDialog.note" type="text" autocomplete="off" />
            </el-form-item>

        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="balanceDialog.visible = false">取消</el-button>
                <el-button type="primary" @click="changeBalance">确定</el-button>
            </span>
        </template>
    </el-dialog>

    <!-- change password -->
    <el-dialog v-model="passwordDialog.visible" title="更改密码" width="560">
        <el-form label-width="110px" v-loading="passwordDialog.loading">
            <el-form-item label="账号">
                <el-input v-model="record.name" type="text" disabled />
            </el-form-item>
            <el-form-item label="密码" required>
                <el-input v-model="passwordDialog.pwd" type="password" autocomplete="off" />
            </el-form-item>
            <el-form-item label="重复密码" required>
                <el-input v-model="passwordDialog.checkPwd" type="password" autocomplete="off" />
            </el-form-item>
        </el-form>
        <el-text type="danger">注意：</el-text>更改密码会注销该用户所有客户端的登录状态
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="passwordDialog.visible = false">取消</el-button>
                <el-button type="primary" @click="changePassword">确定</el-button>
            </span>
        </template>
    </el-dialog>

    <!-- add user -->
    <el-dialog v-model="newuserDialog.visible" title="新建用户" width="560" @close="newuserFormRef?.clearValidate()">
        <el-form label-width="110px" :model="newuserDialog" v-loading="newuserDialog.loading" ref="newuserFormRef"
            :rules="newuserRules">
            <el-form-item label="用户名" prop="username">
                <el-input v-model="newuserDialog.username" type="text" autocomplete="off" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input v-model="newuserDialog.password" type="password" autocomplete="off" />
            </el-form-item>
            <el-form-item label="重复密码" prop="rePassword">
                <el-input v-model="newuserDialog.rePassword" type="password" autocomplete="off" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="newuserDialog.visible = false">取消</el-button>
                <el-button type="primary" @click="addUser">确定</el-button>
            </span>
        </template>
    </el-dialog>

    <el-row justify="start">
        <el-form :inline="true">
            <el-form-item label="用户名">
                <el-input v-model="queryForm.name" placeholder="用户名" @keydown.enter="submit" clearable />
            </el-form-item>

            <el-form-item label="登录状态">
                <el-select v-model="queryForm.signStatus" placeholder="Select">
                    <el-option v-for="item in signStatus" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="submit">查询</el-button>
            </el-form-item>
        </el-form>
    </el-row>
    <el-row justify="start">
        <el-button type="primary" plain @click="add" v-if="userStore.hasAuthorize('/user/user/add')">新建</el-button>
    </el-row><br>
    <el-table v-loading="loading" :data="result.data.rows" style="width: 100%; margin-bottom: 20px" row-key="id" border
        default-expand-all @sort-change="sortChange">
        <el-table-column prop="id" label="id" sortable="custom" width="80" />
        <el-table-column prop="name" label="账号" sortable="custom" />
        <el-table-column prop="credits" label="credits" sortable="custom" />
        <el-table-column prop="lastSignInAt" label="最近登录时间" sortable="custom" />
        <el-table-column prop="lastSignInIp" label="最近登录IP" />
        <el-table-column prop="createdAt" label="注册时间" />
        <el-table-column prop="address" label="操作" width="120">
            <template #default="scope">
                <el-button link type="primary" size="small" @click="show(scope.row)">详情</el-button>&ensp;
                <el-dropdown class="el-dropdown1" size="small" type="primary">
                    <el-text class="more" size="small" type="primary">更多&gt;&gt;</el-text>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item v-if="userStore.hasAuthorize('/user/user/changeBalance')"
                                @click="showBalanceDialog(scope.row)">调整余额</el-dropdown-item>
                            <el-dropdown-item
                                @click="router.push({ path: '/user/balanceLog', query: { name: scope.row.name } })"
                                v-if="userStore.hasAuthorize('/system/log/query')">余额日志</el-dropdown-item>
                            <el-dropdown-item v-if="userStore.hasAuthorize('/user/user/changePassword')"
                                @click="showPasswordDialog(scope.row)">更改密码</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
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
import { ref, reactive, useTemplateRef, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import userApi from '@/api/user'
import type { FormInstance } from 'element-plus'
import { rsaEncrypt } from '@/utils'

const router = useRouter()
const userStore = useUserStore()

const queryData = reactive({
    page: 1,
    pageSize: 20,
    sort: ''
})

const queryForm = reactive({
    name: '',
    signStatus: 1
})
const signStatus = reactive(
    [
        { id: 1, name: "全部" },
        { id: 2, name: "有登录记录" },
        { id: 3, name: "无登录记录" },
    ]
)
const result = reactive({
    data: {
        rows: [],
        totalPages: 0,
        totalRecords: 0
    }
})

const record: any = reactive({
    address: [] as any,
    id: 0
})

const balanceDialog = reactive({
    change: 0,
    note: '',
    loading: false,
    visible: false
})

const passwordDialog = reactive({
    loading: false,
    pwd: '',
    checkPwd: '',
    visible: false
})
const newuserDialog = reactive({
    loading: false,
    username: '',
    password: '',
    rePassword: '',
    visible: false
})
const newuserFormRef = useTemplateRef<FormInstance>('newuserFormRef')
const newuserRules = {
    username: [
        { required: true, message: '请输入用户名', trigger: ['blur', 'change'] }
    ],
    password: [
        { required: true, message: '密码不得为空', trigger: ['blur', 'change'] },
        { min: 8, message: '不能少于8位', trigger: ['blur', 'change'] }
    ],
    rePassword: [
        { required: true, message: '重复密码不得为空', trigger: ['blur', 'change'] }
    ]
}

const loading = ref(true)
const detailVisible = ref(false)

const query = async () => {
    loading.value = true
    const queryParams = { ...queryForm, ...queryData }
    const resultData = await userApi.getUserList(queryParams)
    Object.assign(result, resultData)
    loading.value = false
}
const add = async () => {
    Object.assign(newuserDialog, {
        email: '',
        password: '',
        rePassword: '',
        notify: false,
        visible: true
    })
}
const addUser = async () => {
    newuserFormRef.value?.validate(async (valid: boolean) => {
        if (!valid) return
        if (newuserDialog.password !== newuserDialog.rePassword) {
            ElMessage.error('两次密码不一致')
            return
        }
        newuserDialog.loading = true

        const postData = {
            username: newuserDialog.username,
            password: await rsaEncrypt(newuserDialog.password)
        }
        userApi.addUser(postData).then(() => {
            ElMessage.success('新建用户完成')
            newuserDialog.visible = false
            query()
        }).finally(() => {
            newuserDialog.loading = false
        })
    })

}
const show = (row: any) => {
    userApi.getUser(row).then((result) => {
        Object.assign(record, result.data)
        record.address.length = 0
        Object.assign(record.address, result.data.address)
        detailVisible.value = true
    })
}

const changeBalance = async () => {
    const data = {
        uid: record.id,
        amount: balanceDialog.change,
        note: balanceDialog.note
    }
    await userApi.changeBalance(data)
    ElMessage.success('余额调整完成')
    balanceDialog.visible = false
    query()
}

const changePassword = async () => {
    if (passwordDialog.pwd !== passwordDialog.checkPwd) {
        ElMessage.error('两次密码不一致')
        return
    }
    passwordDialog.loading = true
    const data = {
        id: record.id,
        password: await rsaEncrypt(passwordDialog.pwd)
    }
    userApi.changePassword(data).then(() => {
        ElMessage.success('密码更改完成')
        passwordDialog.visible = false
    }).finally(() => {
        passwordDialog.loading = false
    })

}

const showBalanceDialog = (row: any) => {
    Object.assign(record, row)
    balanceDialog.change = 0
    balanceDialog.note = '系统管理员调整'
    balanceDialog.loading = false
    balanceDialog.visible = true
}

const showPasswordDialog = (row: any) => {
    Object.assign(record, row)
    passwordDialog.loading = false
    passwordDialog.pwd = ''
    passwordDialog.checkPwd = ''
    passwordDialog.visible = true
}

const sortChange = (param: any) => {
    if (!param.order) {
        queryData.sort = ''
        return
    }
    const n = param.prop
    queryData.sort = `${n},${param.order[0] === 'a' ? 'asc' : 'desc'}`
}

const submit = () => {
    if (queryData.page !== 1) {
        queryData.page = 1
    } else {
        query()
    }
}

onMounted(() => {
    query()
})

watch(queryData, (newVal) => {
    query()
}, { deep: true })
</script>

<style lang="scss" scoped>
.el-dropdown1 {
    vertical-align: text-bottom;

    & .more {
        cursor: pointer;
    }
}
</style>