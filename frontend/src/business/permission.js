import router from "./components/common/router/router";
import {TokenKey} from "@/common/js/constants";

/* eslint-disable */
const whiteList = ["/login"]; // no redirect whitelist

router.beforeEach(async (to, from, next) => {

  // determine whether the user has logged in
  const user = JSON.parse(localStorage.getItem(TokenKey));

  if (user) {
    if (to.path === "/login") {
      next({path: "/"});
    } else {
      next()
    }
  } else {
    /* has no token*/

    if (whiteList.indexOf(to.path) !== -1) {
      // in the free login whitelist, go directly
      next()
    } else {
      // other pages that do not have permission to access are redirected to the login page.
      next(`/login`)
    }
  }
});

router.afterEach(() => {
  // finish progress bar
});
