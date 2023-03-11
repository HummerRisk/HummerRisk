import {Message, MessageBox} from "element-ui";
import axios from "axios";
import {getToken, removeToken} from './auth';
import i18n from "../../i18n/i18n";


export default {
  install(Vue) {

    // 登入请求不重定向
    let unRedirectUrls = new Set(["/auth/signin"]);

    if (!axios) {
      window.console.error("You have to install axios");
      return
    }

    if (!Message) {
      window.console.error("You have to install Message of ElementUI");
      return
    }

    let login = function () {
      MessageBox.alert(i18n.t("commons.tips"), i18n.t("commons.prompt"), {
        callback: () => {
          console.log(123)
          axios.get("/auth/signout");
          removeToken();
          localStorage.clear();
          window.location.href = "/login";
        }
      });
    };

    axios.defaults.withCredentials = true;
    let token = getToken();
    if (token) axios.defaults.headers.common['Authorization'] = `Bearer `+ token;
    axios.defaults.baseURL = process.env.VUE_APP_BASE_API;
    axios.interceptors.response.use(response => {
      if (response.headers["authentication-status"] === "invalid") {
        login();
      }
      return response;
    }, error => {
      return Promise.reject(error);
    });

    function then(success, response, result) {
      if (response.status === 201) {
        success(response);
      }
      if (!response.data) {
        success(response);
      } else if (response.data.success) {
        success(response.data);
      } else {
        window.console.warn(response.data);
        if (response.data.message) {
          Message.warning(response.data.message);
        }
        if (response.status === 200) {
          success(response);
        }
      }
      result.loading = false;
    }

    function exception(error, result, url) {
      if (error.response && error.response.status === 401 && !unRedirectUrls.has(url) && error.response.data.message !== "用户名或密码不正确") {
        login();
        return;
      }
      if (error.response && error.response.status === 403 && !unRedirectUrls.has(url)) {
        console.log(223)
        window.location.href = "/";
        return;
      }
      result.loading = false;
      window.console.error(error.response || error.message);
      if (error.response && error.response.data) {
        if (error.response.headers["authentication-status"] !== "invalid") {
          Message.error({message: error.response.data.message || error.response.data, showClose: true});
        }
      } else {
        Message.error({message: error.message, showClose: true});
      }
    }

    Vue.prototype.$get = function (url, success) {
      let result = {loading: true};
      if (!success) {
        return axios.get(url);
      } else {
        axios.get(url).then(response => {
          then(success, response, result);
        }).catch(error => {
          exception(error, result, url);
        });
        return result;
      }
    };

    Vue.prototype.$post = function (url, data, success, failure) {
      // 是否需要设置 token
      const isToken = (data.headers || {}).isToken === false;
      // 是否需要防止数据重复提交
      const isRepeatSubmit = (data.headers || {}).repeatSubmit === false;
      let result = {loading: true};
      if (!success) {
        return axios.post(url, data);
      } else {
        axios.post(url, data).then(response => {
          then(success, response, result);
        }).catch(error => {
          exception(error, result, url);
          if (failure) {
            then(failure, error, result);
          }
        });
        return result;
      }
    };

    Vue.prototype.$request = function (axiosRequestConfig, success, failure) {
      // 是否需要设置 token
      const isToken = (axiosRequestConfig.headers || {}).isToken === false;
      // 是否需要防止数据重复提交
      const isRepeatSubmit = (axiosRequestConfig.headers || {}).repeatSubmit === false;
      if (getToken() && !isToken) {
        axiosRequestConfig.headers['Authorization'] = 'Bearer ' + getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
      }
      let result = {loading: true};
      if (!success) {
        return axios.request(axiosRequestConfig);
      } else {
        axios.request(axiosRequestConfig).then(response => {
          then(success, response, result);
        }).catch(error => {
          exception(error, result);
          if (failure) {
            then(failure, error, result);
          }
        });
        return result;
      }
    };

    Vue.prototype.$all = function (array, callback) {
      if (array.length < 1) return;
      axios.all(array).then(axios.spread(callback));
    };

    Vue.prototype.$fileDownload = function (url) {
      axios.get(url, {responseType: "blob"})
        .then(response => {
          let fileName = window.decodeURI(response.headers["content-disposition"].split("=")[1]);
          let link = document.createElement("a");
          link.href = window.URL.createObjectURL(new Blob([response.data], {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"}));
          link.download = fileName;
          link.click();
        });
    };

    Vue.prototype.$fileUpload = function (url, file, files, param, success, failure) {
      let formData = new FormData();
      if (file) {
        formData.append("file", file);
      }
      if (files) {
        files.forEach(f => {
          formData.append("files", f);
        })
      }
      formData.append("request", new Blob([JSON.stringify(param)], {type: "application/json"}));
      let axiosRequestConfig = {
        method: "POST",
        url: url,
        data: formData,
        headers: {
          "Content-Type": 'multipart/form-data'
        }
      };
      return Vue.prototype.$request(axiosRequestConfig, success, failure);
    }

    Vue.prototype.$download = function (url, param, success, failure) {
      let axiosRequestConfig = {
        method: "POST",
        url: url,
        data: param,
        responseType: 'arraybuffer'
      };
      return Vue.prototype.$request(axiosRequestConfig, success, failure);
    }

  }
}

export function getUploadConfig(url, formData) {
  return {
    method: 'POST',
    url: url,
    data: formData,
    headers: {
      'Content-Type': undefined
    }
  };
}
