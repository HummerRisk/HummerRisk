package com.hummerrisk.controller.request.task;

import com.hummerrisk.base.domain.Task;


public class TaskVo extends Task {

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
