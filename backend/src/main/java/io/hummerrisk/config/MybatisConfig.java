package io.hummerrisk.config;

import com.github.pagehelper.PageInterceptor;
import io.hummerrisk.commons.utils.DBEncryptInterceptor;
import io.hummerrisk.commons.utils.EncryptConfig;
import io.hummerrisk.commons.utils.LogUtil;
import io.hummerrisk.interceptor.UserDesensitizationInterceptor;
import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = {"io.hummerrisk.base.mapper", "io.hummerrisk.xpack.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
@EnableTransactionManagement
public class MybatisConfig {

    @Bean
    @ConditionalOnMissingBean
    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("pageSizeZero", "true");
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public DBEncryptInterceptor dbEncryptInterceptor() {
        DBEncryptInterceptor dbEncryptInterceptor = new DBEncryptInterceptor();
        List<EncryptConfig> configList = new ArrayList<>();
        // 添加加密/解密配置
        configList.add(new EncryptConfig("io.hummerrisk.base.domain.AccountWithBLOBs", "credential"));
        configList.add(new EncryptConfig("io.hummerrisk.base.domain.UserKey", "secretKey"));
        dbEncryptInterceptor.setEncryptConfigList(configList);
        return dbEncryptInterceptor;
    }

    /**
     * 等到ApplicationContext 加载完成之后 装配DBEncryptInterceptor
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        try {
            ApplicationContext context = event.getApplicationContext();
            DBEncryptInterceptor dBEncryptInterceptor = context.getBean(DBEncryptInterceptor.class);
            Map<String, DBEncryptConfig> beansOfType = context.getBeansOfType(DBEncryptConfig.class);
            for (DBEncryptConfig config : beansOfType.values()) {
                if (!CollectionUtils.isEmpty(config.encryptConfig())) {
                    dBEncryptInterceptor.getEncryptConfigList().addAll(config.encryptConfig());
                }
            }
        } catch (Exception e) {
            LogUtil.error("装配子模块的数据库字段加密错误，错误：" + e.getMessage());
        }

    }

    @Bean
    public UserDesensitizationInterceptor userDesensitizationInterceptor() {
        return new UserDesensitizationInterceptor();
    }

}
