package com.hummerrisk.controller.request.dashboard;

import java.util.List;

public class AnslysisVo {

    private String color;

    private Integer cycle;

    private List<Boolean> ids;

    private List<String> types;

    private List<String> users;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getCycle() {
        return cycle;
    }

    public void setCycle(Integer cycle) {
        this.cycle = cycle;
    }

    public List<Boolean> getIds() {
        return ids;
    }

    public void setIds(List<Boolean> ids) {
        this.ids = ids;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
