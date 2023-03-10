const path = require('path');

function resolve(dir) {
  return path.join(__dirname, dir);
}

module.exports = {
  // 部署生产环境和开发环境下的URL。
  // 默认情况下，Vue CLI 会假设你的应用是被部署在一个域名的根路径上
  publicPath: "/",
  productionSourceMap: false,
  configureWebpack: {
    devtool: 'source-map',
    resolve: {
      alias: {
        '@': resolve('src')
      }
    },
    //该选项可以控制 webpack 如何通知「资源(asset)和入口起点超过指定文件限制」
    performance: {
      hints: "warning", // "warning"枚举，"error"性能提示中抛出错误，false 关闭性能提示
      maxAssetSize: 30000000, // 整数类型（以字节为单位）入口起点的最大体积
      maxEntrypointSize: 40000000, // 整数类型（以字节为单位）生成文件的最大体积
      assetFilter: function(assetFilename) { // 只给出 js 文件的性能提示
        // 提供资源文件名的断言函数
        return assetFilename.endsWith('.css') || assetFilename.endsWith('.js');
      }
    },
  },
  css: {
    extract: {
      ignoreOrder: true
    }
  },
  devServer: {
    host: '0.0.0.0',
    port: 8080,
    open: true,
    proxy: {
      [process.env.VUE_APP_BASE_API]: {
        target: `http://localhost:8088`,
        changeOrigin: true,
        pathRewrite: {
          ['^' + process.env.VUE_APP_BASE_API]: ''
        }
      }
    },
    disableHostCheck: true
  },
  pages: {
    business: {
      entry: "src/business/main.js",
      template: "src/business/index.html",
      filename: "index.html"
    },
    login: {
      entry: "src/login/login.js",
      template: "src/login/login.html",
      filename: "login.html"
    }
  }
};
