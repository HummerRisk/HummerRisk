package com.hummerrisk.service.impl.provider;

import com.hummerrisk.base.domain.ImageRepo;
import com.hummerrisk.commons.constants.ImageConstants;
import com.hummerrisk.commons.utils.CommandUtils;
import com.hummerrisk.service.impl.HummerPlugin;
import com.hummerrisk.service.impl.IProvider;
import org.apache.commons.lang3.StringUtils;

@HummerPlugin
public class K8sProvider implements IProvider {

    private static String name = "k8sProvider";

    public String getName() {
        return name;
    }

    public String execute(Object ...obj) {
        String str = "";
        return str;
    }

    public String dockerLogin(Object obj) {
        try {
            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
