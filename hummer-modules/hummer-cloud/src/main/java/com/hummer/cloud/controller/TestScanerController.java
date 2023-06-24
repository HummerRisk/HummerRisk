package com.hummer.cloud.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@Hidden
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

    @PostMapping("test2")
    public String test2(@RequestBody JSONObject request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<?> httpEntity = new HttpEntity<>(request, headers);
        return restTemplate.postForObject("http://hummer-scaner/run",httpEntity,String.class);
    }
}
