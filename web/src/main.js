import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Ant from 'ant-design-vue'
import 'ant-design-vue/dist/antd.css';
import * as Icon from '@ant-design/icons-vue'


const app=createApp(App)
app.use(Ant)
app.use(store).use(router).mount('#app')

//全局引用icons图标
const icons= Icon;
for (const i in icons){
    app.component(i,icons[i])
}