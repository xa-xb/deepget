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
})