import { createRouter, createWebHistory } from 'vue-router'



const routes = [
  {
    path: '/',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import( '../views/main.vue'),
    meta: {
      loginRequire: true
    },
    children:[
      {
        path:"/welcome",
        component: () => import( '../views/main/welcome.vue')
      },
      {
        path: "/about",
        component: ()=> import( '../views/main/about.vue')
      },
      {
        path: "/station",
        component: ()=> import( '../views/main/station.vue')
      }
    ]
  },
  {
    path:"",
    redirect: "/welcome"
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
