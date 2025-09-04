import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
import HomePage from '@/views/HomePage.vue'
import SignInPage from '@/views/SignInPage.vue'
import SignUpPage from '@/views/SignUpPage.vue'
import { useAppStore } from '@/stores/app'



const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'home',
    component: HomePage
  },
  {
    path: '/forgot_password',
    name: 'forgot_password',
    component: () => import('@/views/ForgotPasswordPage.vue')
  },
  {
    path: '/sign_in',
    name: 'sign_in',
    component: SignInPage
  },
  {
    path: '/sign_up',
    name: 'sign_up',
    component: SignUpPage
  },
  {
    path: '/test',
    name: 'test_page',
    component: () => import('@/views/TestPage.vue')
  },
  {
    path: '/test1',
    name: 'test1_page',
    component: () => import('@/views/Test1Page.vue')
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/NotFoundPage.vue')
  }
  // 可以在这里添加更多路由
]


// 未授权时可访问的白名单
const whiteList = ['/forgot_password', '/sign_in', '/sign_up']

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

let needInit: boolean = true
// Add route guard
router.beforeEach(async (to) => {
  let appStore = useAppStore()
  if (needInit) {
    await appStore.initWithProfilePage()
    needInit = false
  }
  if (whiteList.includes(to.path)) {
    return true
  }
  if (appStore.username.length == 0) {
    return 'sign_in'
  }
  return true
})

export default router
