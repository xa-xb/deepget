<template>
    <!-- record dialog -->
    <el-dialog v-model="recordDialog.visible" :title="'新建邮件群发'" width="600" @close="formRef?.clearValidate()">
        <el-form label-width="110px" :model="recordDialog" v-loading="recordDialog.loading" ref="formRef">
            <el-form-item label="发件箱">
                <el-select v-model="recordDialog.sender" placeholder="请选择发件箱" style="width: 300px">
                    <el-option v-for="item in senders" :key="item" :label="item" :value="item" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-checkbox v-model="recordDialog.force">强制发送(不检查30天内重复发送)</el-checkbox>
            </el-form-item>
            <el-form-item label="邮箱" prop="name">
                <el-input v-model="recordDialog.emials" type="textarea" autocomplete="off" :rows="8" />
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
        <el-button type="primary" plain @click="newRec"
            v-if="userStore.hasAuthorize(`/email/emailBlast/add`)">新建任务</el-button>
        <el-button type="success" plain @click="query">刷新列表</el-button>
    </el-row>
    <br>
    <el-text type="primary">共 {{ resultLength }} 条, 队列任务会在每天的 08:00-18:00 之间逐条发送, 同一地址在30天内不会重复发送。
        <el-button type="danger" @click="deleteAll" size="small">清空队列</el-button>
    </el-text>
    <el-table v-loading="loading" :data="result" style="width: 100%; margin-bottom: 20px" row-key="id" border
        default-expand-all>
        <el-table-column prop="to" label="to" />
        <el-table-column prop="force" label="force" width="76">
            <template #default="scope">
                <el-tag v-if="scope.row.force" type="danger">是</el-tag>
                <el-tag v-else type="info">否</el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="sender" label="sender" />
        <el-table-column prop="createdAt" label="created" />
        <el-table-column label="操作" width="120">
            <template #default="scope">
                <el-button link type="primary" size="small" @click="deleteRec(scope.row)"
                    v-if="userStore.hasAuthorize(`/email/emailBlast/delete`)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
</template>
<script setup lang="ts">
import { computed, ref, reactive, useTemplateRef, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import emailApi from '@/api/email'
import type { FormInstance } from 'element-plus'

const userStore = useUserStore()

const result = ref([])
const resultLength = ref(0)
const senders = reactive([] as string[])
const recordDialog = reactive({
    emials: '' as string,
    force: false as boolean,
    sender: '' as string,
    loading: false,
    visible: false
})

const formRef = useTemplateRef<FormInstance>('formRef')


const loading = ref(true)

const query = async () => {
    loading.value = true
    const resp = await emailApi.getEmailBlastList() as any
    result.value = resp.data
    resultLength.value = resp.data.length
    loading.value = false
}

const newRec = () => {
    Object.assign(recordDialog, {
        emials: '',
        force: false,
        sender: '',
        visible: true
    })
}
const saveRec = async () => {
    formRef.value?.validate(async (valid: boolean) => {

        const emailList = strToEmailList(recordDialog.emials)
        if (emailList.length === 0) {
            ElMessage.error('请填写至少一个邮箱')
            return
        }
        recordDialog.loading = true
        const postData = {
            emails: emailList,
            force: recordDialog.force,
            sender: recordDialog.sender
        }
        try {
            await emailApi.addEmailBlast(postData)
            ElMessage.success(`新建任务完成`)
            recordDialog.visible = false
            query()
        }
        finally {
            recordDialog.loading = false
        }
    })
}

const deleteRec = (row: any) => {
    ElMessageBox.confirm(`确定删除该邮件吗？`).then(() => {
        emailApi.deleteEmailBlast({ id: row.id }).then(() => {
            ElMessage.success(`已删除`)
            query()
        })
    }).catch(() => {
        // on cancel
    })
}
const deleteAll = () => {
    ElMessageBox.confirm(`确定清空队列吗？`).then(() => {
        emailApi.deleteEmailBlastAll().then(() => {
            ElMessage.success(`已清空`)
            query()
        })
    }).catch(() => {
        // on cancel
    })
}

onMounted(async () => {
    let resp = await emailApi.getEmailSenderList()
    Object.assign(senders, resp.data)
    query()
})

const strToEmailList = (str: string) => {
    return str.split(/[\s,;]+/).filter(s => s.length > 0)
}
</script>

<style lang="scss" scoped></style>