<template>
    <!-- test email dialog -->
    <el-dialog v-model="testEmailDialog.visible" title="发送测试邮件" width="460" @close="testEmailFormRef?.clearValidate()">
        <el-form label-width="80px" :model="testEmailDialog" v-loading="testEmailDialog.loading" ref="testEmailFormRef"
            :rules="testemailRules">
            <el-form-item label="收件邮箱" prop="email">
                <el-input v-model="testEmailDialog.email" type="text" autocomplete="off"
                    @keydown.enter.prevent="testEmail" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="testEmailDialog.visible = false">取消</el-button>
                <el-button type="primary" @click="testEmail">确定</el-button>
            </span>
        </template>
    </el-dialog>

    <el-form label-position="top" :model="record" v-loading="loading" :disabled="!hasEditAuthroize">
        <el-card>
            <template #header> 系统 </template>
            <el-row>
                <el-col :span="6">
                    <el-form-item label="网站名称">
                        <el-input v-model="record.siteName" placeholder="网站名称" />
                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item label="网址">
                        <el-input v-model="record.siteUrl" placeholder="网址" />
                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item label="备案号">
                        <el-input v-model="record.icpNumber" placeholder="备案号" />
                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item label="手机号日限额(短信)">
                        <el-input-number inputmode="numeric" :min="0" v-model="record.dailySmsLimit" />
                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item label="邮箱地址日限额(Email)">
                        <el-input-number inputmode="numeric" :min="0" v-model="record.dailyEmailLimit" />
                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item>
                        <template #label>
                            真实 IP 解析头
                            <el-tooltip class="item" placement="top">
                                <template #content>
                                    用于获取通过CDN访问的用户真实IP<br>
                                    留空或该头无值时系统优先从X-Forwarded-For里获取<br>
                                    Cloudflare → CF-Connecting-IP<br>
                                    Akamai → True-Client-IP<br>
                                    阿里云 CDN → Ali-CDN-Real-IP<br>
                                    腾讯云 CDN → X-Forwarded-For 或 X-Real-IP<br>
                                </template>
                                <el-icon style=" cursor: pointer;">
                                    <QuestionFilled />
                                </el-icon>
                            </el-tooltip>
                        </template>
                        <el-input v-model="record.realIpHeader" placeholder="真实 IP 解析头" />

                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item label="前端加密">
                        <el-button type="warning" @click="refreshRsaKey">点击刷新RSA密钥对</el-button>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-card><br>

        <el-card>
            <template #header>邮件SMTP服务器</template>
            <el-table :data="record.smtpServers" style="width: 100%">
                <el-table-column prop="name" label="邮箱名称">
                    <template #default="scope">
                        <el-input v-model="scope.row.name" placeholder="邮箱名称" />
                    </template>
                </el-table-column>
                <el-table-column prop="password" label="邮箱密码">
                    <template #default="scope">
                        <el-input v-model="scope.row.password" placeholder="邮箱密码" />
                    </template>
                </el-table-column>
                <el-table-column prop="host" label="Host">
                    <template #default="scope">
                        <el-input v-model="scope.row.host" placeholder="Host" />
                    </template>
                </el-table-column>
                <el-table-column prop="port" label="Port" width="120">
                    <template #default="scope">
                        <el-input type="number" v-model="scope.row.port" placeholder="Port" />
                    </template>
                </el-table-column>
                <el-table-column label="安全" width="150" align="center">
                    <template #default="scope">
                        <el-select v-model="scope.row.starttls" placeholder="Select">
                            <el-option v-for="item in starttlsOptions" :label="item.label" :value="item.value" />
                        </el-select>
                    </template>
                </el-table-column>
                <el-table-column label="启用" width="100" align="center">
                    <template #default="scope">
                        <el-switch v-model="scope.row.enable" inline-prompt :active-icon="Check"
                            :inactive-icon="Close" />
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="160" align="center">
                    <template #default="scope">
                        <el-button-group>
                            <el-button type="primary" plain :icon="Top" @click="swap(scope.$index, scope.$index - 1)" />
                            <el-button type="primary" plain :icon="Delete"
                                @click="record.smtpServers.splice(scope.$index, 1)" />
                            <el-button type="primary" plain :icon="Bottom"
                                @click="swap(scope.$index, scope.$index + 1)" />
                        </el-button-group>
                    </template>
                </el-table-column>
                <template #append>
                    <el-row style="margin: 0.8em auto">
                        <el-col :span="20" style=" text-align: left;">
                            <el-button type="warning" @click="showTestEmailDialog">
                                发送测试邮件
                            </el-button>
                        </el-col>
                        <el-col :span="4" style="text-align: right;">
                            <el-button type="success"
                                @click="record.smtpServers.push(Object.assign({}, SMTP_SERVER_EMPTY))">增加</el-button>

                        </el-col>
                    </el-row>
                </template>
            </el-table>
            <el-row style="padding-top:0.8em">
                <el-text>SMTP需要传输层加密协议: SSL/TLS协议(默认端口:465），或STARTTLS协议(默认端口:587)。
                </el-text>
            </el-row>
        </el-card><br>

        <el-card>
            <template #header> 开发工具 </template>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="前端项目路径">
                        <el-input v-model="record.frontendPath" placeholder="前端项目路径" />
                    </el-form-item>
                </el-col>
            </el-row>
        </el-card><br>
        <el-row justify="center">
            <el-button type="primary" @click="submit" v-if="hasEditAuthroize">保存</el-button>
        </el-row>
    </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, useTemplateRef, onMounted } from 'vue'
