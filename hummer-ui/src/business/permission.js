import router from "./components/common/router/router";
import {getToken} from "@/common/js/auth";
/* eslint-disable */
const whiteList = ["/login"]; // no redirect whitelist

router.beforeEach(async (to, from, next) => {

  console.log(999)
  // determine whether the user has logged in
  const token = getToken()
  if (token) {
    if (to.path === "/login") {
      next({ path: '/' })
    } else {
      next()
    }
  } else {
    console.log(100, whiteList)
    // 没有token
    if (whiteList.indexOf(to.path) !== -1) {
      console.log(110, to.path)
      // 在免登录白名单，直接进入
      next()
    } else {
      next(`/login?redirect=${to.path}`) // 否则全部重定向到登录页
    }
  }
});

router.afterEach(() => {
  // 每次请求结束后关闭进度条
});
