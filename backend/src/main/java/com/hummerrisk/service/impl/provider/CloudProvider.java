package com.hummerrisk.service.impl.provider;

import com.hummerrisk.service.impl.HummerPlugin;
import com.hummerrisk.service.impl.IProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@HummerPlugin
public class CloudProvider implements IProvider {

    private static String name = "cloudProvider";

    private static Logger logger = LoggerFactory.getLogger(CloudProvider.class);

    public String getName() {
        return name;
    }

    public String execute(Object ...obj) {
        String str = "123";
        return str;
    }

}
