<template>
    <!-- organization dialog -->
    <el-dialog v-model="recordDialog.visible" :title="(recordDialog.id === null ? '新建' : '修改') + 'AI供应商'" width="560"
        @close="orgFormRef?.clearValidate()">
        <el-form label-width="110px" :model="recordDialog" v-loading="recordDialog.loading" ref="orgFormRef"
            :rules="orgRules">
            <el-form-item label="名称" prop="name">
                <el-input v-model="recordDialog.name" type="text" autocomplete="off" />
            </el-form-item>
            <el-form-item label="图标(svg)">
                <el-input v-model="recordDialog.iconSvg" type="textarea" :rows="2" />
            </el-form-item>
            <el-form-item label="api协议" prop="apiCompatible">
                <el-select v-model="recordDialog.apiCompatible" placeholder="请选择api协议">
                    <el-option v-for="item in apiCompatibleOptions" :key="item" :label="item" :value="item" />
                </el-select>
            </el-form-item>
            <el-form-item label="网址" prop="url">
                <el-input v-model="recordDialog.url" type="text" autocomplete="off" />
            </el-form-item>
            <el-form-item label="open route only">
                <el-radio-group v-model="recordDialog.openRouter">
                    <el-radio :value="true">启用</el-radio>
                    <el-radio :value="false">停用</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="api url">
                <el-input v-model="recordDialog.apiUrl" type="text" autocomplete="off" />
            </el-form-item>
            <el-form-item label="api key">
                <el-input v-model="recordDialog.apiKey" type="text" autocomplete="off" />
            </el-form-item>
            <el-form-item label="排序" prop="orderNum">
                <el-input-number v-model="recordDialog.orderNum" :min="1" :max="9999" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="recordDialog.visible = false">取消</el-button>
                <el-button type="primary" @click="saveOrg">确定</el-button>
            </span>
        </template>
    </el-dialog>

    <el-row justify="start">
        <el-button type="primary" plain @click="newOrg" v-if="userStore.hasAuthorize('/ai/org/add')">新建</el-button>
    </el-row><br>
    <el-table v-loading="loading" :data="result.data" style="width: 100%; margin-bottom: 20px" row-key="id" border
        default-expand-all>
        <el-table-column prop="id" label="id" width="80" />
        <el-table-column label="名称">
            <template #default="scope">
                <div class="icon" v-html="scope.row.iconSvg">
                </div>
                {{ scope.row.name }}
            </template>
        </el-table-column>
        <el-table-column prop="openRoute" label="open router">
            <template #default="scope">
                <el-tag type="success" size="small" v-if="scope.row.openRouter">启用</el-tag>
                <el-tag type="warning" size="small" v-if="!scope.row.openRouter">停用</el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="120" />
        <el-table-column prop="createdAt" label="建立时间" />
        <el-table-column label="操作" width="120">
            <template #default="scope">
                <el-button link type="primary" size="small" @click="editOrg(scope.row)"
                    v-if="userStore.hasAuthorize('/ai/org/edit')">修改</el-button>
                <el-button link type="primary" size="small" @click="delOrg(scope.row)"
                    v-if="userStore.hasAuthorize('/ai/org/delete')">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
</template>
<script setup lang="ts">
import { nextTick, ref, reactive, onMounted, useTemplateRef } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import aiApi from '@/api/ai'
import type { FormInstance } from 'element-plus'

const userStore = useUserStore()

const result = reactive({
    data: []
})
const apiCompatibleOptions = ref([])
const recordDialog = reactive({
    id: null as number | null,
    name: '' as string,
    iconSvg: '' as string,
    apiCompatible: '' as string,
    url: '' as string,
    openRouter: false as boolean,
    apiUrl: '' as string,
    apiKey: '' as string,
    orderNum: 100 as number,
    createdAt: '' as string,
    loading: false,
    visible: false
})

const orgFormRef = useTemplateRef<FormInstance>('orgFormRef')

const orgRules = {
    name: [
        { required: true, message: '请输入名称', trigger: ['blur', 'change'] }
    ],
    apiCompatible: [
        { required: true, message: '请选择api协议', trigger: ['blur', 'change'] }
    ],
    url: [
        { required: true, message: '请输入网址', trigger: ['blur', 'change'] }
    ],
    orderNum: [
        { required: true, message: '请输入排序', trigger: ['blur', 'change'] }
    ]
}

const loading = ref(true)

const query = async () => {
    loading.value = true
    const resultData = await aiApi.getProviderList({})
    Object.assign(result, resultData)
    loading.value = false
}

const newOrg = async () => {
    Object.assign(recordDialog, {
        id: null,
        name: '',
        iconSvg: '',
        apiCompatible: '',
        url: '',
        apiUrl: '',
        apiKey: '',
        orderNum: 100,
        visible: true
    })
    await nextTick()
    await nextTick()
    orgFormRef.value?.clearValidate()
}
const saveOrg = async () => {
    orgFormRef.value?.validate(async (valid: boolean) => {
        if (!valid) return
        recordDialog.loading = true
        const { loading, visible, ...postData } = recordDialog
        try {
            if (recordDialog.id == null) {
                await aiApi.addProvider(postData)
                ElMessage.success('新建AI供应商完成')
            } else {
                await aiApi.editProvider(postData)
                ElMessage.success('修改AI供应商完成')
            }
            recordDialog.visible = false
            query()
        }
        finally {
            recordDialog.loading = false
        }
    })
}

const editOrg = (row: any) => {
    Object.assign(recordDialog, row)
    recordDialog.visible = true
}

const delOrg = (row: any) => {
    ElMessageBox.confirm('确定删除该AI供应商吗？').then(() => {
        aiApi.deleteProvider({ id: row.id }).then(() => {
            ElMessage.success('删除AI供应商完成')
            query()
        })
    })
}


onMounted(async () => {
    loading.value = true
    const resp = await aiApi.getProviderApiCompatibles()
    apiCompatibleOptions.value = resp.data
    query()
})


</script>

<style lang="scss" scoped>
.icon {
    display: inline-block;
    margin: -0.1em 0.2em 0 0;
    width: 1.4em;
    height: 1.4em;
    border-radius: 10%;
    display: inline-block;
    vertical-align: middle
}
</style>