<template>
  <el-dialog v-model="dialogVisible" :title="form.id === null ? '新建菜单' : '修改菜单'" width="700"
    @close="formRef?.clearValidate()">
    <el-form label-width="78px" :model="form" :rules="rules" inline ref="formRef">

      <el-row>
        <el-col :span="12">
          <el-form-item label="上级">
            <el-cascader v-model="parentId" :options="parentRows" :props="parentMenuProps" style="width:218px"
              clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="类型" required>
            <el-radio-group v-model="form.type">
              <el-radio :value="1">目录</el-radio>
              <el-radio :value="2">菜单</el-radio>
              <el-radio :value="3">按钮</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="form.type == 1 || form.type == 2">
          <el-form-item label="图标">
            <el-icon-picker v-model="form.icon" abs="test" style="width:214px" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="排序" required>
            <el-input-number v-model="form.orderNum" :min="1" :max="99999" style="width:218px" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="名称" prop="name">
            <el-input v-model="form.name" placeholder="名称" style="width:218px" />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="路由地址" prop="route">
            <el-input v-model="form.route" placeholder="路由地址" style="width:218px" />
          </el-form-item>
        </el-col>

        <el-col :span="12" v-if="form.type == 2">
          <el-form-item label="组件路径" prop="component">
            <el-input v-model="form.page" placeholder="页面路径" style="width:218px" />
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="form.type == 2">
          <el-form-item label="是否缓存" required>
            <el-radio-group v-model="form.cache" style="width:218px">
              <el-radio :value="true">缓存</el-radio>
              <el-radio :value="false">不缓存</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="form.type < 3">
          <el-form-item label="显示状态" required>
            <el-radio-group v-model="form.visible" style="width:218px">
              <el-radio :value="true">显示</el-radio>
              <el-radio :value="false">隐藏</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="使用状态" required>
            <el-radio-group v-model="form.enable" style="width:218px">
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
    <el-button type="primary" plain @click="add" v-if="userStore.hasAuthorize('/system/menu/add')">新增</el-button>
    <el-button type="info" plain @click="expand" icon="Sort">展开/折叠</el-button>
  </el-row><br>
  <el-table v-loading="loading" :data="rows" style="width: 100%; margin-bottom: 20px" row-key="id" size="large"
    :header-cell-style="{ background: '#f5f7fa', color: '#606266' }" ref="tableRef">
    <el-table-column prop="name" label="菜单名称" />
    <el-table-column label="图标" width="65">
      <template #default="scope">
        <el-icon size="1.2em" v-if="scope.row.icon.length > 0">
          <component :is="scope.row.icon"></component>
        </el-icon>

      </template>
    </el-table-column>
    <el-table-column align="center" prop="orderNum" label="排序" width="80" />
    <el-table-column prop="route" label="路由地址" />
    <el-table-column prop="page" label="页面路径" />
    <el-table-column align="right" label="状态&emsp;&emsp;&emsp;&emsp;" width="220">
      <template #default="scope">
        <el-tag type="success" size="small" v-if="scope.row.type == 2 && scope.row.cache">缓存</el-tag>
        <el-tag type="info" size="small" v-if="scope.row.type == 2 && !scope.row.cache">不缓存</el-tag>
        &ensp;<el-tag type="success" size="small" v-if="scope.row.type < 3 && scope.row.visible">显示</el-tag>
        <el-tag type="warning" size="small" v-if="scope.row.type < 3 && !scope.row.visible">隐藏</el-tag>
        &ensp;<el-tag type="success" size="small" v-if="scope.row.enable">启用</el-tag>
        <el-tag type="warning" size="small" v-if="!scope.row.enable">停用</el-tag>
      </template>
    </el-table-column>
    <el-table-column align="right" label="操作&emsp;&emsp;&emsp;&emsp;" width="210">
      <template #default="scope">
        <el-button link type="primary" size="small" @click="add(scope.row)" :icon="Plus"
          v-if="userStore.hasAuthorize('/system/menu/add') && scope.row.type < 3">新增</el-button>
        <el-button link type="primary" size="small" @click="edit(scope.row)" :icon="Edit"
          v-if="userStore.hasAuthorize('/system/menu/edit')">编辑</el-button>
        <el-button link type="primary" size="small" @click="del(scope.row)" :icon="Delete"
          v-if="userStore.hasAuthorize('/system/menu/delete')">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>
