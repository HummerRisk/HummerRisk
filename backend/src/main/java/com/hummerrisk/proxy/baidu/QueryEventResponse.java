package com.hummerrisk.proxy.baidu;

import com.baidubce.common.BaseBceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class QueryEventResponse  extends BaseBceResponse{
    private int page;
    private int pageSize;
    private int total;
    private List<Map<String,String>> data;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Map<String, String>> getData() {
        return data;
    }

    public void setData(List<Map<String, String>> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "QueryEventResponse{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", total=" + total +
                ", data=" + data +
                '}';
    }
}
