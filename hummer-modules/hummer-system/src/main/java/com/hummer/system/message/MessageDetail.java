package com.hummer.system.message;


import com.hummer.common.core.domain.MessageTaskWithBLOBs;

import java.util.ArrayList;
import java.util.List;

public class MessageDetail extends MessageTaskWithBLOBs {

    private List<String> userIds = new ArrayList<>();

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

}
