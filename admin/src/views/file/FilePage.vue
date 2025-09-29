<template>
    <el-row>
        <el-link underline="never" @click="goDir('..')">[返回上一级]</el-link>&ensp;
        <el-link underline="never" @click="query()">[刷新目录]</el-link>&emsp;
        <el-button link type="primary" v-if="userStore.hasAuthorize(`/file/file/create_dir`)"
            @click="dialogCreateDirVisible = true; form.newDir = ''">新建目录</el-button>&ensp;
        <el-button link type="primary" v-if="userStore.hasAuthorize(`/file/file/upload`)"
            @click="dialogUploadVisible = true">上传文件</el-button>&emsp;
        <el-text v-text="path" type="primary" />
    </el-row>
    <el-table v-loading="loading" :data="result.rows" style="width: 100%; margin-bottom: 20px" row-key="id" border
        default-expand-all>
        <el-table-column label="名称">
            <template #default="scope">
                <el-link v-if="scope.row.isDirectory" v-text="scope.row.name + '/'" @click="goDir(scope.row.name)" />
                <el-link v-else :href="'/img' + path + scope.row.name" v-text="scope.row.name" target="_blank" />
            </template>
        </el-table-column>

        <el-table-column label="大小" width="110">
            <template #default="scope">
                <span v-if="!scope.row.isDirectory">{{ scope.row.size.toLocaleString('en-us') }}</span>
            </template>
        </el-table-column>
        <el-table-column prop="modifiedAt" label="时间" width="180" />
        <el-table-column label="操作" width="120">
            <template #default="scope">
                <el-button link type="primary" size="small" @click="deleteRec(scope.row.name)"
                    v-if="userStore.hasAuthorize(`/file/file/delete`)">删除</el-button>
                <el-button link type="primary" size="small" @click="openRenameDialog(scope.row.name)"
                    v-if="userStore.hasAuthorize(`/file/file/rename`)">重命名</el-button>
            </template>
        </el-table-column>
    </el-table>

    <el-dialog v-model="dialogCreateDirVisible" title="新建目录" width="500">
        <el-form :model="form">
            <el-form-item label="目录名称" label-width="100">
                <el-input v-model="form.newDir" autocomplete="off" />
            </el-form-item>
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogCreateDirVisible = false">取消</el-button>
                <el-button type="primary" @click="createDir">
                    确定
                </el-button>
            </div>
        </template>
    </el-dialog>

    <el-dialog v-model="dialogUploadVisible" title="上传文件" width="500" @close="uploadRef?.clearFiles()">
        <el-upload ref="uploadRef" :http-request="uploadRequest" :limit="1" :auto-upload="true"
            :on-exceed="handleExceed">
            <template #trigger>
                <el-button type="primary">选择文件</el-button>
            </template>
            <el-progress v-if="uploading" :percentage="uploadProgress" />
        </el-upload>
    </el-dialog>

    <el-dialog v-model="dialogRenameVisible" title="重命名" width="500">
        <el-form :model="form">
            <el-form-item label="新名称" label-width="100">
                <el-input v-model="form.newName" autocomplete="off" />
            </el-form-item>
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogRenameVisible = false">取消</el-button>
                <el-button type="primary" @click="rename">
                    确定
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import fileApi from '@/api/file'
import type { UploadInstance, UploadProps, UploadRawFile } from 'element-plus'
import { genFileId } from 'element-plus'

const userStore = useUserStore()
const path = ref('/')

const result = reactive({
    rows: [] as any,
})

const loading = ref(true)
const dialogCreateDirVisible = ref(false)
const dialogUploadVisible = ref(false)
const dialogRenameVisible = ref(false)
const uploading = ref(false)
const uploadProgress = ref(0.00)

const form = reactive({
    newDir: '',
    oldName: '',
    newName: '',
})

const uploadRef = ref<UploadInstance>()

const handleExceed: UploadProps['onExceed'] = (files) => {
    uploadRef.value!.clearFiles()
    const file = files[0] as UploadRawFile
    file.uid = genFileId()
    uploadRef.value!.handleStart(file)
}

const goDir = async (dir: string) => {
    if (dir === '..') {
        if (path.value === '/') {
            ElMessage.warning('已经是根目录')
            return
        }
        const paths = path.value.split('/')
        paths.pop()
        paths.pop()
        path.value = paths.join('/') + '/'
    } else {
        path.value += dir + '/'
    }

    await query()
}

const query = async () => {
    loading.value = true
    if (!path.value.startsWith('/')) path.value = '/' + path.value
    const queryData = {
        filePath: path.value
    }
    const resp = await fileApi.getFileList(queryData) as any
    result.rows = resp.data
    loading.value = false
}

const deleteRec = (name: string) => {
    let filePath = path.value + name
    ElMessageBox.confirm(`确定删除吗？ ${filePath}`).then(() => {
        fileApi.deleteFile({ filePath: filePath }).then(() => {
            result.rows = result.rows.filter((row: any) => row.name !== name)
            ElMessage.success(`删除完成`)
        })
    })
}

const createDir = () => {
    if (!form.newDir) {
        ElMessage.error('目录名称不能为空')
        return
    }
    const dirName = path.value + form.newDir
    fileApi.createDir({ filePath: dirName }).then(() => {
        ElMessage.success('目录创建成功')
        dialogCreateDirVisible.value = false
        query()
    })
}

const openRenameDialog = (name: string) => {
    form.oldName = name
    form.newName = name
    dialogRenameVisible.value = true
}

const rename = () => {
    const oldPath = path.value + form.oldName
    const newPath = path.value + form.newName
    fileApi.renameFile({ oldPath, newPath }).then(() => {
        ElMessage.success('重命名成功')
        dialogRenameVisible.value = false
        query()
    })
}

const uploadRequest = async (options: any) => {
    uploading.value = true
    uploadProgress.value = 0
    const file = options.file
    const chunkSize = 5 * 2 ** 20 // 5MB
    const totalChunks = Math.ceil(file.size / chunkSize)
    const fileName = file.name

    for (let i = 0; i < totalChunks; i++) {
        const start = i * chunkSize
        const end = Math.min(start + chunkSize, file.size)
        const chunk = file.slice(start, end)
        const formData = new FormData()
        formData.append('file', chunk)
        formData.append('fileName', fileName)
        formData.append('chunkIndex', (i + 1).toString())
        formData.append('totalChunks', totalChunks.toString())
        formData.append('path', path.value)

        try {
            await fileApi.uploadChunk(formData)
            uploadProgress.value = Number((((i + 1) / totalChunks) * 100).toFixed(2))
        } catch (error) {
            uploading.value = false
            return
        }
    }


    ElMessage.success('上传成功')
    dialogUploadVisible.value = false
    query()
    uploading.value = false

}


onMounted(() => {
    query()
})

</script>

<style lang="scss" scoped></style>
