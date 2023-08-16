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
@RequestMapping("test/scaner")
public class TestScanerController {
    @Autowired
    @Qualifier("loadBalanced")
    private RestTemplate restTemplate;

    @GetMapping("test")
    public String test(){
        String result = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            JSONObject  jsonObject = JSONObject.parseObject("{\"plugin\":\"hummer-qcloud-plugin\",\"TENCENT_SECRETID\":\"ak\",\"TENCENT_SECRETKEY\":\"sk\",\"region\":\"ap-beijing\",\"policies\":[{\"name\": \"test\", \"resource\": \"tencent.cvm\"}]}");
            HttpEntity<?> httpEntity = new HttpEntity<>(jsonObject, headers);
            result = restTemplate.postForObject("http://hummer-scaner/run",httpEntity,String.class);
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    @PostMapping("test2")
    public String test2(@RequestBody JSONObject request){
        String result = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<?> httpEntity = new HttpEntity<>(request, headers);
            result = restTemplate.postForObject("http://hummer-scaner/run",httpEntity,String.class);
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    @PostMapping("test3")
    public String test2(@RequestBody String request){
        String result = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            JSONObject jsonObject = JSONObject.parseObject(request);
            HttpEntity<?> httpEntity = new HttpEntity<>(jsonObject, headers);
            result = restTemplate.postForObject("http://hummer-scaner/run",httpEntity,String.class);
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }
}
