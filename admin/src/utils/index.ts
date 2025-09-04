import * as math from 'mathjs'
import JSEncrypt from 'jsencrypt'
import mainApi from '@/api/main'
import { ElMessage } from 'element-plus'

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

export const priceToNumber = (price: number | string): number => {
    return parseInt(math.multiply(math.bignumber(price), math.bignumber(100)).toString())
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
        ElMessage.error('jsencrypt error')
        console.warn(str)
        return ''
    }
    return encrypted
}