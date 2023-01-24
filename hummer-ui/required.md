## UI 模块

~~~
├── build                      // 构建相关  
├── bin                        // 执行脚本
├── public                     // 公共文件
│   ├── favicon.png            // favicon图标
├── src                        // 源代码
│   ├── api                    // 所有请求
│   ├── assets                 // 图片、主题、字体等静态资源
│   ├── business               // 全局公用组件
│   ├── common                 // 通用方法
│   ├── i18n                   // 国际化
│   ├── icons                  // 图标
│   ├── login                  // 登录入口页面
├── .editorconfig              // 编码格式
├── .env.development           // 开发环境配置
├── .env.production            // 生产环境配置
├── .env.staging               // 测试环境配置
├── .eslintignore              // 忽略语法检查
├── .eslintrc.js               // eslint 配置项
├── .gitignore                 // git 忽略项
├── babel.config.js            // babel.config.js
├── package.json               // package.json
├── required.md                // required.md
└── vue.config.js              // vue.config.js
~~~

## 文件命名

- html 小写字母+横线，例如:index.html，org-list.html
- js 小写字母+横线，例如:i18n.js，en-US.js
- vue 驼峰命名，首字母大写，例如 Login.vue，HeaderUser.vue

## 变量命名

- 常量 大写字母加下划线，例如:const ROLE_ADMIN='admin'
- 变量 驼峰命名，首字母小写，例如 let name，let currentProject
- 方法 驼峰命名，首字母小写，例如 function open(){}，function openDialog()

## Vue 组件

- 导出名称 驼峰命名，首字母大写，以开头，例如 User

## 样式规范

- 均写入 vue 文件的<style scope></style>标签内，非全局样式必须添加 scope
- 修改 ElementUI 的样式，仅在必要情况下写在<style></style>
- 命名 小写字母+横线，例如.menu，.header-menu，#header-top

## 格式要求

- 遵循.editorconfig

## Vue 风格指南

- https://cn.vuejs.org/v2/style-guide/

## 开发

```bash
# 克隆项目
git clone https://github.com/HummerRisk/HummerRisk.git

# 进入项目目录
cd hummer-ui

# 安装依赖
npm install

# 建议不要直接使用 cnpm 安装依赖，会有各种诡异的 bug。可以通过如下操作解决 npm 下载速度慢的问题
npm install --registry=https://registry.npmmirror.com

# 启动服务(npm)
npm run dev
```

```bash
# 或者使用 yarn 启动(yarn)

# 项目设置
yarn install

# 编译并最小化生产
yarn build

# 编译和热重装以进行开发
yarn serve
```

浏览器访问 http://localhost:8080

## 发布

```bash
# 构建测试环境
npm run build:stage

# 构建生产环境
npm run build:prod
```
