package com.hummerrisk.service.impl.provider;

import com.hummerrisk.dto.ResultDTO;
import com.hummerrisk.service.impl.HummerPlugin;
import com.hummerrisk.service.impl.IProvider;

@HummerPlugin
public class VulnProvider implements IProvider {

    private static String name = "vulnProvider";

    public String getName() {
        return name;
    }

    public ResultDTO execute(Object... obj) {
        return new ResultDTO();
    }

    public String dockerLogin(Object obj) {
        try {
            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
