import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Ant from 'ant-design-vue'
import 'ant-design-vue/dist/antd.css';
import * as Icon from '@ant-design/icons-vue'
import axios from "axios";


const app=createApp(App)
app.use(Ant)
app.use(store).use(router).mount('#app')

//全局引用icons图标
const icons= Icon;
for (const i in icons){
    app.component(i,icons[i])
}
axios.interceptors.request.use(config=>{
    console.log(config)
    return config
},error => {
    return Promise.reject(error)
})
axios.interceptors.response.use(response=>{
    console.log(response)
    return response
},error=>{
    return Promise.reject(error)
})
axios.defaults.baseURL = process.env.VUE_APP_SERVER;
console.log('环境：', process.env.NODE_ENV);
console.log('服务端：', process.env.VUE_APP_SERVER);