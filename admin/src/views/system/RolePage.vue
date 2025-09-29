<template>
  <el-dialog v-model="dialogVisible" :title="form.id === null ? '新建角色' : '修改角色'" width="460" @close="dialogClose">

    <el-form label-width="78px" :model="form" :rules="rules" inline ref="formRef">

      <el-row>
        <el-col :span="24">
          <el-form-item label="名称" prop="name">
            <el-input v-model="form.name" placeholder="名称" style="width:328px" />
          </el-form-item>
          <el-form-item label="排序" required>
            <el-input-number v-model="form.orderNum" :min="1" :max="999" style="width:328px" />
          </el-form-item>

          <el-form-item label="使用状态" required>
            <el-radio-group v-model="form.enabled" style="width:328px">
              <el-radio :value="true">启用</el-radio>
              <el-radio :value="false">停用</el-radio>
            </el-radio-group>
          </el-form-item>


          <el-form-item label="菜单权限" required>
            <div style="display:flex; width:328px">
              <el-checkbox v-model="expandAll" label="展开/折叠"></el-checkbox>
              <el-checkbox v-model="selectAll" label="全选/全不选"></el-checkbox>
            </div>
          </el-form-item>
          <el-form-item label="&nbsp;">

            <el-tree class="bordered" :data="roleData" :default-checked-keys="form.checkedKeys" show-checkbox
              node-key="id" :props="defaultProps" ref="treeRef" />
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
    <el-button type="primary" plain @click="add" v-if="userStore.hasAuthorize('/system/role/add')">新增</el-button>
  </el-row><br>
  <el-table v-loading="loading" :data="rows" style="max-width: 100%; margin-bottom: 20px" row-key="id"
    default-expand-all size="large" :header-cell-style="{ background: '#f5f7fa', color: '#606266' }">
    <el-table-column prop="name" label="角色名称" />
    <el-table-column align="center" prop="orderNum" label="排序" width="160" />
    <el-table-column align="center" label="状态" width="120">
      <template #default="scope">
        <el-tag type="success" size="small" v-if="scope.row.enabled">启用</el-tag>
        <el-tag type="warning" size="small" v-if="!scope.row.enabled">停用</el-tag>
      </template>
    </el-table-column>
    <el-table-column align="center" label="操作" width="210">
      <template #default="scope">
        <el-button link type="primary" size="small" @click="edit(scope.row)" :icon="Edit"
        v-if="scope.row.id !== 1">编辑</el-button>
        <el-button link type="primary" size="small" @click="del(scope.row)" :icon="Delete"
          v-if="userStore.hasAuthorize('/system/role/delete') && scope.row.id !== 1">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, FormRules } from 'element-plus'
import { Delete, Edit } from '@element-plus/icons-vue'
import systemApi from '@/api/system'
import { useUserStore } from '@/stores/user'
import type { FormInstance, TreeInstance } from 'element-plus'

// 常量和响应式数据
const FORM_EMPTY = {
  id: null,
  name: '',
  enabled: true,
  orderNum: 1,
  authIds: [] as any,
  checkedKeys: [] as any
}
const form = reactive({ ...FORM_EMPTY })
const dialogVisible = ref(false)
const loading = ref(true)
const result = ref<any>({})
const rows = ref<any[]>([])
const roleData = ref<any[]>([])
const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
})
const userStore = useUserStore()
const formRef = ref<FormInstance | null>(null)
const treeRef = ref<TreeInstance>()
const selectAll = ref(false)
const expandAll = ref(false)

const defaultProps = {
  children: 'children',
  label: 'name'
}

// 方法定义
const add = () => {
  Object.assign(form, FORM_EMPTY)
  treeRef.value?.setCheckedNodes([])
  dialogVisible.value = true
}

const del = (row: any) => {
  ElMessageBox.confirm(`确认删除角色 ${row.name}`, 'Warning', { type: 'warning' })
    .then(() => {
      systemApi.deleteRole(row).then(() => {
        dialogVisible.value = false
        ElMessage.success('删除成功')
        query()
      }).catch(() => { })
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

const dialogClose = () => {
  expandAll.value = false
  selectAll.value = false
  formRef.value?.clearValidate()
}

const edit = (row: any) => {
  Object.assign(form, row)
  form.checkedKeys = row.authIds
  treeRef.value?.setCheckedKeys(form.checkedKeys)
  dialogVisible.value = true
}

const query = async () => {
  loading.value = true
  const response = await systemApi.getRoleList()
  result.value = { ...response }
  rows.value = response.data.rows
  roleData.value = response.data.roleData
  loading.value = false
}

const submit = () => {
  formRef.value?.validate((valid: boolean) => {
    if (!valid) return
    const { checkedKeys, ...data } = form
    data.authIds = treeRef.value?.getCheckedKeys()
    const save = data.id == null ? systemApi.addRole : systemApi.editRole
    save(data).then(() => {
      dialogVisible.value = false
      ElMessage.success('保存成功')
      query()
    }).catch(() => { })
  })
}

// 生命周期钩子
onMounted(() => {
  query()
})

// 监听器
watch(expandAll, (val) => {
  const recursion = (list: any[]) => {
    for (const row of list) {
      const node = treeRef.value?.store.nodesMap[row.id]
      if (node) node.expanded = val
      if (row.children?.length > 0) recursion(row.children)
    }
  }
  recursion(roleData.value)
})

watch(selectAll, (val) => {
  if (val) treeRef.value?.setCheckedNodes(roleData.value)
  else treeRef.value?.setCheckedNodes([])
})
</script>

<style lang="scss" scoped>
.bordered {
  border: 1px solid var(--el-border-color);
  margin-top: -1em;
  width: 328px;
}
</style>