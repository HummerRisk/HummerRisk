package com.hummerrisk.commons.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * 对象与yaml字符串互转工具
 */
public class YamlUtil {
    /**
     * 将yaml字符串转成类对象
     * @param yamlStr 字符串
     * @param clazz 目标类
     * @param <T> 泛型
     * @return 目标类
     */
    public static <T> T toObject(String yamlStr, Class<T> clazz){
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        try {
            return mapper.readValue(yamlStr, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将类对象转yaml字符串
     * @param object 对象
     * @return yaml字符串
     */
    public static String toYaml(Object object){
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        StringWriter stringWriter = new StringWriter();
        try {
            mapper.writeValue(stringWriter, object);
            return stringWriter.toString();
        } catch (IOException e) {
            LogUtil.debug(e.getMessage());
        }
        return "";
    }

    /**
     * （此方法非必要）
     * json 2 yaml
     * @param jsonStr json
     * @return yaml
     * @throws JsonProcessingException Exception
     */
    public static String json2Yaml(String jsonStr) throws JsonProcessingException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonStr);
        return new YAMLMapper().writeValueAsString(jsonNode);
    }

    public static boolean validateYaml(String input) {
        try {
            Yaml yaml = new Yaml();
            yaml.loadAs(input, Map.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean validateJson(String input) {
        if(StringUtils.isBlank(input)){
            return false;
        }
        boolean isJsonObject = true;
        boolean isJsonArray = true;
        try {
            JSONObject.parseObject(input);
        } catch (Exception e) {
            isJsonObject = false;
        }
        try {
            JSONObject.parseArray(input);
        } catch (Exception e) {
            isJsonArray = false;
        }
        if(!isJsonObject && !isJsonArray){ //不是json格式
            return false;
        }
        return true;
    }

}
