package com.hummer.common.core.dto;

import com.hummer.common.core.domain.Plugin;

public class PluginDTO extends Plugin {

    private boolean isExist;

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }
}
