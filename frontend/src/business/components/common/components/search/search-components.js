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

export const EMAIL = {
  key: "email", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "commons.email", // 显示名称
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

export const LAST_MODIFIED = {
  key: "lastModified",
  name: "TableSearchDateTimePicker",
  label: "rule.last_modified",
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

//K8s检测插件
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

export const EVENT_ACCOUNT = {
  key: "accountId",
  name: "TableSearchSelect",
  label: "event.cloud_account",
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: { // 异步获取候选项
    url: "/account/allList",
    labelKey: "name",
    valueKey: "id",

  },
  props:{
    multiple: true
  }
}

export const REGIONID = {
  key: "regionId",
  name: "TableSearchInput",
  label: "event.region",
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const REGION = {
  key: "region",
  name: "TableSearchInput",
  label: "event.region",
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const USER = {
  key: "userName",
  name: "TableSearchInput",
  label: "event.user_name",
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const EVENT_NAME = {
  key: "eventName",
  name: "TableSearchInput",
  label: "event.event_name",
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const RESOURCE_NAME = {
  key: "resourceName",
  name: "TableSearchInput",
  label: "event.resource_name",
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const EVENT_RATING = {
  key: "eventRating",
  name: "TableSearchSelect",
  label: "event.risk_level",
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'event.low_level', value: '0'},
    {label: 'event.middle_level', value: '1'},
    {label: 'event.high_level', value: '2'}
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}
export const RESOURCE_TYPE = {
  key: "resourceType",
  name: "TableSearchInput",
  label: "event.resource_type",
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}
export const EVENT_TIME = {
  key: "eventTime",
  name: 'TableSearchDateTimePicker',
  label: 'event.event_time',
  operator: {
    options: [OPERATORS.BETWEEN, OPERATORS.GT, OPERATORS.GE, OPERATORS.LT, OPERATORS.LE, OPERATORS.EQ]
  },
}

export const CREATE_TIME = {
  key: "createTime",
  name: 'TableSearchDateTimePicker',
  label: 'commons.create_time',
  operator: {
    options: [OPERATORS.BETWEEN, OPERATORS.GT, OPERATORS.GE, OPERATORS.LT, OPERATORS.LE, OPERATORS.EQ]
  },
}

export const READ_TIME = {
  key: "readTime",
  name: 'TableSearchDateTimePicker',
  label: 'commons.update_time',
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
    {label: 'server.UNLINK', value: 'UNLINK'},
    {label: 'account.NEW', value: 'NEW'}
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const RESULT_STATUS = {
  key: "status",
  name: 'TableSearchSelect',
  label: 'commons.status',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'resource.i18n_in_process', value: 'APPROVED'},
    {label: 'resource.i18n_done', value: 'FINISHED'},
    {label: 'resource.i18n_has_exception', value: 'ERROR'},
    {label: 'resource.i18n_has_warn', value: 'WARNING'},
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const K8S_RESULT_STATUS = {
  key: "resultStatus",
  name: 'TableSearchSelect',
  label: 'commons.status',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'resource.i18n_in_process', value: 'APPROVED'},
    {label: 'resource.i18n_done', value: 'FINISHED'},
    {label: 'resource.i18n_has_exception', value: 'ERROR'},
    {label: 'resource.i18n_has_warn', value: 'WARNING'},
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

export const SERVER_RULE_NAME = {
  key: "ruleName", // 返回结果Map的key
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

export const RULE_GROUP = {
  key: "groupId",
  name: 'TableSearchSelect',
  label: 'rule.rule_set',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: { // 异步获取候选项
    url: "/rule/allCloudRuleGroups",
    labelKey: "name",
    valueKey: "id",
    showLabel: option => {
      return option.label;
    }
  },
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: false
  }
}

export const VULN_RULE_GROUP = {
  key: "groupId",
  name: 'TableSearchSelect',
  label: 'rule.rule_set',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: { // 异步获取候选项
    url: "/rule/allVulnRuleGroups",
    labelKey: "name",
    valueKey: "id",
    showLabel: option => {
      return option.label;
    }
  },
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: false
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
    {label: 'rule.CriticalRisk', value: 'CriticalRisk'},
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
    url: "/rule/all/cloudResourceTypes",
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

export const VULN_RULE_RESOURCE_TYPE = {
  key: "resourceType",
  name: 'TableSearchSelect',
  label: 'rule.resource_type',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: { // 异步获取候选项
    url: "/rule/all/vulnResourceTypes",
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

export const SITUATION_LOG_STATUS = {
  key: "flag",
  name: 'TableSearchSelect',
  label: 'commons.status',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'commons.success', value: true},
    {label: 'commons.error', value: false}
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const REPOSITORY = {
  key: "repository", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "Repository", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const PATH = {
  key: "path", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "Path", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const SIZE = {
  key: "size", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "Size", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const TAG = {
  key: "tag", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "Tag", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const RESOURCE_USER_NAME = {
  key: "resourceUserName", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "dashboard.resource_user_name", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const TIME = {
  key: "time",
  name: 'TableSearchDateTimePicker',
  label: 'dashboard.time',
  operator: {
    options: [OPERATORS.BETWEEN, OPERATORS.GT, OPERATORS.GE, OPERATORS.LT, OPERATORS.LE, OPERATORS.EQ]
  },
}

export const RESOURCE_USER_ID = {
  key: "resourceUserId",
  name: 'TableSearchSelect',
  label: 'dashboard.resource_user_id',
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

export const ACTIVE_RESOURCE_TYPE = {
  key: "resourceType",
  name: 'TableSearchSelect',
  label: 'dashboard.resource_type',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'USER', value: 'USER'},
    {label: 'ROLE', value: 'ROLE'},
    {label: 'ORGANIZATION', value: 'ORGANIZATION'},
    {label: 'WORKSPACE', value: 'WORKSPACE'},
    {label: 'CLOUD_ACCOUNT', value: 'CLOUD_ACCOUNT'},
    {label: 'CLOUD_NATIVE', value: 'CLOUD_NATIVE'},
    {label: 'CLOUD_NATIVE_CONFIG', value: 'CLOUD_NATIVE_CONFIG'},
    {label: 'RULE', value: 'RULE'},
    {label: 'RULE_TAG', value: 'RULE_TAG'},
    {label: 'RESOURCE', value: 'RESOURCE'},
    {label: 'TASK', value: 'TASK'},
    {label: 'SYSTEM', value: 'SYSTEM'},
    {label: 'QUOTA', value: 'QUOTA'},
    {label: 'PROXY', value: 'PROXY'},
    {label: 'SERVER', value: 'SERVER'},
    {label: 'IMAGE', value: 'IMAGE'},
    {label: 'CODE', value: 'CODE'},
    {label: 'SBOM', value: 'SBOM'},
    {label: 'SBOM_VERSION', value: 'SBOM_VERSION'},
    {label: 'SYNC', value: 'SYNC'},
    {label: 'FILE_SYSTEM', value: 'FILE_SYSTEM'},
    {label: 'OSS', value: 'OSS'}
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const PROXY_TYPE = {
  key: "proxyType",
  name: 'TableSearchSelect',
  label: 'commons.proxy_type',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'Http', value: 'Http'},
    {label: 'Https', value: 'Https'},
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const PROXY_IP = {
  key: "proxyIp", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "Proxy IP", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const SCAN_TYPE = {
  key: "scanType",
  name: 'TableSearchSelect',
  label: 'system.plugin_scan_type',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'custodian', value: 'custodian'},
    {label: 'native', value: 'native'},
    {label: 'xray', value: 'xray'},
    {label: 'nuclei', value: 'nuclei'},
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const PLUGIN_TYPE = {
  key: "type",
  name: 'TableSearchSelect',
  label: 'system.plugin_type',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'cloud', value: 'cloud'},
    {label: 'native', value: 'native'},
    {label: 'vuln', value: 'vuln'},
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const USER_KEY_STATUS = {
  key: "status",
  name: 'TableSearchSelect',
  label: 'commons.status',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'ACTIVE', value: 'ACTIVE'},
    {label: 'DISABLED', value: 'DISABLED'},
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const ACCESS_KEY = {
  key: "accessKey", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "Access Key", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const SECRET_KEY = {
  key: "secretKey", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "Secret Key", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const PREV_FIRE_TIME = {
  key: "prevFireTime",
  name: 'TableSearchDateTimePicker',
  label: 'account.prev_fire_time',
  operator: {
    options: [OPERATORS.BETWEEN, OPERATORS.GT, OPERATORS.GE, OPERATORS.LT, OPERATORS.LE, OPERATORS.EQ]
  },
}

export const LAST_FIRE_TIME = {
  key: "lastFireTime",
  name: 'TableSearchDateTimePicker',
  label: 'account.last_fire_time',
  operator: {
    options: [OPERATORS.BETWEEN, OPERATORS.GT, OPERATORS.GE, OPERATORS.LT, OPERATORS.LE, OPERATORS.EQ]
  },
}

export const CLOUD_QUARTZ_STATUS = {
  key: "status",
  name: 'TableSearchSelect',
  label: 'account.task_status',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'account.PAUSE', value: 'PAUSE'},
    {label: 'account.RUNNING', value: 'RUNNING'},
    {label: 'account.ERROR', value: 'ERROR'},
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const CLOUD_QUARTZ_TYPE = {
  key: "qzType",
  name: 'TableSearchSelect',
  label: 'account.choose_qztype',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'account.qztype_account', value: 'ACCOUNT'},
    {label: 'account.qztype_rule', value: 'RULE'},
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const APPLY_USER = {
  key: "applyUser",
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

export const REPO = {
  key: "repo", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "image.image_repo_url", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const ITEM_SORT_FIRST_LEVEL = {
  key: "itemSortFirstLevel", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "resource.security_level", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const ITEM_SORT_SECOND_LEVEL = {
  key: "itemSortSecondLevel", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "resource.control_point", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const PROJECT = {
  key: "project", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "resource.basic_requirements_for_grade_protection", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const IMPROVEMENT = {
  key: "improvement", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "resource.suggestions_for_improvement", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const SERVER_TYPE = {
  key: "type",
  name: 'TableSearchSelect',
  label: 'commons.type',
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'Linux', value: 'linux'},
    {label: 'Windows', value: 'windows'},
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const RESOURCE = {
  key: "resource", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "Resource", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const PKGNAME = {
  key: "pkgname", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "PkgName", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const TITLE = {
  key: "title", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "Title", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const NUMBER = {
  key: "number", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "Number", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const VULNERABILITYID = {
  key: "vulnerabilityId", // 返回结果Map的key
  name: "TableSearchInput", // Vue控件名称
  label: "VulnerabilityID", // 显示名称
  operator: { // 运算符设置
    value: OPERATORS.LIKE.value, // 如果未设置value初始值，则value初始值为options[0]
    options: [OPERATORS.LIKE, OPERATORS.NOT_LIKE] // 运算符候选项
  },
}

export const SEVERITY = {
  key: "severity", // 返回结果Map的key
  name: "TableSearchSelect", // Vue控件名称
  label: "Severity", // 显示名称
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'CRITICAL', value: 'CRITICAL'},
    {label: 'HIGH', value: 'HIGH'},
    {label: 'MEDIUM', value: 'MEDIUM'},
    {label: 'LOW', value: 'LOW'},
    {label: 'UNKNOWN', value: 'UNKNOWN'},
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const KUBENCH_SEVERITY = {
  key: "severity", // 返回结果Map的key
  name: "TableSearchSelect", // Vue控件名称
  label: "Severity", // 显示名称
  operator: {
    options: [OPERATORS.IN, OPERATORS.NOT_IN]
  },
  options: [
    {label: 'FAIL', value: 'FAIL'},
    {label: 'WARN', value: 'WARN'},
    {label: 'INFO', value: 'INFO'},
    {label: 'PASS', value: 'PASS'},
  ],
  props: { // 尾部控件的props，一般为element ui控件的props
    multiple: true
  }
}

export const USER_CONFIGS = [NAME, EMAIL, UPDATE_TIME, CREATE_TIME];
export const PROXY_CONFIGS = [PROXY_TYPE, PROXY_IP, UPDATE_TIME, CREATE_TIME];
export const MSG_CONFIGS = [READ_TIME, CREATE_TIME];
export const PLUGIN_CONFIGS = [NAME, PLUGIN_NAME, SCAN_TYPE, PLUGIN_TYPE, UPDATE_TIME];
export const USER_KEY_CONFIGS = [ACCESS_KEY, SECRET_KEY, USER_KEY_STATUS, CREATE_TIME];
export const ACTIVE_CONFIGS = [RESOURCE_USER_NAME, TIME, RESOURCE_USER_ID, ACTIVE_RESOURCE_TYPE];
export const ACCOUNT_CONFIGS = [NAME, PLUGIN_NAME, UPDATE_TIME, CREATE_TIME, ACCOUNT_STATUS, CREATOR];
export const CLOUD_TASK_CONFIGS = [NAME, APPLY_USER, CLOUD_QUARTZ_STATUS, CLOUD_QUARTZ_TYPE, PREV_FIRE_TIME, LAST_FIRE_TIME, CREATE_TIME];
export const OSS_CONFIGS = [NAME, PLUGIN_NAME, UPDATE_TIME, CREATE_TIME, RESULT_STATUS, CREATOR];
export const OSS_BUCKET_CONFIGS = [NAME, PLUGIN_NAME, UPDATE_TIME, CREATE_TIME];
export const VULN_CONFIGS = [NAME, VULN_PLUGIN_NAME, UPDATE_TIME, CREATE_TIME, ACCOUNT_STATUS, CREATOR];
export const VULN_RULE_CONFIGS = [RULE_NAME, VULN_PLUGIN_NAME, RULE_SEVERITY, VULN_RULE_RESOURCE_TYPE, VULN_RULE_GROUP];
export const RULE_TAG_CONFIGS = [TAG_KEY, TAG_NAME, TAG_FLAG, TAG_INDEX];
export const RULE_GROUP_CONFIGS = [NAME, DESCRIPTION, TAG_FLAG, RULE_PLUGIN_NAME];
export const RULE_CONFIGS = [RULE_NAME, RULE_PLUGIN_NAME, RULE_SEVERITY, RULE_RESOURCE_TYPE, RULE_GROUP];
export const REPORT_CONFIGS = [NAME, DESCRIPTION, TAG_FLAG];
export const RESULT_CONFIGS = [RULE_NAME, RULE_SEVERITY, RESULT_STATUS, APPLY_USER, CREATE_TIME];
export const RESOURCE_CONFIGS = [REGIONID, RULE_SEVERITY, RULE_RESOURCE_TYPE, APPLY_USER, CREATE_TIME, UPDATE_TIME];
export const SERVER_CONFIGS = [NAME, IP, SERVER_TYPE, UPDATE_TIME, CREATE_TIME, ACCOUNT_STATUS, CREATOR, UPDATE_TIME];
export const SERVER_CERTIFICATE_CONFIGS = [NAME, DESCRIPTION, CREATOR, LAST_MODIFIED];
export const SERVER_RULE_CONFIGS = [RULE_NAME, RULE_SEVERITY, SERVER_TYPE, LAST_MODIFIED];
export const SERVER_RESULT_CONFIGS = [NAME, IP, SERVER_TYPE, SERVER_RULE_NAME, RULE_SEVERITY, RESULT_STATUS, CREATOR, UPDATE_TIME];
export const SERVER_RESULT_CONFIGS2 = [NAME, IP, SERVER_TYPE, UPDATE_TIME, CREATE_TIME, ACCOUNT_STATUS, CREATOR, UPDATE_TIME];
export const SERVER_RULE_GROUP_CONFIGS = [NAME, DESCRIPTION, TAG_FLAG];
export const IMAGE_CONFIGS = [NAME, IMAGE_URL, ACCOUNT_STATUS, CREATOR, CREATE_TIME, UPDATE_TIME];
export const IMAGE_REPO_CONFIGS = [NAME, REPO, ACCOUNT_STATUS, CREATOR, UPDATE_TIME];
export const IMAGE_REPO_IMAGE_CONFIGS = [REPOSITORY, PATH, SIZE, TAG];
export const IMAGE_RULE_CONFIGS = [RULE_NAME, RULE_SEVERITY];
export const IMAGE_RESULT_CONFIGS = [NAME, IMAGE_URL, RULE_NAME, K8S_RESULT_STATUS, CREATE_TIME, UPDATE_TIME];
export const K8S_CONFIGS = [NAME, K8S_PLUGIN_NAME, UPDATE_TIME, CREATE_TIME, ACCOUNT_STATUS, CREATOR];
export const K8S_SITUATION_CONFIGS = [SOURCE_NAME, SOURCE_NAMESPACE, SOURCE_TYPE, CREATOR, UPDATE_TIME, CREATE_TIME];
export const K8S_SITUATION_LOG_CONFIGS = [K8S_PLUGIN_NAME ,CREATE_TIME];
export const SITUATION_CONFIGS = [EVENT_ACCOUNT, PLUGIN_NAME, REGIONID, RULE_RESOURCE_TYPE, UPDATE_TIME, CREATE_TIME];
export const SITUATION_LOG_CONFIGS = [EVENT_ACCOUNT, PLUGIN_NAME ,CREATE_TIME];
export const K8S_RESULT_CONFIGS = [NAME, K8S_PLUGIN_NAME, CREATOR, K8S_RESULT_STATUS, CREATE_TIME, UPDATE_TIME];
export const CONFIG_CONFIGS = [NAME, ACCOUNT_STATUS, UPDATE_TIME, CREATE_TIME, CREATOR];
export const CONFIG_RESULT_CONFIGS = [NAME, K8S_RESULT_STATUS, CREATOR, UPDATE_TIME];
export const TASK_CONFIGS = [NAME, CREATOR, CREATE_TIME, UPDATE_TIME];
export const CODE_CONFIGS = [NAME, UPDATE_TIME, CREATE_TIME, ACCOUNT_STATUS, CREATOR];
export const CODE_RULE_CONFIGS = [RULE_NAME, RULE_SEVERITY];
export const CODE_RESULT_CONFIGS = [NAME, RULE_NAME, RULE_SEVERITY, K8S_RESULT_STATUS, CREATOR, UPDATE_TIME];
export const CLOUD_EVENT_SYNC_CONFIGS = [EVENT_ACCOUNT, REGION, CREATE_TIME];
export const CLOUD_EVENT_CONFIGS = [EVENT_ACCOUNT, REGION, EVENT_TIME,USER, EVENT_NAME, RESOURCE_TYPE, RESOURCE_NAME, EVENT_RATING];
export const SBOM_CONFIGS = [NAME, UPDATE_TIME, CREATE_TIME, CREATOR];
export const FS_CONFIGS = [NAME, UPDATE_TIME, CREATE_TIME, ACCOUNT_STATUS, CREATOR];
export const FS_RESULT_CONFIGS = [NAME, RULE_NAME, RULE_SEVERITY, K8S_RESULT_STATUS, CREATOR, UPDATE_TIME];
export const RULE_INSPECTION_REPORT_CONFIGS = [ITEM_SORT_FIRST_LEVEL, ITEM_SORT_SECOND_LEVEL, PROJECT, IMPROVEMENT];
export const DETAIL_RESULT_CONFIGS = [PKGNAME, RESOURCE, VULNERABILITYID, SEVERITY];
export const K8S_KUBENCH_RESULT_CONFIGS = [TITLE, NUMBER, DESCRIPTION, KUBENCH_SEVERITY];
