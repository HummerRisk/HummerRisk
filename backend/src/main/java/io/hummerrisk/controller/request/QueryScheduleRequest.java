package io.hummerrisk.controller.request;

import io.hummerrisk.base.domain.Schedule;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class QueryScheduleRequest extends Schedule implements Serializable {

    private List<OrderRequest> orders;

    private Map<String, List<String>> filters;

    private static final long serialVersionUID = 1L;

    public List<OrderRequest> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderRequest> orders) {
        this.orders = orders;
    }

    public Map<String, List<String>> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, List<String>> filters) {
        this.filters = filters;
    }
}
