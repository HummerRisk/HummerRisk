package com.hummer.common.core.dto;


import com.hummer.common.core.domain.ServerLynisResultDetail;

import java.util.List;

/**
 * @author harris
 */
public class ServerLynisResultDetailDTO extends ServerLynisResultDetail {

    private List<ServerLynisResultDetail> details;

    public List<ServerLynisResultDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ServerLynisResultDetail> details) {
        this.details = details;
    }
}
