<template>
    <!-- model dialog -->
    <el-dialog v-model="modelDialog.visible" :title="(modelDialog.id === null ? '新建' : '修改') + '模型'" width="860"
        @close="orgFormRef?.clearValidate()">
        <el-form label-width="92px" :model="modelDialog" v-loading="modelDialog.loading" ref="orgFormRef"
            :rules="orgRules">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="供应商" prop="providerId">
                        <el-select v-model="modelDialog.providerId" placeholder="Select">
                            <el-option v-for="item in aiProviders.value" :key="item.id" :label="item.name"
                                :value="item.id" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="免费">
                        <el-radio-group v-model="modelDialog.free">
                            <el-radio :value="true">是</el-radio>
                            <el-radio :value="false">否</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="单token价格">
                        <el-input-number v-model="modelDialog.cnyPerToken" style="width: 200px;" :precision="10"
                            :step="0.0000000001" :min="0.0000000001" :max="10" controls-position="right">
                            <template #suffix>
                                <span>RMB</span>
                            </template>
                        </el-input-number>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="模型名称" prop="name">
                        <el-input v-model="modelDialog.name" type="text" autocomplete="off" />
                    </el-form-item>
                </el-col>

                <el-col :span="12">
                    <el-form-item label="系统名称" prop="sysName">
                        <el-input v-model="modelDialog.sysName" type="text" autocomplete="off" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="openrouter名称" prop="openRouterName">
                        <el-input v-model="modelDialog.openRouterName" type="text" autocomplete="off" />
                    </el-form-item>
                </el-col>

                <el-col :span="12">
                    <el-form-item label="使用状态">
                        <el-radio-group v-model="modelDialog.enabled">
                            <el-radio :value="true">启用</el-radio>
                            <el-radio :value="false">停用</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="排序" prop="orderNum">
                        <el-input-number v-model="modelDialog.orderNum" :min="1" :max="99999" style="width: 200px;" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="24">
                    <el-form-item label="简介">
                        <el-input v-model="modelDialog.intro" :rows="2" type="textarea" />
                    </el-form-item>
                </el-col>
            </el-row>


        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="modelDialog.visible = false">取消</el-button>
                <el-button type="primary" @click="saveOrg">确定</el-button>
            </span>
        </template>
    </el-dialog>

    <el-row justify="start">
        <el-button type="primary" plain @click="newModel" v-if="userStore.hasAuthorize('/ai/model/add')">新建</el-button>
    </el-row><br>
    <el-table v-loading="loading" :data="result.data" style="width: 100%; margin-bottom: 20px" row-key="id" border
        default-expand-all>
        <el-table-column prop="id" label="id" width="80" />
        <el-table-column>
            <template #header>
                名称<br>
            </template>
            <template #default="scope">
                {{ scope.row.name }}<br>
                {{ scope.row.providerName }}
            </template>
        </el-table-column>
        <el-table-column>
            <template #header>
                系统名称<br>
                openrouter名称
            </template>
            <template #default="scope">
                {{ scope.row.sysName }}<br>
                {{ scope.row.openRouterName }}
            </template>
        </el-table-column>
        <el-table-column prop="name" label="状态" width="80">
            <template #default="scope">
                <el-tag size="small" type="success" v-if="scope.row.enabled">启用</el-tag>
                <el-tag size="small" type="warning" v-else>停用</el-tag>
                <el-tag size="small" type="success" v-if="scope.row.free">free</el-tag>
                <el-tag size="small" type="primary" v-else>paid</el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="120" />
        <el-table-column prop="createdAt" label="建立时间" />
        <el-table-column label="操作" width="120">
            <template #default="scope">
                <el-button link type="primary" size="small" @click="editOrg(scope.row)"
                    v-if="userStore.hasAuthorize('/ai/model/edit')">修改</el-button>
                <el-button link type="primary" size="small" @click="delOrg(scope.row)"
                    v-if="userStore.hasAuthorize('/ai/model/delete')">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
</template>
<script setup lang="ts">
import { nextTick, ref, reactive, useTemplateRef, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import aiApi from '@/api/ai'
import type { FormInstance } from 'element-plus'

const userStore = useUserStore()

const result = reactive({
    data: []
})
const aiProviders = reactive([]) as any
const modelDialog = reactive({
    id: null as number | null,
    name: '' as string,
    sysName: '' as string,
    openRouterName: '' as string,
    free: false as boolean,
    cnyPerToken: 0 as number,
    providerId: 0 as number,
    providerName: "" as string,
    enabled: true as boolean,
    intro: '' as string,
    orderNum: 100 as number,
    createdAt: '' as string,
    loading: false,
    visible: false
})

const orgFormRef = useTemplateRef<FormInstance>('orgFormRef')
const orgRules = {
    providerId: [
        { required: true, message: '请选择供应商', trigger: ['blur', 'change'] }
    ],
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' }
    ],

    orderNum: [
        { required: true, message: '请输入排序', trigger: 'blur' }
    ]
}

const loading = ref(true)

const query = async () => {
    loading.value = true
    const resultData = await aiApi.getModelList({})
    Object.assign(result, resultData)
    loading.value = false
}

const newModel = async () => {
    Object.assign(modelDialog, {
        id: null,
        cnyPerToken: 0 as number,
        name: '',
        sysName: '',
        openRouterName: '',
        free: false,
        intro: '',
        orderNum: 100,
        providerId: null,
        visible: true
    })
    await nextTick()
    await nextTick()
    orgFormRef.value?.clearValidate()
}
const saveOrg = async () => {
    orgFormRef.value?.validate(async (valid: boolean) => {
        if (!valid) return
        modelDialog.loading = true
        const { loading, visible, providerName, ...postData } = modelDialog
        try {
            if (modelDialog.id == null) {
                await aiApi.addModel(postData)
                ElMessage.success('新建模型完成')
            } else {
                await aiApi.editModel(postData)
                ElMessage.success('修改模型完成')
            }
            modelDialog.visible = false
            query()
        }
        catch (err) {

        }
        finally {

            modelDialog.loading = false
        }
    })
}

const editOrg = (row: any) => {
    Object.assign(modelDialog, row)
    modelDialog.visible = true
}

const delOrg = (row: any) => {
    ElMessageBox.confirm('确定删除该模型吗？').then(() => {
        aiApi.deleteModel({ id: row.id }).then(() => {
            ElMessage.success('删除模型完成')
            query()
        })
    })
}

onMounted(async () => {
    aiProviders.value = (await aiApi.getProviderList({})).data
    query()
})

</script>

<style lang="scss" scoped></style>