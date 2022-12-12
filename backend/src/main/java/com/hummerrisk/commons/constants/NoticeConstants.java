package com.hummerrisk.commons.constants;

public interface NoticeConstants {

    interface TaskType {
        String RESOURCE_TASK = "RESOURCE_TASK";
        String REVIEW_TASK = "REVIEW_TASK";
        String DEFECT_TASK = "DEFECT_TASK";
    }

    interface Type {
        String EMAIL = "EMAIL";
        String NAIL_ROBOT = "NAIL_ROBOT";
        String WECHAT_ROBOT = "WECHAT_ROBOT";
        String WEBHOOK = "WEBHOOK";
    }

    interface Event {
        String EXECUTE_SUCCESSFUL = "EXECUTE_SUCCESSFUL";
        String EXECUTE_FAILED = "EXECUTE_FAILED";
        String EXECUTE_CLOUD = "EXECUTE_CLOUD";
        String EXECUTE_VULN = "EXECUTE_VULN";
        String EXECUTE_SERVER = "EXECUTE_SERVER";
        String EXECUTE_K8S = "EXECUTE_K8S";
        String EXECUTE_CONFIG = "EXECUTE_CONFIG";
        String EXECUTE_IMAGE = "EXECUTE_IMAGE";
        String EXECUTE_CODE = "EXECUTE_CODE";
        String EXECUTE_FS = "EXECUTE_FS";
        String CREATE = "CREATE";
        String UPDATE = "UPDATE";
        String DELETE = "DELETE";
        String COMMENT = "COMMENT";
    }

    interface RelatedUser {
        String FOUNDER = "FOUNDER";//创建人
    }

    interface MessageOrderStatus {
        String PROCESSING = "PROCESSING";
        String FINISHED = "FINISHED";
        String ERROR = "ERROR";
    }
}
