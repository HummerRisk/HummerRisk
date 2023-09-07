package com.hummer.system.dto;

import java.util.List;
import java.util.Map;

public class ReportDTO {

    private String id;

    private String name;

    private List<Map<String, String>> list;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Map<String, String>> getList() {
        return list;
    }

    public void setList(List<Map<String, String>> list) {
        this.list = list;
    }
}