<script setup lang="ts">
import { ref, reactive, useTemplateRef, onMounted } from 'vue'
import ElIconPicker from './components/ElIconPicker.vue'
import { CascaderOption, ElMessage, ElMessageBox, FormRules, FormInstance, TableInstance } from 'element-plus'
import { Delete, Edit, Plus, Sort } from '@element-plus/icons-vue'
import systemApi from '@/api/system'
import { useUserStore } from '@/stores/user'

// Update the type at the top of the file
interface MenuCascaderOption extends CascaderOption {
  children: MenuCascaderOption[]
}

// 常量定义
const FORM_EMPTY = {
  id: null,
  cache: false,
  page: '',
  icon: '',
  name: '',
  type: 1,
  enable: true,
  orderNum: 1,
  parentId: '0',
  route: '',
  visible: true
}

// 响应式数据
const form = reactive({ ...FORM_EMPTY })
const dialogVisible = ref(false)
const expandVal = ref(false)
const loading = ref(true)
const parentId = ref('0')
const parentRows = ref<MenuCascaderOption[]>([])
const result = ref<any>({})
const rows = ref<any[]>([])
const rules = reactive<FormRules>({
  page: [{ required: true, message: '请输入页面路径', trigger: 'blur' }],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  route: [{ required: true, message: '请输入路由地址', trigger: 'blur' }]
})
const userStore = useUserStore()

// Add this with other reactive declarations
const parentMenuProps = {
  checkStrictly: true,
  value: 'value',
  label: 'label',
  emitPath: false
}

// 方法定义
const add = (parent: any) => {
  parentId.value = '0'
  Object.assign(form, FORM_EMPTY)
  if (parent != null) {
    parentId.value = parent.id
    if (parent.type < 3) form.type = parent.type + 1
  }
  dialogVisible.value = true
}

const del = (row: any) => {
  const itemName = ['目录', '菜单', '按钮'][row.type - 1] || ''
  ElMessageBox.confirm(`确认删除 ${itemName} ${row.name}`, 'Warning', { type: 'warning' })
    .then(() => {
      systemApi.deleteMenu(row).then(() => {
        dialogVisible.value = false
        ElMessage({ showClose: true, message: '删除成功', type: 'success' })
        query()
      })
    })
    .catch(() => {
      ElMessage({ type: 'info', message: '删除已取消' })
    })
}

const edit = (row: any) => {
  Object.assign(form, row)
  parentId.value = row.parentId
  dialogVisible.value = true
}

const expand = () => {
  expandVal.value = !expandVal.value
  rows.value.forEach((row: any) => expandRow(row))
}

const expandRow = (row: any) => {
  tableRef.value?.toggleRowExpansion(row, expandVal.value)
  if (row.children?.length > 0) row.children.forEach(expandRow)
}

const query = async () => {
  loading.value = true
  const response = await systemApi.getMenuList({})
  result.value = { ...response }
  parentRows.value = []
  rows.value = []

  for (const row of result.value.data) {
    if (row.parentId === 0) {
      parentRows.value.push({
        label: row.name,
        value: row.id,
        children: []
      })
      rows.value.push({ ...row })
    }
  }

  rows.value.forEach((row, index) => recursion(parentRows.value[index], row))
  loading.value = false
}

const recursion = (parentItem: MenuCascaderOption, rowsItem: any) => {
  parentItem.children = []
  rowsItem.children = []

  result.value.data.forEach((row: any) => {
    if (row.parentId === rowsItem.id) {
      rowsItem.children.push({ ...row })
      parentItem.children.push({
        label: row.name,
        value: row.id,
        children: []
      })
    }
  })

  rowsItem.children.forEach((child: any, index: number) => {
    recursion(parentItem.children[index], child)
  })
}

const submit = () => {
  formRef.value?.validate((valid: boolean) => {
    if (!valid) return
    form.parentId = Array.isArray(parentId.value) ? parentId.value[parentId.value.length - 1] : parentId.value || 0
    const save = form.id == null ? systemApi.addMenu : systemApi.editMenu
    save({ ...form }).then(() => {
      dialogVisible.value = false
      ElMessage({ showClose: true, message: '保存完成', type: 'success' })
      query()
    })
  })
}

// 引用
const formRef = useTemplateRef<FormInstance>('formRef')
const tableRef = useTemplateRef<TableInstance>('tableRef')

// 生命周期钩子
onMounted(() => {
  query()
})
</script>

<style lang="scss" scoped></style>