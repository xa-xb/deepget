import request from '@/utils/system/request'

export default ({
    getFileList: (data: object) => {
        return request({
            url: '/file/file/list',
            method: 'post',
            data
        })
    },
    addFile: (data: object) => {
        return request({
            url: '/file/file/add',
            method: 'post',
            data
        })
    },
    editFile: (data: object) => {
        return request({
            url: '/file/file/edit',
            method: 'post',
            data
        })
    },
    deleteFile: (data: object) => {
        return request({
            url: '/file/file/delete',
            method: 'post',
            data
        })
    },
    createDir: (data: object) => {
        return request({
            url: '/file/file/create_dir',
            method: 'post',
            data
        })
    },
    renameFile: (data: object) => {
        return request({
            url: '/file/file/rename',
            method: 'post',
            data
        })
    },
    uploadChunk: (data: FormData) => {
        return request({
            url: '/file/file/upload_chunk',
            method: 'post',
            data,
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
    },
})
