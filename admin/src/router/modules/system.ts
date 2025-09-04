import type { Route } from '../index.type'
import Layout from '@/layout/index.vue'
import { createPage } from '../createNode'
const route: Route[] = [
  {
    path: '/system',
    component: Layout,
    redirect: '/404',
    hideMenu: true,
    meta: { title: '系统目录' },
    children: [
      {
        path: '/404',
        component: createPage(() => import('@/views/main/404.vue')),
        meta: { title: '404', hideTabs: true }
      },
      {
        path: '/401',
        component: createPage(() => import('@/views/main/401.vue')),
        meta: { title: '401', hideTabs: true }
      },
      {
        path: '/redirect/:path(.*)',
        component: createPage(() => import('@/views/main/redirect.vue')),
        meta: { title: '重定向页面', hideTabs: true }
      }
    ]
  },
  {
    path: '/sign_in',
    component: createPage(() => import('@/views/main/SignInPage.vue')),
    hideMenu: true,
    meta: { title: '登录', hideTabs: true }
  },
  {
    // 找不到路由重定向到404页面
    path: "/:pathMatch(.*)",
    component: Layout,
    redirect: "/404",
    hideMenu: true,
    meta: { title: '' },
    children: []
  }
]

export default route