import JSEncrypt from 'jsencrypt'
import { mainApi } from '@/api/main'
import { toast } from 'vue3-toastify'


/**
 * 从目标对象中拷贝已有的属性
 * 
 * @param objA 
 * @param objB 
 */
export const objAssign = (objA: any, objB: any): void => {
    let keysA = Object.keys(objA)
    let keysB = Object.keys(objB)
    //交集
    let arr = keysA.filter(item => keysB.indexOf(item) > -1)
    for (const key of arr) {
        objA[key] = objB[key]
    }

}


/**
 * price format, like this: 1020 -> 10.20
 * 
 * @param 
 */
export const priceFormat = (price: number | string): string => {
    const num = Number(price)
    if (num < 10) {
        return "0.0" + price;
    } else if (num < 100) {
        return "0." + num;
    }
    let rem = num % 100;
    let i = Math.floor(num / 100);
    if (rem < 10) {
        return i + ".0" + rem;
    } else {
        return i + "." + rem;
    }
}

export const fixThinkNewlines = (input: string, isStreaming = true): string => {
    const hasOpenTag = input.includes('<think>');
    const hasCloseTag = input.includes('</think>');

    if (isStreaming) {
        // 情况1：只有 <think> 没有 </think>
        if (hasOpenTag && !hasCloseTag) {
            return input.replace(/(<think>)([\s\S]*)/s, (_, open, content) => {
                return `${open}${content.replaceAll('\n', '<br>')}`;
            });
        }
        // 其他情况：,没有<think>标签，原样返回
        return input;
    }

    // non-stream
    return input.replace(/(<think>)([\s\S]*?)(<\/think>)/gs, (_, open, content, close) => {
        return `${open}${content.replaceAll('\n', '<br>')}${close}`;
    });
};


// Caching mechanism
let rsaPubKey: string | null = null;
let lastFetchTime: number | null = null;

/**
 * 
 * @param str 要加密的字符串
 * @returns 加密后数据
 */
export const rsaEncrypt = async (str: string): Promise<string> => {
    const jsencrypt = new JSEncrypt()

    if (rsaPubKey && lastFetchTime && (Date.now() - lastFetchTime < 30000)) {
        // If the key is cached and less than 1 minute has passed, use the cached key
        jsencrypt.setPublicKey(rsaPubKey)
    } else {
        // Fetch the RSA public key
        const response = await mainApi.getRsaPubKey()
        rsaPubKey = response.data.rsaPubKey
        lastFetchTime = Date.now()
        if (rsaPubKey) {
            jsencrypt.setPublicKey(rsaPubKey)
        }
    }

    let encrypted = jsencrypt.encrypt(str)
    if (encrypted === false) {
        toast.error('jsencrypt error')
        console.warn(str)
        return ''
    }
    return encrypted
}