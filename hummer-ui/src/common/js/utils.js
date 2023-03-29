import {ACCOUNT_ID, ACCOUNT_NAME, IsCollapse, K8S_ID, K8S_NAME, REFRESH_SESSION_USER_URL, TokenKey} from "./constants";
import axios from "axios";

export function getCurrentUser() {
  return JSON.parse(localStorage.getItem(TokenKey));
}

export function getCurrentAccountID() {
  return localStorage.getItem(ACCOUNT_ID);
}

export function getCurrentAccountName() {
  return localStorage.getItem(ACCOUNT_NAME);
}

export function getK8sID() {
  return localStorage.getItem(K8S_ID);
}

export function getK8sName() {
  return localStorage.getItem(K8S_NAME);
}

export function getIsCollapse() {
  let isCollapse = sessionStorage.getItem(IsCollapse);
  return isCollapse === 'true';
}

export function saveLocalStorage(response) {
  // 登录信息保存 cookie
  localStorage.setItem(TokenKey, JSON.stringify(response.data));
  let rolesArray = response.data.roles;
  let roles = rolesArray.map(r => r.id);
  // 保存角色
  localStorage.setItem("roles", roles);
}

export function refreshSessionAndCookies(sign, sourceId) {
  axios.post(REFRESH_SESSION_USER_URL + "/" + sign + "/" + sourceId).then(r => {
    saveLocalStorage(r.data);
    window.location.reload();
  })
}


export function jsonToMap(jsonStr) {
  let obj = JSON.parse(jsonStr);
  let strMap = new Map();
  for (let k of Object.keys(obj)) {
    strMap.set(k, obj[k]);
  }
  return strMap;
}

export function mapToJson(strMap) {
  let obj = Object.create(null);
  for (let [k, v] of strMap) {
    obj[k] = v;
  }
  return JSON.stringify(obj);
}

// 驼峰转换下划线
export function humpToLine(name) {
  return name.replace(/([A-Z])/g, "_$1").toLowerCase();
}

//表格数据过滤
export function _filter(filters, condition) {
  if (!condition.filters) {
    condition.filters = {};
  }
  for (let filter in filters) {
    if (filters.hasOwnProperty(filter)) {
      if (filters[filter] && filters[filter].length > 0) {
        condition.filters[humpToLine(filter)] = filters[filter];
      } else {
        condition.filters[humpToLine(filter)] = null;
      }
    }
  }
}

//表格数据排序
export function _sort(column, condition) {
  column.prop = humpToLine(column.prop);
  if (column.order === "descending") {
    column.order = "desc";
  } else {
    column.order = "asc";
  }
  if (!condition.orders) {
    condition.orders = [];
  }
  let hasProp = false;
  condition.orders.forEach(order => {
    if (order.name === column.prop) {
      order.type = column.order;
      hasProp = true;
    }
  });
  if (!hasProp) {
    condition.orders.push({name: column.prop, type: column.order});
  }
}

export function downloadFile(name, content) {
  const blob = new Blob([content]);
  if ("download" in document.createElement("a")) {
    // 非IE下载
    //  chrome/firefox
    let aTag = document.createElement("a");
    aTag.download = name;
    aTag.href = URL.createObjectURL(blob);
    aTag.click();
    URL.revokeObjectURL(aTag.href)
  } else {
    // IE10+下载
  }
}

export function listenGoBack( callback) {
  //监听浏览器返回操作，关闭该对话框
  if (window.history && window.history.pushState) {
    history.pushState(null, null, document.URL);
    window.addEventListener("popstate", callback);
  }
}

export function removeGoBackListener(callback) {
  window.removeEventListener("popstate", callback);
}

export function getUUID() {
  function S4() {
    return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
  }
  return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}


