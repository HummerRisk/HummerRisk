/* eslint-disable */
export default {
  path: "/setting",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ "@/business/components/settings/Setting")
  },
  children: [
    {
      path: "user",
      component: () => import(/* webpackChunkName: "setting" */ "@/business/components/settings/system/User"),
      meta: {system: true, title: "system.user"}
    },
    {
      path: "system-parameter-setting",
      component: () => import(/* webpackChunkName: "setting" */ "@/business/components/settings/system/SystemParameterSetting"),
      meta: {system: true, title: "commons.system_parameter_setting"}
    },
    {
      path: "message-setting",
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
      path: "system",
      component: () => import("@/business/components/settings/system/System"),
      meta: {system: true, title: "system.system"}
    },
    {
      path: "plugin",
      component: () => import("@/business/components/settings/system/Plugin"),
      meta: {system: true, title: "system.plugin"}
    },
    {
      path: "about",
      component: () => import("@/business/components/settings/system/About"),
      meta: {system: true, title: "commons.about_us"}
    },
    {
      path: "person-setting",
      component: () => import(/* webpackChunkName: "setting" */ "@/business/components/settings/personal/PersonSetting"),
      meta: {person: true, title: "commons.personal_setting"}
    },
    {
      path: "api-keys",
      component: () => import(/* webpackChunkName: "setting" */ "@/business/components/settings/personal/ApiKeys"),
      meta: {
        person: true,
        title: "commons.api_keys",
        roles: ["admin"]
      }
    },
  ]
}
