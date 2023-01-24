package com.hummer.common.core.xss;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.hummer.common.core.utils.StringUtils;

/**
 * 自定义xss校验注解实现
 * 
 * @author harris1943
 */
public class XssValidator implements ConstraintValidator<Xss, String>
{
    private static final String HTML_PATTERN = "<(\\S*?)[^>]*>.*?|<.*? />";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext)
    {
        if (StringUtils.isBlank(value))
        {
            return true;
        }
        return !containsHtml(value);
    }

    public static boolean containsHtml(String value)
    {
        Pattern pattern = Pattern.compile(HTML_PATTERN);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
