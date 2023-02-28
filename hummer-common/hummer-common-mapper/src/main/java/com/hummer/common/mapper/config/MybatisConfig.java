package com.hummer.common.mapper.config;

import com.github.pagehelper.PageInterceptor;
import com.hummer.common.core.utils.EncryptConfig;
import com.hummer.common.core.utils.LogUtil;
import com.hummer.common.mapper.interceptor.DBEncryptInterceptor;
import com.hummer.common.mapper.interceptor.UserDesensitizationInterceptor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.openstack4j.api.Builders.dataSource;

@Configuration
@MapperScan(basePackages = {"com.hummer.common.mapper.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
@EnableTransactionManagement
public class MybatisConfig {

    @Bean
    public HikariDataSource hikariDataSourceBean(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:33060/hummer_config?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false");
        config.setUsername("root");
        config.setPassword("123456");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = null;
        try {
            // 加载JNDI配置
            Context context = new InitialContext();
            // 实例SessionFactory
            sqlSessionFactoryBean = new SqlSessionFactoryBean();
            // 配置数据源
            sqlSessionFactoryBean.setDataSource(dataSource);
            // 加载MyBatis配置文件
            PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            // 能加载多个，所以可以配置通配符(如：classpath*:mapper/**/*.xml)
            sqlSessionFactoryBean.setMapperLocations(resourcePatternResolver.getResources("classpath:com/hummer/common/mapper/mapper/*.xml"));
            // 配置mybatis的config文件(我目前用不上)
            // sqlSessionFactoryBean.setConfigLocation("mybatis-config.xml");
        } catch (Exception e) {
            System.out.println("创建SqlSession连接工厂错误：{}");
        }
        return sqlSessionFactoryBean;
    }
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactoryBean sqlSessionFactoryBean) throws Exception {
        SqlSessionTemplate sqlSessionTemplate=new SqlSessionTemplate(sqlSessionFactoryBean.getObject(), ExecutorType.BATCH);
        return sqlSessionTemplate;
    }

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
        configList.add(new EncryptConfig("AccountWithBLOBs", "credential"));
        configList.add(new EncryptConfig("UserKey", "secretKey"));
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
