import request from '@/plugins/axios'

export const aiApi = {
    /** get ai models */
    listModels: async () => {
        const { data } = await request({
            url: '/v1/ai/model/list',
            method: 'post'
        })
        return data
    },

    /** get ai chat history for thread */
    getThread: async (params: any) => {
        const { data } = await request({
            url: '/v1/ai/thread/get',
            method: 'post',
            data: params
        })
        return data
    },

    /** delete ai chat history for thread */
    deleteThread: async (params: any) => {
        return await request({
            url: '/v1/ai/thread/delete',
            method: 'post',
            data: params
        })
    },

    /** delete ai chat history for thread */
    deleteAllThreads: async () => {
        return await request({
            url: '/v1/ai/thread/delete_all',
            method: 'post',
        })
    },

    /** rename ai chat history for thread */
    renameThread: async (params: any) => {
        return await request({
            url: '/v1/ai/thread/rename',
            method: 'post',
            data: params
        })
    },

    /** get ai chat threads */
    listThreads: async () => {
        const { data } = await request({
            url: '/v1/ai/thread/list',
            method: 'post'
        })
        return data
    },
     /** prepare for finally response */
    prepare: async (params: ChatDto) => {
        const { data } = await request({
            url: '/v1/ai/chat/prepare',
            method: 'post',
            data: params
        })
        return data
    }
}

export interface ChatDto {
    threadUuid: string | null
    prompt: string
    modelId: number
}
