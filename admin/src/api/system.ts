import request from '@/utils/system/request'

export default ({

    getConfig: () => {
        return request({
            url: '/system/config',
            method: 'post'
        })
    },
    refreshRsaKey: () => {
        return request({
            url: '/system/config/refresh_rsa_key',
            method: 'post'
        })
    },
    saveConfig: (data: object) => {
        return request({
            url: '/system/config/save',
            method: 'post',
            data
        })
    },

    testEmail: (data: object) => {
        return request({
            url: '/system/config/test_email',
            method: 'post',
            data
        })
    },

    getSystemInfo: () => {
        return request({
            url: '/system/info/get',
            method: 'post'
        })
    },

    getLogList: (data: object) => {
        return request({
            url: '/system/log/list',
            method: 'post',
            data
        })
    },
    addAdmin: (data: object) => {
        return request({
            url: '/system/admin/add',
            method: 'post',
            data
        })
    },

    deleteAdmin: (data: object) => {
        return request({
            url: '/system/admin/delete',
            method: 'post',
            data
        })
    },

    editAdmin: (data: object) => {
        return request({
            url: '/system/admin/edit',
            method: 'post',
            data
        })
    },

    getAdminList: (data: object) => {
        return request({
            url: '/system/admin/list',
            method: 'post',
            data
        })
    },


    addMenu: (data: object) => {
        return request({
            url: '/system/menu/add',
            method: 'post',
            data
        })
    },

    deleteMenu: (data: object) => {
        return request({
            url: '/system/menu/delete',
            method: 'post',
            data
        })
    },

    editMenu: (data: object) => {
        return request({
            url: '/system/menu/edit',
            method: 'post',
            data
        })
    },

    getMenuList: (data: object) => {
        return request({
            url: '/system/menu/list',
            method: 'post',
            data
        })
    },

    getPaymentList: (data: object) => {
        return request({
            url: '/system/payment/list',
            method: 'post',
            data
        })
    },
    getPayment: (data: object) => {
        return request({
            url: '/system/payment/get',
            method: 'post',
            data
        })
    },
    editPayment: (data: object) => {
        return request({
            url: '/system/payment/edit',
            method: 'post',
            data
        })
    },

    addRole: (data: object) => {
        return request({
            url: '/system/role/add',
            method: 'post',
            data
        })
    },

    deleteRole: (data: object) => {
        return request({
            url: '/system/role/delete',
            method: 'post',
            data
        })
    },

    getRoleList: () => {
        return request({
            url: '/system/role/list',
            method: 'post'
        })
    },
    getRoleListSimple: () => {
        return request({
            url: '/system/role/list_simple',
            method: 'post'
        })
    },
    editRole: (data: object) => {
        return request({
            url: '/system/role/edit',
            method: 'post',
            data
        })
    },


})
