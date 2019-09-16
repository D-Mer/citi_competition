# readme

## 说明

该文件是程序的前端部分，主要使用了以下工具：

1. IDEA，作为开发的 IDE
2. Node.js，作为 JavaScript 的运行环境
3. Vue.js，作为 JavaScript 框架
6. npm 包管理工具及相关库。

需要运行本前端程序，请确保计算机至少安装了以下程序：

- Node.js 10.16.3 或者以上版本
- Vue.js 3.11.0 或者以上版本
- npm 6.9.0 或者以上版本，并安装了以下包：
  - cnpm
  - vue-cli
  - webpack
  - echarts
  - vue-axios
  - vue-router
  - element-ui

## 修改配置文件

打开 application - config - index.js 文件，按照实际需求修改以下部分：

- assetsPublicPath：打包文件路径
- port：端口号



## 运行程序

回到项目的根目录下，执行 ` npm run dev` 命令，直接运行本程序

## 打包程序

回到项目的根目录下，执行 `npm run build` 命令，将整个项目打包。

## 部署

将打包后的 dist 文件夹里的文件部署到对应服务器上。