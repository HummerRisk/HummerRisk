package com.hummer.k8s.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport  {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestTemplate restTemplateWithTimeOut() {
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(4000);
        httpRequestFactory.setConnectTimeout(4000);
        restTemplate.setRequestFactory(httpRequestFactory);
        return restTemplate;
    }

    /**
     * 添加静态资源配置映射
     * @param registry
     */
    @Override
    public void addResourceHandlers (ResourceHandlerRegistry registry) {
        //过滤swagger
        registry.addResourceHandler("/sigar").addResourceLocations("classpath:/sigar/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/files/**").addResourceLocations("file:" + "/opt/hummerrisk/file/");//用于前端访问服务器文件
        registry.addResourceHandler("/images/**").addResourceLocations("file:" + "/opt/hummerrisk/image/");//用于前端访问服务器文件
        registry.addResourceHandler("/**").addResourceLocations("classpath:/resources/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");

        super.addResourceHandlers(registry);

    }
}
