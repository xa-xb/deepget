<template>
    <div class="gen-page">
        <el-form :model="form" :rules="formRules" ref="formRef" label-width="120px">
            <el-row>
                <el-col :span="24">
                    <el-form-item label="前端目录" prop="frontendPath">
                        <el-input v-model="form.frontendPath" placeholder="前端目录" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="模块(目录)" prop="moduleId">
                        <el-select v-model="form.moduleId" placeholder="Select">
                            <el-option v-for="item in modules" :key="item.id" :label="item.name" :value="item.id">
                                <span style="float: left">{{ item.name }}</span>
                                <span style="float: right; color: var(--el-text-color-secondary); font-size: 13px;">
                                    {{ item.label }}
                                </span>
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="业务名称" prop="businessName">
                        <el-input v-model="form.businessName" placeholder="请输入名称" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="菜单名称" prop="menuName">
                        <el-input v-model="form.menuName" placeholder="请输入名称" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="验证码" prop="captcha">
                        <div class="captcha-container">
                            <el-input v-model="form.captcha" placeholder="请输入验证码" />
                            <img :src="captchaUrl" @click="refreshCaptcha" class="captcha-img" alt="验证码" />
                        </div>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item>
                <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
            </el-form-item>
            <el-row>
                <el-col :span="24" style="padding: 0 10px;">
                    <el-input v-model="apiResult" :rows="12" type="textarea" disabled />
                </el-col>
            </el-row>
        </el-form>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, useTemplateRef, onMounted } from 'vue'
import develApi from '@/api/devel'
import systemApi from '@/api/system'
import type { FormInstance } from 'element-plus'

const apiResult = ref("")
const loading = ref(false)
const formRef = useTemplateRef<FormInstance>('formRef')
const modules = reactive([] as any)

// Add new form related state
const form = reactive({
    frontendPath: '' as string,
    moduleId: undefined as number | undefined,
    businessName: '',
    menuName: '',
    captcha: ''
})

const formRules = {
    frontendPath: [{ required: true, message: '请输入前端项目路径', trigger: ['blur', 'change'] }],
    moduleId: [{ required: true, message: '请选择模块', trigger: ['blur', 'change'] }],
    businessName: [{ required: true, message: '请输入名称', trigger: ['blur', 'change'] }],
    menuName: [{ required: true, message: '请输入名称', trigger: ['blur', 'change'] }],
    captcha: [{ required: true, message: '请输入验证码', trigger: ['blur', 'change'] }]
}

const captchaUrl = ref('')

const refreshCaptcha = () => {
    captchaUrl.value = `${import.meta.env.VITE_BASE_URL}/main/captcha?t=${Date.now()}`
}

// Add to onMounted
onMounted(() => {
    query()
    refreshCaptcha()
})

const handleSubmit = async () => {
    if (!formRef.value) return
    try {
        await formRef.value.validate()
        loading.value = true
        apiResult.value = ''
        const res = await develApi.addGen(form)
        apiResult.value = res.data
        form.captcha = ''
        refreshCaptcha()
    } finally {
        loading.value = false
    }
}

const query = async () => {
    loading.value = true
    const r = await systemApi.getMenuList({ parentId: 0 })
    modules.splice(0, modules.length, ...r.data.map((item: { id: number, route: String, name: string }) => ({
        id: item.id,
        name: item.route.substring(1),
        label: item.name
    })))
    const r1 = await develApi.getGenList({}) as any
    Object.assign(form, r1.data)
    loading.value = false
}

</script>

<style lang="scss" scoped>
.gen-page {
    padding: 30px;
}

.captcha-container {
    display: flex;
    gap: 10px;
    align-items: center;
}

.captcha-img {
    height: 32px;
    cursor: pointer;
}
</style>
