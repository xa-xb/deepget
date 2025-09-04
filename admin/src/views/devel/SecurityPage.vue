<template>
    <el-form>
        <el-row justify="start">
            <el-col :span="24">
                <el-input v-model="password" style="width: 340px" placeholder="请输入密码" clearable />
                <el-button :loading="passwordButtonLoading" type="primary" plain
                    @click="encodePassword">密码编码</el-button>
                <el-input v-model="passwordHash" style="width: 340px" readonly />
                <el-button @click="copyToClipboard(passwordHash)">复制到粘贴板</el-button>
            </el-col>
        </el-row>
    </el-form>

</template>
<script setup lang="ts">
import { ref } from 'vue'

import { ElMessage } from 'element-plus'
import develApi from '@/api/devel'
import { rsaEncrypt } from '@/utils'

const password = ref('')
const passwordHash = ref('')
const passwordButtonLoading = ref(false)

const encodePassword = async () => {
    passwordButtonLoading.value = true
    try {
        const resp = await develApi.encodePassword({ "password": await rsaEncrypt(password.value) }) as any
        passwordHash.value = resp.data
    }
    catch {
        passwordHash.value = ''
    }
    finally {
        passwordButtonLoading.value = false
    }


}

const copyToClipboard = async (text: string): Promise<void> => {
    try {
        await navigator.clipboard.writeText(text)
        ElMessage.success("已复制到粘贴板")
    } catch (err) {
        ElMessage.error("Failed to copy text:" + err)
    }
};


</script>

<style lang="scss" scoped></style>