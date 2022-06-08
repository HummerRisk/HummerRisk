package io.hummerrisk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author harris
 */
@PropertySource(value = {
        "classpath:properties/global.properties",
        "classpath:properties/quartz.properties"
}, encoding = "UTF-8", ignoreResourceNotFound = true)
@Configuration
public class CommonConfig {

}
