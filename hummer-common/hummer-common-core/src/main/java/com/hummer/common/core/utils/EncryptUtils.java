package com.hummer.common.core.utils;

import java.util.List;
import java.util.stream.Collectors;

public class EncryptUtils extends com.hummer.common.core.utils.CodingUtil {

    private static final String secretKey = "www.hummer.co";
    private static final String iv = "1234567890123456";


    public static Object aesEncrypt(Object o) throws Exception {
        if (o == null) {
            return null;
        }
        return aesEncrypt(o.toString(), secretKey, iv);
    }

    public static Object aesDecrypt(Object o) {
        if (o == null) {
            return null;
        }
        return aesDecrypt(o.toString(), secretKey, iv);
    }

    public static <T> Object aesDecrypt(List<T> o, String attrName) {
        if (o == null) {
            return null;
        }
        return o.stream()
                .filter(element -> com.hummer.common.core.utils.BeanUtils.getFieldValueByName(attrName, element) != null)
                .peek(element -> com.hummer.common.core.utils.BeanUtils.setFieldValueByName(element, attrName, aesDecrypt(com.hummer.common.core.utils.BeanUtils.getFieldValueByName(attrName, element).toString(), secretKey, iv), String.class))
                .collect(Collectors.toList());
    }

    public static Object md5Encrypt(Object o) throws Exception {
        if (o == null) {
            return null;
        }
        return md5(o.toString());
    }
}
