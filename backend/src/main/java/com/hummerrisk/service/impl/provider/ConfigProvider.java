package com.hummerrisk.service.impl.provider;

import com.hummerrisk.service.impl.HummerPlugin;
import com.hummerrisk.service.impl.IProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@HummerPlugin
public class ConfigProvider implements IProvider {

    private static String name = "configProvider";

    private static Logger logger = LoggerFactory.getLogger(ConfigProvider.class);

    public String getName() {
        return name;
    }

    public String execute(Object ...obj) {
        String str = "123";
        return str;
    }

}
