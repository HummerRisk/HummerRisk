package io.hummerrisk.controller.request.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditPassWordRequest {
    private String password;
    private String newpassword;
    private String id;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
