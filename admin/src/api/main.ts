import request from '@/utils/system/request'
import { SignInRequestDto } from '@/types/main'

const mainApi = {
  /** get RSA public key */
  getRsaPubKey: () => {
    return request({
      url: '/main/rsa_pub_key',
      method: 'post'
    })
  },

  /** 登录api */
  signIn(data: SignInRequestDto) {
    return request({
      url: '/main/sign_in',
      method: 'post',
      data
    })
  },

  /** 退出登录Api */
  signOut: () => {
    return request({
      url: '/main/sign_out',
      method: 'post'
    })
  },

  /** get user profile */
  getProfile: () => {
    return request({
      url: '/main/profile',
      method: 'post'
    })
  },

}
export default mainApi