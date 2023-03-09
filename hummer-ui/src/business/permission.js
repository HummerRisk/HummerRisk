import router from "./components/common/router/router";
import {getToken} from "@/common/js/auth";
import NProgress from 'nprogress';
/* eslint-disable */
const whiteList = ["/login"]; // no redirect whitelist

NProgress.configure({ showSpinner: false })

router.beforeEach(async (to, from, next) => {

  // determine whether the user has logged in
  const token = getToken()
  if (token) {
    if (to.path === "/login") {
      next({ path: '/' })
      NProgress.done()
    } else {
      next()
    }
  } else {
    // 没有token
    if (whiteList.indexOf(to.path) !== -1) {
      // 在免登录白名单，直接进入
      next()
    } else {
      next(`/login?redirect=${to.fullPath}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }
});

router.afterEach(() => {
  // finish progress bar
});
