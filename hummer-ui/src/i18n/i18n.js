import Vue from "vue";
import VueI18n from "vue-i18n";
import axios from "axios";
import enLocale from "element-ui/lib/locale/lang/en";
import zh_CNLocale from "element-ui/lib/locale/lang/zh-CN";
import zh_TWLocale from "element-ui/lib/locale/lang/zh-TW";
import zh_CN from "./zh-CN";
import en_US from "./en-US";
import zh_TW from "./zh-TW";

Vue.use(VueI18n);

const messages = {
  "en_US": {
    ...enLocale,
    ...en_US
  },
  "zh_CN": {
    ...zh_CNLocale,
    ...zh_CN
  },
  "zh_TW": {
    ...zh_TWLocale,
    ...zh_TW
  }
};

const i18n = new VueI18n({
  locale: "zh_CN",
  messages,
  silentTranslationWarn: true, // 去除国际化警告,打包发布留用,本地开发可注释
  // silentTranslationWarn: false, //本地开发打开
});

const loadedLanguages = ["en_US", "zh_CN", "zh_TW"];

function setI18nLanguage(lang) {
  i18n.locale = lang;
  axios.defaults.headers.common["Accept-Language"] = lang;
  document.querySelector("html").setAttribute("lang", lang);
  return lang;
}

Vue.prototype.$setLang = function (lang) {
  if (i18n.locale !== lang) {
    if (!loadedLanguages.includes(lang)) {
      let file = lang.replace("_", "-");
      i18n.mergeLocaleMessage(lang, require(`@/i18n/${file}.js`).default);
      loadedLanguages.push(lang);
      return setI18nLanguage(lang)
    }
    return Promise.resolve(setI18nLanguage(lang))
  }
  return Promise.resolve(lang)
};

export default i18n;
