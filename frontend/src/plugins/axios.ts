import router from '@/router'
import axios from 'axios'
import type { AxiosInstance, AxiosResponse, AxiosError, InternalAxiosRequestConfig } from 'axios'
import { toast } from 'vue3-toastify'

// 创建 Axios 实例
const service: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api', // 从环境变量获取基础URL
    timeout: 30000, // 请求超时时间(ms)
    headers: {
        'Content-Type': 'application/json',
    },
})

// 请求拦截器
service.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        // 在发送请求之前做些什么
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error: AxiosError) => {
        // 对请求错误做些什么
        console.error('Request error:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    (response: AxiosResponse) => {
        // 对响应数据做点什么
        const { data } = response

        // 根据后端接口规范，统一处理响应
        switch (data.code) {
            case 1:
                return data;
            case 2:
                router.push('/sign_in')
                return data;
            default:
                toast.error(`${data.msg}`)
                return Promise.reject(data)
        }

        // 处理其他状态码
        //console.error('API Error:', data.message)
        //return Promise.reject(new Error(data.message || 'Error'))
    },
    (error: AxiosError) => {
        // 对响应错误做点什么
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // 未授权，清除 token 并跳转到登录页
                    localStorage.removeItem('token')
                    router.push('/sign_in')
                    break
                case 403:
                    // 权限不足
                    console.error('Permission denied')
                    break
                default:
                    toast.error(`Error ${error.response.status}: ${error.message}`)
            }
        }
        return Promise.reject(error)
    }
)

export default service 