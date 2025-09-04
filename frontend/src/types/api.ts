// 通用响应接口
export interface ApiResult<T = any> {
    code: number
    msg: string
    data: T
}
export type ChatVO = {
    modelId: number,
    uuid: string,
    threadUuid: string,
    message: string | null,
    responseMessage: string,
    inputTokens: number,
    outputTokens: number,
    totalTokens: number
}
// ai model
export type Model = {
    id: number
    name: string
    iconSvg: string
    providerId: number
    providerName: string
    createdAt: string
}
// ai chat thread
export type Thread = {
    uuid: string
    title: string
    createdAt: string
}

// ai chat message
export interface Message {
    uuid: string | null
    modleId: number | null
    text: string
}
