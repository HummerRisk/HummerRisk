// const requireContext = require.context("@/business/components/xpack/", true, /router\.js$/)
/* eslint-disable */
export default {
  path: "/setting",
  name: "Setting",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ "@/business/components/settings/Setting")
  },
  children: [
    {
      path: "user",
      component: () => import(/* webpackChunkName: "setting" */ "@/business/components/settings/system/User"),
      meta: {system: true, title: "commons.user"}
    },
    {
      path: "systemparametersetting",
      component: () => import(/* webpackChunkName: "setting" */ "@/business/components/settings/system/SystemParameterSetting"),
      meta: {system: true, title: "commons.system_parameter_setting"}
    },
    {
      path: "messagesetting",
      component: () => import("@/business/components/settings/system/MessageSetting"),
      meta: {system: true, title: "system_parameter_setting.message.setting"}
    },
    {
      path: "proxy",
      component: () => import(/* webpackChunkName: "setting" */ "@/business/components/settings/system/Proxy"),
      meta: {system: true, title: "commons.proxy"}
    },
    {
      path: "webmsg",
      component: () => import("@/business/components/settings/system/Msg"),
      meta: {system: true, title: "webmsg.web_msg"}
    },
    {
      path: "personsetting",
      component: () => import(/* webpackChunkName: "setting" */ "@/business/components/settings/personal/PersonSetting"),
      meta: {person: true, title: "commons.personal_setting"}
    },
    {
      path: "apikeys",
      component: () => import(/* webpackChunkName: "setting" */ "@/business/components/settings/personal/ApiKeys"),
      meta: {
        person: true,
        title: "commons.api_keys",
        roles: ["admin"]
      }
    },
  ]
}
