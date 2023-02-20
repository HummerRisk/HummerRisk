package com.hummer.common.mapper.domain.request.task;

import com.hummer.common.mapper.domain.Task;


public class TaskVo extends Task {

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
