package com.hummer.auth.config;

import com.hummer.auth.security.UserModularRealmAuthenticator;
import com.hummer.auth.security.realm.ShiroDBRealm;
import com.hummer.common.core.utils.ShiroUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration( enforceUniqueMethods = false)
public class ShiroConfig implements EnvironmentAware {

    private Environment env;

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        shiroFilterFactoryBean.setSuccessUrl("/");

        Map<String, String> filterChainDefinitionMap = shiroFilterFactoryBean.getFilterChainDefinitionMap();

        ShiroUtils.loadBaseFilterChain(filterChainDefinitionMap);

        filterChainDefinitionMap.put("/**", "apikey, authc");
        return shiroFilterFactoryBean;
    }

    @Bean
    public MemoryConstrainedCacheManager memoryConstrainedCacheManager() {
        return new MemoryConstrainedCacheManager();
    }

    @Bean
    public SessionManager sessionManager() {
        Long timeout = env.getProperty("spring.session.timeout", Long.class);
        String storeType = env.getProperty("spring.session.store-type");
        if (StringUtils.equals(storeType, "none")) {
            return ShiroUtils.getSessionManager(timeout, memoryConstrainedCacheManager());
        }
        return new ServletContainerSessionManager();
    }

    /**
     * securityManager 不用直接注入 Realm，可能会导致事务失效
     * 解决方法见 handleContextRefresh
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager(@Qualifier("sessionManager") SessionManager sessionManager, CacheManager cacheManager) {
        DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
        dwsm.setSessionManager(sessionManager);
        dwsm.setCacheManager(cacheManager);
        dwsm.setAuthenticator(modularRealmAuthenticator());
        return dwsm;
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public ShiroDBRealm shiroDBRealm() {
        return new ShiroDBRealm();
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }

    @Bean
    public ModularRealmAuthenticator modularRealmAuthenticator() {
        //自己重写的ModularRealmAuthenticator
        UserModularRealmAuthenticator modularRealmAuthenticator = new UserModularRealmAuthenticator();
        modularRealmAuthenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        return modularRealmAuthenticator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(DefaultWebSecurityManager defaultWebSecurityManager) {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(defaultWebSecurityManager);
        return new AuthorizationAttributeSourceAdvisor();
    }

    @Bean
    public SessionManager sessionManager(MemoryConstrainedCacheManager memoryConstrainedCacheManager) {
        Long sessionTimeout = env.getProperty("session.timeout", Long.class, 43200L); // 默认43200s, 12个小时
        return ShiroUtils.getSessionManager(sessionTimeout, memoryConstrainedCacheManager);
    }

    /**
     * 等到ApplicationContext 加载完成之后 装配shiroRealm
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        List<Realm> realmList = new ArrayList<>();
        ShiroDBRealm shiroDBRealm = context.getBean(ShiroDBRealm.class);
        // 基本realm
        realmList.add(shiroDBRealm);
        context.getBean(DefaultWebSecurityManager.class).setRealms(realmList);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
