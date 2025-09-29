import request from '@/utils/system/request'

export default ({
    getEmailLogList: (data: object) => {
        return request({
            url: '/email/emailLog/list',
            method: 'post',
            data
        })
    },
    addEmailLog: (data: object) => {
        return request({
            url: '/email/emailLog/add',
            method: 'post',
            data
        })
    },
    editEmailLog: (data: object) => {
        return request({
            url: '/email/emailLog/edit',
            method: 'post',
            data
        })
    },
    deleteEmailLog: (data: object) => {
        return request({
            url: '/email/emailLog/delete',
            method: 'post',
            data
        })
    },
    getEmailBlastList: () => {
        return request({
            url: '/email/emailBlast/list',
            method: 'post'
        })
    },
    getEmailSenderList: () => {
        return request({
            url: '/email/emailBlast/senders',
            method: 'post'
        })
    },
    addEmailBlast: (data: object) => {
        return request({
            url: '/email/emailBlast/add',
            method: 'post',
            data
        })
    },

    deleteEmailBlast: (data: object) => {
        return request({
            url: '/email/emailBlast/delete',
            method: 'post',
            data
        })
    },
     deleteEmailBlastAll: () => {
        return request({
            url: '/email/emailBlast/delete_all',
            method: 'post'
        })
    },
})