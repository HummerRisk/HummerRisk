package com.hummer.common.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import static com.alibaba.fastjson.JSON.parseArray;
import static com.alibaba.fastjson.JSON.parseObject;

public class JsonUtils {

    public static String toPrettyJSONString(String jsonString) {
        Object json = null;
        if (jsonString.startsWith("[")) {
            json = parseArray(jsonString);
        } else if (jsonString.startsWith("{")) {
            json = parseObject(jsonString);
        } else {
            throw new RuntimeException("is not a jsonString:" + jsonString);
        }
        return JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
    }

}
