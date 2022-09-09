package com.hummerrisk.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@HummerPlugin
public class ImageProvider implements IProvider {

    private static String name = "image";

    private static Logger logger = LoggerFactory.getLogger(ImageProvider.class);

    public String getName() {
        return name;
    }

    public String execute(Object o) {
        String str = "";
        return str;
    }

}
