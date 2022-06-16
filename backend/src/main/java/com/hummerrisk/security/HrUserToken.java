package com.hummerrisk.security;

import org.apache.shiro.authc.UsernamePasswordToken;

public class HrUserToken extends UsernamePasswordToken {
    private String loginType;

    public HrUserToken() {
    }

    public HrUserToken(final String username, final String password) {
        super(username, password);
    }

    public HrUserToken(final String username, final String password, final String loginType) {
        super(username, password);
        this.loginType = loginType;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

}
