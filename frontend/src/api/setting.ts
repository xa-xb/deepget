import request from '@/plugins/axios'

export const settingApi = {
  // 绑定邮箱
  bindEmail: async (params: { email: string; code: string }) => {
    await request({
      url: '/v1/setting/bind_email',
      method: 'post',
      data: params
    })
  },
  // 删除所有对话
  deleteAllConversations: async () => {
    await request({
      url: '/v1/setting/delete_all_conversations',
      method: 'post'
    })
  }
}