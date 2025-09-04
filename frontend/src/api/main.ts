import request from '@/plugins/axios'

export const mainApi = {
    /** get user profile */
    getProfile: async () => {
        const { data } = await request({
            url: '/v1/main/profile',
            method: 'get'
        })
        return data
    },

    getRsaPubKey: async () => {
        return request({
            url: '/v1/main/rsa_pub_key',
            method: 'post'
        })
    },

    resetPassword: async (params: ResetPasswordDto) => {
        return request({
            url: '/v1/main/reset_password',
            method: 'post',
            data: params,
        });
    },

    sendEmailCode: async (params: SendEmailCodeDto) => {
        return request({
            url: '/v1/main/send_email_code',
            method: 'post',
            data: params,
        });
    },

    // 登录
    signIn: async (params: SignInDto) => {
        const { data } = await request({
            url: '/v1/main/sign_in',
            method: 'post',
            data: params
        })
        return data
    },
    // 退出登录
    signOut: async () => {
        await request({
            url: '/v1/main/sign_out',
            method: 'post',
        })
    },
    // 退出全部登录
    signOutAll: async () => {
        await request({
            url: '/v1/main/sign_out_all',
            method: 'post',
        })
    },

    // 注册会员
    signUp: async (params: NewUserDto) => {
        return request({
            url: '/v1/main/sign_up',
            method: 'post',
            data: params
        })
    }
}

// 注册接口参数
export interface NewUserDto {
    username: string;   // 用户名
    password: string;   // 密码
    captcha: string;    // 验证码
}

// 登录接口参数
export interface SignInDto {
    account: string;   // 用户名
    password: string;   // 密码
}


export interface SendEmailCodeDto {
    email: string
    captcha?: string
    name: string
}

export interface ResetPasswordDto {
    email: string;
    code: string;
    password: string;
}

