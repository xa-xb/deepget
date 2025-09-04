<template>
    <div class="p-8 space-x-4">
        <h1 class="text-2xl font-bold mb-4">Component Demos</h1>
        <button class="cursor-pointer rounded-md bg-blue-500 px-4 py-2 text-sm font-medium text-white hover:bg-blue-600"
            @click="openModal">
            Open Modal
        </button>
    </div>
    <MdPreview :editorId="id" :modelValue="text" />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { aiApi, ChatDto } from '@/api/ai';
import { useRoute } from 'vue-router';
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';

const route = useRoute()

const id = 'preview-only';
const text = ref('# Hello Editor');

let modelId = Number(route.query.modelId) as number
if (!modelId) modelId = 16


const openModal = async () => {
    text.value = ''
    const chatDto = {
        threadUuid: null,
        prompt: '24点游戏, 3,5,7,17',
        modelId: modelId
    } as ChatDto
    const uuid = await aiApi.prepare(chatDto)

    const evtSource = new EventSource(`/api/v1/ai/chat/stream?chatUuid=${uuid}`);
    evtSource.addEventListener("end", () => {
        evtSource.close(); 
    });
     evtSource.addEventListener("error", (event : MessageEvent) => {
        let obj = JSON.parse(event.data)
        text.value = `<font color="red">${obj.m}</font>`
        evtSource.close()
    });
    evtSource.onmessage = (event) => {

        let obj = JSON.parse(event.data)
     
        text.value += obj.m
    };

};


</script>
