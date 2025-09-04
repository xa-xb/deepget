import type { Route } from '../index.type'
import Layout from '@/layout/index.vue'
import { createPage } from '../createNode'
const route: Route[] = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { title: 'dashboard', icon: 'sfont system-home' },
    children: [
      {
        path: 'dashboard',
        component: createPage(() => import('@/views/main/dashboard/index.vue')),
        meta: { cache: true, title: '首页', icon: 'sfont system-home', hideClose: true }
      }
    ]
  },
  {
    path: "/system",
    component: Layout,
    meta: { title: "系统管理", icon: "sfont system-document" },
    children: [
      {
        path: "menu",
        meta: { cache: true, title: "菜单管理" },
        component: createPage(() => import('@/views/system/menu.vue'))
      },
      {
        path: "log",
        meta: { cache: true, title: "管理日志" },
        component: createPage(() => import('@/views/system/LogPage.vue'))
      },
      {
        path: "info",
        meta: { cache: true, title: "系统信息" },
        component: createPage(() => import('@/views/system/InfoPage.vue'))
      },
    ]
  }
]

export default route