package com.hummer.cloud.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("testScaner")
public class TestScanerController {
    @Autowired
    @Qualifier("loadBalanced")
    private RestTemplate restTemplate;

    @GetMapping("test")
    public String test(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        JSONObject  jsonObject = JSONObject.parseObject("{\"plugin\":\"hummer-qcloud-plugin\",\"TENCENT_SECRETID\":\"ak\",\"TENCENT_SECRETKEY\":\"sk\",\"region\":\"ap-beijing\",\"policies\":[{\"name\": \"test\", \"resource\": \"tencent.cvm\"}]}");
        HttpEntity<?> httpEntity = new HttpEntity<>(jsonObject, headers);
        String result = restTemplate.postForObject("http://hummer-scaner/run",httpEntity,String.class);
        return result;
    }
}
