import request from '@/utils/system/request'

export default ({
    addUser: (data: object) => {
        return request({
            url: '/user/user/add',
            method: 'post',
            data
        })
    },
    getBalanceLogList: (data: object) => {
        return request({
            url: '/user/balanceLog/list',
            method: 'post',
            data
        })
    },
    getLogList: (data: object) => {
        return request({
            url: '/user/log/list',
            method: 'post',
            data
        })
    },
    changeBalance: (data: object) => {
        return request({
            url: '/user/user/changeBalance',
            method: 'post',
            data
        })
    },
    changePassword: (data: object) => {
        return request({
            url: '/user/user/changePassword',
            method: 'post',
            data
        })
    },

    getUser: (data: object) => {
        return request({
            url: '/user/user/get',
            method: 'post',
            data
        })
    },

    getUserList: (data: object) => {
        return request({
            url: '/user/user/list',
            method: 'post',
            data
        })
    }
})