package io.hummerrisk.commons.constants;

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
    }

    interface Event {
        String EXECUTE_SUCCESSFUL = "EXECUTE_SUCCESSFUL";
        String EXECUTE_FAILED = "EXECUTE_FAILED";
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
