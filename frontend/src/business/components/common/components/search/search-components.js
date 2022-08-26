import TableSearchInput from "./TableSearchInput";
import TableSearchDateTimePicker from "./TableSearchDateTimePicker";
import TableSearchDatePicker from "./TableSearchDatePicker";
import TableSearchSelect from "./TableSearchSelect";

/* eslint-disable */
export default {
  TableSearchInput, TableSearchDatePicker, TableSearchDateTimePicker, TableSearchSelect
}

export const OPERATORS = {
  LIKE: {
    label: "commons.adv_search.operators.like",
    value: "like"
  },
  NOT_LIKE: {
    label: "commons.adv_search.operators.not_like",
    value: "not like"
  },
  IN: {
    label: "commons.adv_search.operators.in",
    value: "in"
  },
  NOT_IN: {
    label: "commons.adv_search.operators.not_in",
    value: "not in"
  },
  GT: {
    label: "commons.adv_search.operators.gt",
    value: "gt"
  },
  GE: {
    label: "commons.adv_search.operators.ge",
    value: "ge"
  },
  LT: {
    label: "commons.adv_search.operators.lt",
    value: "lt"
  },
  LE: {
    label: "commons.adv_search.operators.le",
    value: "le"
  },
  EQ: {
    label: "commons.adv_search.operators.equals",
    value: "eq"
  },
  BETWEEN: {
    label: "commons.adv_search.operators.between",
    value: "between"
  },
  CURRENT_USER: {
    label: "commons.adv_search.operators.current_user",
    value: "current user"
  },
}

