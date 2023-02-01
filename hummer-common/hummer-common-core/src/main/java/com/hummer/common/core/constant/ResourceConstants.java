package com.hummerrisk.commons.constants;

/**
 * @author harris
 */
public class ResourceConstants {

    public enum RESOURCE_STATUS {
        NotFixed, NotNeedFix, AlreadyFixed, Error
    }

    public final static String QUERY_ALL_RESOURCE =
            "policies:\n" +
            "  - name: {resource_name}\n" +
            "    resource: {resource_type}";

    public final static String ResourceTypeConstants = "TASK";
    public final static String SystemConstants = "SYSTEM";
    public final static String AccountConstants = "ACCOUNT";
    public final static String RuleConstants = "RULE";

    public final static String PluginType = "infrastructure";

}
