package com.hummerrisk.dto;

import com.hummerrisk.base.domain.AccountWithBLOBs;


public class ValidateDTO extends AccountWithBLOBs {

   private String message;

   private boolean flag;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
