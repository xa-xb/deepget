import request from '@/utils/system/request'

export default ({
    getProviderApiCompatibles: () => {
        return request({
            url: '/ai/provider/api_compatibles',
            method: 'post'
        })
    },
    getProviderList: (data: object) => {
        return request({
            url: '/ai/provider/list',
            method: 'post',
            data
        })
    },
    addProvider: (data: object) => {
        return request({
            url: '/ai/provider/add',
            method: 'post',
            data
        })
    },
    editProvider: (data: object) => {
        return request({
            url: '/ai/provider/edit',
            method: 'post',
            data
        })
    },
    deleteProvider: (data: object) => {
        return request({
            url: '/ai/provider/delete',
            method: 'post',
            data
        })
    },

    getModelList: (data: object) => {
        return request({
            url: '/ai/model/list',
            method: 'post',
            data
        })
    },
    addModel: (data: object) => {
        return request({
            url: '/ai/model/add',
            method: 'post',
            data
        })
    },
    editModel: (data: object) => {
        return request({
            url: '/ai/model/edit',
            method: 'post',
            data
        })
    },
    deleteModel: (data: object) => {
        return request({
            url: '/ai/model/delete',
            method: 'post',
            data
        })
    },
    getChatList: (data: object) => {
        return request({
            url: '/ai/chat/list',
            method: 'post',
            data
        })
    },
    addChat: (data: object) => {
        return request({
            url: '/ai/chat/add',
            method: 'post',
            data
        })
    },
    editChat: (data: object) => {
        return request({
            url: '/ai/chat/edit',
            method: 'post',
            data
        })
    },
    deleteChat: (data: object) => {
        return request({
            url: '/ai/chat/delete',
            method: 'post',
            data
        })
    },
    getChatThreadList: (data: object) => {
        return request({
            url: '/ai/thread/list',
            method: 'post',
            data
        })
    },
    addChatThread: (data: object) => {
        return request({
            url: '/ai/thread/add',
            method: 'post',
            data
        })
    },
    editChatThread: (data: object) => {
        return request({
            url: '/ai/thread/edit',
            method: 'post',
            data
        })
    },
    deleteChatThread: (data: object) => {
        return request({
            url: '/ai/thread/delete',
            method: 'post',
            data
        })
    },
    getAiConfig: () => {
        return request({
            url: '/ai/config/get',
            method: 'post'
        })
    },
    saveAiConfig: (data: object) => {
        return request({
            url: '/ai/config/save',
            method: 'post',
            data
        })
    }
})