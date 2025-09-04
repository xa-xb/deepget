import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  base: '/',
  build: {
    chunkSizeWarningLimit: 20_000
  },
  plugins: [tailwindcss(), vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, './src')
    }
  },
  server: {
    host: '::',
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://[::1]:8080',
        // 跨域
        changeOrigin: true
      }
    }
  }
})
