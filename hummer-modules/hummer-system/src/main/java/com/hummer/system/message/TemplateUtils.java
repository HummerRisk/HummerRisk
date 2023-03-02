package com.hummer.system.message;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.dialect.SpringStandardDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Map;

/**
 * HTML模板渲染工具类
 */
public class TemplateUtils {

    private final static SpringTemplateEngine templateEngine = new SpringTemplateEngine();

    static {
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        SpringStandardDialect dialect = new SpringStandardDialect();
        dialect.setEnableSpringELCompiler(true);
        templateEngine.setDialect(dialect);
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.setTemplateResolver(templateResolver);
    }

    /**
     * 使用 Thymeleaf 渲染 HTML
     *
     * @param template HTML模板
     * @param params   参数
     * @return 渲染后的HTML
     */
    public static String merge(String template, Map<String, Object> params) {
        Context context = new Context();
        context.setVariables(params);
        return templateEngine.process(template, context);
    }
}
