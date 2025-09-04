<template>
    <div v-loading="loading">

        <el-descriptions title="database" :column="1" size="large" border>
            <template #extra>
                <el-button @click="query" :icon="Refresh" circle></el-button>
            </template>
                <el-descriptions-item label="version"> {{ data.dbVersion }} </el-descriptions-item>
            </el-descriptions>

        <el-descriptions class="margin-top" title="redis / valkey" :column="2" size="large" border>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">redis version</div>
                </template>
                {{ data.redisVersion }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">valkey version</div>
                </template>
                {{ data.valkeyVersion }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">used memory</div>
                </template>
                {{ data.redisMemory }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">uptime</div>
                </template>
                {{ data.redisUptime }}
            </el-descriptions-item>
        </el-descriptions>

        <el-descriptions class="margin-top" title="java" :column="2" size="large" border>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">os name</div>
                </template>
                {{ data.javaOsName }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">os version</div>
                </template>
                {{ data.javaOsVersion }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">name</div>
                </template>
                {{ data.javaName }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">version</div>
                </template>
                {{ data.javaVersion }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">vendor</div>
                </template>
                {{ data.javaVendor }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">vm info</div>
                </template>
                {{ data.vmInfo }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">cpu cores</div>
                </template>
                {{ data.cpuCores }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">max memory</div>
                </template>
                {{ data.maxMemory }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">total memory</div>
                </template>
                {{ data.totalMemory }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">free memory</div>
                </template>
                {{ data.freeMemory }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">spring boot</div>
                </template>
                {{ data.springBootVersion }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">application dir</div>
                </template>
                {{ data.applicationDir }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">build time</div>
                </template>
                {{ data.buildTime }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">uptime</div>
                </template>
                {{ data.uptime }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">GC</div>
                </template>
                {{ data.javaGc }}
            </el-descriptions-item>
        </el-descriptions>
        <el-descriptions :column="1" size="large" border>
            <el-descriptions-item label="args"> {{ data.args }} </el-descriptions-item>
        </el-descriptions>
        <el-descriptions class="margin-top" title="front" :column="2" size="large" border>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">vue</div>
                </template>
                {{ vueVersion }}
            </el-descriptions-item>
            <el-descriptions-item :width="width25">
                <template #label>
                    <div class="cell-item">element-plus</div>
                </template>
                {{ epVersion }}
            </el-descriptions-item>
        </el-descriptions>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import systemApi from '@/api/system';
import { version as vueVersion } from 'vue';
import { version as epVersion } from 'element-plus';
import { Refresh } from '@element-plus/icons-vue';

// 状态管理
const loading = ref(true);
const data = ref<any>({});
const width25 = '25%';

// 初始化方法
const query = async () => {
    loading.value = true;
    systemApi.getSystemInfo().then(result => {
        Object.assign(data.value, result.data)
    }).finally(() => {
        loading.value = false
    })

};

// 生命周期钩子
onMounted(() => {
    query();
});
</script>

<style lang="scss" scoped>
.el-descriptions {
    margin-top: 20px;
}

.cell-item {
    display: flex;
    align-items: center;
}
</style>
