package com.hummerrisk.service.impl.provider;

import com.hummerrisk.service.impl.HummerPlugin;
import com.hummerrisk.service.impl.IProvider;

@HummerPlugin
public class VulnProvider implements IProvider {

    private static String name = "vulnProvider";

    public String getName() {
        return name;
    }

    public String execute(Object ...obj) {
        String str = "";
        return str;
    }

}