import systemApi from '@/api/system'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { useUserStore } from '@/stores/user'
import { Bottom, Check, Close, Delete, Top } from '@element-plus/icons-vue'
import { rsaEncrypt } from '@/utils';


// 获取用户权限
const userStore = useUserStore()
const hasEditAuthroize = userStore.hasAuthorize("/system/config/edit")

// 创建响应式数据
const loading = ref(true)
const record = reactive({
    "dailyEmailLimit": 0 as number,
    "dailySmsLimit": 0 as number,
    "icpNumber": '',
    "keywords": '',
    "realIpHeader": '',
    "siteName": '',
    "siteUrl": '',
    "smtpServers": [] as any,
    frontendPath: '' as string
})
const SMTP_SERVER_EMPTY = {
    "name": '',
    "password": '',
    "host": '',
    "port": '',
    "starttls": false as boolean,
    "enable": true as boolean
}
const starttlsOptions = reactive([
    {
        value: false,
        label: 'SSL/TLS',
    },
    {
        value: true,
        label: 'STARTTLS',
    }])


const encrypt = async (smtpServers: any[]) => {
    const result = []
    for (const item of smtpServers) {
        let obj = { ...item }
        obj.password = await rsaEncrypt(obj.password)
        result.push(obj)
    }
    return result
}
// 初始化方法
const query = async () => {
    loading.value = true
    systemApi.getConfig().then((response) => {
        Object.assign(record, response.data)
    }).finally(() => loading.value = false)
}
const refreshRsaKey = async () => {

    ElMessageBox.confirm(
        '确定要刷新RSA密钥对么？',
        '提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(async () => {
            await systemApi.refreshRsaKey()
            // 用户点击了确认
            ElMessage({
                type: 'success',
                message: 'RSA密钥对刷新成功！',
            })
        })

}

// 提交表单方法
const submit = async () => {
    loading.value = true
    const postData = { ...record }
    postData.smtpServers = await encrypt(postData.smtpServers)
    systemApi.saveConfig(postData).then(() => {
        ElMessage.success('保存成功')
        query()
    }).finally(() => loading.value = false)
}
const swap = (from: number, to: number) => {
    if (record.smtpServers.length < 2) return
    if (to < 0) to = record.smtpServers.length - 1
    else if (to >= record.smtpServers.length) to = 0
    let obj = Object.assign({}, record.smtpServers[from])
    record.smtpServers[from] = Object.assign({}, record.smtpServers[to])
    record.smtpServers[to] = obj

}

const testEmailDialog = reactive({
    loading: false,
    email: '',
    visible: false
})
const testEmailFormRef = useTemplateRef<FormInstance>('testEmailFormRef')
const testemailRules = {
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email' as const, message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] }
    ]
}

const showTestEmailDialog = () => {
    Object.assign(testEmailDialog, {
        email: '',
        visible: true
    })
}
const testEmail = () => {
    // 验证表单
    testEmailFormRef.value?.validate(async (valid) => {
        if (valid) {
            let postData = {
                to: testEmailDialog.email,
                smtpServers: await encrypt(record.smtpServers)
            }
            systemApi.testEmail(postData).then(() => {
                ElMessage.success('测试请求已发送，请稍后检查邮箱.')
                testEmailDialog.visible = false
            })
        } else {
            ElMessage.error('请填写有效的邮箱地址')
        }
    })
}

// 在组件挂载时初始化
onMounted(() => {
    query()
})
</script>


<style lang="scss" scoped>
.el-col {
    padding-right: 1em;
}
</style>