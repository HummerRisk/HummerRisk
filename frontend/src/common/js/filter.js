/* eslint-disable */
const options = function (value, array) {
  if (!value) return '';
  if (array) {
    for (let i = 0; i < array.length; i++) {
      if (value === array[i].key) {
        return array[i].value;
      }
    }
  }
  return value;
};

const timestampFormatDate = function (timestamp) {
  if (!timestamp) {
    return timestamp;
  }

  let date = new Date(timestamp);

  let y = date.getFullYear();

  let MM = date.getMonth() + 1;
  MM = MM < 10 ? ('0' + MM) : MM;

  let d = date.getDate();
  d = d < 10 ? ('0' + d) : d;

  let h = date.getHours();
  h = h < 10 ? ('0' + h) : h;

  let m = date.getMinutes();
  m = m < 10 ? ('0' + m) : m;

  //let s = date.getSeconds();
  //s = s < 10 ? ('0' + s) : s;

  return y + '-' + MM + '-' + d + ' ' + h + ':' + m;
  //return y + '-' + MM + '-' + d + ' ' + h + ':' + m + ':' + s;
};

const timestampFormatMinutesDate = function (timestamp) {
  if (!timestamp) {
    return timestamp;
  }

  let date = new Date(timestamp);

  let y = date.getFullYear();

  let MM = date.getMonth() + 1;
  MM = MM < 10 ? ('0' + MM) : MM;

  let d = date.getDate();
  d = d < 10 ? ('0' + d) : d;

  let h = date.getHours();
  h = h < 10 ? ('0' + h) : h;

  let m = date.getMinutes();
  m = m < 10 ? ('0' + m) : m;

  return y + '-' + MM + '-' + d + ' ' + h + ':' + m;
};

const timestampFormatDayDate = function (timestamp) {
  if (!timestamp) {
    return timestamp;
  }

  let date = new Date(timestamp);

  let y = date.getFullYear();

  let MM = date.getMonth() + 1;
  MM = MM < 10 ? ('0' + MM) : MM;

  let d = date.getDate();
  d = d < 10 ? ('0' + d) : d;

  return y + '-' + MM + '-' + d;
};

const packageDependencyJsonLeft = function (vulnerabilities) {
  if (!vulnerabilities) {
    return vulnerabilities;
  }

  let list = JSON.parse(vulnerabilities);

  const half = Math.ceil(list.length / 2);

  let leftData = [
    {
      name: 'VulnerableSoftware',
      severity: 'N/A',
      source: 'package',
      description: 'The scan results of this package are as follows',
      children: list.splice(0, half),
    }
  ];

  return leftData;
};

const packageDependencyJsonRight = function (vulnerabilities) {
  if (!vulnerabilities) {
    return vulnerabilities;
  }

  let list = JSON.parse(vulnerabilities);

  const half = Math.ceil(list.length / 2);

  let rightData = [
    {
      name: 'VulnerableSoftware',
      severity: 'N/A',
      source: 'package',
      description: 'The scan results of this package are as follows',
      children: list.splice(-half),
    }
  ];

  return rightData;
};

const filters = {
  "options": options,
  "timestampFormatDate": timestampFormatDate,
  "timestampFormatMinutesDate": timestampFormatMinutesDate,
  "timestampFormatDayDate": timestampFormatDayDate,
  "packageDependencyJsonLeft": packageDependencyJsonLeft,
  "packageDependencyJsonRight": packageDependencyJsonRight,
};

export default {
  install(Vue) {
    // 注册公用过滤器
    Object.keys(filters).forEach(key => {
      Vue.filter(key, filters[key])
    });
  }
}