export const NAME = {
  key: "name", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "commons.name", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const DESCRIPTION = {
  key: "description", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "commons.description", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const UPDATE_TIME = {
  key: "updateTime",
  name: "TableSearchDateTimePicker",
  label: "commons.update_time",
  operator: {
    options: [OPERATORS.BETWEEN, OPERATORS.GT, OPERATORS.GE, OPERATORS.LT, OPERATORS.LE, OPERATORS.EQ]
  },
}

//云账号插件
export const PLUGIN_NAME = {
  key: "pluginId",
  name: 'TableSearchSelect',
  label: 'commons.adv_search.plugin',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: { // 异步获取候选项
    url: "/plugin/cloud",
    labelKey: "name",
    valueKey: "id",
    showLabel: option => {
      return option.label + " (" + option.value + ") ";
    }
  },
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

//漏洞检测插件
export const VULN_PLUGIN_NAME = {
  key: "pluginId",
  name: 'TableSearchSelect',
  label: 'commons.adv_search.plugin',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: { // 异步获取候选项
    url: "/plugin/vuln",
    labelKey: "name",
    valueKey: "id",
    showLabel: option => {
      return option.label + " (" + option.value + ") ";
    }
  },
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

//云原生检测插件
export const K8S_PLUGIN_NAME = {
  key: "pluginId",
  name: 'TableSearchSelect',
  label: 'commons.adv_search.plugin',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: { // 异步获取候选项
    url: "/plugin/native",
    labelKey: "name",
    valueKey: "id",
    showLabel: option => {
      return option.label + " (" + option.value + ") ";
    }
  },
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const CREATE_TIME = {
  key: "createTime",
  name: 'TableSearchDateTimePicker',
  label: 'commons.create_time',
  operator: {
    options: [OPERATORS.BETWEEN, OPERATORS.GT, OPERATORS.GE, OPERATORS.LT, OPERATORS.LE, OPERATORS.EQ]
  },
}

export const ACCOUNT_STATUS = {
  key: "status",
  name: 'TableSearchSelect',
  label: 'commons.status',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'account.INVALID', value: 'INVALID'},
    {label: 'account.VALID', value: 'VALID'},
    {label: 'account.DELETE', value: 'DELETE'},
    {label: 'account.NEW', value: 'NEW'}
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const CREATOR = {
  key: "creator",
  name: 'TableSearchSelect',
  label: 'resource.creator',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN, OPERATORS.CURRENT_USER],
    change: function (component, value) { // 运算符change事件
      if (value === OPERATORS.CURRENT_USER.value) {
        component.value = value;
      }
    }
  },
  options: { // 异步获取候选项
    url: "/user/list/all",
    labelKey: "name",
    valueKey: "id",
    showLabel: option => {
      return option.label + "(" + option.value + ")";
    }
  },
  props: {
    multiple: true
  },
  isShow: operator => {
    return operator !== OPERATORS.CURRENT_USER.value;
  }
}

export const TAG_KEY = {
  key: "tagKey", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "rule.tag_key", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const TAG_NAME = {
  key: "tagName", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "rule.tag_name", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const TAG_FLAG = {
  key: "flag",
  name: 'TableSearchSelect',
  label: 'rule.tag_flag',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'rule.tag_flag_true', value: 1},
    {label: 'rule.tag_flag_false', value: 0}
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const TAG_INDEX = {
  key: "index", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "rule._index", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE] // 运算符候选项
  }
}

export const RULE_NAME = {
  key: "name", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "rule.rule_name", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const RULE_PLUGIN_NAME = {
  key: "pluginId",
  name: 'TableSearchSelect',
  label: 'commons.adv_search.plugin',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: { // 异步获取候选项
    url: "/plugin/cloud",
    labelKey: "name",
    valueKey: "id",
    showLabel: option => {
      return option.label + " (" + option.value + ") ";
    }
  },
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const RULE_SEVERITY = {
  key: "severity",
  name: 'TableSearchSelect',
  label: 'rule.severity',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'rule.HighRisk', value: 'HighRisk'},
    {label: 'rule.MediumRisk', value: 'MediumRisk'},
    {label: 'rule.LowRisk', value: 'LowRisk'}
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const RULE_RESOURCE_TYPE = {
  key: "resourceType",
  name: 'TableSearchSelect',
  label: 'rule.resource_type',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: { // 异步获取候选项
    url: "/rule/all/resourceTypes",
    labelKey: "name",
    valueKey: "name",
    showLabel: option => {
      return option.label;
    }
  },
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: false
  }
}

export const RULE_TAG = {
  key: "ruleTag",
  name: 'TableSearchSelect',
  label: 'rule.rule_tag',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: { // 异步获取候选项
    url: "/rule/ruleTags",
    labelKey: "tagName",
    valueKey: "tagKey",
    showLabel: option => {
      return option.label;
    }
  },
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: false
  }
}

export const IP = {
  key: "ip", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "IP", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const PACKAGE_NAME = {
  key: "packageName", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "package.package_name", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const PACKAGE_STATUS = {
  key: "status",
  name: 'TableSearchSelect',
  label: 'commons.status',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'account.INVALID', value: 'INVALID'},
    {label: 'account.VALID', value: 'VALID'},
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const IMAGE_URL = {
  key: "imageUrl", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "image.image_url", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const SOURCE_NAME = {
  key: "sourceName", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "k8s.source_name", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const SOURCE_NAMESPACE = {
  key: "name", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "k8s.source_namespace", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const SOURCE_TYPE = {
  key: "sourceType",
  name: 'TableSearchSelect',
  label: 'k8s.source_type',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'Namespace', value: 'Namespace'},
    {label: 'Pod', value: 'Pod'},
    {label: 'Node', value: 'Node'},
    {label: 'Deployment', value: 'Deployment'},
    {label: 'Daemonset', value: 'Daemonset'},
    {label: 'Service', value: 'Service'},
    {label: 'Ingress', value: 'Ingress'},
    {label: 'Role', value: 'Role'},
    {label: 'Secret', value: 'Secret'},
    {label: 'Configmap', value: 'Configmap'},
    {label: 'StatefulSet', value: 'StatefulSet'},
    {label: 'CronJob', value: 'CronJob'},
    {label: 'Job', value: 'Job'},
    {label: 'PV', value: 'PV'},
    {label: 'PVC', value: 'PVC'},
    {label: 'Lease', value: 'Lease'},
    {label: 'EndpointSlice', value: 'EndpointSlice'},
    {label: 'Event', value: 'Event'},
    {label: 'NetworkPolicy', value: 'NetworkPolicy'},
    {label: 'Version', value: 'Version'},
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const ACCOUNT_CONFIGS = [NAME, PLUGIN_NAME, UPDATE_TIME, CREATE_TIME, ACCOUNT_STATUS, CREATOR];
export const VULN_CONFIGS = [NAME, VULN_PLUGIN_NAME, UPDATE_TIME, CREATE_TIME, ACCOUNT_STATUS, CREATOR];
export const RULE_TAG_CONFIGS = [TAG_KEY, TAG_NAME, TAG_FLAG, TAG_INDEX];
export const RULE_GROUP_CONFIGS = [NAME, DESCRIPTION, RULE_PLUGIN_NAME];
export const RULE_CONFIGS = [RULE_NAME, RULE_PLUGIN_NAME, RULE_SEVERITY, RULE_RESOURCE_TYPE];
export const SERVER_CONFIGS = [NAME, IP, UPDATE_TIME, CREATE_TIME, ACCOUNT_STATUS, CREATOR];
export const SERVER_RULE_CONFIGS = [RULE_NAME, RULE_SEVERITY];
export const SERVER_RESULT_CONFIGS = [NAME, IP, RULE_NAME, RULE_SEVERITY, CREATOR, UPDATE_TIME];
export const PACKAGE_CONFIGS = [NAME, PACKAGE_NAME, PACKAGE_STATUS, CREATOR, UPDATE_TIME];
export const PACKAGE_RULE_CONFIGS = [RULE_NAME, RULE_SEVERITY];
export const PACKAGE_RESULT_CONFIGS = [NAME, PACKAGE_NAME, RULE_NAME, RULE_SEVERITY, CREATOR, UPDATE_TIME];
export const IMAGE_CONFIGS = [NAME, IMAGE_URL, ACCOUNT_STATUS, CREATOR, CREATE_TIME, UPDATE_TIME];
export const IMAGE_REPO_CONFIGS = [NAME, ACCOUNT_STATUS, CREATOR, UPDATE_TIME];
export const IMAGE_RULE_CONFIGS = [RULE_NAME, RULE_SEVERITY];
export const IMAGE_RESULT_CONFIGS = [NAME, IMAGE_URL, RULE_NAME, RULE_SEVERITY, CREATOR, UPDATE_TIME];
export const K8S_CONFIGS = [NAME, K8S_PLUGIN_NAME, UPDATE_TIME, CREATE_TIME, ACCOUNT_STATUS, CREATOR];
export const SITUATION_CONFIGS = [SOURCE_NAME, SOURCE_NAMESPACE, SOURCE_TYPE, CREATOR, UPDATE_TIME, CREATE_TIME];
export const K8S_RESULT_CONFIGS = [NAME, K8S_PLUGIN_NAME, ACCOUNT_STATUS, CREATOR, CREATE_TIME, UPDATE_TIME];
export const CONFIG_CONFIGS = [NAME, ACCOUNT_STATUS, UPDATE_TIME, CREATE_TIME, CREATOR];
export const CONFIG_RESULT_CONFIGS = [NAME, CREATOR, UPDATE_TIME];
export const TASK_CONFIGS = [NAME, CREATOR, CREATE_TIME, UPDATE_TIME];
export const CODE_CONFIGS = [NAME, UPDATE_TIME, CREATE_TIME, ACCOUNT_STATUS, CREATOR];
export const CODE_RULE_CONFIGS = [RULE_NAME, RULE_SEVERITY];
export const CODE_RESULT_CONFIGS = [NAME, RULE_NAME, RULE_SEVERITY, CREATOR, UPDATE_TIME];
