package com.hummer.common.core.domain.request.task;

import com.hummer.common.core.domain.Task;


public class TaskVo extends Task {

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
