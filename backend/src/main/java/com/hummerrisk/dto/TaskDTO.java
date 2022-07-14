package com.hummerrisk.dto;

import com.hummerrisk.base.domain.Task;


public class TaskDTO extends Task {

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
