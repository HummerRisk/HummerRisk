package com.hummerrisk.commons.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import static com.alibaba.fastjson.JSON.parseArray;
import static com.alibaba.fastjson.JSON.parseObject;

public class JsonUtils {

    public static String toPrettyJSONString(String jsonString) {
        String res = JSON.parse(jsonString).toString();
        Object json = null;
        if(jsonString.startsWith("[")){
            json = parseArray(res);
        }else if(jsonString.startsWith("{")){
            json = parseObject(jsonString);
        }else{
            throw new RuntimeException("is not a jsonString:"+jsonString);
        }
        return JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
    }

}
