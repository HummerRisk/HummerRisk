package com.hummer.common.core.utils;


import com.hummer.common.core.domain.OrderRequest;

import java.util.ArrayList;
import java.util.List;

public class ServiceUtils {

    public static List<OrderRequest> getDefaultOrder(List<OrderRequest> orders) {
        if (orders == null || orders.size() < 1) {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setName("update_time");
            orderRequest.setType("desc");
            orders = new ArrayList<>();
            orders.add(orderRequest);
            return orders;
        }
        return orders;
    }
}
