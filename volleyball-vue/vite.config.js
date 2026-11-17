import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 后端 contextPath，可通过环境变量覆盖
const BACKEND = process.env.VITE_BACKEND || 'http://localhost:8080/volleyballScoringSystem'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      // 所有 /api 请求转发到后端，cookie 随代理透传
      '/api': {
        target: BACKEND,
        changeOrigin: true,
        // 后端 JSESSIONID 的 Path 是 /volleyballScoringSystem，
        // 而浏览器实际请求的是 /api，必须重写 Path 才能被带上
        cookiePathRewrite: '/',
        cookieDomainRewrite: 'localhost',
      },
    },
  },
})