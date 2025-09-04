import request from '@/utils/system/request'

export default ({
    getGenList: (data: object) => {
        return request({
            url: '/devel/gen/list',
            method: 'post',
            data
        })
    },
    addGen: (data: object) => {
        return request({
            url: '/devel/gen/add',
            method: 'post',
            data
        })
    },
    encodePassword: (data: object) => {
        return request({
            url: '/devel/security/encode_password',
            method: 'post',
            data
        })
    },
})