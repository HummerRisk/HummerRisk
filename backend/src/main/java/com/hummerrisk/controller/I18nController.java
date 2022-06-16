package com.hummerrisk.controller;


import com.hummerrisk.commons.constants.I18nConstants;
import com.hummerrisk.i18n.Lang;
import com.hummerrisk.service.UserService;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.i18n.Translator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApiIgnore
@RestController
public class I18nController {

    private static final int FOR_EVER = 3600 * 24 * 30 * 12 * 10; //10 years in second

    @Value("${run.mode:release}")
    private String runMode;

    @Resource
    private UserService userService;

    @GetMapping("lang/change/{lang}")
    public void changeLang(@PathVariable String lang, HttpServletRequest request, HttpServletResponse response) {
        Lang targetLang = Lang.getLangWithoutDefault(lang);
        if (targetLang == null) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            LogUtil.error("Invalid parameter: " + lang);
            HRException.throwException(Translator.get("error_lang_invalid"));
        } else {
            userService.setLanguage(targetLang.getDesc());
            Cookie cookie = new Cookie(I18nConstants.LANG_COOKIE_NAME, targetLang.getDesc());
            cookie.setPath("/");
            cookie.setMaxAge(FOR_EVER);
            response.addCookie(cookie);
            //重新登录
            if ("release".equals(runMode)) {
                Cookie hummerCookie = new Cookie("HR_SESSION_ID", "deleteMe");
                hummerCookie.setPath("/");
                hummerCookie.setMaxAge(0);
                response.addCookie(hummerCookie);
            }
        }
    }
}
