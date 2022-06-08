import {NUMBER} from './reg'

export const
  sortNum = (a, b) => {
    return a - b
  },
  isNumber = (str) => {
    return new RegExp(NUMBER).test(str)
  },
  getLocale = () => {
    const lang = localStorage.getItem('locale') || sessionStorage.getItem('locale') || (navigator.systemLanguage ? navigator.systemLanguage : navigator.language),
      index = lang.indexOf('-')
    return lang.substring(0, index) + '_' + lang.substring(index + 1).toUpperCase()
  }
