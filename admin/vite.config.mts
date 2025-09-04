import { ConfigEnv, UserConfigExport } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

const pathResolve = (dir: string): any => {
    return resolve(__dirname, ".", dir)
}

const alias: Record<string, string> = {
    '@': pathResolve("src")
}

/** 
 * @description-en vite document address
 * @description-cn vite官网
 * https://vitejs.cn/config/ */
export default ({ command }: ConfigEnv): UserConfigExport => {
    return {
        base: './',
        build: {
            chunkSizeWarningLimit: 20_000
        },
        // optimized dependencies changed
        optimizeDeps: {
            include: ['mathjs']
        },
        server: {
            host: '0.0.0.0',
            port: 8082,
            proxy: {
                // 接口地址代理
                '/admin': {
                    target: 'http://127.0.0.1:8080',
                    changeOrigin: true, // 跨域
                    // rewrite: path => path.replace(/^\/demo/, '/demo')
                },
                // 图片资源代理
                '/img': {
                    target: 'http://127.0.0.1:8080',
                    changeOrigin: true, // 跨域
                },
            }
        },
        resolve: {
            alias
        },
        plugins: [
            vue()
        ]
    };
}
